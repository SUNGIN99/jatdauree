package com.example.jatdauree.config;

import lombok.Getter;

/**
 * 에러 코드 관리
 */
@Getter
public enum BaseResponseStatus {
    /**
     * 1000 : 요청 성공
     */
    SUCCESS(true, 1000, "요청에 성공하였습니다."),

    /**
     * 2000 : Request 오류
     */
    // Common
    REQUEST_ERROR(false, 2000, "입력값을 확인해주세요."),
    EMPTY_JWT(false, 2001, "JWT를 입력해주세요."),
    INVALID_JWT(false, 2002, "유효하지 않은 JWT입니다."),
    INVALID_USER_JWT(false,2003,"권한이 없는 유저의 접근입니다."),
    POST_USERS_INVALID_UID(false, 2010, "아이디를 다시 확인해주세요."),
    POST_USERS_INVALID_PASSWORD(false, 2011, "비밀번호를 다시 확인해주세요."),
    POST_USERS_INVALID_NICK_NAME(false, 2012, "닉네임을 다시 확인해주세요."),
    // users
    USERS_EMPTY_USER_ID(false, 2010, "유저 아이디 값을 확인해주세요."),

    POST_USERS_INVALID_BIRTHDAY(false, 2013, "생년월일 양식을 yyyy.MM.dd로 설정해주세요."),
    POST_USERS_INVALID_PHONENUM(false, 2014, "전화번호는 - 없이 숫자만 입력해주세요."),
    POST_USERS_INVALID_AUTHYNUM(false, 2015, "유효하지 않은 인증번호입니다."),

    // 회원가입
    // [POST] /users
    POST_USERS_ALREADY_EXISTS(false, 2016, "이미 가입한 회원입니다."),
    POST_USERS_EXISTS_EMAIL(false,2017,"중복된 이메일입니다."),
    // 회원가입 > 중복아이디 체크
    POST_USERS_EXISTS_ID(false,2018,"중복된 아이디입니다."),
    // 회원가입 > 중복닉네임 체크
    POST_USERS_EXISTS_NICK_NAME(false,2019,"중복된 닉네임입니다."),
    POST_USERS_NOT_FOUND(false,2021,"가입하지 않은 회원입니다."),
    // 시험과제게시판 등록
    POST_EXAM_SUB_TITLE_ISEMPTY(false,2022,"제목을 등록해주세요."),
    // 시험과제게시판 수정
    PATCH_EXAM_SUB_TITLE_ISEMPTY(false,2023,"제목을 등록해주세요."),

    // 2030~) 가게관련 및 메인메뉴/원산지 등록
    POST_STORES_NOT_REGISTERD(false, 2030, "사용자의 가게가 등록되어있지 않습니다."),
    STORE_MAINMENU_DATA_UNVALID(false, 2031, "메인 메뉴 등록 정보가 올바르지 않습니다."),
    STORE_SIDEMENU_DATA_UNVALID(false, 2032, "사이드 메뉴 등록 정보가 올바르지 않습니다."),
    STORE_INGREDIENT_DATA_UNVALID(false, 2033, "원산지 등록 정보가 올바르지 않습니다."),
    STORE_ALREADY_REGISTERD(false, 2034, "가게 등록 요청을 이미 하였습니다(메뉴 등록 또는 관리자의 승인을 기다리세요)."),

    // 2040~) 오늘의 떨이메뉴 등록 요청
    POST_TODAY_MAINMENU_DATA_UNVALID(false, 2041, "오늘의 떨이메뉴(메인) 등록 정보가 올바르지 않습니다."),
    POST_TODAY_SIDEMENU_DATA_UNVALID(false, 2042, "오늘의 떨이메뉴(사이드) 등록 정보가 올바르지 않습니다."),

    // 리뷰 -> 가게에 해당 리뷰 존재하는지
    POST_REVIEW_EXISTS_REVIEW(false,2050,"가게에 해당 리뷰가 존재하지 않습니다."),
    //2050~) 리뷰 답글 등록
    POST_REVIEW_COMMENT_DATA_UNVALID(false,2051,"리뷰 답글을 작성하지 않았습니다."),

    //2050
    /**
     * 3000 : Response 오류
     */
    // Common
    RESPONSE_ERROR(false, 3000, "값을 불러오는데 실패하였습니다."),

    // [POST] /users
    DUPLICATED_EMAIL(false, 3013, "중복된 이메일입니다."),
    FAILED_TO_LOGIN(false,3014,"없는 아이디거나 비밀번호가 틀렸습니다."),


    /**
     * 4000 : Database, Server 오류
     */
    DATABASE_ERROR(false, 4000, "데이터베이스 연결에 실패하였습니다."),
    SERVER_ERROR(false, 4001, "서버와의 연결에 실패하였습니다."),

    //[PATCH] /users/{userIdx}
    MODIFY_FAIL_USERNAME(false,4014,"유저네임 수정 실패"),
    MODIFY_FAIL_USERPASSWORD(false,4015,"유저 비밀번호 수정 실패"),
    MODIFY_FAIL_USERSTATUS_OFF(false,4016,"회원탈퇴 실패"),
    MODIFY_FAIL_USERSTATUS_ON(false,4017,"로그인 실패"),
    PASSWORD_ENCRYPTION_ERROR(false, 4011, "비밀번호 암호화에 실패하였습니다."),
    PASSWORD_DECRYPTION_ERROR(false, 4012, "비밀번호 복호화에 실패하였습니다."),

    // SMS
    SMS_DATA_SAVE_ERROR(false, 4020, "SMS전송 데이터 저장 실패하였습니다."),
    SMS_DATA_FIND_ERROR(false, 4021, "유효하지 않은 SMS 인증번호 요청입니다."),
    SMS_CERTIFICATE_FAILED(false, 4022, "SMS 인증 실패"),

    // 4030~) 가게 메뉴 등록, 원산지 등록
    SELLER_ALL_REGISTER_COMPLETE_ERROR(false, 4030, "판매자의 최초로그인, 메뉴/원산지 등록이 완료되지 못하였습니다."),
    STORE_MAINMENU_SAVE_ERROR(false, 4031, "메인메뉴 등록에 실패하였습니다."),
    STORE_SIDEMENU_SAVE_ERROR(false, 4032, "사이드메뉴 등록에 실패하였습니다."),
    STORE_INGREDIENT_SAVE_ERROR(false, 4033, "원산지 등록에 실패하였습니다."),
    STORE_MENU_ALREADY_SAVED(false, 4034, "메뉴등록이 이미 이루어진 판매자입니다."),
    STORE_REGISTER_NOT_PERMITTED(false, 4035, "관리자에게 가게 승인되지 않은 판매자 계정입니다."),
    STORE_URL_POST_ERROR(false, 4034, "등록하는 이미지의 형태가 잘못 처리되었습니다."),

    // 4040~) 오늘의 떨이메뉴 등록
    POST_TODAY_MAINMENU_SAVE_ERROR(false, 4041, "오늘의 떨이메뉴(메인) 등록에 실패하였습니다."),
    POST_TODAY_SIDEMENU_SAVE_ERROR(false, 4042, "오늘의 떨이메뉴(사이드) 등록에 실패하였습니다."),


    /**
     * 5000 : 외부 API 오류
     */
    // SMS
    COOLSMS_API_ERROR(false, 5010, "SMS 인증번호 발송을 실패하였습니다."),

    // 결제
    BILLING_API_ERROR(false,5020, "결제 실패입니다."),

    // s3
    S3_ACCESS_API_ERROR(false,5030, "이미지 url 생성에 실패하였습니다.");

    // 6000 : 필요시 만들어서 쓰세요
    private final boolean isSuccess;
    private final int code;
    private final String message;

    private BaseResponseStatus(boolean isSuccess, int code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}
