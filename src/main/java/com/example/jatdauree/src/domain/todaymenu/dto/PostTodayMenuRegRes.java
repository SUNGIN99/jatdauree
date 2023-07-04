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
    private int todaymenuIdx;
    private int storeIdx;
    private String menu_name;
    private String price;
    private int discount;
    private int remain;
    private int status;
}
