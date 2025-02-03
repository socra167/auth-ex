package com.auth.domain.post.post.controller;

import com.auth.domain.member.member.entity.Member;
import com.auth.domain.member.member.service.MemberService;
import com.auth.domain.post.post.dto.PostDto;
import com.auth.domain.post.post.entity.Post;
import com.auth.domain.post.post.service.PostService;
import com.auth.global.dto.RsData;
import com.auth.global.exception.ServiceException;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

import org.hibernate.validator.constraints.Length;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class ApiV1PostController {

	private final PostService postService;
	private final MemberService memberService;

	@GetMapping
	public RsData<List<PostDto>> getItems() {

		List<Post> posts = postService.getItems();
		List<PostDto> postDtos = posts.stream()
			.map(PostDto::new)
			.toList();

		return new RsData<>(
			"200-1",
			"글 목록 조회가 완료되었습니다.",
			postDtos
		);
	}

	@GetMapping("{id}")
	public RsData<PostDto> getItem(@PathVariable long id) {

		Post post = postService.getItem(id).get();

		return new RsData<>(
			"200-1",
			"글 조회가 완료되었습니다.",
			new PostDto(post)
		);
	}

	record DeleteReqBody(@NotNull Long authorId, @NotBlank @Length(min = 3) String password) {
	}

	@DeleteMapping("/{id}")
	public RsData<Void> delete(@PathVariable long id, @RequestHeader Long authorId, @RequestHeader String password) {
		// 인증
		Member actor = memberService.findById(authorId).get();
		if (!actor.getPassword().equals(password)) { // 비밀번호 검사
			throw new ServiceException("401-1", "비밀번호가 일치하지 않습니다.");
		}

		Post post = postService.getItem(id).get();

		// 자신이 등록한 글만 삭제할 수 있다 : 인가
		if (post.getAuthor().getId() != authorId) {
			throw new ServiceException("403-1", "자신이 작성한 글만 삭제 가능합니다.");
		}

		postService.delete(post);

		return new RsData<>(
			"204-1",
			"%d번 글 삭제가 완료되었습니다.".formatted(id)
		);
	}

	record ModifyReqBody(
		@NotBlank @Length(min = 3) String title,
		@NotBlank @Length(min = 3) String content,
		@NotNull Long authorId,
		@NotBlank @Length(min = 3) String password) {
	}

	@PutMapping("/{id}")
	public RsData<Void> modify(@PathVariable long id, @RequestBody @Valid ModifyReqBody body) {
		// 인증
		Member actor = memberService.findById(body.authorId()).get();
		if (!actor.getPassword().equals(body.password)) { // 비밀번호 검사
			throw new ServiceException("401-1", "비밀번호가 일치하지 않습니다.");
		}

		Post post = postService.getItem(id).get();

		// 자신이 등록한 글만 수정할 수 있다 : 인가
		if (post.getAuthor().getId() != body.authorId()) {
			throw new ServiceException("403-1", "자신이 작성한 글만 수정 가능합니다.");
		}

		postService.modify(post, body.title(), body.content());
		return new RsData<>(
			"200-1",
			"%d번 글 수정이 완료되었습니다.".formatted(id),
			null
		);
	}

	record WriteReqBody(
		@NotBlank @Length(min = 3) String title,
		@NotBlank @Length(min = 3) String content,
		@NotNull Long authorId, // validation 체크를 하려면 원시타입이 아닌 객체 타입이어야 한다
		@NotBlank @Length(min = 3) String password) { // 글 작성을 위해 요청을 보낸 사람이 본인임을 인증하기 위해 비밀번호를 받도록 한다
	}

	@PostMapping
	public RsData<PostDto> write(@RequestBody @Valid WriteReqBody body) {

		Member actor = memberService.findById(body.authorId()).get();

		if (!actor.getPassword().equals(body.password)) { // 비밀번호 검사
			throw new ServiceException("401-1", "비밀번호가 일치하지 않습니다.");
		}

		Post post = postService.write(actor, body.title(), body.content());

		return new RsData<>(
			"200-1",
			"글 작성이 완료되었습니다.",
			new PostDto(post)
		);
	}
}
