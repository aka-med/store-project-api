package com.akamed.storeproject.controllers;


import com.akamed.storeproject.config.JwtConfig;
import com.akamed.storeproject.dtos.JwtResponse;
import com.akamed.storeproject.dtos.LonginRequest;
import com.akamed.storeproject.dtos.UserDto;
import com.akamed.storeproject.mappers.UserMapper;
import com.akamed.storeproject.repositories.UserRepository;
import com.akamed.storeproject.services.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


@AllArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {


    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final JwtConfig jwtConfig;
    private final UserRepository userRepository;
    private UserMapper userMapper;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> loginUser(
            @Valid @RequestBody LonginRequest request,
            HttpServletResponse response
            ) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var user = userRepository.findByEmail(request.getEmail()).orElse(null);
        if(user == null)
            return ResponseEntity.notFound().build();
        var accessToken = jwtService.generateAccessToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        var cookie = new Cookie("refreshToken", refreshToken.toString());
        cookie.setHttpOnly(true);
        cookie.setPath("/auth/refresh");
        cookie.setMaxAge(jwtConfig.getRefreshTokenExpiration());
        cookie.setSecure(true);
        response.addCookie(cookie);

        return ResponseEntity.ok(new JwtResponse(accessToken.toString()));
    }


    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> refresh(
            @CookieValue(value = "refreshToken") String refreshToken
    ) {
        var jwt = jwtService.parseToken(refreshToken);
        if(jwt == null || jwt.isExpired()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        var user = userRepository.findById(jwt.getUserId()).orElseThrow();
        var accessToken = jwtService.generateAccessToken(user);

        return ResponseEntity.ok(new JwtResponse(accessToken.toString()));
    }


    @GetMapping("/me")
    public ResponseEntity<UserDto> me() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var userId = (Long)authentication.getPrincipal();

        var user = userRepository.findById(userId).orElse(null);
        if(user == null)
            return ResponseEntity.notFound().build();

        var userDto = userMapper.toDto(user);
        return ResponseEntity.ok(userDto);
    }


    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Void> handleBadCredentialsException() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
