package com.example.jatdauree.src.domain.app.store.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetAppStoreDetailInfoRes {// 가게상세 - 정보(1,2,3,4 클래스 다 합친거) 빈환값
    private int storeIdx;
    private GetAppStoreDetailStoreInfo detailStoreInfo;
    private GetAppStoreDetailStatisticsInfo detailStatisticsInfo;
    private GetAppStoreDetailSellerInfo detailSellerInfo;
    private String detailIngredientInfo;//combinedDetailIngredientInfo


}
