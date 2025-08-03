package darak.community.infra.repository;

import darak.community.domain.heart.CommentHeart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentHeartJpaRepository extends JpaRepository<CommentHeart, Long> {

}
