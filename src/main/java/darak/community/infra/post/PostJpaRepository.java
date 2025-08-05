package darak.community.infra.post;

import darak.community.domain.post.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostJpaRepository extends JpaRepository<Post, Long> {

    @Query("select p from Post p where p.board.id = :boardId order by p.createdDate desc")
    Page<Post> findByBoardIdPaged(@Param("boardId") Long boardId, Pageable pageable);
}
