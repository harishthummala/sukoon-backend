package com.sukoon;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/moods")
public class MoodEntryController {

    @Autowired
    private MoodEntryRepository moodEntryRepository;

    @Autowired
    private UserRepository userRepository;

    //Log a new Mood Entry
    @PostMapping("/log/{userId}")
    public MoodEntry logMood(@PathVariable Long userId, @RequestBody MoodEntry moodEntry){
        User user = userRepository.findById(userId).orElse(null);
        if(user == null){
            return null;
        }
        moodEntry.setUser(user);
        return moodEntryRepository.save(moodEntry);
    }

    //get all moods for a specific user
    @GetMapping("/user/{userId}")
    public List<MoodEntry>getUserMoods(@PathVariable Long userId){
        return moodEntryRepository.findByUserId(userId);
    }
}
