package darak.community.infra.adaptor;

import darak.community.domain.heart.PostHeart;
import darak.community.domain.heart.PostHeartRepository;
import darak.community.infra.repository.PostHeartJpaRepository;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PostHeartRepositoryAdaptor implements PostHeartRepository {

    private final EntityManager em;
    private final PostHeartJpaRepository repository;

    @Override
    public void save(PostHeart postHeart) {
        repository.save(postHeart);
    }

    @Override
    public void delete(PostHeart postHeart) {
        repository.delete(postHeart);
    }

    // JPA 외래 키 조회 불필요 조인으로, Spring Data JPA 사용 X
    // TODO: queryDSL 작성
    @Override
    public int countByPostId(Long postId) {
        return em.createQuery("select count(ph) from PostHeart ph where ph.post.id = :postId", Long.class)
                .setParameter("postId", postId)
                .getSingleResult()
                .intValue();
    }

    // JPA 외래 키 조회 불필요 조인으로, Spring Data JPA 사용 X
    // TODO: queryDSL 작성
    @Override
    public List<PostHeart> findByMemberId(Long memberId) {
        return em.createQuery("select ph from PostHeart ph where ph.member.id = :memberId", PostHeart.class)
                .setParameter("memberId", memberId)
                .getResultList();
    }

    // JPA 외래 키 조회 불필요 조인으로, Spring Data JPA 사용 X
    // TODO: queryDSL 작성
    @Override
    public Optional<PostHeart> findByPostIdAndMemberId(Long postId, Long memberId) {
        List<PostHeart> result = em.createQuery(
                        "select ph from PostHeart ph where ph.post.id = :postId and ph.member.id = :memberId",
                        PostHeart.class)
                .setParameter("postId", postId)
                .setParameter("memberId", memberId)
                .getResultList();
        return result.stream().findAny();
    }

    // JPA 외래 키 조회 불필요 조인으로, Spring Data JPA 사용 X
    // TODO: queryDSL 작성
    @Override
    public Page<PostHeart> findByMemberIdFetchPost(Long memberId, Pageable pageable) {
        return em.createQuery("select ph from PostHeart ph join fetch ph.post p where ph.member.id = :memberId",
                        PostHeart.class)
                .setParameter("memberId", memberId)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList()
                .stream()
                .collect(Collectors.collectingAndThen(Collectors.toList(),
                        list -> new PageImpl<>(list, pageable, list.size())));
    }

    // JPA 외래 키 조회 불필요 조인으로, Spring Data JPA 사용 X
    // TODO: queryDSL 작성
    @Override
    public List<PostHeart> findByPostId(Long postId) {
        return em.createQuery("select ph from PostHeart ph where ph.post.id = :postId", PostHeart.class)
                .setParameter("postId", postId)
                .getResultList();
    }
}
