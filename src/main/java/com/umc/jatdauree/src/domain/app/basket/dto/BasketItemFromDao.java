package com.umc.jatdauree.src.domain.app.basket.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BasketItemFromDao {
    private int storeIdx;
    private String storeName;
    private int todaymenuIdx;
    private int baksetIdx;
    private String menuUrl;
    private String menuName;
    private int count;
    private int price;
    private int discount;
    private int todayPrice;
}
