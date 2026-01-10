package ru.mentee.blog.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.mentee.blog.api.annotation.CurrentUser;
import ru.mentee.blog.api.dto.CreatePostRequest;
import ru.mentee.blog.domain.model.Post;
import ru.mentee.blog.domain.model.User;
import ru.mentee.blog.domain.repository.PostRepository;
import ru.mentee.blog.exception.PostNotFoundException;
import ru.mentee.blog.service.NotificationService;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

  private final PostRepository postRepository;
  private final NotificationService notificationService;

  @GetMapping(produces = {
      MediaType.APPLICATION_JSON_VALUE,
      MediaType.APPLICATION_XML_VALUE
  })
  public List<Post> getAllPosts() {
    return postRepository.findAll();
  }

  @GetMapping("/{id}")
  public Post getPostById(@PathVariable Long id) {
    return postRepository.findById(id)
        .orElseThrow(() -> new PostNotFoundException(id));
  }

  @PostMapping
  public CompletableFuture<Post> createPost(
      @RequestBody @Valid CreatePostRequest request,
      @CurrentUser User user) {

    Post post = Post.builder()
        .title(request.getTitle())
        .content(request.getContent())
        .author(user.getUsername())
        .build();

    Post saved = postRepository.save(post);

    // Async - не блокирует request thread
    notificationService.sendPostNotification(post);

    return CompletableFuture.completedFuture(saved);
  }
}
