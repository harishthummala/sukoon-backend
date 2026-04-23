package com.sukoon;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/moods")
public class MoodEntryController {

    @Autowired
    private MoodEntryRepository moodEntryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AIService aiService;

    // Log mood + get AI response
    @PostMapping("/log/{userId}")
    public Map<String, Object> logMood(@PathVariable Long userId,
                                       @RequestBody MoodEntry moodEntry) {
        // Find user
        User user = userRepository.findById(userId).orElse(null);
        if(user == null) {
            return Map.of("error", "User not found");
        }

        // Save mood entry
        moodEntry.setUser(user);
        MoodEntry saved = moodEntryRepository.save(moodEntry);

        // Get AI response
        String aiResponse = aiService.getMoodResponse(
                moodEntry.getMood(),
                moodEntry.getNotes()
        );

        // Return both mood entry and AI response
        return Map.of(
                "moodEntry", saved,
                "aiResponse", aiResponse
        );
    }
    //get all moods for a specific user
    @GetMapping("/user/{userId}")
    public List<MoodEntry>getUserMoods(@PathVariable Long userId){
        return moodEntryRepository.findByUserId(userId);
    }
}
