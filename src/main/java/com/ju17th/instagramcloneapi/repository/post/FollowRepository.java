package com.ju17th.instagramcloneapi.repository.post;

import com.ju17th.instagramcloneapi.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
    List<Follow> findAllByFollowerId(Long followerId);
}
