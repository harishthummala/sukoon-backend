package com.sukoon;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/journal")
public class JournalEntryController {

    @Autowired
    private JournalEntryRepository journalEntryRepository;

    @Autowired
    private UserRepository userRepository;


    //Enter new Journal
    @PostMapping("/add/{userId}")
    public JournalEntry addJournal(@PathVariable Long userId,
                                   @RequestBody JournalEntry journalEntry) {
        // Find user
        User user = userRepository.findById(userId).orElse(null);
        journalEntry.setUser(user);
        return journalEntryRepository.save(journalEntry);
    }
    //get all journals for a specific user
    @GetMapping("/all/{userId}")
    public List<JournalEntry>getAllJournals(@PathVariable Long userId){
        return journalEntryRepository.findByUserId(userId);
    }

    //delete journals by id
    @DeleteMapping("/delete/{id}")
    public String deleteJournalById(@PathVariable Long id){
        journalEntryRepository.deleteById(id);
        return "Journal Deleted";
    }

}

