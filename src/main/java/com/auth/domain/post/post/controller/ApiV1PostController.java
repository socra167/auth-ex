package com.auth.domain.post.post.controller;

import java.util.List;
import java.util.Optional;

import org.hibernate.validator.constraints.Length;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.auth.domain.member.member.entity.Member;
import com.auth.domain.member.member.service.MemberService;
import com.auth.domain.post.post.dto.PostDto;
import com.auth.domain.post.post.entity.Post;
import com.auth.domain.post.post.service.PostService;
import com.auth.global.dto.RsData;
import com.auth.global.exception.ServiceException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class ApiV1PostController {

	private final PostService postService;
	private final MemberService memberService;
	private final HttpServletRequest request; // HttpServletRequest에서 직접 인증 정보를 가져오기

	@GetMapping
	public RsData<List<PostDto>> getItems() {

		List<Post> posts = postService.getItems();
		List<PostDto> postDtos = posts.stream().map(PostDto::new).toList();

		return new RsData<>("200-1", "글 목록 조회가 완료되었습니다.", postDtos);
	}

	@GetMapping("{id}")
	public RsData<PostDto> getItem(@PathVariable long id) {

		Post post = postService.getItem(id).get();

		return new RsData<>("200-1", "글 조회가 완료되었습니다.", new PostDto(post));
	}

	@DeleteMapping("/{id}")
	public RsData<Void> delete(@PathVariable long id) {
		Member actor = getAuthenticatedActor();
		Post post = postService.getItem(id).get();

		// 자신이 등록한 글만 삭제할 수 있다 : 인가
		if (post.getAuthor().getId() != actor.getId()) {
			throw new ServiceException("403-1", "자신이 작성한 글만 삭제 가능합니다.");
		}

		postService.delete(post);

		return new RsData<>("204-1", "%d번 글 삭제가 완료되었습니다.".formatted(id));
	}

	record ModifyReqBody(@NotBlank @Length(min = 3) String title, @NotBlank @Length(min = 3) String content) {
	}

	@PutMapping("/{id}")
	public RsData<Void> modify(@PathVariable long id, @RequestBody @Valid ModifyReqBody body) {
		Member actor = getAuthenticatedActor();

		Post post = postService.getItem(id).get();

		// 자신이 등록한 글만 수정할 수 있다 : 인가
		if (post.getAuthor().getId() != actor.getId()) {
			throw new ServiceException("403-1", "자신이 작성한 글만 수정 가능합니다.");
		}

		postService.modify(post, body.title(), body.content());
		return new RsData<>("200-1", "%d번 글 수정이 완료되었습니다.".formatted(id), null);
	}

	record WriteReqBody(@NotBlank @Length(min = 3) String title, @NotBlank @Length(min = 3) String content) {
	}

	@PostMapping
	public RsData<PostDto> write(@RequestBody @Valid WriteReqBody body) {
		Member actor = getAuthenticatedActor();

		Post post = postService.write(actor, body.title(), body.content());

		return new RsData<>("200-1", "글 작성이 완료되었습니다.", new PostDto(post));
	}

	private Member getAuthenticatedActor() {
		// Bearer 4/user11234
		String credentials = request.getHeader("Authorization");
		// HttpServletRequest에서 직접 인증 정보를 가져온다
		// 각 메서드에서 Header를 신경쓰지 않아도 된다 (@RequestHeader 제거)
		String password2 = credentials.substring("Bearer ".length());
		Optional<Member> opActor = memberService.findByPassword2(password2);

		if (opActor.isEmpty()) { // 이제 동일한 password2가 DB에 존재하는지만 확인하면 된다
			throw new ServiceException("401-1", "비밀번호가 일치하지 않습니다.");
		}

		Member actor = opActor.get();

		// if (!actor.getPassword2().equals(password2)) {
		// 	throw new ServiceException("401-1", "비밀번호가 일치하지 않습니다.");
		// }

		return actor;
	}
}
