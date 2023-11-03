package com.coyote.post;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private static final Logger logger = LoggerFactory.getLogger(PostController.class);
    private final JsonPlaceholderService jsonPlaceholderService;
    List<Post> posts = new ArrayList<>();

    public PostController(JsonPlaceholderService jsonPlaceholderService) {
        this.jsonPlaceholderService = jsonPlaceholderService;
    }

//    @PostConstruct
//    public void init() {
//        posts.add(new Post(1, "Post 1", "Content 1"));
//        posts.add(new Post(2, "Post 2", "Content 2"));
//        posts.add(new Post(3, "Post 3", "Content 3"));
//    }

    @GetMapping
    List<Post> getPosts() {
        return posts;
    }
    @GetMapping("/id")
    Optional<Post> getPostById(@PathVariable Integer id) {
        return Optional.ofNullable(posts
                .stream()
                .filter(post -> post.id().equals(id))
                .findFirst()
                .orElseThrow(
                        () -> new PostNotFoundException("Post with id " + id + " not found.")
                ));
    }

    @PostMapping
    void createPost(@RequestBody Post post) {
        posts.add(post);
    }

    @PutMapping("/{id}")
    void updatePost(@PathVariable Integer id, @RequestBody Post post) {
        posts.stream()
                .filter(p -> p.id().equals(id))
                .findFirst()
                .ifPresentOrElse(
                        value -> {
                            posts.set(posts.indexOf(value), post);
                        },
                        () -> {
                            throw new PostNotFoundException("Post with id " + id + " not found.");
                        }
                );
    }
    @DeleteMapping
    void deletePost(@PathVariable Integer id) {
        posts.removeIf(post -> post.id().equals(id));
    }

    @PostConstruct
    public void init() {
        if(posts.isEmpty()) {
            logger.info("Loading posts from jsonplaceholder.typicode.com");
            posts = jsonPlaceholderService.loadPosts();
        }
    }

}
