package com.umc.jatdauree.src.domain.app.basket.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BasketOrderRes {
    private int storeIdx;
    private String storeName;
    private String storeAddress;
    private String storePhone;
    private int orderPrice;
}
