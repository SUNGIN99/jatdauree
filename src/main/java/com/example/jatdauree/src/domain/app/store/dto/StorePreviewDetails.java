package com.example.jatdauree.src.domain.app.store.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StorePreviewDetails {
    private int storeIdx;
    private String storeName;
    private String storeLogoUrl;
    private String storeSignUrl;
    private double x,y;
}
