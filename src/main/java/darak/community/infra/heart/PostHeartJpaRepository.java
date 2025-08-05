package darak.community.infra.heart;

import darak.community.domain.heart.PostHeart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostHeartJpaRepository extends JpaRepository<PostHeart, Long> {
}
