package com.example.jatdauree.src.domain.app.store.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;




@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetAppStoreDetailStoreInfo {

    private String storeName;
    private String storeOpen;
    private String storeClose;
    private String breakday;
    private String storePhone;

}
