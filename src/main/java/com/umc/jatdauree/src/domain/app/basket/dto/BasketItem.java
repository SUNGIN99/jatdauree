package com.umc.jatdauree.src.domain.app.basket.dto;

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
    private int basketIdx;
    private String menuUrl;
    private String menuName;
    private int count;
    private int price;
    private int discount;
    private int todayPrice;

}
