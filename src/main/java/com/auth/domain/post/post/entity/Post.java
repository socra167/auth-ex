package com.auth.domain.post.post.entity;

import java.util.List;

import com.auth.domain.member.member.entity.Member;
import com.auth.domain.post.comment.entity.Comment;
import com.auth.global.entity.BaseTime;

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

    @OneToMany(mappedBy = "post_id", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<Comment> comments;
}
