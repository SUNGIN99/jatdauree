package com.umc.jatdauree.src.domain.web.seller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostSignUpReq {
    private String uid; // 유저아이디
    private String name; // 유저 이름
    private String birthday; // 유저 생일  yyyy.MM.dd
    private String phone; // 유저 전화번호 010xxxxxxxx
    private String password; // 유저 비밀번호
    private String email; //이메일
    private int serviceCheck; // 서비스 이용동의
    private int personalCheck; // 개인정보 이용동의
    private int smsCheck; // sms 이용동의
    private int emailCheck; // 이메일 이용동의
    private int callCheck; // 전하 수신 동의
}
