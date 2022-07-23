package com.ju17th.instagramcloneapi.service;

import com.ju17th.instagramcloneapi.model.post.Like;
import com.ju17th.instagramcloneapi.model.post.Post;
import com.ju17th.instagramcloneapi.model.user.User;
import com.ju17th.instagramcloneapi.payload.post.CommentRequest;
import com.ju17th.instagramcloneapi.payload.post.PostRequest;
import com.ju17th.instagramcloneapi.payload.post.response.*;
import com.ju17th.instagramcloneapi.payload.post.response.comment.CommentResponse;
import com.ju17th.instagramcloneapi.payload.post.response.like.LikeCountResponse;
import com.ju17th.instagramcloneapi.payload.post.response.like.LikeResponse;
import com.ju17th.instagramcloneapi.payload.post.response.like.LikedReponse;
import com.ju17th.instagramcloneapi.payload.post.response.post.PhotoModalResponse;
import com.ju17th.instagramcloneapi.payload.post.response.post.PostResponse;
import com.ju17th.instagramcloneapi.payload.post.response.post.SavedPostResponse;
import com.ju17th.instagramcloneapi.security.UserPrincipal;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Service
public interface PostService {
     PagedResponse<PostResponse> getAllPosts(int page, int size, UserPrincipal currentUser);

     ResponseEntity<?> createPost(PostRequest postRequest, MultipartFile image);

     CommentResponse addComment(UserPrincipal currentUser, Long postId, CommentRequest commentRequest);

     PostResponse getPostById(Long postId, UserPrincipal currentUser);

     ResponseEntity<Resource> getPostImage(String fileName, HttpServletRequest request);

     PagedResponse<PostResponse> getPostByUserId(UserPrincipal currentUser, int page, int size, Long userId);

     PagedResponse<LikeResponse> getLikesByPostId(UserPrincipal currentUser, int page, int size, Long postId);

     LikedReponse addPostLike(Long postId, UserPrincipal currentUser);

     SavedPostResponse savePostForUser(Long postId, UserPrincipal currentUser);

     LikedReponse checkIfPostLiked(Long postId, UserPrincipal currentUser);

     SavedPostResponse checkIfPostSaved(Long postId, UserPrincipal currentUser);

     LikeCountResponse getLikesCountByPostId(Long postId);

     PhotoModalResponse getPhotoModalInfo(Long postId, UserPrincipal currentUser);

     Map<Long, User> getPostCreatorMap(List<Post> posts);

     Map<Long, User> getLikeCreatorMap(List<Like> likes);

     void validatePageNumberAndSize(int page, int size);
}
