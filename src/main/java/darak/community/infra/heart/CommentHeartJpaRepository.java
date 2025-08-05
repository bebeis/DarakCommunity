package darak.community.infra.heart;

import darak.community.domain.heart.CommentHeart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentHeartJpaRepository extends JpaRepository<CommentHeart, Long> {

}
