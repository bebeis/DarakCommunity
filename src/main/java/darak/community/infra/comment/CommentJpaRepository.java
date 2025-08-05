package darak.community.infra.comment;

import darak.community.domain.comment.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentJpaRepository extends JpaRepository<Comment, Long> {

}
