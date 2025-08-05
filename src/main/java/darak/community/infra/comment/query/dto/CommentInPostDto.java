package darak.community.infra.comment.query.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommentInPostDto {

    private Long commentId; // 댓글 ID
    private Long authorId; // 댓글 작성자 ID임!
    private Boolean anonymous; // 댓글 작성자가 익명인지
    private String authorName; // 댓글 작성자 이름
    private String content; // 댓글 내용
    private Boolean isReply; // 댓글이 답글인지 여부

    private LocalDateTime createdDate;
    private Boolean myHeart; // 내가 댓글에 좋아요 눌렀는지
    private int heartCount; // 댓글 좋아요 개수
}
