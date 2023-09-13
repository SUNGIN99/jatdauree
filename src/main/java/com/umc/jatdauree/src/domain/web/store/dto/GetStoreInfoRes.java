package com.umc.jatdauree.src.domain.web.store.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetStoreInfoRes {
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
