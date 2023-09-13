package com.umc.jatdauree.src.domain.app.customer.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostSignUpReq {

    private String uid;
    private String name;
    private String birthday;
    private String phone;
    private String password;
    private String email;
    private int serviceCheck;
    private int personalCheck;
    private int info_service_check;
    private int smsCheck;
    private int emailCheck;
    private int callCheck;

}
