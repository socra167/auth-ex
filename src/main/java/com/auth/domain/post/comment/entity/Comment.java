package com.auth.domain.post.comment.entity;

import com.auth.domain.member.member.entity.Member;
import com.auth.domain.post.post.entity.Post;
import com.auth.global.entity.BaseTime;
import com.auth.global.exception.ServiceException;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Table(name = "comments")
public class Comment extends BaseTime {

	@ManyToOne(fetch = FetchType.LAZY)
	private Member author;

	@ManyToOne(fetch = FetchType.LAZY) // ManyToOne은 기본 fetch 전략이 EAGER라서, LAZY로 지정해주면 좋다
	private Post post;

	private String content;

	public void modify(String content) {
		this.content = content;
	}

	public void canModify(Member actor) {
		if (actor == null) {
			throw new ServiceException("401-1", "인증 정보가 없습니다.");
		}
		if (actor.isAdmin())
			return;
		if (actor.equals(this.author))
			return;
		throw new ServiceException("403-1", "자신이 작성한 글만 수정 가능합니다.");
	}

	public void canDelete(Member actor) {
		if (actor == null) {
			throw new ServiceException("401-1", "인증 정보가 없습니다.");
		}
		if (actor.isAdmin())
			return;
		if (actor.equals(this.author))
			return;
		throw new ServiceException("403-1", "자신이 작성한 글만 삭제 가능합니다.");
	}
}
