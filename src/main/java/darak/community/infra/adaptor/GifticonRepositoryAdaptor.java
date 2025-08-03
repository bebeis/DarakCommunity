package darak.community.infra.adaptor;

import darak.community.domain.gifticon.Gifticon;
import darak.community.domain.gifticon.GifticonRepository;
import darak.community.domain.gifticon.GifticonStatus;
import darak.community.infra.repository.GifticonJpaRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class GifticonRepositoryAdaptor implements GifticonRepository {

    private final GifticonJpaRepository repository;

    @Override
    public List<Gifticon> findActiveGifticons(GifticonStatus status, LocalDateTime now) {
        return repository.findActiveGifticons(status, now);
    }

    @Override
    public Page<Gifticon> findAllByOrderByCreatedDateDesc(Pageable pageable) {
        return repository.findAllByOrderByCreatedDateDesc(pageable);
    }

    @Override
    public Optional<Gifticon> findByIdWithLock(Long id) {
        return repository.findByIdWithLock(id);
    }
}
