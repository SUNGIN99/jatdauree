package com.example.jatdauree.src.domain.app.customer.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Customer {
    private int customerIdx;
    private String name;
    private String birthday;
    private String phone;
    private String uid;
    private String salt;
    private String password;
    private String email;
}
