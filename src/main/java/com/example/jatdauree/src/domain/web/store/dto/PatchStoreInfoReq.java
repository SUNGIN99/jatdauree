package com.example.jatdauree.src.domain.web.store.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PatchStoreInfoReq {
    private String storeName;
    private String businessPhone;
    private String businessEmail;
    private String breakDay;
    private String storeOpen;
    private String storeClose;
    private String storePhone;
    private MultipartFile storeLogoUrl;
    private MultipartFile signUrl;
}
