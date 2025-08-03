package darak.community.infra.repository;

import darak.community.domain.board.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardJpaRepository extends JpaRepository<Board, Long> {

    Page<Board> findByBoardCategory_IdOrderById(Long boardCategoryId, Pageable pageable);

    Page<Board> findAllBy(Pageable pageable);
}
