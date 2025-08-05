package darak.community.infra.jpa;

import darak.community.domain.post.Attachment;
import darak.community.domain.post.Post;
import darak.community.infra.post.AttachmentJpaRepository;
import darak.community.infra.post.PostJpaRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class AttachmentJpaRepositoryTest {

    @Autowired
    private PostJpaRepository postJpaRepository;

    @Autowired
    private AttachmentJpaRepository attachmentJpaRepository;

    @Autowired
    private EntityManager em;

    @DisplayName("쿼리 메서드 외래 키 조회 시 조인 여부 확인")
    @Test
    void findByPostId() {
        Post post = new Post();
        postJpaRepository.save(post);

        Attachment attachment = Attachment.builder()
                .post(post)
                .build();
        attachmentJpaRepository.save(attachment);

        em.flush();
        em.clear();

    }

}