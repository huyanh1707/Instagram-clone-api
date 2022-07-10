package com.ju17th.instagramcloneapi.service.impl;

import com.ju17th.instagramcloneapi.entity.Follow;
import com.ju17th.instagramcloneapi.entity.Post;
import com.ju17th.instagramcloneapi.entity.User;
import com.ju17th.instagramcloneapi.payload.post.PagedResponse;
import com.ju17th.instagramcloneapi.payload.post.response.PostResponse;
import com.ju17th.instagramcloneapi.repository.UserRepository;
import com.ju17th.instagramcloneapi.repository.post.FollowRepository;
import com.ju17th.instagramcloneapi.repository.post.PostRepository;
import com.ju17th.instagramcloneapi.security.services.UserDetailsImpl;
import com.ju17th.instagramcloneapi.service.PostService;
import com.ju17th.instagramcloneapi.utils.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

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

    @Override
    public PagedResponse<PostResponse> getAllPosts(int page, int size, UserDetailsImpl currentUser) {

        // lấy danh sách người đã theo dõi
        List<Follow> followList = followRepository.findAllByFollowerId(currentUser.getId());
        List<Long> followingIdsList = new ArrayList<>();

        for(Follow follow : followList) {
            Long followingId = new Long(follow.getFollowing().getId());
            if (followList.contains(followingId)) {
               continue;
            }
            followingIdsList.add(followingId);
        }

        // Retrieve posts
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        Page<Post> posts = postRepository.findAllPostsByFollowedUsers(followingIdsList, pageable);

        if (posts.getNumberOfElements() == 0) {
            return new PagedResponse<>(Collections.emptyList(), posts.getNumber(),
                    posts.getSize(), posts.getTotalElements(), posts.getTotalPages(), posts.isLast());
        }

        // Map Posts to PostResponses containing photos and post creator details
        Map<Long, User> creatorMap = getPostCreatorMap(posts.getContent());

        List<PostResponse> postResponses = posts.map(post -> {
            return ModelMapper.mapPostToPostResponse(post,
                    creatorMap.get(post.getCreatedBy()));
        }).getContent();

        return new PagedResponse<>(postResponses, posts.getNumber(),
                posts.getSize(), posts.getTotalElements(), posts.getTotalPages(), posts.isLast());
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
}
