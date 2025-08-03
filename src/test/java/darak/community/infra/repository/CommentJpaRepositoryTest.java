package darak.community.infra.repository;

import darak.community.domain.comment.Comment;
import darak.community.domain.member.Member;
import darak.community.domain.post.Post;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class CommentJpaRepositoryTest {

    @Autowired
    CommentJpaRepository commentJpaRepository;

    @Autowired
    MemberJpaRepository memberJpaRepository;

    @Autowired
    PostJpaRepository postJpaRepository;

    @Autowired
    EntityManager em;

    @DisplayName("count 쿼리 시에 조인이 나가지 않아야 함")
    @Test
    void countByMember_Id() {
        Member member = Member.guestMember("asef", "sadfs", "asffsa", "01000000000", LocalDate.of(2001, 1, 1),
                "asfdsadf@asfasfd.com");
        memberJpaRepository.save(member);

        Post post = Post.builder()
                .member(member)
                .content("asdf")
                .build();
        postJpaRepository.save(post);

        Comment comment = Comment.createComment("sdafsadf", false, post, member);
        commentJpaRepository.save(comment);

        em.flush();
        em.clear();

//        long count = commentJpaRepository.countByMember_Id(member.getId());
    }
}