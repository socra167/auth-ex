package com.auth.global;

import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import com.auth.domain.member.member.entity.Member;
import com.auth.domain.member.member.service.MemberService;
import com.auth.global.exception.ServiceException;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

// Request, Response, Session, Cookie, Header 같은 전역적인 작업을 처리하는 클래스
@Component
@RequiredArgsConstructor
@RequestScope // @RequestScope를 적용하면 요청마다 생성되고 사라진다. Request와 같은 생명주기를 갖는다
public class Rq {

	private final HttpServletRequest request; // 요청마다 달라지기 때문에 request가 고정되면 안된다 -> @RequestScope
	private final MemberService memberService;

	public Member getAuthenticatedActor() {
		// Bearer 4/user11234
		String credentials = request.getHeader("Authorization");
		// HttpServletRequest에서 직접 인증 정보를 가져온다
		// 각 메서드에서 Header를 신경쓰지 않아도 된다 (@RequestHeader 제거)
		String apiKey = credentials.substring("Bearer ".length());
		Optional<Member> opActor = memberService.findByApiKey(apiKey);

		if (opActor.isEmpty()) { // 이제 동일한 apiKey가 DB에 존재하는지만 확인하면 된다
			throw new ServiceException("401-1", "아이디 또는 비밀번호가 일치하지 않습니다.");
		}

		Member actor = opActor.get();

		// if (!actor.getapiKey().equals(apiKey)) {
		// 	throw new ServiceException("401-1", "비밀번호가 일치하지 않습니다.");
		// }

		return actor;
	}

}
