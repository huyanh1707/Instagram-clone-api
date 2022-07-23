package com.ju17th.instagramcloneapi.payload.post;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class PostRequest {
    @NotBlank
    @Size(max = 100)
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
