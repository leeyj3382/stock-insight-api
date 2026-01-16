package com.leeyujun.stockinsightapi.domain.post.service;

import com.leeyujun.stockinsightapi.api.post.dto.CreatePostRequest;
import com.leeyujun.stockinsightapi.api.post.dto.UpdatePostRequest;
import com.leeyujun.stockinsightapi.domain.post.entity.Post;
import com.leeyujun.stockinsightapi.domain.post.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class PostService {

    private final PostRepository postRepository;
    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Transactional
    public Post create(Long userId, CreatePostRequest req){
        Post p = new Post();
        p.setUserId(userId);
        p.setTitle(req.title());
        p.setContent(req.content());
        return postRepository.save(p);
    }

    @Transactional(readOnly = true)
    public List<Post> listMine(Long userId) {
        return postRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Transactional(readOnly = true)
    public List<Post> listAll() {
        return postRepository.findAllByOrderByCreatedAtDesc();
    }



    @Transactional(readOnly = true)
    public Post getMine(Long userId, Long postId) {
        return postRepository.findByIdAndUserId(postId, userId)
                .orElseThrow(() -> new RuntimeException("post not found"));
    }

    @Transactional(readOnly = true)
    public Post getPublic(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("post not found"));
    }


    @Transactional
    public Post updateMine(Long  userId, Long postId, UpdatePostRequest req){
        Post p = postRepository.findByIdAndUserId(postId, userId)
                .orElseThrow(() -> new RuntimeException("post not found"));
        p.setTitle(req.title());
        p.setContent(req.content());

        return p;
    }

    @Transactional
    public void deleteMine(Long userId, Long postId) {
        postRepository.findByIdAndUserId(postId, userId)
                .orElseThrow(() -> new RuntimeException("post not found"));

        postRepository.deleteByIdAndUserId(postId, userId);
    }
}
