package com.umc.jatdauree.src.domain.app.store.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetAppStoreInfoRes {

    private int storeIdx;
    private String storeName;
    private int categoryIdx;
    private String storePhone;
    private double x;
    private double y;
    private String storeAddress;
    private int distance;
    private int duration;
    private double starAvg;
    private int subscribeCount;
    private String detailIngredientInfo;
    private int subscribeCheck;


}
