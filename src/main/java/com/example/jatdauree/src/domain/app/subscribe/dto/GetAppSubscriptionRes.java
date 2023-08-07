package com.example.jatdauree.src.domain.app.subscribe.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetAppSubscriptionRes {
    private int storeIdx;
    private String storeLogoUrl;
    private String StoreSignUrl;
    private int categoryIdx;
    private String storeName;
    private double x;
    private double y;
    private double starAvg;
    private String subSatus;
}
