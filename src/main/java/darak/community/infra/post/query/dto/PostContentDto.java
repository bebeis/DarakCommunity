package darak.community.infra.post.query.dto;

import darak.community.domain.member.MemberGrade;
import darak.community.domain.post.PostType;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostContentDto {

    private Long postId;
    private String title;
    private String content;
    private Boolean anonymous;
    private PostType postType;

    private Long authorId;
    private String authorName;
    private MemberGrade authorGrade;

    private Long readCount;
    private LocalDateTime createdDate;
    private int commentCount;
    private Boolean myHeart;
    private int heartCount;

}
