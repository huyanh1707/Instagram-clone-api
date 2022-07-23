package com.ju17th.instagramcloneapi.service.impl;

import com.ju17th.instagramcloneapi.exception.BadRequestException;
import com.ju17th.instagramcloneapi.exception.InvalidOldPasswordException;
import com.ju17th.instagramcloneapi.exception.ResourceNotFoundException;
import com.ju17th.instagramcloneapi.model.notification.ObserveNotification;
import com.ju17th.instagramcloneapi.model.notification.PostNotification;
import com.ju17th.instagramcloneapi.model.post.Post;
import com.ju17th.instagramcloneapi.model.post.SavedPost;
import com.ju17th.instagramcloneapi.model.user.Follow;
import com.ju17th.instagramcloneapi.model.user.FollowRequest;
import com.ju17th.instagramcloneapi.model.user.User;
import com.ju17th.instagramcloneapi.payload.*;
import com.ju17th.instagramcloneapi.payload.post.response.PagedResponse;
import com.ju17th.instagramcloneapi.payload.post.response.like.LikesAndCommentsResponse;
import com.ju17th.instagramcloneapi.payload.post.response.notification.FollowRequestPayload;
import com.ju17th.instagramcloneapi.payload.post.response.notification.NotificationsPayload;
import com.ju17th.instagramcloneapi.payload.post.response.notification.ObserveNotificationPayload;
import com.ju17th.instagramcloneapi.payload.post.response.notification.PostNotificationPayload;
import com.ju17th.instagramcloneapi.payload.post.response.profile.EditProfileResponse;
import com.ju17th.instagramcloneapi.payload.post.response.profile.PrivateResponse;
import com.ju17th.instagramcloneapi.payload.post.response.profile.ProfilePictureUpdateResponse;
import com.ju17th.instagramcloneapi.payload.post.response.profile.UserProfileResponse;
import com.ju17th.instagramcloneapi.payload.user.*;
import com.ju17th.instagramcloneapi.repository.UserRepository;
import com.ju17th.instagramcloneapi.repository.follow.FollowRepository;
import com.ju17th.instagramcloneapi.repository.follow.FollowRequestRepository;
import com.ju17th.instagramcloneapi.repository.notification.ObserveNotificationRepository;
import com.ju17th.instagramcloneapi.repository.notification.PostNotificationRepository;
import com.ju17th.instagramcloneapi.repository.post.CommentRepository;
import com.ju17th.instagramcloneapi.repository.post.LikeRepository;
import com.ju17th.instagramcloneapi.repository.post.PostRepository;
import com.ju17th.instagramcloneapi.repository.post.SavedPostRepository;
import com.ju17th.instagramcloneapi.security.UserPrincipal;
import com.ju17th.instagramcloneapi.service.FileStorageService;
import com.ju17th.instagramcloneapi.service.UserService;
import com.ju17th.instagramcloneapi.util.AppConstants;
import com.ju17th.instagramcloneapi.util.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SavedPostRepository savedPostRepository;

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private ObserveNotificationRepository observeNotificationRepository;

    @Autowired
    private PostNotificationRepository postNotificationRepository;

    @Autowired
    private FollowRequestRepository followRequestRepository;

    @Autowired
    private FileStorageService fileStorageService;

    private String defaultImagePath = "http://localhost:5000/api/posts/images/default_avatar.jpg";

    public PagedResponse<UserSummary> getUsersByUsernameOrName(int page, int size, String usernameOrName) {
        validatePageNumberAndSize(page, size);

        // Retrieve Users
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        Page<User> users = userRepository.findByUsernameContainingOrNameContaining(usernameOrName, usernameOrName, pageable);

        if (users.getNumberOfElements() == 0) {
            return new PagedResponse<>(Collections.emptyList(), users.getNumber(),
                    users.getSize(), users.getTotalElements(), users.getTotalPages(), users.isLast());
        }

        List<UserSummary> userSummaries = users.map(user -> {
            return ModelMapper.mapUsersToUserSummaries(user);
        }).getContent();

        return new PagedResponse<>(userSummaries, users.getNumber(),
                users.getSize(), users.getTotalElements(), users.getTotalPages(), users.isLast());
    }

    public UserProfileResponse getUserProfileByUsername(String username, UserPrincipal currentUser) {
        //userSummary
        Optional<User> user = userRepository.findByUsername(username);

        //userPhotos
        List<Post> posts = postRepository.findByCreatedBy(user.get().getId());
        List<LikesAndCommentsResponse> likesAndCommentsResponses = new ArrayList<>();
        for (Post post : posts) {
            LikesAndCommentsResponse likesAndCommentsResponse = new LikesAndCommentsResponse();
            likesAndCommentsResponse.setCommentsCount((int) commentRepository.countByPostId(post.getId()));
            likesAndCommentsResponse.setLikesCount((int) likeRepository.countByPostId(post.getId()));
            likesAndCommentsResponses.add(likesAndCommentsResponse);
        }

        //savedPhotos
        List<SavedPost> savedPostsList = savedPostRepository.findByCreatedBy(user.get().getId());
        List<Post> savedPosts = new ArrayList<>();
        for (SavedPost savedPost : savedPostsList) {
            savedPosts.add(postRepository.findById(savedPost.getPost().getId()).get());
        }
        List<LikesAndCommentsResponse> likesAndCommentsResponsesForSavedPosts = new ArrayList<>();
        for (Post post : savedPosts) {
            LikesAndCommentsResponse likesAndCommentsResponse = new LikesAndCommentsResponse();
            likesAndCommentsResponse.setCommentsCount((int) commentRepository.countByPostId(post.getId()));
            likesAndCommentsResponse.setLikesCount((int) likeRepository.countByPostId(post.getId()));
            likesAndCommentsResponsesForSavedPosts.add(likesAndCommentsResponse);
        }

        //postCount
        long postCount = postRepository.countByCreatedBy(user.get().getId());

        //followersCount
        int followersCount = followRepository.countByFollowingId(user.get().getId());

        //followingCount
        int followingCount = followRepository.countByFollowerId(user.get().getId());

        Optional<Follow> follow = followRepository.findFollowByFollowerIdAndFollowingId(currentUser.getId(), user.get().getId());

        boolean isFollowed = follow.isPresent();

        boolean requestSent = followRequestRepository.findByFollowerIdAndFollowingId(currentUser.getId(), user.get().getId()).isPresent();

        return ModelMapper.mapDataToUserProfileResponse(user.get(), posts, likesAndCommentsResponses, savedPosts, likesAndCommentsResponsesForSavedPosts, postCount, followersCount, followingCount, isFollowed, requestSent);
    }

    public EditProfileResponse getProfileDetailsToEdit(UserPrincipal currentUser) {
        User user = userRepository.findByUsername(currentUser.getUsername()).get();
        return ModelMapper.mapUserToEditProfileResponse(user);
    }

    public void updateUser(UserPrincipal currentUser, UpdateUserRequest updateUserRequest) {
        User userToUpdate = userRepository.getOne(currentUser.getId());

        userToUpdate.setName(updateUserRequest.getName());
        userToUpdate.setUsername(updateUserRequest.getUsername());
        userToUpdate.setUrl(updateUserRequest.getUrl());
        userToUpdate.setBio(updateUserRequest.getBio());
        userToUpdate.setEmail(updateUserRequest.getEmail());
        userToUpdate.setPhone(updateUserRequest.getPhone());
        userToUpdate.setSex(updateUserRequest.getSex());

        userRepository.save(userToUpdate);
    }

    public UserProfile getUserProfile(String username){
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        long postsCount = postRepository.countByCreatedBy(user.getId());

        UserProfile userProfile = new UserProfile(user.getId(), user.getUsername(), user.getName(), user.getCreatedAt(), postsCount);

        return userProfile;
    }

    public ResponseEntity<?> updateUserPicture(UserPrincipal currentUser, MultipartFile image) {
        String fileName = fileStorageService.storeFile(image);
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("api/posts/images/")
                .path(fileName)
                .toUriString();

        User userToUpdate = userRepository.getOne(currentUser.getId());
        if (fileDownloadUri == null)
            userToUpdate.setImagePath(defaultImagePath);
        else
            userToUpdate.setImagePath(fileDownloadUri);

        userRepository.save(userToUpdate);

        return ResponseEntity.ok().body(new ProfilePictureUpdateResponse(fileDownloadUri));
    }

    public PrivateResponse setIsPrivate(UserPrincipal currentUser) {
        User userToUpdate = userRepository.getOne(currentUser.getId());

        userToUpdate.setPrivate(!userToUpdate.isPrivate());
        userRepository.save(userToUpdate);
        return new PrivateResponse(userToUpdate.isPrivate());
    }

    public ResponseEntity<?> changeUserPassword(ChangePasswordRequest changePasswordRequest) {
        Optional<User> user = userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());

        if (!checkIfValidOldPassword(user.get(), changePasswordRequest.getOldPassword()))
            throw new InvalidOldPasswordException();

        user.get().setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        userRepository.save(user.get());

        return ResponseEntity.ok(new ApiResponse(true, "Password changed successfully."));
    }

    public boolean checkIfValidOldPassword(User user, String oldPassword) {
        return passwordEncoder.matches(oldPassword, user.getPassword());
    }

    public NotificationsPayload getUserNotifications(UserPrincipal currentUser) {
        List<PostNotification> postNotifications = postNotificationRepository.findAllByNotificationReceiverId(currentUser.getId());
        List<ObserveNotification> observeNotifications = observeNotificationRepository.findAllByNotificationReceiverId(currentUser.getId());
        List<FollowRequest> followRequests = followRequestRepository.findAllByFollowingId(currentUser.getId());

        List<PostNotificationPayload> postNotificationPayloads = new ArrayList<>();
        for (PostNotification postNotification : postNotifications) {
            User user = userRepository.getOne(postNotification.getNotificationCreator().getId());
            String imagePath = postNotification.getPost().getImagePath();
            postNotificationPayloads.add(ModelMapper.mapPostNotificationToPostNotificationResponse(postNotification, user, imagePath));
        }

        List<ObserveNotificationPayload> observeNotificationPayloads = new ArrayList<>();
        for (ObserveNotification observeNotification : observeNotifications) {
            Optional<Follow> follow = followRepository.findFollowByFollowerIdAndFollowingId(currentUser.getId(), observeNotification.getNotificationCreator().getId());
            boolean isFollowed = follow.isPresent();
            User user = userRepository.getOne(observeNotification.getNotificationCreator().getId());

            observeNotificationPayloads.add(ModelMapper.mapObserveNotificationToObserveNotificationResponse(observeNotification, user, isFollowed));
        }

        List<FollowRequestPayload> followRequestPayloads = new ArrayList<>();
        for (FollowRequest followRequest : followRequests) {
            followRequestPayloads.add(ModelMapper.mapFollowRequestToFollowRequestResponse(followRequest));
        }

        return new NotificationsPayload(postNotificationPayloads, observeNotificationPayloads, followRequestPayloads, followRequests.size());
    }

    public void validatePageNumberAndSize(int page, int size) {
        if (page < 0) {
            throw new BadRequestException("Page number cannot be less than zero.");
        }

        if (size > AppConstants.MAX_PAGE_SIZE) {
            throw new BadRequestException("Page size must not be greater than " + AppConstants.MAX_PAGE_SIZE);
        }
    }

    public UserIdentityAvailability checkUsernameAvailability(String username){
        Boolean isAvailable = !userRepository.existsByUsername(username);
        return new UserIdentityAvailability(isAvailable);
    }

    public UserIdentityAvailability checkEmailAvailability(String email){
        Boolean isAvailable = !userRepository.existsByEmail(email);
        return new UserIdentityAvailability(isAvailable);
    }
}
