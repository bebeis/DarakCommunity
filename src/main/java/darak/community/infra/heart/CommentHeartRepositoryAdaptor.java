package darak.community.infra.heart;

import darak.community.domain.heart.CommentHeart;
import darak.community.domain.heart.CommentHeartRepository;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

// TODO : 외래 키 조회가 대부분이라, Spring Data Jpa로 fk 조회 최적화가 안 됨 -> queryDSL
@Repository
@RequiredArgsConstructor
public class CommentHeartRepositoryAdaptor implements CommentHeartRepository {

    private final EntityManager em;
    private final CommentHeartJpaRepository repository;

    @Override
    public void save(CommentHeart commentHeart) {
        repository.save(commentHeart);
    }

    @Override
    public void delete(CommentHeart commentHeart) {
        repository.delete(commentHeart);
    }

    @Override
    public List<CommentHeart> findByMemberId(Long memberId) {
        return em.createQuery("select ch from CommentHeart ch where ch.member.id = :memberId",
                        CommentHeart.class)
                .setParameter("memberId", memberId)
                .getResultList();
    }

    @Override
    public List<CommentHeart> findByMemberIdFetchComments(Long memberId) {
        return em.createQuery(
                        "select ch from CommentHeart ch join fetch ch.comment c where ch.member.id = :memberId",
                        CommentHeart.class)
                .setParameter("memberId", memberId)
                .getResultList();
    }

    @Override
    public Optional<CommentHeart> findByCommentIdAndMemberId(Long commentId, Long memberId) {
        List<CommentHeart> result = em.createQuery(
                        "select ch from CommentHeart ch where ch.comment.id = :commentId and ch.member.id = :memberId",
                        CommentHeart.class)
                .setParameter("commentId", commentId)
                .setParameter("memberId", memberId)
                .getResultList();
        return result.stream().findAny();
    }

    @Override
    public int countByCommentId(Long commentId) {
        return em.createQuery("select count(ch) from CommentHeart ch where ch.comment.id = :commentId", Long.class)
                .setParameter("commentId", commentId)
                .getSingleResult()
                .intValue();
    }

}
