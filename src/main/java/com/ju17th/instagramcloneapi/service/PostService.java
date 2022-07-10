package com.ju17th.instagramcloneapi.service;

import com.ju17th.instagramcloneapi.entity.Post;
import com.ju17th.instagramcloneapi.entity.User;
import com.ju17th.instagramcloneapi.payload.post.PagedResponse;
import com.ju17th.instagramcloneapi.payload.post.response.PostResponse;
import com.ju17th.instagramcloneapi.security.services.UserDetailsImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface PostService {
    PagedResponse<PostResponse> getAllPosts(int page, int size, UserDetailsImpl currentUser);

    Map<Long, User> getPostCreatorMap(List<Post> posts);
}
