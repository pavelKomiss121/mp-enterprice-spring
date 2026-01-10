package ru.mentee.blog.domain.repository;

import org.springframework.stereotype.Repository;
import ru.mentee.blog.domain.model.Post;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class PostRepository {
  private final Map<Long, Post> storage = new ConcurrentHashMap<>();
  private final AtomicLong idGenerator  = new AtomicLong(1);

  public PostRepository() {
    save(Post.builder()
        .title("Первый пост")
        .content("Изучаю Spring MVC")
        .author("demo")
        .build());
  }

  public Post save(Post post) {
    if (post.getId() == null) {
      post.setId(idGenerator.getAndIncrement());
    }
    storage.put(post.getId(), post);
    return post;
  }

  public Optional<Post> findById(Long id) {
    return Optional.ofNullable(storage.get(id));
  }

  public List<Post> findAll() {
    return new ArrayList<>(storage.values());
  }

  public void delete(Long id) {
    storage.remove(id);
  }
}
