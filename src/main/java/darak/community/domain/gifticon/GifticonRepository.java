package darak.community.domain.gifticon;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GifticonRepository {

    List<Gifticon> findActiveGifticons(GifticonStatus status, LocalDateTime now);

    Page<Gifticon> findAllByOrderByCreatedDateDesc(Pageable pageable);

    Optional<Gifticon> findByIdWithLock(Long id);
}
