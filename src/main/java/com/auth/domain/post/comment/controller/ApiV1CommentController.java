package com.auth.domain.post.comment.controller;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

	@Transactional
	@PostMapping
	public RsData<Void> write(@PathVariable long postId, @RequestBody WriteReqBody body) {
		Member actor = rq.getAuthenticatedActor();
		Post post = postService.getItem(postId)
			.orElseThrow(() -> new ServiceException("404-1", "존재하지 않는 게시물입니다."));
		Comment comment = post.addComment(actor, body.content());

		postService.flush();

		return new RsData<>(
			"201-1",
			"%d번 댓글 작성이 완료되었습니다.".formatted(comment.getId())
		);
	}

	record ModifyReqBody(String content) {
	}

	@Transactional
	@PutMapping("{id}")
	public RsData<Void> modify(@PathVariable long postId, @PathVariable long id, @RequestBody ModifyReqBody body) {
		Member actor = rq.getAuthenticatedActor();
		Post post = postService.getItem(postId)
			.orElseThrow(() -> new ServiceException("404-1", "존재하지 않는 게시물입니다."));

		Comment comment = post.getCommentById(id);
		// admin도 아니고, 작성자도 아닌 경우 댓글을 수정할 수 없다
		comment.canModify(actor);
		comment.modify(body.content()); // setter를 사용하는 것보다, 수정 메서드를 만들어 사용하는 게 낫다
		// setter보다 메서드 이름으로 의도를 파악하기 쉽고, 수정 작업의 전처리, 후처리 작업을 관리하기 좋다

		return new RsData<>(
			"200-1",
			"%d번 댓글 수정이 완료되었습니다.".formatted(id)
		);
	}

	@Transactional
	@DeleteMapping("{id}")
	public RsData<Void> delete(@PathVariable long postId, @PathVariable long id) {
		Member actor = rq.getAuthenticatedActor();
		Post post = postService.getItem(postId)
			.orElseThrow(() -> new ServiceException("404-1", "존재하지 않는 게시물입니다."));

		Comment comment = post.getCommentById(id);
		comment.canDelete(actor);
		post.deleteComment(comment);

		return new RsData<>(
			"200-1",
			"%d번 댓글 삭제가 완료되었습니다.".formatted(id)
		);
	}
}

