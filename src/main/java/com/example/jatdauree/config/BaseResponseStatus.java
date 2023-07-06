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

    // 회원가입
    // [POST] /users
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
    // 강의 추가 중복 예외
    POST_COURSE_EXISTS(false,2024,"이미 추가한 강의입니다."),
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


    /**
     * 5000 : 외부 API 오류
     */
    // SMS
    COOLSMS_API_ERROR(false, 5010, "SMS 인증번호 발송을 실패하였습니다."),

    // 결제
    BILLING_API_ERROR(false,5020, "결제 실패입니다.");

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
