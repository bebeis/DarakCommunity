package darak.community.domain.member;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberRepository {
    Member save(Member member);

    boolean existsByLoginId(String loginId);

    Optional<Member> findById(Long id);

    Optional<Member> findByLoginId(String loginId);

    List<Member> findAll();

    long count();

    Page<Member> findAllPaged(Pageable pageable);

    Page<Member> searchMembers(String keyword, MemberGrade grade, Pageable pageable);
}
