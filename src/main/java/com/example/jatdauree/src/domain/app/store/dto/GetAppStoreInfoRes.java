package com.example.jatdauree.src.domain.app.store.dto;
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
    private GetAppStoreInfo storeInfo;
    private double starAvg;
    private Integer subscribeCount;
    //private String ingredientInfo;
    //private List<GetAppStoreDetailIngredientInfo> ingredientInfo;
    private String detailIngredientInfo;
    //여기에 구독 state도 필요할 듯 추가하기


}
