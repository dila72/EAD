package com.example.ead_backend.controller;

import com.example.ead_backend.dto.AuthResponse;
import com.example.ead_backend.dto.LoginRequest;
import com.example.ead_backend.dto.SignupRequest;
import com.example.ead_backend.model.entity.Customer;
import com.example.ead_backend.model.entity.User;
import com.example.ead_backend.service.CustomerService;
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
    private CustomerService customerService;

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

            // Determine role from user's customer or employee relationship
            String role = "CUSTOMER";
            if (user.getEmployee() != null) {
                role = user.getEmployee().getRole().name();
            }

            return ResponseEntity.ok(new AuthResponse(token, user.getEmail(), role));
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

        try {
            // Hash the password before creating user
            String encodedPassword = passwordEncoder.encode(signupRequest.getPassword());

            // Create User entity
            User user = userService.createUser(
                    signupRequest.getFirstName(),
                    signupRequest.getLastName(),
                    encodedPassword,
                    signupRequest.getEmail()
            );

            // Create Customer entity linked to the User
            Customer customer = customerService.createCustomer(user, signupRequest.getPhoneNumber());

            // Set the customer relationship in user
            user.setCustomer(customer);

            // Generate JWT token for the newly created user
            String token = jwtUtil.generateToken(user);

            return ResponseEntity.ok(new AuthResponse(token, user.getEmail(), "CUSTOMER"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Registration failed: " + e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // JWT tokens are stateless, so logout is handled client-side by removing the token
        return ResponseEntity.ok("Logout successful");
    }
}
