package com.ju17th.instagramcloneapi.controllers;

import com.ju17th.instagramcloneapi.payload.post.PagedResponse;
import com.ju17th.instagramcloneapi.payload.post.request.PostRequest;
import com.ju17th.instagramcloneapi.payload.post.response.PostResponse;
import com.ju17th.instagramcloneapi.security.services.CurrentUser;
import com.ju17th.instagramcloneapi.security.services.UserDetailsImpl;
import com.ju17th.instagramcloneapi.service.PostService;
import com.ju17th.instagramcloneapi.utils.PagingConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@RestController
@RequestMapping("api/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @GetMapping()
    public PagedResponse<PostResponse> getPosts(@RequestParam(value = "page", defaultValue = PagingConstant.DEFAULT_PAGE_NUMBER) int page,
                                                @RequestParam(value = "size", defaultValue = PagingConstant.DEFAULT_PAGE_SIZE) int size,
                                                @CurrentUser UserDetailsImpl currentUser) {
        return postService.getAllPosts(page, size, currentUser);
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> createPost(@Valid @ModelAttribute PostRequest postRequest,
                                        @RequestParam("image")MultipartFile image) {
        return postService.createPost(postRequest, image);
    }

    @GetMapping
    public PostResponse getPostById(@CurrentUser UserDetailsImpl currentUser,
                                    @PathVariable Long postId) {
        return postService.getPostById(postId, currentUser);
    }
}
