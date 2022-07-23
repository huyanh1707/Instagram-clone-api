package com.ju17th.instagramcloneapi.payload.post.response.like;

import com.ju17th.instagramcloneapi.payload.user.UserSummary;

public class LikeResponse {
    private Long id;
    private UserSummary createdBy;
    private boolean isObserved;

    public boolean isObserved() {
        return isObserved;
    }

    public void setObserved(boolean observed) {
        isObserved = observed;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserSummary getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UserSummary createdBy) {
        this.createdBy = createdBy;
    }
}
