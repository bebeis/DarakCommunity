package darak.community.infra.log;

import darak.community.domain.log.AdminLog;
import darak.community.domain.log.AdminLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AdminLogRepositoryAdaptor implements AdminLogRepository {

    private final AdminLogJpaRepository repository;

    @Override
    public void save(AdminLog log) {
        repository.save(log);
    }
}
