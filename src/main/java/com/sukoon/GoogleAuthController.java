package com.sukoon;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class GoogleAuthController {
    private static final Logger logger = LoggerFactory.getLogger(GoogleAuthController.class);
    private static final NetHttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private static final GsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${google.client.id:}")
    private String googleClientId;

    @PostMapping("/google")
    public ResponseEntity<Map<String, Object>> googleLogin(
            @RequestBody Map<String, String> body) {

        String googleToken = getGoogleToken(body);

        if(googleToken == null || googleToken.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Google token is required"));
        }

        if(googleClientId == null || googleClientId.isBlank()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Google client ID is not configured"));
        }

        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier
                    .Builder(HTTP_TRANSPORT, JSON_FACTORY)
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(googleToken);

            if(idToken == null) {
                return ResponseEntity.status(401)
                        .body(Map.of("error", "Invalid Google token"));
            }

            GoogleIdToken.Payload payload = idToken.getPayload();
            if(!Boolean.TRUE.equals(payload.getEmailVerified())) {
                return ResponseEntity.status(401)
                        .body(Map.of("error", "Google email is not verified"));
            }

            String email = normalizeEmail(payload.getEmail());
            if(email == null) {
                return ResponseEntity.status(401)
                        .body(Map.of("error", "Google account email is required"));
            }

            String name = resolveName(payload, email);

            User user = userRepository.findByEmailIgnoreCase(email);
            if(user == null) {
                user = new User();
                user.setEmail(email);
                user.setName(name);
                user.setPassword(passwordEncoder.encode(
                        UUID.randomUUID().toString()
                ));
                user = userRepository.save(user);
            } else if((user.getName() == null || user.getName().isBlank()) && name != null) {
                user.setName(name);
                user = userRepository.save(user);
            }

            String token = jwtUtil.generateToken(user.getEmail());

            return ResponseEntity.ok(authResponse(token, user));

        } catch(Exception e) {
            logger.warn("Google authentication failed", e);
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Google authentication failed"));
        }
    }

    private String getGoogleToken(Map<String, String> body) {
        if(body == null) {
            return null;
        }

        String credential = body.get("credential");
        if(credential != null && !credential.isBlank()) {
            return credential;
        }

        String idToken = body.get("idToken");
        if(idToken != null && !idToken.isBlank()) {
            return idToken;
        }

        return body.get("token");
    }

    private String normalizeEmail(String email) {
        if(email == null || email.isBlank()) {
            return null;
        }

        return email.trim().toLowerCase(Locale.ROOT);
    }

    private String resolveName(GoogleIdToken.Payload payload, String email) {
        Object googleName = payload.get("name");
        if(googleName instanceof String name && !name.isBlank()) {
            return name;
        }

        int atIndex = email.indexOf("@");
        if(atIndex <= 0) {
            return email;
        }

        return email.substring(0, atIndex);
    }

    private Map<String, Object> authResponse(String token, User user) {
        return Map.of(
                "token", token,
                "user", Map.of(
                        "id", user.getId(),
                        "email", user.getEmail(),
                        "name", user.getName() == null ? "" : user.getName()
                )
        );
    }
}
