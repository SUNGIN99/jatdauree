package com.example.jatdauree.src.domain.todaymenu.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostTodayMenuRegRes {
    private Long todaymenuIdx;
    private String date;
    private Long storeIdx;
    private Long menuIdx;
    private int price;
    private int discount;
    private int remain;
    private String status;
    
}
