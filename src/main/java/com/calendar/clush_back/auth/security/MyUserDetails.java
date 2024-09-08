package com.calendar.clush_back.auth.security;

import com.calendar.clush_back.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import com.calendar.clush_back.auth.entity.User;
import com.calendar.clush_back.auth.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MyUserDetails implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        final User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new CustomException("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        if (user == null) {
            throw new UsernameNotFoundException("User '" + email + "' not found");
        }

        return org.springframework.security.core.userdetails.User
            .withUsername(email)
            .password(user.getPassword())
            .authorities(user.getUserRoles())
            .accountExpired(false)
            .accountLocked(false)
            .credentialsExpired(false)
            .disabled(false)
            .build();
    }

}
