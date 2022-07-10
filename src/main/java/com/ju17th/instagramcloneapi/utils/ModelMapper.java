package com.ju17th.instagramcloneapi.utils;

import com.ju17th.instagramcloneapi.entity.Post;
import com.ju17th.instagramcloneapi.entity.User;
import com.ju17th.instagramcloneapi.payload.post.response.PostResponse;
import com.ju17th.instagramcloneapi.payload.user.UserSummary;

public class ModelMapper {

    public static PostResponse mapPostToPostResponse(Post post, User author) {
        PostResponse postResponse = new PostResponse();

        postResponse.setId(post.getId());
        postResponse.setDescription(post.getDescription());
        postResponse.setCreationDateTime(post.getCreatedDate());
        postResponse.setImagePath(post.getImagePath());

        UserSummary userSummary = new UserSummary(author.getId(), author.getUsername(), author.getName(), author.getImagePath());
        postResponse.setCreatedBy(userSummary);

        return postResponse;
    }

    public static UserSummary mapUsersToUserSummaries(User user) {
        UserSummary userSummary = new UserSummary();

        userSummary.setId(user.getId());
        userSummary.setName(user.getName());
        userSummary.setImagePath(user.getImagePath());
        userSummary.setUsername(user.getUsername());

        return userSummary;
    }
}
