package darak.community.domain.board;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BoardRepository {
    void save(Board board);

    Optional<Board> findById(Long id);

    List<Board> findAll();

    List<Board> findByBoardCategoryId(Long boardCategoryId);

    Page<Board> findByBoardCategoryIdPaged(Long boardCategoryId, Pageable pageable);
    
    long count();

    void delete(Board board);

    Page<Board> findAllPaged(Pageable pageable);
}
