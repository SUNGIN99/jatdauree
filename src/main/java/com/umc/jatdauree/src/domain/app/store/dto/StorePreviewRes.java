package com.umc.jatdauree.src.domain.app.store.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StorePreviewRes {
    private int storeIdx;
    private String storeName;
    private String storeLogoUrl;
    private String storeSignUrl;
    private double star;
    private int distance;
    private int duration;
    private int subscribed;

}
