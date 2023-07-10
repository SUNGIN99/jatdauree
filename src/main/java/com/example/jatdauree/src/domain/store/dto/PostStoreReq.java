package com.example.jatdauree.src.domain.store.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostStoreReq {
    private String storeName; // 기업명(상호명)
    private int categoryIdx;  // 가게업종
    private String city; // 시/도
    private String local; // 군/구
    private String town; // 읍/면/동
    private String businessPhone; // 사업주 휴대번호
    private String businessEmail; // 사업주 이메일
    @Nullable private MultipartFile businessCertificateFile; // 사업자 등록증 이미지 url
    @Nullable private MultipartFile sellerCertificateFile; // 영업자 등록증 이미지 url
    @Nullable private MultipartFile copyAccountFile; // 통장사본 이미지 url
    private String breakDay; // 휴무일
    private String storeOpen; // 운영시작시간
    private String storeClose; // 운영 종료시간
    private String storePhone; //가게 전화번호
    private String storeAddress; // 가게 주소
    @Nullable private MultipartFile storeLogoFile; //가게 로고 이미지 url
    @Nullable private MultipartFile signFile; // 매장간판 이미지 url




}