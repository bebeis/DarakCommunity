package darak.community.infra.adaptor;

import darak.community.domain.board.Board;
import darak.community.domain.board.BoardFavorite;
import darak.community.domain.board.BoardFavoriteRepository;
import darak.community.domain.member.Member;
import darak.community.infra.repository.BoardFavoriteJpaRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BoardFavoriteRepositoryAdaptor implements BoardFavoriteRepository {

    //    private final EntityManager em;
    private final BoardFavoriteJpaRepository repository;

    @Override
    public void save(BoardFavorite boardFavorite) {
        repository.save(boardFavorite);
    }

    @Override
    public void delete(BoardFavorite boardFavorite) {
        repository.delete(boardFavorite);
    }

    @Override
    public List<BoardFavorite> findByMemberIdFetch(Long memberId) {
        /*return em.createQuery(
                        "select bf from BoardFavorite bf " +
                                "join fetch bf.board " +
                                "where bf.member.id = :memberId " +
                                "order by bf.priority asc", BoardFavorite.class)
                .setParameter("memberId", memberId)
                .getResultList();*/
        return repository.findByMemberIdOrderByPriorityAsc(memberId);
    }

    @Override
    public Optional<BoardFavorite> findByMemberIdAndBoardId(Long memberId, Long boardId) {
        /*List<BoardFavorite> result = em.createQuery(
                        "select bf from BoardFavorite bf " +
                                "where bf.member.id = :memberId " +
                                "and bf.board.id = :boardId", BoardFavorite.class)
                .setParameter("memberId", memberId)
                .setParameter("boardId", boardId)
                .getResultList();
        return result.stream().findAny();*/
        return repository.findByMemberIdAndBoardId(memberId, boardId);
    }

    @Override
    public int countByMemberId(Long memberId) {
       /* return em.createQuery(
                        "select count(bf) from BoardFavorite bf " +
                                "where bf.member.id = :memberId", Long.class)
                .setParameter("memberId", memberId)
                .getSingleResult()
                .intValue();*/
        return repository.countByMemberId(memberId);
    }

    @Override
    public boolean existsByMemberAndBoard(Member member, Board board) {
        /*Long count = em.createQuery("select count(bf) from BoardFavorite bf "
                        + "where bf.member = :member "
                        + "and bf.board = :board", Long.class)
                .setParameter("member", member)
                .setParameter("board", board)
                .getSingleResult();
        return count > 0;*/
        return repository.existsByMemberAndBoard(member, board);
    }
}