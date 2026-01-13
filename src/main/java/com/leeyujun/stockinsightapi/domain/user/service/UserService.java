package com.leeyujun.stockinsightapi.domain.user.service;


import com.leeyujun.stockinsightapi.common.exception.EmailAlreadyExistsException;
import com.leeyujun.stockinsightapi.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import com.leeyujun.stockinsightapi.domain.user.entity.User;
import com.leeyujun.stockinsightapi.domain.user.entity.UserRole;
import com.leeyujun.stockinsightapi.api.auth.dto.SignupRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.leeyujun.stockinsightapi.common.exception.InvalidCredentialException;
import com.leeyujun.stockinsightapi.common.security.JwtTokenProvider;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final JwtTokenProvider jwtTokenProvider;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Transactional
    public User signup(SignupRequest req){
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new EmailAlreadyExistsException(req.getEmail());
        }

        String hashed = passwordEncoder.encode(req.getPassword());
        User user = new User(req.getEmail(), hashed, req.getNickname(), UserRole.USER);

        return userRepository.save(user);

    }

    @Transactional
    public String login(String email, String password){
        User user = userRepository.findByEmail(email)
                .orElseThrow(InvalidCredentialException::new);
        if (!passwordEncoder.matches(password, user.getPasswordHash())){
            throw new InvalidCredentialException();
        }

        return jwtTokenProvider.createAccessToken(user.getId(), user.getEmail(), user.getRole().name());
    }
}
