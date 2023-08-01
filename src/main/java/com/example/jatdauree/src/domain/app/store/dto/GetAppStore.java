package com.example.jatdauree.src.domain.app.store.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetAppStore {
    private int storeIdx;
    private String storeLogoUrl;
    private String StoreSignUrl;
    private int categoryIdx;
    private String storeName;
    private double longitude;
    private double latitude;
    private Float starAvg;
}
