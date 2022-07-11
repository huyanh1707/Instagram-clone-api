package com.ju17th.instagramcloneapi.service;

import com.ju17th.instagramcloneapi.entity.Post;
import com.ju17th.instagramcloneapi.entity.User;
import com.ju17th.instagramcloneapi.payload.post.PagedResponse;
import com.ju17th.instagramcloneapi.payload.post.request.PostRequest;
import com.ju17th.instagramcloneapi.payload.post.response.PostResponse;
import com.ju17th.instagramcloneapi.security.services.UserDetailsImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Service
public interface PostService {
    PagedResponse<PostResponse> getAllPosts(int page, int size, UserDetailsImpl currentUser);

    ResponseEntity<?> createPost(PostRequest postRequest, MultipartFile image);

    Map<Long, User> getPostCreatorMap(List<Post> posts);
}
