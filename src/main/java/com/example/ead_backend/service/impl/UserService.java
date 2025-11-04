package com.example.ead_backend.service.impl;

import com.example.ead_backend.model.enums.Role;
import com.example.ead_backend.model.entity.User;
import com.example.ead_backend.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepo userRepo;

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    public User save(User user) {
        return userRepo.save(user);
    }

    public User createUser(String firstName, String lastName, String password, String email, String phoneNumber, Role role) {
        // Validate email format
        if (!isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email format");
        }

        // Validate password length
        if (password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters long");
        }

        // Check if user already exists by email
        if (existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = new User(firstName, lastName, password, email, phoneNumber, role);
        return save(user);
    }

    public boolean existsByEmail(String email) {
        return userRepo.existsByEmail(email);
    }

    public User findByEmail(String email) {
        return userRepo.findByEmail(email).orElse(null);
    }

    private boolean isValidEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }
}
