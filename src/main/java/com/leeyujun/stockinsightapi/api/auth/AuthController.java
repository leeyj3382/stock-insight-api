package com.leeyujun.stockinsightapi.api.auth;


import com.leeyujun.stockinsightapi.api.auth.dto.SignupRequest;
import com.leeyujun.stockinsightapi.api.auth.dto.SignupResponse;
import com.leeyujun.stockinsightapi.domain.user.entity.User;
import com.leeyujun.stockinsightapi.domain.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(@Valid @RequestBody SignupRequest req){
        User user = userService.signup(req);
        return ResponseEntity.ok(new SignupResponse(user.getId(), user.getEmail(), user.getNickname()));
    }
}
