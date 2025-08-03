package darak.community.infra.adaptor;

import darak.community.domain.post.Attachment;
import darak.community.domain.post.AttachmentRepository;
import darak.community.infra.repository.AttachmentJpaRepository;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AttachmentRepositoryAdaptor implements AttachmentRepository {

    private final AttachmentJpaRepository repository;
    private final EntityManager em;

    @Override
    public void save(Attachment attachment) {
        repository.save(attachment);
    }

    @Override
    public void delete(Attachment attachment) {
        repository.delete(attachment);
    }

    // 스프링 Data JPA 쿼리 최적화 안되서 직접 작성함. TODO: Querydsl 최적화
    @Override
    public List<Attachment> findByPostId(Long postId) {
        return em.createQuery("select a from Attachment a where a.post.id = :postId", Attachment.class)
                .setParameter("postId", postId)
                .getResultList();
    }

    @Override
    public Optional<Attachment> findById(Long attachmentId) {
        return repository.findById(attachmentId);
    }
}
