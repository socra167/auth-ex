package com.auth.domain.post.comment.dto;

import java.time.LocalDateTime;

import com.auth.domain.post.comment.entity.Comment;

import lombok.Getter;

@Getter
public class CommentDto {

	private String content;
	private long postId;
	private long authorId;
	private String authorNickname;
	private LocalDateTime createdTime;
	private LocalDateTime modifiedTime;

	public CommentDto(Comment comment) {
		this.content = comment.getContent();
		this.postId = comment.getPost().getId();
		this.authorId = comment.getAuthor().getId();
		this.authorNickname = comment.getAuthor().getNickname();
		this.createdTime = comment.getCreatedDate();
		this.modifiedTime = comment.getModifiedDate();
	}
}
