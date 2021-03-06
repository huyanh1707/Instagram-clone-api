package com.ju17th.instagramcloneapi.service.impl;

import com.ju17th.instagramcloneapi.model.notification.ObserveNotification;
import com.ju17th.instagramcloneapi.model.user.Follow;
import com.ju17th.instagramcloneapi.model.user.FollowRequest;
import com.ju17th.instagramcloneapi.model.user.User;
import com.ju17th.instagramcloneapi.payload.follow.FollowListResponse;
import com.ju17th.instagramcloneapi.payload.follow.FollowResponse;
import com.ju17th.instagramcloneapi.repository.UserRepository;
import com.ju17th.instagramcloneapi.repository.follow.FollowRepository;
import com.ju17th.instagramcloneapi.repository.follow.FollowRequestRepository;
import com.ju17th.instagramcloneapi.repository.notification.ObserveNotificationRepository;
import com.ju17th.instagramcloneapi.security.UserPrincipal;
import com.ju17th.instagramcloneapi.service.FollowService;
import com.ju17th.instagramcloneapi.util.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FollowServiceImpl implements FollowService {
    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObserveNotificationRepository observeNotificationRepository;

    @Autowired
    private FollowRequestRepository followRequestRepository;

    public FollowResponse followUser(Long userId, UserPrincipal currentUser) {
        User follower = userRepository.getOne(currentUser.getId());
        User following = userRepository.getOne(userId);
        Optional<Follow> follow = followRepository.findFollowByFollowerIdAndFollowingId(follower.getId(), following.getId());
        if (follow.isPresent()) {
            followRepository.deleteById(follow.get().getId());

            Optional<ObserveNotification> observeNotification = observeNotificationRepository.findByNotificationCreatorIdAndNotificatorReceiverId(currentUser.getId(), userId);
            if (observeNotification.isPresent())
                observeNotificationRepository.delete(observeNotification.get());

            return new FollowResponse(false);
        }

        Optional<FollowRequest> followRequestObj = followRequestRepository.findByFollowerIdAndFollowingId(currentUser.getId(), userId);
        if (followRequestObj.isPresent()) {
            followRequestRepository.deleteById(followRequestObj.get().getId());
            return new FollowResponse(false);
        }

        if (following.isPrivate()) {
            FollowRequest followRequest = new FollowRequest();

            followRequest.setFollower(follower);
            followRequest.setFollowing(following);

            followRequestRepository.save(followRequest);

            return new FollowResponse(false);
        }


        Follow followObject = new Follow();
        followObject.setFollower(follower);
        followObject.setFollowing(following);
        followRepository.save(followObject);

        ObserveNotification observeNotification = new ObserveNotification();
        observeNotification.setNotificationCreator(follower);
        observeNotification.setNotificationReceiver(following);
        observeNotificationRepository.save(observeNotification);

        return new FollowResponse(true);
    }

    public FollowResponse isFollowing(Long userId, UserPrincipal currentUser) {
        User follower = userRepository.getOne(currentUser.getId());
        User following = userRepository.getOne(userId);
        Optional<Follow> follow = followRepository.findFollowByFollowerIdAndFollowingId(follower.getId(), following.getId());
        if (follow.isPresent()) {
            return new FollowResponse(true);
        }
        return new FollowResponse(false);
    }

    public FollowListResponse getUserFollowers(Long userId) {
        User user = userRepository.getOne(userId);

        List<Follow> followerList = followRepository.findAllByFollowingId(user.getId());
        List<User> userList = new ArrayList<>();
        for (Follow follow : followerList) {
            userList.add(userRepository.getOne(follow.getFollower().getId()));
        }
        return ModelMapper.mapUserListToUsersSummaries(userList);
    }

    public FollowResponse isUserFollowedByCurrentUser(UserPrincipal currentUser, Long userId) {

        return new FollowResponse(false);
    }

    public FollowListResponse getUserFollowing(Long userId) {
        User user = userRepository.getOne(userId);

        List<Follow> followingList = followRepository.findAllByFollowerId(user.getId());
        List<User> userList = new ArrayList<>();
        for (Follow follow : followingList) {
            userList.add(userRepository.getOne(follow.getFollowing().getId()));
        }
        return ModelMapper.mapUserListToUsersSummaries(userList);
    }

    public FollowResponse acceptFollow(Long followRequestId) {
        FollowRequest followRequest = followRequestRepository.getOne(followRequestId);
        System.out.println(followRequest);
        if (followRequest != null) {
            Follow followObject = new Follow();
            followObject.setFollower(followRequest.getFollower());
            followObject.setFollowing(followRequest.getFollowing());
            followRepository.save(followObject);


            ObserveNotification observeNotification = new ObserveNotification();
            observeNotification.setNotificationCreator(followRequest.getFollower());
            observeNotification.setNotificationReceiver(followRequest.getFollowing());
            observeNotificationRepository.save(observeNotification);

            followRequestRepository.delete(followRequest);

            return new FollowResponse(true);
        }

        return new FollowResponse(false);
    }

    public FollowResponse declineFollow(Long followRequestId) {
        followRequestRepository.deleteById(followRequestId);
        return new FollowResponse(false);
    }
}
