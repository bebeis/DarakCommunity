package darak.community.domain.board;

import java.util.List;
import java.util.Optional;

public interface BoardCategoryRepository {

    void save(BoardCategory boardCategory);

    List<BoardCategory> findAll();

    Optional<BoardCategory> findById(Long id);

    Optional<BoardCategory> findByName(String name);

    long count();

    void delete(BoardCategory boardCategory);
}
