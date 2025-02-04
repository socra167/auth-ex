package com.auth.domain.post.post.entity;

import java.util.ArrayList;
import java.util.List;

import com.auth.domain.member.member.entity.Member;
import com.auth.domain.post.comment.entity.Comment;
import com.auth.global.entity.BaseTime;
import com.auth.global.exception.ServiceException;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "posts")
public class Post extends BaseTime {

    @ManyToOne(fetch = FetchType.LAZY)
    private Member author;

    private String title;

    private String content;

    @OneToMany(mappedBy = "post", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();

    public Comment addComment(Member author, String content) {
        Comment comment = Comment.builder()
            .post(this)
            .author(author)
            .content(content)
            .build();
        comments.add(comment); // CascadeType.PERSIST에 의해 List에 추가하면 실제 Comment도 생성된다
        return comment;
    }

    public Comment getCommentById(long id) {
        return comments.stream()
            .filter(comment -> comment.getId() == id)
            .findFirst()
            .orElseThrow(() -> new ServiceException("404-2", "존재하지 않는 댓글입니다."));
    }

    public void deleteComment(Comment comment) {
        comments.remove(comment);
    }
}
