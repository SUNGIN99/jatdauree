package com.umc.jatdauree.src.domain.app.customer.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostSignUpRes {
    private String name;
    private String birthday;
    private String phone;
    private String email;
    private String completeDate;

}
