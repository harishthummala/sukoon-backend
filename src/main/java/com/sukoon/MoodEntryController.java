package com.sukoon;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



@RestController
@RequestMapping("/api/mood")
public class MoodEntryController {

    @Autowired
    private MoodEntryRepository moodEntryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatRepository chatRepository;

    // Log mood
    @PostMapping("/{chatId}/logmood")
    public MoodEntry logMood(@PathVariable Long chatId, @RequestBody MoodEntry moodEntry) {
        Chat chat = chatRepository.findById(chatId).orElse(null);
        moodEntry.setChat(chat);
        return moodEntryRepository.save(moodEntry);
    }

    //view mood
    @GetMapping("{chatId}/viewmood")
    public List<MoodEntry> viewMood(@PathVariable Long chatId){
        return moodEntryRepository.findByChatId(chatId);
    }
}
