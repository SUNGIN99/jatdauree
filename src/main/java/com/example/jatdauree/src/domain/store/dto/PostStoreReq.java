package com.example.jatdauree.src.domain.store.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class PostStoreReq { //1.클라이언트가 요청을 보낸다 이거 적으라고

    private long sellerIdx;
    private long categoryIdx;
    private String city;
    private String local;
    private String town;
    private String storeName;
    private String businessPhone;
    private String businessEmail;
    private String businessCertificateUrl;
    private String sellerCertificateUrl;
    private String copyAccountUrl;
    private String breakDay;
    private String storeOpen;
    private String storeClose;
    private String storePhone;
    private String storeAddress;
    private String storeLogoUrl;
    private String signUrl;




}