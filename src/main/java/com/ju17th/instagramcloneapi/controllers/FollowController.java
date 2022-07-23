package com.ju17th.instagramcloneapi.controllers;

import com.ju17th.instagramcloneapi.payload.follow.FollowListResponse;
import com.ju17th.instagramcloneapi.payload.follow.FollowResponse;
import com.ju17th.instagramcloneapi.security.CurrentUser;
import com.ju17th.instagramcloneapi.security.UserPrincipal;
import com.ju17th.instagramcloneapi.service.FollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/follow")
public class FollowController {
    @Autowired
    private FollowService followService;

    @PostMapping("/{userId}")
    public FollowResponse followUser(@PathVariable(value = "userId") Long userId,
                                     @CurrentUser UserPrincipal currentUser) {
        return followService.followUser(userId, currentUser);
    }

    @GetMapping("/{userId}")
    public FollowResponse isUserFollowingAnotherUser(@PathVariable(value = "userId") Long userId,
                                                     @CurrentUser UserPrincipal currentUser) {
        return followService.isFollowing(userId, currentUser);
    }

    @GetMapping("/{userId}/followers")
    public FollowListResponse getUserFollowers(@PathVariable(value = "userId") Long userId) {
        return followService.getUserFollowers(userId);
    }

    @GetMapping("/{userId}/following")
    public FollowListResponse getUserFollowing(@PathVariable(value = "userId") Long userId) {
        return followService.getUserFollowing(userId);
    }

    @GetMapping("/{userId}/follow")
    public FollowResponse isUserFollowedByCurrentUser(@CurrentUser UserPrincipal currentUser,
                                                      @PathVariable(value = "userId") Long userId) {
        return followService.isUserFollowedByCurrentUser(currentUser, userId);
    }

    @PostMapping("/{followId}/accept")
    public FollowResponse acceptFollow(@PathVariable(value = "followId") Long followRequestId) {
        return followService.acceptFollow(followRequestId);
    }

    @PostMapping("/{followId}/decline")
    public FollowResponse declineFollow(@PathVariable(value = "followId") Long followRequestId) {
        return followService.declineFollow(followRequestId);
    }
}
