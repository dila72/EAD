package com.example.ead_backend.controller;

import com.example.ead_backend.dto.AuthResponse;
import com.example.ead_backend.dto.LoginRequest;
import com.example.ead_backend.dto.SignupRequest;
import com.example.ead_backend.model.enums.Role;
import com.example.ead_backend.model.entity.User;
import com.example.ead_backend.service.impl.UserService;
import com.example.ead_backend.util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    @Lazy
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error ->
                    errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtUtil.generateToken(userDetails);
            User user = (User) userDetails;

            return ResponseEntity.ok(new AuthResponse(token, user.getEmail(), user.getRole().name()));
        } catch (BadCredentialsException e) {
            return ResponseEntity.badRequest().body("Invalid email or password");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Authentication failed: " + e.getMessage());
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest signupRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error ->
                    errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }

        // Ensure only customers can sign up through this endpoint
        if (signupRequest.getRole() != Role.CUSTOMER) {
            return ResponseEntity.badRequest().body("Only customer accounts can be created through signup. Employee accounts must be created by administrators.");
        }

        try {
            // Hash the password before creating user
            String encodedPassword = passwordEncoder.encode(signupRequest.getPassword());

            User user = userService.createUser(
                    signupRequest.getName(),
                    encodedPassword,
                    signupRequest.getEmail(),
                    signupRequest.getPhoneNumber(),
                    Role.CUSTOMER // Force customer role
            );

            // Generate JWT token for the newly created user
            String token = jwtUtil.generateToken(user);

            return ResponseEntity.ok(new AuthResponse(token, user.getEmail(), user.getRole().name()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Registration failed: " + e.getMessage());
        }
    }
}
