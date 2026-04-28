package com.sukoon;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ChatRepository chatRepository;
    @Autowired
    private UserRepository userRepository;


    //new chat
    @PostMapping("/new/{userId}")
    public Chat newChat(@PathVariable Long userId, @RequestBody Chat chat){

        // Find user
        User user = userRepository.findById(userId).orElse(null);
        chat.setUser(user);
        return chatRepository.save(chat);
    }

    //delete chat
    @DeleteMapping("/delete/{id}")
    public String deleteChat(@PathVariable Long id){
        if(chatRepository.findById(id).isEmpty()){
            return "Chat not found";
        }
        chatRepository.deleteById(id);
        return "Chat Deleted";
    }

}
