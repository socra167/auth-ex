package com.auth.domain.post.comment.entity;

import com.auth.domain.member.member.entity.Member;
import com.auth.domain.post.post.entity.Post;
import com.auth.global.entity.BaseTime;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "comments")
public class Comment extends BaseTime {

	@ManyToOne(fetch = FetchType.LAZY)
	private Member author;

	@ManyToOne(fetch = FetchType.LAZY) // ManyToOne은 기본 fetch 전략이 EAGER라서, LAZY로 지정해주면 좋다
	private Post post;

	private String content;
}
