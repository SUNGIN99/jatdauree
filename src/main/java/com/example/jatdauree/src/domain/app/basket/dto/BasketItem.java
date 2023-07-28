package com.example.jatdauree.src.domain.app.basket.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BasketItem {
    private int storeIdx;
    private int todaymenuIdx;
    private int menuName;
    private int menuCount;
    private int menuPrice;
    private int menuTotalPrice;

}
