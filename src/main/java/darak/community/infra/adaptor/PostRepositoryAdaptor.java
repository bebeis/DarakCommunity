package darak.community.infra.adaptor;

import darak.community.domain.post.Attachment;
import darak.community.domain.post.Post;
import darak.community.domain.post.PostRepository;
import darak.community.infra.adaptor.dto.PostContentDto;
import darak.community.infra.adaptor.dto.PostWithAllDto;
import darak.community.infra.repository.PostJpaRepository;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
    public Page<PostWithAllDto> findPostsWithMetaByBoardId(Long boardId, Pageable pageable) {
        String jpql = """
                select new darak.community.infra.adaptor.dto.PostWithAllDto(
                            p.id, p.title, p.content, p.anonymous, p.postType,
                            m.id, m.name, m.memberGrade, b.id, b.name,
                            p.readCount, p.createdDate,
                            cast((select count(c) from Comment c
                             where c.post = p) as int),
                            case when
                                 (select count(ph2) from PostHeart ph2
                                  where ph2.post = p
                                    and ph2.member.id = :memberId) > 0
                                 then true
                                 else false
                             end,
                            cast((select count(ph) from PostHeart ph
                             where ph.post = p) as int)
                        )
                        from Post p
                        join p.member m
                        join p.board b
                        where b.id = :boardId
                        order by p.createdDate desc
                """;
        String countJpql = """
                select count(p)
                from Post p
                where p.board.id = :boardId
                """;

        List<PostWithAllDto> content = em.createQuery(jpql, PostWithAllDto.class)
                .setParameter("boardId", boardId)
                .setParameter("memberId",
                        pageable.getPageNumber())
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        Long total = em.createQuery(countJpql, Long.class)
                .setParameter("boardId", boardId)
                .getSingleResult();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<PostWithAllDto> findPostsWithMetaWrittenByMemberId(Long memberId, Pageable pageable) {
        String jpql = """
                select new darak.community.infra.adaptor.dto.PostWithAllDto(
                            p.id, p.title, p.content, p.anonymous, p.postType,
                            m.id, m.name, m.memberGrade, b.id, b.name,
                            p.readCount, p.createdDate,
                            cast((select count(c) from Comment c
                             where c.post = p) as int),
                            case when
                                 (select count(ph2) from PostHeart ph2
                                  where ph2.post = p
                                    and ph2.member.id = :memberId) > 0
                                 then true
                                 else false
                             end,
                            cast((select count(ph) from PostHeart ph
                             where ph.post = p) as int)
                        )
                        from Post p
                        join p.member m
                        join p.board b
                        where p.member.id = :memberId
                        order by p.createdDate desc
                """;
        String countJpql = """
                select count(p)
                from Post p
                where p.member.id = :memberId
                """;

        List<PostWithAllDto> content = em.createQuery(jpql, PostWithAllDto.class)
                .setParameter("memberId", memberId)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        Long total = em.createQuery(countJpql, Long.class)
                .setParameter("memberId", memberId)
                .getSingleResult();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<PostWithAllDto> findPostsWithMetaByMemberLiked(Long memberId, Pageable pageable) {
        String jpql = """
                select new darak.community.infra.adaptor.dto.PostWithAllDto(
                            p.id, p.title, p.content, p.anonymous, p.postType,
                            m.id, m.name, m.memberGrade, b.id, b.name,
                            p.readCount, p.createdDate,
                            cast((select count(c) from Comment c
                             where c.post = p) as int),
                            case when
                                 (select count(ph2) from PostHeart ph2
                                  where ph2.post = p
                                    and ph2.member.id = :memberId) > 0
                                 then true
                                 else false
                             end,
                            cast((select count(ph) from PostHeart ph
                             where ph.post = p) as int)
                        )
                        from Post p
                        join p.member m
                        join p.board b
                        join PostHeart ph on ph.post.id = p.id
                        where ph.member.id = :memberId
                        order by p.createdDate desc
                """;
        String countJpql = """
                select count(p)
                from Post p
                where p.member.id = :memberId
                """;

        List<PostWithAllDto> content = em.createQuery(jpql, PostWithAllDto.class)
                .setParameter("memberId", memberId)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        Long total = em.createQuery(countJpql, Long.class)
                .setParameter("memberId", memberId)
                .getSingleResult();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Optional<PostContentDto> findPostContentByMemberIdAndPostId(Long postId, Long memberId) {
        String jpql = """
                select new darak.community.infra.adaptor.dto.PostContentDto(
                            p.id, p.title, p.content, p.anonymous, p.postType,
                            m.id, m.name, m.memberGrade,
                            p.readCount, p.createdDate,
                            cast((select count(c) from Comment c where c.post = p) as int),
                            case when
                                 (select count(ph) from PostHeart ph where ph.post.id = p.id and ph.member.id = :memberId) > 0
                                 then true
                                 else false
                             end,
                            cast((select count(ph) from PostHeart ph where ph.post.id = p.id) as int)
                        )
                        from Post p
                        join p.member m
                        where p.id = :postId
                """;

        return Optional.ofNullable(em.createQuery(jpql, PostContentDto.class)
                .setParameter("postId", postId)
                .setParameter("memberId", memberId)
                .getSingleResult());
    }

    @Override
    public Page<PostContentDto> findPostsByBoardId(Long boardId, Pageable pageable) {
        String jpql = """
                select new darak.community.infra.adaptor.dto.PostContentDto(
                            p.id, p.title, p.content, p.anonymous, p.postType,
                            m.id, m.name, m.memberGrade,
                            p.readCount, p.createdDate,
                            cast((select count(c) from Comment c where c.post = p) as int),
                            false,
                            cast((select count(ph) from PostHeart ph where ph.post.id = p.id) as int)
                        )
                        from Post p
                        join p.member m
                        where p.board.id = :boardId
                        order by p.createdDate desc
                """;

        List<PostContentDto> content = em.createQuery(jpql, PostContentDto.class)
                .setParameter("boardId", boardId)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        Long total = em.createQuery("select count(p) from Post p where p.board.id = :boardId", Long.class)
                .setParameter("boardId", boardId)
                .getSingleResult();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public long count() {
        return repository.count();
    }
}
