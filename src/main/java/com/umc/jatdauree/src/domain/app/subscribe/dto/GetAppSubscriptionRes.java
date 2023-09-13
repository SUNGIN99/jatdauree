package com.umc.jatdauree.src.domain.app.subscribe.dto;
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
    private String storeName;
    private String storeLogoUrl;
    private String storeSignUrl;
    private double x;
    private double y;
    private int distance;
    private int duration;
    private double starAvg;
    private int subscribed;
}
