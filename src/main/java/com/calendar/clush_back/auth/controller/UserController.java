package com.calendar.clush_back.auth.controller;

import com.calendar.clush_back.auth.dto.LoginRequest;
import com.calendar.clush_back.auth.dto.UserDataDTO;
import com.calendar.clush_back.auth.dto.UserResponseDTO;
import com.calendar.clush_back.auth.entity.User;
import com.calendar.clush_back.auth.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@Tag(name = "users", description = "사용자 관리 API")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final ModelMapper modelMapper;

    @PostMapping("/signin")
    @Operation(summary = "사용자 로그인", description = "사용자가 아이디와 비밀번호로 로그인합니다.")
    // @ApiOperation 대신 @Operation 사용
    @ApiResponses(value = {
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "422", description = "잘못된 사용자 이름/비밀번호 입력")
    })
    public String login(@RequestBody LoginRequest loginRequest) {
        return userService.signin(loginRequest.getEmail(), loginRequest.getPassword());
    }

    @PostMapping("/signup")
    @Operation(summary = "회원 가입", description = "새로운 사용자를 등록합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "422", description = "이미 사용 중인 사용자 이름")
    })
    public String signup(@RequestBody UserDataDTO user) {
        return userService.signup(modelMapper.map(user, User.class));
    }

    @GetMapping("/{email}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
        @Operation(summary = "사용자 검색", description = "특정 사용자의 정보를 검색합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "403", description = "접근이 거부됨"),
        @ApiResponse(responseCode = "404", description = "사용자가 존재하지 않음")
    })
    public UserResponseDTO search(@PathVariable String email) {
        return modelMapper.map(userService.search(email), UserResponseDTO.class);
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
    @Operation(summary = "내 정보 조회", description = "현재 로그인한 사용자의 정보를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "403", description = "접근이 거부됨")
    })
    public UserResponseDTO whoami(HttpServletRequest req) {
        return modelMapper.map(userService.whoami(req), UserResponseDTO.class);
    }

    @GetMapping("/refresh")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
    @Operation(summary = "JWT 토큰 갱신", description = "JWT 토큰을 갱신합니다.")
    public String refresh(@AuthenticationPrincipal UserDetails userDetails) {
        return userService.refresh(userDetails);
    }
}
