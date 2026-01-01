package com.expensetracker.service;

import com.expensetracker.model.Users;
import com.expensetracker.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository; // your JPA repo

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        // username can be email or id, depending on what you put in JWT subject
       // Users user = userRepository.findById(Long.valueOf(username)).stream().findFirst()
        Users user = userRepository.findByEmail(username).stream().findFirst()
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())   // or email
                .password(user.getPasswordHash())
                .authorities("USER")
                .build();
    }
}
