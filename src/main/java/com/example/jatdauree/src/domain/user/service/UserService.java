package com.example.jatdauree.src.domain.user.service;

import com.example.jatdauree.src.domain.user.dto.UserAuthentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    public UserDetails loadUserByUserIdx(Long userId) {
        return new UserAuthentication(1L, "id", "pw", null);
    }
}
