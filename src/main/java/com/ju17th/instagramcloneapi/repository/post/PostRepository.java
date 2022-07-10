package com.ju17th.instagramcloneapi.repository.post;

import com.ju17th.instagramcloneapi.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Optional<Post> findById(Long postId);

    @Query("SELECT p from Post p where p.createdBy in :followingUsersIds")
    Page<Post> findAllPostsByFollowedUsers(@Param("followingUsersIds") List<Long> followingUsersIds, Pageable pageable);
}
