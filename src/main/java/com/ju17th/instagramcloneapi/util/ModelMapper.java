package com.ju17th.instagramcloneapi.util;

import com.ju17th.instagramcloneapi.model.notification.ObserveNotification;
import com.ju17th.instagramcloneapi.model.notification.PostNotification;
import com.ju17th.instagramcloneapi.model.post.Comment;
import com.ju17th.instagramcloneapi.model.post.Like;
import com.ju17th.instagramcloneapi.model.post.Post;
import com.ju17th.instagramcloneapi.model.user.Follow;
import com.ju17th.instagramcloneapi.model.user.FollowRequest;
import com.ju17th.instagramcloneapi.model.user.User;
import com.ju17th.instagramcloneapi.payload.follow.FollowListResponse;
import com.ju17th.instagramcloneapi.payload.post.response.comment.CommentResponse;
import com.ju17th.instagramcloneapi.payload.post.response.like.LikeResponse;
import com.ju17th.instagramcloneapi.payload.post.response.like.LikedReponse;
import com.ju17th.instagramcloneapi.payload.post.response.like.LikesAndCommentsResponse;
import com.ju17th.instagramcloneapi.payload.post.response.notification.FollowRequestPayload;
import com.ju17th.instagramcloneapi.payload.post.response.notification.ObserveNotificationPayload;
import com.ju17th.instagramcloneapi.payload.post.response.notification.PostNotificationPayload;
import com.ju17th.instagramcloneapi.payload.post.response.post.PhotoModalResponse;
import com.ju17th.instagramcloneapi.payload.post.response.post.PostResponse;
import com.ju17th.instagramcloneapi.payload.post.response.post.ProfilePostResponse;
import com.ju17th.instagramcloneapi.payload.post.response.post.SavedPostResponse;
import com.ju17th.instagramcloneapi.payload.post.response.profile.EditProfileResponse;
import com.ju17th.instagramcloneapi.payload.post.response.profile.UserProfileResponse;
import com.ju17th.instagramcloneapi.payload.user.UserSummary;

import java.util.ArrayList;
import java.util.List;

public class ModelMapper {
    public static PostResponse mapPostToPostResponse(Post post, User author) {
        PostResponse postResponse = new PostResponse();

        postResponse.setId(post.getId());
        postResponse.setDescription(post.getDescription());
        postResponse.setCreationDateTime(post.getCreatedAt());
        postResponse.setImagePath(post.getImagePath());

        UserSummary userSummary = new UserSummary(author.getId(), author.getUsername(), author.getName(), author.getImagePath());
        postResponse.setCreatedBy(userSummary);

        return postResponse;
    }

    public static CommentResponse mapCommentToCommentResponse(Comment comment, User author) {
        CommentResponse commentResponse = new CommentResponse();

        commentResponse.setId(comment.getId());
        commentResponse.setBody(comment.getBody());
        commentResponse.setCreationDateTime(comment.getCreatedAt());
        commentResponse.setPost_id(comment.getPost().getId());

        UserSummary userSummary = new UserSummary(author.getId(), author.getUsername(), author.getName(), author.getImagePath());
        commentResponse.setCreatedBy(userSummary);

        return commentResponse;
    }

    public static LikeResponse mapLikeToLikeResponse(Like like, User author, List<Follow> followList) {
        LikeResponse likeResponse = new LikeResponse();

        likeResponse.setId(like.getId());
        UserSummary userSummary = new UserSummary(author.getId(), author.getUsername(), author.getName(), author.getImagePath());
        likeResponse.setCreatedBy(userSummary);
        likeResponse.setObserved(false);

        for (Follow follow : followList) {
            if (userSummary.getId() == follow.getFollowing().getId())
                likeResponse.setObserved(true);
        }

        return likeResponse;
    }

    public static UserSummary mapUsersToUserSummaries(User user) {
        UserSummary userSummary = new UserSummary();

        userSummary.setId(user.getId());
        userSummary.setName(user.getName());
        userSummary.setImagePath(user.getImagePath());
        userSummary.setUsername(user.getUsername());

        return userSummary;
    }

    public static List<ProfilePostResponse> mapPostToProfilePostResponse(List<Post> posts, List<LikesAndCommentsResponse> likesAndCommentsResponses) {
        List<ProfilePostResponse> profilePostResponses = new ArrayList<>();
        for (int i = 0; i < posts.size(); i++) {
            ProfilePostResponse profilePostResponse = new ProfilePostResponse();
            profilePostResponse.setId(posts.get(i).getId());
            profilePostResponse.setImagePath(posts.get(i).getImagePath());
            profilePostResponse.setCommentCount(Long.valueOf(likesAndCommentsResponses.get(i).getCommentsCount()));
            profilePostResponse.setLikeCount(Long.valueOf(likesAndCommentsResponses.get(i).getLikesCount()));
            profilePostResponses.add(profilePostResponse);
        }

        return profilePostResponses;
    }

    public static UserProfileResponse mapDataToUserProfileResponse(User user, List<Post> posts, List<LikesAndCommentsResponse> likesAndCommentsResponses, List<Post> savedPosts, List<LikesAndCommentsResponse> likesAndCommentsResponsesForSavedPosts, long postCount, int followersCount, int followingCount, boolean isFollowed, boolean requestSent) {
        UserProfileResponse userProfileResponse = new UserProfileResponse();
        List<ProfilePostResponse> profilePostResponses = mapPostToProfilePostResponse(posts, likesAndCommentsResponses);
        List<ProfilePostResponse> savedPhotosResponses = mapPostToProfilePostResponse(savedPosts, likesAndCommentsResponsesForSavedPosts);

        UserSummary userSummary = mapUsersToUserSummaries(user);
        userProfileResponse.setUserSummary(userSummary);
        userProfileResponse.setPostCount(postCount);
        userProfileResponse.setFollowersCount(Long.valueOf(followersCount));
        userProfileResponse.setFollowingCount(Long.valueOf(followingCount));
        userProfileResponse.setPosts(profilePostResponses);
        userProfileResponse.setSavedPosts(savedPhotosResponses);
        userProfileResponse.setPrivate(user.isPrivate());
        userProfileResponse.setFollowed(isFollowed);
        userProfileResponse.setRequestSent(requestSent);
        userProfileResponse.setBio(user.getBio());
        userProfileResponse.setUrl(user.getUrl());

        return userProfileResponse;
    }

    public static FollowListResponse mapUserListToUsersSummaries(List<User> followersList) {
        FollowListResponse followListResponse = new FollowListResponse();
        List<UserSummary> userSummaries = new ArrayList<>();

        for (User user : followersList)
            userSummaries.add(mapUsersToUserSummaries(user));

        followListResponse.setUserSummaryList(userSummaries);

        return followListResponse;
    }

    public static PhotoModalResponse mapDataToPhotoModalResponse(Post post, User user, long likeCount, LikedReponse isLiked, SavedPostResponse isSaved) {
        PhotoModalResponse photoModalResponse = new PhotoModalResponse();

        photoModalResponse.setLikeCount((int) likeCount);
        photoModalResponse.setPostResponse(mapPostToPostResponse(post, user));
        photoModalResponse.setLikedReponse(isLiked);
        photoModalResponse.setSavedPostResponse(isSaved);

        return photoModalResponse;
    }

    public static EditProfileResponse mapUserToEditProfileResponse(User user) {
        EditProfileResponse editProfileResponse = new EditProfileResponse();

        editProfileResponse.setId(user.getId());
        editProfileResponse.setName(user.getName());
        editProfileResponse.setUsername(user.getUsername());
        editProfileResponse.setImagePath(user.getImagePath());
        editProfileResponse.setPrivate(user.isPrivate());
        editProfileResponse.setBio(user.getBio());
        editProfileResponse.setUrl(user.getUrl());
        editProfileResponse.setEmail(user.getEmail());
        editProfileResponse.setSex(user.getSex());

        return editProfileResponse;
    }

    public static PostNotificationPayload mapPostNotificationToPostNotificationResponse(PostNotification postNotification, User user, String imagePath) {
        PostNotificationPayload postNotificationPayload = new PostNotificationPayload();

        postNotificationPayload.setId(postNotification.getId());
        postNotificationPayload.setCreator(mapUsersToUserSummaries(user));
        postNotificationPayload.setCreationDateTime(postNotification.getCreatedAt());
        postNotificationPayload.setImagePath(imagePath);
        postNotificationPayload.setPostNotificationType(postNotification.getNotificationType().name());

        return postNotificationPayload;
    }

    public static ObserveNotificationPayload mapObserveNotificationToObserveNotificationResponse(ObserveNotification observeNotification, User user, boolean isFollowed) {
        ObserveNotificationPayload observeNotificationPayload = new ObserveNotificationPayload();

        observeNotificationPayload.setId(observeNotification.getId());
        observeNotificationPayload.setCreator(mapUsersToUserSummaries(user));
        observeNotificationPayload.setCreationDateTime(observeNotification.getCreatedAt());
        observeNotificationPayload.setObserved(isFollowed);

        return observeNotificationPayload;
    }

    public static FollowRequestPayload mapFollowRequestToFollowRequestResponse(FollowRequest followRequest) {
        FollowRequestPayload followRequestPayload = new FollowRequestPayload();

        followRequestPayload.setId(followRequest.getId());
        followRequestPayload.setCreator(mapUsersToUserSummaries(followRequest.getFollower()));
        followRequestPayload.setCreationDateTime(followRequest.getCreatedAt());

        return followRequestPayload;
    }
}
