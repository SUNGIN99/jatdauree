package com.example.jatdauree.src.domain.seller.service;

import com.example.jatdauree.config.BaseException;
import com.example.jatdauree.config.secret.SmsSecret;
import com.example.jatdauree.src.domain.seller.dao.SellerDao;
import com.example.jatdauree.src.domain.seller.dto.*;
import com.example.jatdauree.src.domain.sms.dao.SmsDao;
import com.example.jatdauree.utils.jwt.JwtTokenProvider;
import com.example.jatdauree.utils.SHA256;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import static com.example.jatdauree.config.BaseResponseStatus.*;
import static com.example.jatdauree.utils.ValidationRegex.*;

@Slf4j
@Service
public class SellerService {


    private final SellerDao sellerDao;
    private final SmsDao smsDao;

    private final JwtTokenProvider jwtTokenProvider;
    private final DefaultMessageService messageService;

    @Autowired
    public SellerService(SellerDao sellerDao, SmsDao smsDao, @Lazy JwtTokenProvider jwtTokenProvider) {
        this.sellerDao = sellerDao;
        this.smsDao = smsDao;
        this.jwtTokenProvider = jwtTokenProvider;
        this.messageService = NurigoApp.INSTANCE.initialize(SmsSecret.APIKey, SmsSecret.Secret, "https://api.coolsms.co.kr");
    }

    public UserDetails loadUserByUserIdx(Long userId) {
        return sellerDao.loadUserByUserIdx(userId);
    }

    @Transactional
    public PostSignUpRes signUp(PostSignUpReq postSignUpReq) throws BaseException {
        String salt;
        // 아이디, 비밀번호, 닉네임 정규식 처리
        if(postSignUpReq.getUid().length() == 0 || postSignUpReq.getPassword().length() == 0 || postSignUpReq.getName().length() == 0){
            throw new BaseException(REQUEST_ERROR); // 2000 : 입력값 전체 빈 값일때
        }
        if(!isRegexUid(postSignUpReq.getUid())){
            throw new BaseException(POST_USERS_INVALID_UID); // 2010 : 아이디 정규 표현식 예외
        }
        if(!isRegexPassword(postSignUpReq.getPassword())){
            throw new BaseException(POST_USERS_INVALID_PASSWORD); // 2011 : 비밀번호 정규 표현식 예외
        }
        // 중복 아이디 체크
        if(sellerDao.checkUid(postSignUpReq.getUid()) == 1){
            throw new BaseException(POST_USERS_EXISTS_ID); // 2018 : 중복 아이디
        }
        // 비밀번호 암호화
        try{
            salt = SHA256.createSalt(postSignUpReq.getPassword()); // 비밀번호를 이용하여 salt 생성
            String pwd = new SHA256().encrypt(postSignUpReq.getPassword(), salt); // 비밀번호 암호화
            postSignUpReq.setPassword(pwd);
        } catch (Exception ignored) {
            throw new BaseException(PASSWORD_ENCRYPTION_ERROR); // 4011 : 비밀번호 암호화에 실패하였습니다
        }
        try{
            // 유저 고유식별번호
            int sellerIdx = sellerDao.signUp(postSignUpReq, salt);

            PostSignUpRes postSignUpres = sellerDao.signUpComplete(sellerIdx);
            postSignUpres.setCompleteDate(postSignUpres.getCompleteDate().substring(0,10));

            return postSignUpres;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    @Transactional
    public PostLoginRes login(PostLoginReq postLoginReq) throws BaseException{
        if(postLoginReq.getUid().length() == 0 || postLoginReq.getPassword().length() == 0){
            throw new BaseException(REQUEST_ERROR); // 2000 : 입력값 전체 빈 값일때
        }

        // 1) 아이디가 존재하는지 확인, 회원정보 우선 조회
        Seller seller;
        try{
            seller = sellerDao.login(postLoginReq);
        }catch(Exception exception){
            throw new BaseException(FAILED_TO_LOGIN);
        }

        // 2) 회원가입후 가게 승인 및 메뉴등록이 모두 마쳐진 유저 일 경우우
       String storeName = "";
        try{
            if(seller.getFirst_login() == 0 && seller.getMenu_register()== 0){
                storeName = "가게이름가져오기미완성";
                // storeName from menuDao
            }
        }catch(Exception exception){
            throw new BaseException(FAILED_TO_LOGIN);
        }

        // 3) 비밀 번호 암호화
        try{
            String salt = seller.getSalt();
            String pwd = new SHA256().encrypt(postLoginReq.getPassword(), salt);
            //System.out.println("{SellerService.Class} pwd : " + pwd);
            if (postLoginReq.getUid().equals(seller.getUid()) && pwd.equals(seller.getPassword())){
                String jwt = jwtTokenProvider.createJwt(seller.getSellerIdx());
                return new PostLoginRes(jwt,
                        seller.getSellerIdx(),
                        seller.getName(),
                        seller.getFirst_login(),
                        seller.getMenu_register(),
                        seller.getFirst_login() == 0 && seller.getMenu_register()== 0 ? storeName: "");
            }
            else{
                throw new BaseException(FAILED_TO_LOGIN);
            }
        }catch(Exception exception){
            throw new BaseException(FAILED_TO_LOGIN);
        }
    }


    public IdDuplicateRes checkUid(IdDuplicateReq idDuplicateReq) throws BaseException{
        try {
            return new IdDuplicateRes(sellerDao.checkUid(idDuplicateReq.getUid()));
        }catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    @Transactional
    public int lostIdAndPw(SmsCertificateReq smsCertificateReq) throws BaseException {
        // 1) 올바른 바디 value로 요청되지 않았을때
        if(smsCertificateReq.getPhoneNum() == null &&
                (smsCertificateReq.getName() == null|| smsCertificateReq.getUid() == null)){
            throw new BaseException(REQUEST_ERROR); // 2000 : 입력값을 확인해주세요
        }

        // 2) 인증 타입 설정
        String findType;
        if (smsCertificateReq.getName() != null && smsCertificateReq.getUid() == null){
            findType = "I"; // ID 찾기는 이름 != null && 아이디 == null
        }
        else if (smsCertificateReq.getName() == null && smsCertificateReq.getUid() != null){
            findType = "P"; // PW 찾기는 아이디 != null && 이름 == null
        }
        else{
            throw new BaseException(REQUEST_ERROR); // 2000 : 입력값을 확인해주세요
        }

        // 3) 가입하지 않은 회원일때
        int sellerValidCheck = sellerDao.validLoginInfo(smsCertificateReq, findType);
        if (sellerValidCheck == 0){
            throw new BaseException(POST_USERS_NOT_FOUND); // 2021 : 가입되지 않은 회원입니다.
        }

        // 4) 랜덤 인증번호 생성 (번호)
        Random rand  = new Random();
        String certificationNum = "";
        for(int i=0; i<6; i++) {
            String ran = Integer.toString(rand.nextInt(10));
            certificationNum+=ran;
        }

        // 5) cool SMS인증번호 발송
        try{
            // 인증 메시지 생성
            Message message = new Message();
            message.setFrom("01043753181");
            message.setTo(smsCertificateReq.getPhoneNum());
            message.setText((findType == "I"? "Jatteoli 아이디 찾기\n인증번호" : "Jatteoli 비밀번호 찾기\n인증번호") + " : ["+certificationNum+"]");

            // coolSMS API 사용하여 사용자 핸드폰에 전송
            SingleMessageSentResponse response = this.messageService.sendOne(new SingleMessageSendingRequest(message));
            log.info("coolSMS API요청 :{}", response);

            // DB에 전송 인증정보 저장
            int smsSendRes = smsDao.smsSend(smsCertificateReq, certificationNum, findType);

            return smsSendRes;
        }catch(Exception exception){
            throw new BaseException(COOLSMS_API_ERROR); // 5010 : SMS 인증번호 발송을 실패하였습니다.
        }
    }

    public List<ReceivedNumConfRes> idFind(ReceivedNumConfReq receivedNumConfReq) throws BaseException{
        try{
            int smsIdx = smsDao.smsCheckId(receivedNumConfReq);

            if (smsIdx == 1){
                return sellerDao.idFind(receivedNumConfReq);
            }
            else{
                throw new BaseException(SMS_CERTIFICATE_FAILED); // 4022 : SMS 인증 실패
            }

        }catch(Exception exception){
            throw new BaseException(SMS_DATA_FIND_ERROR); // 4021 : 유효하지 않은 SMS 인증번호 요청입니다.
        }
    }

    public ReceivedNumConfPwRes pwFind(ReceivedNumConfReq receivedNumConfReq) throws BaseException{
        try{
            int smsIdx = smsDao.smsCheckPw(receivedNumConfReq);

            if (smsIdx == 1){
                int sellerIdx = sellerDao.JwtForRestorePw(receivedNumConfReq);
                String jwt = jwtTokenProvider.createJwt(sellerIdx);
                return new ReceivedNumConfPwRes(jwt, receivedNumConfReq.getUid(), 1);
            }
            else{
                throw new BaseException(SMS_CERTIFICATE_FAILED); // 4021 : 비밀번호 암호화에 실패하였습니다
            }
        }catch(Exception exception){
            throw new BaseException(SMS_DATA_FIND_ERROR); // 4022 : SMS 인증 실패
        }
    }

    public RestorePwRes pwRestore(RestorePwReq restorePwReq, int sellerIdx) throws BaseException{
        if (!restorePwReq.getPw().equals(restorePwReq.getPwCheck())){
            throw new BaseException(MODIFY_FAIL_USERPASSWORD); // 4015 : 유저 비밀번호 수정 실패
        }

        String pwd, salt;
        try{
            salt = SHA256.createSalt(restorePwReq.getPw()); // 비밀번호를 이용하여 salt 생성
            pwd = new SHA256().encrypt(restorePwReq.getPw(), salt); // 비밀번호 암호화
        }catch(Exception exception){
            throw new BaseException(PASSWORD_ENCRYPTION_ERROR); // 4011 : 비밀번호 암호화에 실패하였습니다.
        }

        try{
            sellerDao.pwRestore(sellerIdx, salt, pwd);
            return new RestorePwRes(0,1);
        }catch(Exception exception){
            throw new BaseException(MODIFY_FAIL_USERPASSWORD); // 4015 : 유저 비밀번호 수정 실패
        }
    }
}
