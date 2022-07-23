package com.ju17th.instagramcloneapi.service;

import com.ju17th.instagramcloneapi.exception.BadRequestException;
import com.ju17th.instagramcloneapi.exception.ResourceNotFoundException;
import com.ju17th.instagramcloneapi.model.notification.PostNotification;
import com.ju17th.instagramcloneapi.model.notification.PostNotificationType;
import com.ju17th.instagramcloneapi.model.post.Comment;
import com.ju17th.instagramcloneapi.model.post.Post;
import com.ju17th.instagramcloneapi.model.user.User;
import com.ju17th.instagramcloneapi.payload.post.CommentRequest;
import com.ju17th.instagramcloneapi.payload.post.response.PagedResponse;
import com.ju17th.instagramcloneapi.payload.post.response.comment.CommentResponse;
import com.ju17th.instagramcloneapi.repository.UserRepository;
import com.ju17th.instagramcloneapi.repository.notification.PostNotificationRepository;
import com.ju17th.instagramcloneapi.repository.post.CommentRepository;
import com.ju17th.instagramcloneapi.repository.post.PostRepository;
import com.ju17th.instagramcloneapi.security.UserPrincipal;
import com.ju17th.instagramcloneapi.util.AppConstants;
import com.ju17th.instagramcloneapi.util.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostNotificationRepository postNotificationRepository;

    public Comment commentPost(Long postId, CommentRequest commentRequest, UserPrincipal currentUser) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Poll", "id", postId));

        User user = userRepository.getOne(currentUser.getId());

        Comment comment = new Comment();
        comment.setBody(commentRequest.getBody());
        comment.setPost(post);
        comment.setUser(user);

        PostNotification postNotification = new PostNotification();
        postNotification.setPost(post);
        postNotification.setNotificationType(PostNotificationType.POST_COMMENTED);
        postNotification.setNotificationCreator(user);
        postNotification.setNotificationReceiver(userRepository.getOne(post.getCreatedBy()));
        postNotificationRepository.save(postNotification);

        return commentRepository.save(comment);
    }

    public PagedResponse<CommentResponse> getCommentsByPostId(UserPrincipal currentUser, int page, int size, Long postId) {
        validatePageNumberAndSize(page, size);

        // Retrieve Polls
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        Page<Comment> comments = commentRepository.findCommentsByPostId(postId, pageable);

        if (comments.getNumberOfElements() == 0) {
            return new PagedResponse<>(Collections.emptyList(), comments.getNumber(),
                    comments.getSize(), comments.getTotalElements(), comments.getTotalPages(), comments.isLast());
        }

        Map<Long, User> creatorMap = getCommentCreatorMap(comments.getContent());

        List<CommentResponse> commentResponses = comments.map(comment -> {
            return ModelMapper.mapCommentToCommentResponse(comment,
                    creatorMap.get(comment.getCreatedBy()));
        }).getContent();

        return new PagedResponse<>(commentResponses, comments.getNumber(),
                comments.getSize(), comments.getTotalElements(), comments.getTotalPages(), comments.isLast());
    }

    public CommentResponse getCommentByPostIdAndCommentId(UserPrincipal currentUser, Long postId, Long commentId) {
        Comment comment = commentRepository.findCommentByPostIdAndCommentId(postId, commentId).orElseThrow(
                () -> new ResourceNotFoundException("Comment", "id", commentId));

        // Retrieve comment creator details
        User creator = userRepository.findById(comment.getCreatedBy())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", comment.getCreatedBy()));

        return ModelMapper.mapCommentToCommentResponse(comment, creator);
    }

    private void validatePageNumberAndSize(int page, int size) {
        if (page < 0) {
            throw new BadRequestException("Page number cannot be less than zero.");
        }

        if (size > AppConstants.MAX_PAGE_SIZE) {
            throw new BadRequestException("Page size must not be greater than " + AppConstants.MAX_PAGE_SIZE);
        }
    }

    Map<Long, User> getCommentCreatorMap(List<Comment> comments) {
        // Get Poll Creator details of the given list of polls
        List<Long> creatorIds = comments.stream()
                .map(Comment::getCreatedBy)
                .distinct()
                .collect(Collectors.toList());

        List<User> creators = userRepository.findByIdIn(creatorIds);
        Map<Long, User> creatorMap = creators.stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        return creatorMap;
    }
}
