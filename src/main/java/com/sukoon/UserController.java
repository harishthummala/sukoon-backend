package com.sukoon;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    //Register new user
    @PostMapping("/register")
    public User registerUser(@RequestBody User user){
        return userRepository.save(user);
    }
    //get all users
    @GetMapping("/all")
    public java.util.List<User> getAllUsers(){
        return userRepository.findAll();
    }
    // specific user by id
    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id){
        return userRepository.findById(id).orElse(null);
    }
    //delete user by id
    @DeleteMapping("/{id}")
    public String deleteUserById(@PathVariable Long id){
        userRepository.deleteById(id);
        return "User Deleted";
    }
}
