package com.example.jatdauree.src.domain.seller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Seller {
    private int sellerIdx;
    private String name;
    private String birthday;
    private String phone;
    private String uid;
    private String salt;
    private String password;
    private String email;
    private int first_login;
    private int menu_register;
    private String store_name;
}
