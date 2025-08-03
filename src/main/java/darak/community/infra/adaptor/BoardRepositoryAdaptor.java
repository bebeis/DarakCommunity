package darak.community.infra.adaptor;

import darak.community.domain.board.Board;
import darak.community.domain.board.BoardRepository;
import darak.community.infra.repository.BoardJpaRepository;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BoardRepositoryAdaptor implements BoardRepository {

    private final EntityManager em;
    private final BoardJpaRepository repository;

    @Override
    public void save(Board board) {
        em.persist(board);
    }

    @Override
    public Optional<Board> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public List<Board> findAll() {
        return repository.findAll();
    }

    // 쿼리 메서드로 작성하기에 메서드 명이 너무 김. TODO: queryDSL
    @Override
    public List<Board> findByBoardCategoryId(Long boardCategoryId) {
        return em.createQuery(
                        "select b from Board b where b.boardCategory.id = :boardCategoryId order by b.priority asc, b.id asc",
                        Board.class)
                .setParameter("boardCategoryId", boardCategoryId)
                .getResultList();
    }

    // TODO: ID 우선순위 AND PRIORY 우선순위 메서드 분리 필요
    @Override
    public Page<Board> findByBoardCategoryIdPaged(Long boardCategoryId, Pageable pageable) {
        /*List<Board> boards = em.createQuery(
                        "select b from Board b where b.boardCategory.id = :boardCategoryId order by b.id asc",
                        Board.class)
                .setParameter("boardCategoryId", boardCategoryId)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        Long count = em.createQuery("select count(b) from Board b where b.boardCategory.id = :boardCategoryId",
                        Long.class)
                .setParameter("boardCategoryId", boardCategoryId)
                .getSingleResult();

        return new PageImpl<>(boards, pageable, count);*/
        return repository.findByBoardCategory_IdOrderById(boardCategoryId, pageable);
    }

    @Override
    public long count() {
        return repository.count();
    }

    @Override
    public void delete(Board board) {
        repository.delete(board);
    }

    @Override
    public Page<Board> findAllPaged(Pageable pageable) {
        return repository.findAllBy(pageable);
    }
}
