package darak.community.service.board;

import darak.community.domain.board.Board;
import darak.community.domain.board.BoardFavorite;
import darak.community.domain.board.BoardFavoriteRepository;
import darak.community.domain.board.BoardRepository;
import darak.community.domain.member.Member;
import darak.community.domain.member.MemberRepository;
import darak.community.service.board.response.BoardResponse;
import darak.community.service.board.response.FavoriteServiceResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BoardFavoriteServiceImpl implements BoardFavoriteService {

    private final BoardFavoriteRepository boardFavoriteRepository;
    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;

    @Override
    @Transactional
    public void addFavorite(Long memberId, Long boardId) {
        Member member = findMemberBy(memberId);
        Board board = findBoardBy(boardId);

        validateFavoriteNotExists(member, board);

        BoardFavorite boardFavorite = BoardFavorite.of(member, board);
        boardFavorite.setPriority(boardFavoriteRepository.countByMemberId(member.getId()) + 1);

        boardFavoriteRepository.save(boardFavorite);
    }

    @Override
    @Transactional
    public void removeFavorite(Long memberId, Long boardId) {
        BoardFavorite boardFavorite = findBoardFavoriteBy(memberId, boardId);
        boardFavoriteRepository.delete(boardFavorite);
    }

    @Override
    public FavoriteServiceResponse isFavorite(Long memberId, Long boardId) {
        boolean present = boardFavoriteRepository.findByMemberIdAndBoardId(memberId, boardId).isPresent();
        return new FavoriteServiceResponse(present);
    }

    @Override
    public List<BoardResponse> findFavoriteBoardsBy(Long memberId) {
        List<BoardFavorite> boardFavorites = boardFavoriteRepository.findByMemberIdFetch(memberId);
        return boardFavorites.stream()
                .map(BoardFavorite::getBoard)
                .map(BoardResponse::of)
                .toList();
    }

    private void validateFavoriteNotExists(Member member, Board board) {
        if (boardFavoriteRepository.existsByMemberAndBoard(member, board)) {
            throw new IllegalStateException("이미 즐겨찾기에 추가된 게시판입니다.");
        }
    }

    private Board findBoardBy(Long boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시판입니다"));
    }

    private Member findMemberBy(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
    }

    private BoardFavorite findBoardFavoriteBy(Long memberId, Long boardId) {
        return boardFavoriteRepository.findByMemberIdAndBoardId(memberId, boardId)
                .orElseThrow(() -> new IllegalArgumentException("즐겨찾기에 추가되지 않은 게시판입니다."));
    }
}