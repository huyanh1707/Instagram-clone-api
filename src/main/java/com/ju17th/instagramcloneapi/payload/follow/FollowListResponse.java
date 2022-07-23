package com.ju17th.instagramcloneapi.payload.follow;

import com.ju17th.instagramcloneapi.payload.user.UserSummary;

import java.util.List;

public class FollowListResponse {
    private List<UserSummary> userSummaryList;

    public List<UserSummary> getUserSummaryList() {
        return userSummaryList;
    }

    public void setUserSummaryList(List<UserSummary> userSummaryList) {
        this.userSummaryList = userSummaryList;
    }
}
