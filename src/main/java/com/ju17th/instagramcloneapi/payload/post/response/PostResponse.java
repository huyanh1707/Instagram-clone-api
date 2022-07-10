package com.ju17th.instagramcloneapi.payload.post.response;

import com.ju17th.instagramcloneapi.payload.user.UserSummary;
import lombok.Data;

import java.time.Instant;

@Data
public class PostResponse {
    private Long id;
    private String description;
    private String imagePath;
    private UserSummary createdBy;
    private Instant creationDateTime;
}
