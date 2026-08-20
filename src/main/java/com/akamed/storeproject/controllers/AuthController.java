package com.akamed.storeproject.controllers;


import com.akamed.storeproject.dtos.JwtResponse;
import com.akamed.storeproject.dtos.LonginRequest;
import com.akamed.storeproject.dtos.UserDto;
import com.akamed.storeproject.mappers.UserMapper;
import com.akamed.storeproject.repositories.UserRepository;
import com.akamed.storeproject.services.JwtService;
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
    private final UserRepository userRepository;
    private UserMapper userMapper;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> loginUser(
            @Valid @RequestBody LonginRequest request
            ) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var token = jwtService.generateToken(request.getEmail());
        return ResponseEntity.ok(new JwtResponse(token));
    }


    @GetMapping("me")
    public ResponseEntity<UserDto> me() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var email = (String)authentication.getPrincipal();

        var user = userRepository.findByEmail(email).orElse(null);
        if(user == null) {
            return ResponseEntity.notFound().build();

        }
        var userDto = userMapper.toDto(user);
        return ResponseEntity.ok(userDto);
    }


    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Void> handleBadCredentialsException() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
