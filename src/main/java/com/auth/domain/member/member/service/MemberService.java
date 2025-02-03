package com.auth.domain.member.member.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.auth.domain.member.member.entity.Member;
import com.auth.domain.member.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;

	public Member join(String username, String password, String nickname) {

		UUID uuid = UUID.randomUUID();

		Member member = Member.builder()
			.username(username)
			.password(password)
			.apiKey(username)
			.nickname(nickname)
			.build();

		return memberRepository.save(member);
	}

	public long count() {
		return memberRepository.count();
	}

	public Optional<Member> findByUsername(String username) {
		return memberRepository.findByUsername(username);
	}

	public Optional<Member> findById(long authorId) {
		return memberRepository.findById(authorId);
	}

	public Optional<Member> findByApiKey(String apiKey) {
		return memberRepository.findByApiKey(apiKey);
	}
}