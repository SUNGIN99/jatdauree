package com.example.jatdauree.src.domain.seller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostSignUpRes {
    private String uid;
    private String name;
    private String birthday;
    private String phone;
    private String email;
    private String completeDate;
    private int smsCheck;
    private int emailCheck;
    private int CallCheck;
}
