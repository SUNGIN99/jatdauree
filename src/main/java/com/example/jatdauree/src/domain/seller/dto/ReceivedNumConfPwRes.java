package com.example.jatdauree.src.domain.seller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReceivedNumConfPwRes {
    private String jwt;
    private String uid;
    private int pwRestoreAble;
}
