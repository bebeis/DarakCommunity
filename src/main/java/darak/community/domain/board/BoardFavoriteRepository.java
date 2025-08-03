package darak.community.domain.board;

import darak.community.domain.member.Member;

import java.util.List;
import java.util.Optional;

public interface BoardFavoriteRepository {
    void save(BoardFavorite boardFavorite);
    
    void delete(BoardFavorite boardFavorite);
    
    List<BoardFavorite> findByMemberIdFetch(Long memberId);
    
    Optional<BoardFavorite> findByMemberIdAndBoardId(Long memberId, Long boardId);
    
    int countByMemberId(Long memberId);
    
    boolean existsByMemberAndBoard(Member member, Board board);
} 