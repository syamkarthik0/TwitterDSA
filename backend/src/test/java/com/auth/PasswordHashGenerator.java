package com.auth;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHashGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        String password1 = "shashank";
        String password2 = "test123";
        
        System.out.println("Hash for '" + password1 + "': " + encoder.encode(password1));
        System.out.println("Hash for '" + password2 + "': " + encoder.encode(password2));
    }
}
