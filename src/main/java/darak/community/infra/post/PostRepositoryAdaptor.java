package darak.community.infra.post;

import darak.community.domain.post.Attachment;
import darak.community.domain.post.Post;
import darak.community.domain.post.PostRepository;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PostRepositoryAdaptor implements PostRepository {

    private final EntityManager em;
    private final PostJpaRepository repository;

    @Override
    public void save(Post post) {
        repository.save(post);
    }

    @Override
    public Optional<Post> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public void delete(Post post) {
        repository.delete(post);
    }

    @Override
    public List<Attachment> findRecentGalleryImages(int limit) {
        return em.createQuery(
                        "select a from Attachment a " +
                                "join a.post p " +
                                "join p.board b " +
                                "where (lower(b.name) like '%갤러리%' or lower(b.name) like '%gallery%') " +
                                "and a.uploadFile.fileType like 'image/%' " +
                                "order by p.createdDate desc", Attachment.class)
                .setMaxResults(limit)
                .getResultList();
    }

    @Override
    public Page<Post> findByBoardIdPaged(Long boardId, Pageable pageable) {
        /*List<Post> posts = em.createQuery(
                        "select p from Post p where p.board.id = :boardId order by p.createdDate desc", Post.class)
                .setParameter("boardId", boardId)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        Long count = em.createQuery("select count(p) from Post p where p.board.id = :boardId", Long.class)
                .setParameter("boardId", boardId)
                .getSingleResult();

        return new PageImpl<>(posts, pageable, count);*/
        return repository.findByBoardIdPaged(boardId, pageable);
    }

    @Override
    public List<Post> findRecentGalleryPostsWithImages(int limit) {
        return em.createQuery(
                        "select distinct p from Post p " +
                                "join p.board b " +
                                "where exists (select 1 from Attachment a " +
                                "              where a.post = p " +
                                "              and a.uploadFile.fileType like 'image/%') " +
                                "and (lower(b.name) like '%갤러리%' or lower(b.name) like '%gallery%') " +
                                "order by p.createdDate desc", Post.class)
                .setMaxResults(limit)
                .getResultList();
    }

    @Override
    public long countByMemberId(Long memberId) {
        return em.createQuery("select count(p) from Post p where p.member.id = :memberId", Long.class)
                .setParameter("memberId", memberId)
                .getSingleResult();
    }

    @Override
    public long countLikesByMemberId(Long memberId) {
        return em.createQuery(
                        "select count(ph) from PostHeart ph " +
                                "join ph.post p " +
                                "where p.member.id = :memberId", Long.class)
                .setParameter("memberId", memberId)
                .getSingleResult();
    }

    @Override
    public long count() {
        return repository.count();
    }
}
