package darak.community.infra.post;

import darak.community.domain.post.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttachmentJpaRepository extends JpaRepository<Attachment, Long> {

}
