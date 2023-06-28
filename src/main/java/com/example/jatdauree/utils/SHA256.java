package com.example.jatdauree.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class SHA256 {
    public SHA256() {
    }

    public static String createSalt(String plainText) throws NoSuchAlgorithmException {
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        byte [] bytes = new byte[16];
        random.nextBytes(bytes);

        //SALT 생성
        String salt = new String(Base64.getEncoder().encode(bytes));

        //System.out.println("{SHA256.Class} salt : " + salt);
        return salt;
    }

    public static String encrypt(String plainText, String salt) {
        try {
            // 1) 해시값 계산 MessageDigest , SHA 256 알고리즘 사용
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            String rawAndSalt = plainText + salt;
            //System.out.println("{SHA256.Class} rawAndSalt : " + rawAndSalt);

            // 2) MessageDigest.update() : 메소드 호출할 때 마다 객체 내에 저장된 digest 값이 계속해서 갱신
            md.update(rawAndSalt.getBytes());

            // 3) digest()메서드를 호출하여 객체내 digest 값 반환/ 바이트배열로 해쉬를 반환함, 패딩 등 최종 처리를하여 해시계산 완료
            byte[] byteData = md.digest(); // 1byte = 8bit

            // hash 값이 음수일 경우 양수로 변환하기위한 작업
            // 1byte = 8bit, 10진수 표현범위 = -128~127, 1111111(2진수) = -1(10진수) 2^7자리 비트는 양(0)/음수(1) 판별
            // 0xFF = 255(10진수), Int형 정수 타입으로 255를 표기하면 0000 0000 0000 0000 0000 0000 1111 1111
            // 그래서 바이트 데이터와 AND연산 처리 시에 10진수 형태 (0~255)로 바꿀수가 있다.
            StringBuffer hexString = new StringBuffer();
            for(int i = 0; i < byteData.length; ++i) {
                String hex = Integer.toHexString(255 & byteData[i]);
                if (hex.length() == 1) {
                    // 각 byteData당 두 자리 수 16진수로 변환
                    hexString.append('0');
                }

                hexString.append(hex);
            }

            return hexString.toString();

        } catch (Exception var7) {
            var7.printStackTrace();
            throw new RuntimeException();
        }
    }
}
