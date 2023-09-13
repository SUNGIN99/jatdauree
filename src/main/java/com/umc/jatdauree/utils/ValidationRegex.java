package com.umc.jatdauree.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationRegex {
    // 이메일 정규식
    public static boolean isRegexEmail(String target) {
        String regex = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(target);
        return matcher.find();
    }
    // 아이디 정규식 - 시작은 영문으로만, '_'를 제외한 특수문자 안되며 영문, 숫자, '_'으로만 이루어진 4 ~ 20자 이하
    public static boolean isRegexUid(String target) {
        String regex = "^[a-zA-Z]{1}[a-zA-Z0-9_]{3,19}$";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(target);
        return matcher.find();
    }
    // 비밀번호 정규식
    public static boolean isRegexPassword(String target) {
        boolean result;
        //최소 8글자, 최대16글자, 대문자 1개, 소문자 1개, 숫자 1개, 특수문자 1개
        //String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,16}$";

        //최대 30글자, 소문자 1개, 숫자 1개, 특수문자 1개 : 영문 + 숫자 + 특수기호 8 자이상
        String regex1 = "^(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,30}$";

        //최대 30글자 : 영문 + 숫자 10자 이상
        String regex2 = "^(?=.*[a-zA-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{10,30}$";

        Pattern pattern1 = Pattern.compile(regex1, Pattern.CASE_INSENSITIVE);
        Pattern pattern2 = Pattern.compile(regex2, Pattern.CASE_INSENSITIVE);

        Matcher matcher1 = pattern1.matcher(target);
        Matcher matcher2 = pattern1.matcher(target);

        //result=target.matches(regex1) || target.matches(regex2);
        result = matcher1.find() || matcher2.find();
        return result;
    }
    // 닉네임 정규식 - 2자 이상 16자 이하, 영어 또는 숫자 또는 한글로 구성
    public static boolean isRegexNickName(String target) {
        String regex = "^(?=.*[a-z0-9가-힣])[a-z0-9가-힣]{2,10}$"; // * 특이사항 : 한글 초성 및 모음은 허가하지 않는다.
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(target);
        return matcher.find();
    }

    public static boolean isRegexBirth(String target){
        String regex = "^\\d{4}\\.\\d{2}\\.\\d{2}$";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(target);

        return matcher.find();
    }

    public static boolean isRegexPhone(String target){
        String regex = "^\\d{3}\\d{4}\\d{4}$";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(target);

        return matcher.find();
    }
}

