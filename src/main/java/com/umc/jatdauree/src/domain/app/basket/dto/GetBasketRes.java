package com.umc.jatdauree.src.domain.app.basket.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetBasketRes {
    private int storeIdx;
    private String storeName;
    private String storeUrl;
    private int totalMenuCount;
    private int totalMenuPrice;
    private List<BasketItem> basketItems;
}
