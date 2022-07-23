package com.ju17th.instagramcloneapi.repository.follow;

import com.ju17th.instagramcloneapi.model.user.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
    Optional<Follow> findFollowByFollowerIdAndFollowingId(Long followerId, Long followingId);

    List<Follow> findAllByFollowingId(Long followingId);

    List<Follow> findAllByFollowerId(Long followerId);

    int countByFollowerId(Long followerId);

    int countByFollowingId(Long followingId);
}
