package com.example.jatdauree.src.domain.store.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostStoreModifyReq {


    private String storeName;
    private String businessPhone;
    private String businessEmail;
    private String breakDay;
    private String storeOpen;
    private String storeClose;
    private String storePhone;
    private String storeLogoUrl;
    private String signUrl;
}
