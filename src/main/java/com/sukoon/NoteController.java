package com.sukoon;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private User getUserFromToken(String authHeader) {
        String email = jwtUtil.extractEmail(authHeader.substring(7));
        return userRepository.findByEmail(email);
    }

    private Map<String, Object> formatNote(Note note) {
        return Map.of(
                "id", note.getId().toString(),
                "title", note.getTitle(),
                "content", note.getContent(),
                "createdAt", note.getCreatedAt().toString(),
                "updatedAt", note.getUpdatedAt().toString()
        );
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getNotes(
            @RequestHeader("Authorization") String authHeader) {
        User user = getUserFromToken(authHeader);
        List<Note> notes = noteRepository
                .findByUserIdOrderByUpdatedAtDesc(user.getId());
        return ResponseEntity.ok(
                notes.stream().map(this::formatNote).collect(Collectors.toList())
        );
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createNote(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, String> body) {
        User user = getUserFromToken(authHeader);
        Note note = new Note();
        note.setTitle(body.get("title"));
        note.setContent(body.get("content"));
        note.setUser(user);
        Note saved = noteRepository.save(note);
        return ResponseEntity.status(201).body(formatNote(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateNote(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        Note note = noteRepository.findById(id).orElse(null);
        if(note == null) {
            return ResponseEntity.status(404)
                    .body(Map.of("error", "Note not found"));
        }
        note.setTitle(body.get("title"));
        note.setContent(body.get("content"));
        note.setUpdatedAt(LocalDateTime.now());
        Note saved = noteRepository.save(note);
        return ResponseEntity.ok(formatNote(saved));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteNote(
            @PathVariable Long id) {
        if(!noteRepository.existsById(id)) {
            return ResponseEntity.status(404)
                    .body(Map.of("error", "Note not found"));
        }
        noteRepository.deleteById(id);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Note deleted successfully"
        ));
    }
}