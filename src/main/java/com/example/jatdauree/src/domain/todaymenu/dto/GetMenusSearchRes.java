package com.example.jatdauree.src.domain.todaymenu.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetMenusSearchRes {

    private int menuIdx;
    private int storeIdx;
    private String menu_name;
    private int price;
    private String status;
}
