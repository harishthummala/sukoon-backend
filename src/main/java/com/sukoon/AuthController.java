package com.sukoon;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Register
    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody User user) {
        // Check if email already exists
        if(userRepository.findByEmail(user.getEmail()) != null) {
            return Map.of("error", "Email already registered");
        }

        // Encrypt password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User saved = userRepository.save(user);

        return Map.of(
                "message", "Registration successful",
                "userId", saved.getId(),
                "name", saved.getName()
        );
    }

    // Login
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");

        // Find user by email
        User user = userRepository.findByEmail(email);

        if(user == null) {
            return Map.of("error", "User not found");
        }

        // Check password
        if(!passwordEncoder.matches(password, user.getPassword())) {
            return Map.of("error", "Invalid password");
        }

        // Generate JWT token
        String token = jwtUtil.generateToken(email);

        return Map.of(
                "token", token,
                "userId", user.getId(),
                "name", user.getName(),
                "message", "Login successful"
        );
    }
}