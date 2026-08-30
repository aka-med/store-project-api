package com.akamed.storeproject.services;


import com.akamed.storeproject.entities.User;
import com.akamed.storeproject.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    public User getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var userId = (Long)authentication.getPrincipal();

        return userRepository.findById(userId).orElse(null);
    }
}
