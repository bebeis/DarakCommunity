package darak.community.infra.board;

import darak.community.domain.board.BoardCategory;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardCategoryJpaRepository extends JpaRepository<BoardCategory, Long> {

    Optional<BoardCategory> findFirstByName(String name);

    long count();
}
