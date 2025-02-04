package com.auth.domain.post.comment.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.auth.domain.member.member.entity.Member;
import com.auth.domain.post.comment.dto.CommentDto;
import com.auth.domain.post.comment.entity.Comment;
import com.auth.domain.post.post.entity.Post;
import com.auth.domain.post.post.service.PostService;
import com.auth.global.Rq;
import com.auth.global.dto.RsData;
import com.auth.global.exception.ServiceException;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/posts/{postId}/comments")
public class ApiV1CommentController {

	private final PostService postService;
	private final Rq rq;
	@Autowired @Lazy
	private ApiV1CommentController self;

	@GetMapping
	public List<CommentDto> getItems(@PathVariable long postId) {
		Post post = postService.getItem(postId)
			.orElseThrow(() -> new ServiceException("404-1", "존재하지 않는 게시물입니다."));
		return post.getComments()
			.stream()
			.map(CommentDto::new)
			.toList();
	}

	@GetMapping("{id}")
	public CommentDto getItem(@PathVariable long postId, @PathVariable long id) {
		Post post = postService.getItem(postId)
			.orElseThrow(() -> new ServiceException("404-1", "존재하지 않는 게시물입니다."));
		Comment comment = post.getCommentById(id);
		return new CommentDto(comment);
	}

	record WriteReqBody(@NotBlank String content) {
	}

	@PostMapping
	public RsData<Void> write(@PathVariable long postId, @RequestBody WriteReqBody body) {
		Member actor = rq.getAuthenticatedActor();
		Comment comment = self._write(postId, actor, body.content());
		return new RsData<>(
			"201-1",
			"%d번 댓글 작성이 완료되었습니다.".formatted(comment.getId())
		);
	}

	@Transactional
	public Comment _write(long postId, Member actor, String content) {
		Post post = postService.getItem(postId)
			.orElseThrow(() -> new ServiceException("404-1", "존재하지 않는 게시물입니다."));
		Comment comment = post.addComment(actor, content);
		return comment;
	}
}
