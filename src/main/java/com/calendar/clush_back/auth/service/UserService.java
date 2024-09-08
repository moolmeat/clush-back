package com.calendar.clush_back.auth.service;

import com.calendar.clush_back.auth.entity.UserRole;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import com.calendar.clush_back.common.exception.CustomException;
import com.calendar.clush_back.auth.entity.User;
import com.calendar.clush_back.auth.repository.UserRepository;
import com.calendar.clush_back.auth.security.JwtTokenProvider;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    public String signin(String email, String password) {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password));
            return jwtTokenProvider.createToken(email,
                userRepository.findByEmail(email)
                    .orElseThrow(() -> new CustomException("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND))
                    .getUserRoles());
        } catch (AuthenticationException e) {
            throw new CustomException("Invalid email/password supplied",
                HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    public String signup(User user) {
        if (user.getUserRoles() == null) {
            user.setUserRoles(Collections.singletonList(UserRole.ROLE_CLIENT));
        }
        if (!userRepository.existsByEmail(user.getEmail())) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(user);
            return jwtTokenProvider.createToken(user.getEmail(), user.getUserRoles());
        } else {
            throw new CustomException("Username is already in use",
                HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    public User search(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new CustomException("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));
        if (user == null) {
            throw new CustomException("The user doesn't exist", HttpStatus.NOT_FOUND);
        }
        return user;
    }

    public User whoami(HttpServletRequest req) {
        return userRepository.findByEmail(
                jwtTokenProvider.getUsername(jwtTokenProvider.resolveToken(req)))
            .orElseThrow(() -> new CustomException("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));
    }

    public String refresh(UserDetails userDetails) {
        String email = userDetails.getUsername();
        return jwtTokenProvider.createToken(email,
            userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND))
                .getUserRoles());
    }

}
