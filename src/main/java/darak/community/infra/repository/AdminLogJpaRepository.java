package darak.community.infra.repository;

import darak.community.domain.log.AdminLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminLogJpaRepository extends JpaRepository<AdminLog, Long> {
}
