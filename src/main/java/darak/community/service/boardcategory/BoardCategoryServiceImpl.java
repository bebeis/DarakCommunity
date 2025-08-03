package darak.community.service.boardcategory;

import darak.community.core.auth.ServiceAuth;
import darak.community.domain.board.Board;
import darak.community.domain.board.BoardCategory;
import darak.community.domain.member.MemberGrade;
import darak.community.infra.adaptor.BoardCategoryRepositoryAdaptor;
import darak.community.infra.adaptor.BoardRepositoryAdaptor;
import darak.community.service.boardcategory.request.BoardCategoryCreateServiceRequest;
import darak.community.service.boardcategory.request.BoardCategoryUpdateServiceRequest;
import darak.community.service.boardcategory.response.BoardCategoryResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardCategoryServiceImpl implements BoardCategoryService {

    private final BoardCategoryRepositoryAdaptor boardCategoryRepository;
    private final BoardRepositoryAdaptor boardRepository;
    private final List<BoardCategory> sortedBoardCategories = new ArrayList<>();

    @Override
    @Transactional
    @ServiceAuth(MemberGrade.ADMIN)
    public void createBoardCategory(BoardCategoryCreateServiceRequest request) {
        boardCategoryRepository.save(request.toEntity());
        refreshCache();
    }

    @Transactional
    @Override
    @ServiceAuth(MemberGrade.ADMIN)
    public void updateBoardCategory(BoardCategoryUpdateServiceRequest request) {
        BoardCategory boardCategory = findBoardCategoryBy(request.getBoardCategoryId());
        boardCategory.updateCategory(request.getName(), request.getPriority());
        refreshCache();
    }

    @Transactional
    @Override
    @ServiceAuth(MemberGrade.ADMIN)
    public void deleteCategory(Long id) {
        BoardCategory boardCategory = findBoardCategoryBy(id);

        List<Board> boards = boardRepository.findByBoardCategoryId(id);
        if (!boards.isEmpty()) {
            throw new IllegalArgumentException("해당 카테고리에 속한 게시판이 있어 삭제할 수 없습니다.");
        }

        boardCategoryRepository.delete(boardCategory);
        refreshCache();
    }

    @Override
    public List<BoardCategoryResponse> findAll() {
        return sortedBoardCategories.stream()
                .map(category -> {
                    int boardCount = boardRepository.findByBoardCategoryId(category.getId()).size();
                    return BoardCategoryResponse.of(category, boardCount);
                })
                .toList();
    }

    @Override
    public BoardCategoryResponse findById(Long boardCategoryId) {
        BoardCategory boardCategory = findBoardCategoryBy(boardCategoryId);
        int boardCount = boardRepository.findByBoardCategoryId(boardCategoryId).size();
        return BoardCategoryResponse.of(boardCategory, boardCount);
    }

    @Override
    public BoardCategoryResponse findByName(String name) {
        BoardCategory boardCategory = boardCategoryRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다."));
        int boardCount = boardRepository.findByBoardCategoryId(boardCategory.getId()).size();
        return BoardCategoryResponse.of(boardCategory, boardCount);
    }

    @Override
    public long getTotalCategoryCount() {
        return boardCategoryRepository.count();
    }

    @Override
    public long getFirstBoardIdByCategoryId(Long categoryId) {
        List<Board> boards = boardRepository.findByBoardCategoryId(categoryId);
        Board board = boards.stream().sorted()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 카테고리에 속하는 게시판이 존재하지 않습니다."));

        return board.getId();
    }

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        refreshCache();
        log.info("게시판 카테고리 캐시 초기화 완료. 총 {}개 카테고리", sortedBoardCategories.size());
    }

    private void refreshCache() {
        sortedBoardCategories.clear();
        sortedBoardCategories.addAll(boardCategoryRepository.findAll());
        Collections.sort(sortedBoardCategories);
    }

    private BoardCategory findBoardCategoryBy(Long boardCategoryId) {
        return boardCategoryRepository.findById(boardCategoryId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다."));
    }
}
