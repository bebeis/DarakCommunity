package darak.community.domain.post;

import java.util.List;
import java.util.Optional;

public interface AttachmentRepository {
    void save(Attachment attachment);

    void delete(Attachment attachment);

    List<Attachment> findByPostId(Long postId);

    Optional<Attachment> findById(Long attachmentId);
}
