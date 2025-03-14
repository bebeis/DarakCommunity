package darak.community.domain;

import darak.community.domain.heart.PostHeart;
import darak.community.domain.member.Member;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Entity
@Getter
public class Post extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;

    private String title;

    private String content;

    private Long readCount;

    private Boolean anonymous;

    @Enumerated(EnumType.STRING)
    private ContentType contentType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    // 좋아요 양방향 관계 설정
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostHeart> hearts = new ArrayList<>();

    // 댓글 양방향 관계 설정 (필요한 경우)
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    public synchronized void increaseReadCount() {
        readCount++;
    }

    public int getLikeCount() {
        return hearts.size();
    }

    public int getCommentCount() {
        return comments.size();
    }

    // 조회수 반환 메서드 (이미 readCount가 있지만 템플릿과 일치시키기 위해 만듬)
    public int getViewCount() {
        return readCount != null ? readCount.intValue() : 0;
    }
}
