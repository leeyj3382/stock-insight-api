package com.leeyujun.stockinsightapi.api.post;

import com.leeyujun.stockinsightapi.api.post.dto.CreatePostRequest;
import com.leeyujun.stockinsightapi.api.post.dto.PostListItemResponse;
import com.leeyujun.stockinsightapi.api.post.dto.PostResponse;
import com.leeyujun.stockinsightapi.api.post.dto.UpdatePostRequest;
import com.leeyujun.stockinsightapi.common.security.AuthUtil;
import com.leeyujun.stockinsightapi.domain.post.entity.Post;
import com.leeyujun.stockinsightapi.domain.post.service.PostService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public PostResponse create(@Valid @RequestBody CreatePostRequest req) {
        Long userId = AuthUtil.currentUserId();
        Post p = postService.create(userId, req);
        return toResponse(p);
    }

    @GetMapping
    public List<PostListItemResponse> listMine() {
        Long userId = AuthUtil.currentUserId();
        return postService.listMine(userId).stream()
                .map(p -> new PostListItemResponse(p.getId(), p.getTitle(), p.getCreatedAt(), p.getUpdatedAt()))
                .toList();
    }

    @GetMapping("/{id}")
    public PostResponse getMine(@PathVariable Long id) {
        Long userId = AuthUtil.currentUserId();
        Post p = postService.getMine(userId, id);
        return toResponse(p);
    }

    @PutMapping("/{id}")
    public PostResponse updateMine(@PathVariable Long id, @Valid @RequestBody UpdatePostRequest req) {
        Long userId = AuthUtil.currentUserId();
        Post p = postService.updateMine(userId, id, req);
        return toResponse(p);
    }

    @DeleteMapping("/{id}")
    public void deleteMine(@PathVariable Long id) {
        Long userId = AuthUtil.currentUserId();
        postService.deleteMine(userId, id);
    }

    private PostResponse toResponse(Post p) {
        return new PostResponse(
                p.getId(),
                p.getTitle(),
                p.getContent(),
                p.getCreatedAt(),
                p.getUpdatedAt()
        );
    }
}
