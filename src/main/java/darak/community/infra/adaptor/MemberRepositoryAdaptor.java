package darak.community.infra.adaptor;

import darak.community.domain.member.Member;
import darak.community.domain.member.MemberGrade;
import darak.community.domain.member.MemberRepository;
import darak.community.infra.repository.MemberJpaRepository;
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
public class MemberRepositoryAdaptor implements MemberRepository {

    private final EntityManager em;
    private final MemberJpaRepository repository;

    @Override
    public Member save(Member member) {
        return repository.save(member);
    }

    @Override
    public boolean existsByLoginId(String loginId) {
        /*return em.createQuery("SELECT COUNT(m) FROM Member m WHERE m.loginId = :loginId", Long.class)
                .setParameter("loginId", loginId)
                .getSingleResult() > 0;*/
        return repository.existsByLoginId(loginId);
    }

    @Override
    public Optional<Member> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Optional<Member> findByLoginId(String loginId) {
        return repository.findByLoginId(loginId);
    }

    @Override
    public List<Member> findAll() {
        return repository.findAll();
    }

    @Override
    public long count() {
        return repository.count();
    }

    @Override
    public Page<Member> findAllPaged(Pageable pageable) {
        /*List<Member> members = em.createQuery("SELECT m FROM Member m", Member.class)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        long total = count();
        return new PageImpl<>(members, pageable, total);*/
        return repository.findAll(pageable);
    }

    // TODO: queryDSL 동적 쿼리 작성
    @Override
    public Page<Member> searchMembers(String keyword, MemberGrade grade, Pageable pageable) {
        StringBuilder jpql = new StringBuilder("SELECT m FROM Member m WHERE 1=1");

        if (keyword != null && !keyword.trim().isEmpty()) {
            jpql.append(" AND (m.name LIKE :keyword OR m.loginId LIKE :keyword OR m.email LIKE :keyword)");
        }

        if (grade != null) {
            jpql.append(" AND m.memberGrade = :grade");
        }

        jpql.append(" ORDER BY m.createdDate DESC");

        var query = em.createQuery(jpql.toString(), Member.class)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize());

        if (keyword != null && !keyword.trim().isEmpty()) {
            query.setParameter("keyword", "%" + keyword + "%");
        }

        if (grade != null) {
            query.setParameter("grade", grade);
        }

        List<Member> members = query.getResultList();

        StringBuilder countJpql = new StringBuilder("SELECT COUNT(m) FROM Member m WHERE 1=1");

        if (keyword != null && !keyword.trim().isEmpty()) {
            countJpql.append(" AND (m.name LIKE :keyword OR m.loginId LIKE :keyword OR m.email LIKE :keyword)");
        }

        if (grade != null) {
            countJpql.append(" AND m.memberGrade = :grade");
        }

        var countQuery = em.createQuery(countJpql.toString(), Long.class);

        if (keyword != null && !keyword.trim().isEmpty()) {
            countQuery.setParameter("keyword", "%" + keyword + "%");
        }

        if (grade != null) {
            countQuery.setParameter("grade", grade);
        }

        Long count = countQuery.getSingleResult();

        return new PageImpl<>(members, pageable, count);
    }
}
