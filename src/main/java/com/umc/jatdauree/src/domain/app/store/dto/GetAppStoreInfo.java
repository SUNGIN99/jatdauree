package com.umc.jatdauree.src.domain.app.store.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;




@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetAppStoreInfo {
    private String storeName;
    private String storePhone;
    private int storeCategory;
    private double x;
    private double y;
    private String storeAddress;

}
