package darak.community.repository;

import darak.community.domain.BoardFavorite;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BoardFavoriteRepository {
    
    private final EntityManager em;
    
    public void save(BoardFavorite boardFavorite) {
        em.persist(boardFavorite);
    }
    
    public void delete(BoardFavorite boardFavorite) {
        em.remove(boardFavorite);
    }
    
    public List<BoardFavorite> findByMemberId(Long memberId) {
        return em.createQuery(
                "select bf from BoardFavorite bf " +
                "join fetch bf.board " +
                "where bf.member.id = :memberId " +
                "order by bf.priority asc", BoardFavorite.class)
                .setParameter("memberId", memberId)
                .getResultList();
    }
    
    public Optional<BoardFavorite> findByMemberIdAndBoardId(Long memberId, Long boardId) {
        List<BoardFavorite> result = em.createQuery(
                "select bf from BoardFavorite bf " +
                "where bf.member.id = :memberId " +
                "and bf.board.id = :boardId", BoardFavorite.class)
                .setParameter("memberId", memberId)
                .setParameter("boardId", boardId)
                .getResultList();
        return result.stream().findAny();
    }
    
    public int countByMemberId(Long memberId) {
        return em.createQuery(
                "select count(bf) from BoardFavorite bf " +
                "where bf.member.id = :memberId", Long.class)
                .setParameter("memberId", memberId)
                .getSingleResult()
                .intValue();
    }
} 