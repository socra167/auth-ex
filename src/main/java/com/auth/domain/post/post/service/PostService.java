package com.auth.domain.post.post.service;

import com.auth.domain.member.member.entity.Member;
import com.auth.domain.post.post.entity.Post;
import com.auth.domain.post.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    public Post write(Member author, String title, String content) {

        return postRepository.save(
                Post
                        .builder()
                        .author(author)
                        .title(title)
                        .content(content)
                        .build()
        );
    }

    public List<Post> getItems() {
        return postRepository.findAll();
    }

    public Optional<Post> getItem(long id) {
        return postRepository.findById(id);
    }

    public long count() {
        return postRepository.count();
    }

    public void delete(Post post) {
        postRepository.delete(post);
    }

    @Transactional
    public void modify(Post post, String title, String content) {
        post.setTitle(title);
        post.setContent(content);
    }

    public void flush() {
        postRepository.flush(); // Repository를 사용해 flush()를 할 수 있다.
        // Spring Data JPA를 벗어나지 않으면서(EntityManager를 사용하지 않고) flush()를 하고싶으면 Repository를 사용하는게 낫다.
    }
}
