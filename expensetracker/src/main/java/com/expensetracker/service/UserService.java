package com.expensetracker.service;

import com.expensetracker.dto.LoginResponse;
import com.expensetracker.dto.UserDTO;
import org.springframework.stereotype.Service;

public interface UserService {
    boolean registerUser(String email,String password);
    LoginResponse checkLoginStatus(String email,String password);
    String extractSubject(String token);
    LoginResponse refresh(String refreshToken);
}
