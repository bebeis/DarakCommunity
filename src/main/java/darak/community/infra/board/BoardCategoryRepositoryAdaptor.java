package darak.community.infra.board;

import darak.community.domain.board.BoardCategory;
import darak.community.domain.board.BoardCategoryRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BoardCategoryRepositoryAdaptor implements BoardCategoryRepository {

    private final BoardCategoryJpaRepository repository;

    @Override
    public void save(BoardCategory boardCategory) {
        repository.save(boardCategory);
    }

    @Override
    public List<BoardCategory> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<BoardCategory> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Optional<BoardCategory> findByName(String name) {
        return repository.findFirstByName(name);
    }

    @Override
    public long count() {
        return repository.count();
    }

    @Override
    public void delete(BoardCategory boardCategory) {
        repository.delete(boardCategory);
    }
}
