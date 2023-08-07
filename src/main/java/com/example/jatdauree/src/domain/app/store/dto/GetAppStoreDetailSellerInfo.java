package com.example.jatdauree.src.domain.app.store.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;




@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetAppStoreDetailSellerInfo { //가게상세 -정보에서 3.사업자정보 반환
    private String sellerName;
    private String storeName;
    private String storeAddress;
    private String businessRegiNum;
}
