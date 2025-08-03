package darak.community.web.controller.community.board;

import darak.community.infra.adaptor.dto.PostContentDto;
import darak.community.service.board.BoardService;
import darak.community.service.boardcategory.BoardCategoryService;
import darak.community.service.post.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@Slf4j
public class BoardController {

    private final BoardCategoryService boardCategoryService;
    private final PostService postService;
    private final BoardService boardService;


    @GetMapping("/community/categories/{categoryId}")
    public String redirectFirstBoard(@PathVariable Long categoryId) {
        return "redirect:/community/boards/" + boardCategoryService.getFirstBoardIdByCategoryId(categoryId);
    }

    @GetMapping("/community/boards/{boardId}")
    public String board(@PathVariable Long boardId,
                        @RequestParam(defaultValue = "1") int page,
                        @RequestParam(defaultValue = "10") int size,
                        Model model) {

        Page<PostContentDto> postPage = postService.findPostsByBoardId(boardId,
                PageRequest.of(page - 1, size, Sort.by("createdDate").descending()));

        model.addAttribute("currentBoard", boardService.findBoardInfoBy(boardId));
        model.addAttribute("posts", postPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", postPage.getTotalPages());
        model.addAttribute("startPage", Math.max(1, page - 2));
        model.addAttribute("endPage", Math.min(postPage.getTotalPages(), page + 2));

        return "community/board/board";
    }
}
