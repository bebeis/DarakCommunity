package darak.community.infra.board;

import darak.community.domain.board.Board;
import darak.community.domain.board.BoardFavorite;
import darak.community.domain.member.Member;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardFavoriteJpaRepository extends JpaRepository<BoardFavorite, Long> {

    @EntityGraph(attributePaths = "board")
    List<BoardFavorite> findByMemberIdOrderByPriorityAsc(Long memberId);

    Optional<BoardFavorite> findByMemberIdAndBoardId(Long memberId, Long boardId);

    int countByMemberId(Long memberId);

    boolean existsByMemberAndBoard(Member member, Board board);
}
