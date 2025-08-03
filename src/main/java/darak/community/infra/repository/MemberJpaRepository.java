package darak.community.infra.repository;

import darak.community.domain.member.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberJpaRepository extends JpaRepository<Member, Long> {
    boolean existsByLoginId(String loginId);

    Optional<Member> findByLoginId(String loginId);
}
