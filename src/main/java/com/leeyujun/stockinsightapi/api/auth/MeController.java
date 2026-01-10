package com.leeyujun.stockinsightapi.api.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MeController {

    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication authentication){
        return ResponseEntity.ok(authentication.getName());
    }
}
