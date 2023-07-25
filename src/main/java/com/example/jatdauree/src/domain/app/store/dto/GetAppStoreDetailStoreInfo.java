package com.example.jatdauree.src.domain.app.store.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;




@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetAppStoreDetailStoreInfo {//가게상세-정보에서 1.가게정보 반환값

    private String storeName;
    private String storeOpen;
    private String storeClose;
    private String breakday;
    private String storePhone;

}
