package darak.community.service.board;

import darak.community.infra.adaptor.dto.PostWithAllDto;
import darak.community.service.board.request.BoardCreateServiceRequest;
import darak.community.service.board.request.BoardUpdateServiceRequest;
import darak.community.service.board.response.BoardAdminResponse;
import darak.community.service.board.response.BoardResponse;
import darak.community.service.boardcategory.response.BoardCategoryResponse;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BoardService {

    void createBoard(BoardCreateServiceRequest request);

    void updateBoard(BoardUpdateServiceRequest request);

    void deleteBoard(Long boardId);

    BoardResponse findBoardInfoBy(Long boardId);

    List<BoardResponse> findBoardsBy(Long categoryId);

    Map<BoardCategoryResponse, List<BoardResponse>> findBoardsGroupedByCategory();

    Map<BoardResponse, List<PostWithAllDto>> findRecentPostsGroupedByBoardLimit(int limit);

    Page<BoardAdminResponse> getAllBoardsWithCategoryPaged(Pageable pageable);

    Page<BoardAdminResponse> getBoardsWithCategoryByCategoryPaged(Long categoryId, Pageable pageable);

    BoardAdminResponse findBoardAdminInfoBy(Long boardId);

    long getTotalBoardCount();

}
