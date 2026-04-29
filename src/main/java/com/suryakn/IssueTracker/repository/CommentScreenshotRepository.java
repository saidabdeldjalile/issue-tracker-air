package com.suryakn.IssueTracker.repository;

import com.suryakn.IssueTracker.entity.CommentScreenshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentScreenshotRepository extends JpaRepository<CommentScreenshot, Long> {
    List<CommentScreenshot> findByCommentId(Long commentId);
}