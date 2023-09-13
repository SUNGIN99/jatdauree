package com.umc.jatdauree.src.domain.app.basket.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetStoreList {
    private int storeIdx;
    private String storeName;
    private String storeLogoUrl;
    private String storeSignUrl;
    private String storeAddress;
    private double x, y;
    private double star;
    private int customerIdx;
}
