package com.ju17th.instagramcloneapi.service.impl;

import com.ju17th.instagramcloneapi.entity.Follow;
import com.ju17th.instagramcloneapi.entity.Post;
import com.ju17th.instagramcloneapi.entity.User;
import com.ju17th.instagramcloneapi.exception.ResourceNotFoundException;
import com.ju17th.instagramcloneapi.payload.ApiResponse;
import com.ju17th.instagramcloneapi.payload.post.PagedResponse;
import com.ju17th.instagramcloneapi.payload.post.request.PostRequest;
import com.ju17th.instagramcloneapi.payload.post.response.PostResponse;
import com.ju17th.instagramcloneapi.repository.UserRepository;
import com.ju17th.instagramcloneapi.repository.post.FollowRepository;
import com.ju17th.instagramcloneapi.repository.post.PostRepository;
import com.ju17th.instagramcloneapi.security.services.UserDetailsImpl;
import com.ju17th.instagramcloneapi.service.FileStorageService;
import com.ju17th.instagramcloneapi.service.PostService;
import com.ju17th.instagramcloneapi.utils.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Override
    public PagedResponse<PostResponse> getAllPosts(int page, int size, UserDetailsImpl currentUser) {

        // lấy danh sách người đã theo dõi
        List<Follow> followList = followRepository.findAllByFollowerId(currentUser.getId());
        List<Long> followingIdsList = new ArrayList<>();

        for(Follow follow : followList) {
            Long followingId = follow.getFollowing().getId();
            if (followList.contains(followingId)) {
               continue;
            }
            followingIdsList.add(followingId);
        }

        // phân trang
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        Page<Post> posts = postRepository.findAllPostsByFollowedUsers(followingIdsList, pageable);

        if (posts.getNumberOfElements() == 0) {
            return new PagedResponse<>(Collections.emptyList(), posts.getNumber(),
                    posts.getSize(), posts.getTotalElements(), posts.getTotalPages(), posts.isLast());
        }

        // Map Posts với PostResponses chứa ảnh và chi tiết người tạo bài viết
        Map<Long, User> creatorMap = getPostCreatorMap(posts.getContent());

        List<PostResponse> postResponses = posts.map(post ->
                    ModelMapper.mapPostToPostResponse(post, creatorMap.get(post.getCreatedBy()))
        ).getContent();

        return new PagedResponse<>(postResponses, posts.getNumber(),
                posts.getSize(), posts.getTotalElements(), posts.getTotalPages(), posts.isLast());
    }

    @Override
    public ResponseEntity<?> createPost(PostRequest postRequest, MultipartFile image) {
        String fileName = fileStorageService.storeFile(image);
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("api/posts/images/")
                .path(fileName)
                .toUriString();

        Post post = new Post();
        post.setDescription(postRequest.getDescription());
        post.setImagePath(fileDownloadUri);
        postRepository.save(post);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{postId}")
                .buildAndExpand(post.getId()).toUri();

        return ResponseEntity.created(location)
                .body(new ApiResponse(true, "Post created successfully."));
    }

    public Map<Long, User> getPostCreatorMap(List<Post> posts) {
        // Get Poll Creator details of the given list of polls
        List<Long> creatorIds = posts.stream()
                .map(Post::getCreatedBy)
                .distinct()
                .collect(Collectors.toList());

        List<User> creators = userRepository.findByIdIn(creatorIds);
        Map<Long, User> creatorMap = creators.stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        return creatorMap;
    }

    @Override
    public PostResponse getPostById(Long postId, UserDetailsImpl currentUser) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new ResourceNotFoundException("Post", "id", postId));

        // Retrieve post creator details
        User creator = userRepository.findById(post.getCreatedBy())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", post.getCreatedBy()));

        return ModelMapper.mapPostToPostResponse(post, creator);
    }
}
