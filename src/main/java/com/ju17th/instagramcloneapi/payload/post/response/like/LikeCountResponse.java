package com.ju17th.instagramcloneapi.payload.post.response.like;

public class LikeCountResponse {
    private Long count;

    public LikeCountResponse(Long count) {
        this.count = count;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
