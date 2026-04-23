package com.sukoon;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
@Entity // tells JPA this class this java class represents a database table
@Table(name = "users") // this tells JPA what to name the table in database
public class User {
    @Id // tells JPA this field is primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // to auto increment id from 1to 2 to 3 soon
    private Long id;

    private String name;
    private String email;
    @JsonIgnore
    private String password;

    // Default Constructor - required by JPA
    public User(){}

    //parameterized Constructor
    public User(String name, String email, String password){
        this.name = name;
        this.email = email;
        this.password = password;
    }
    //getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email ;}
    public String getPassword() { return  password; }
    //setter
    public void setName(String name) {this.name = name;}
    public void setEmail(String email) {this.email = email;}
    public void setPassword(String password) {this.password = password;}



}
