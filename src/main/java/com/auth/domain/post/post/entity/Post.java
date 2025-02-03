package com.auth.domain.post.post.entity;

import com.auth.domain.member.member.entity.Member;
import com.auth.global.entity.BaseTime;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Post extends BaseTime {

    @ManyToOne(fetch = FetchType.LAZY)
    private Member author;
    private String title;
    private String content;

}
