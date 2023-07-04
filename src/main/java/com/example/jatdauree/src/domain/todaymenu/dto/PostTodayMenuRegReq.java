package com.example.jatdauree.src.domain.todaymenu.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostTodayMenuRegReq {
    private int menuIdx;
    private int storeIdx;
    private int discount;
    private int remain;
    private String status;
}
