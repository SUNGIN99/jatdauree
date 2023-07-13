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
    private int storeIdx;
    private String date;
    private int mainTodayMenuItemCount;
    private int sideTodayMenuItemCount;
}
