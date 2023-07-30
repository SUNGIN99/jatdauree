package com.example.jatdauree.src.domain.app.customer.service;

import com.example.jatdauree.config.BaseException;
import com.example.jatdauree.config.secret.SmsSecret;
import com.example.jatdauree.src.domain.app.customer.dao.CustomerDao;
import com.example.jatdauree.src.domain.app.customer.dto.*;

import com.example.jatdauree.src.domain.app.customer.dto.PostLoginRes;
import com.example.jatdauree.src.domain.app.customer.dto.PostSignUpReq;
import com.example.jatdauree.src.domain.app.customer.dto.PostSignUpRes;
import com.example.jatdauree.src.domain.web.seller.dto.*;

import com.example.jatdauree.src.domain.web.seller.dto.PostSignUpAuthyReq;
import com.example.jatdauree.src.domain.web.sms.dao.SmsDao;
import com.example.jatdauree.utils.SHA256;
import com.example.jatdauree.utils.jwt.JwtTokenProvider;
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

import java.util.List;
import java.util.Random;

import static com.example.jatdauree.config.BaseResponseStatus.*;
import static com.example.jatdauree.config.BaseResponseStatus.POST_USERS_EXISTS_ID;
import static com.example.jatdauree.utils.ValidationRegex.*;
import static com.example.jatdauree.utils.ValidationRegex.isRegexPhone;

@Slf4j
@Service
public class CustomerService {

    private final SmsDao smsDao;
    private final CustomerDao customerDao;
    private final JwtTokenProvider jwtTokenProvider;
    private final DefaultMessageService messageService;
    @Autowired
    public CustomerService(SmsDao smsDao, CustomerDao customerDao, @Lazy JwtTokenProvider jwtTokenProvider) {
        this.smsDao = smsDao;
        this.customerDao = customerDao;
        this.jwtTokenProvider = jwtTokenProvider;
        this.messageService = NurigoApp.INSTANCE.initialize(SmsSecret.APIKey, SmsSecret.Secret, "https://api.coolsms.co.kr");
    }

    public UserDetails loadUserByUserIdx(Long userId) {
        return customerDao.loadUserByUserIdx(userId);
    }

    @Transactional(rollbackFor = BaseException.class)
    public PostSignUpRes signUp(PostSignUpReq postSignUpReq) throws BaseException {

        // 아이디, 비밀번호, 닉네임 정규식 처리
        if (postSignUpReq.getUid().length() == 0 || postSignUpReq.getPassword().length() == 0 || postSignUpReq.getName().length() == 0) {
            throw new BaseException(REQUEST_ERROR); // 2000 : 입력값 전체 빈 값일때 ( 입력값을 확인해주세요.)
        }
        if (!isRegexUid(postSignUpReq.getUid())) {
            throw new BaseException(POST_USERS_INVALID_UID); // 2010 : 아이디 정규 표현식 예외 (아이디를 다시 확인해주세요.)
        }
        if (!isRegexPassword(postSignUpReq.getPassword())) {
            throw new BaseException(POST_USERS_INVALID_PASSWORD); // 2011 : 비밀번호 정규 표현식 예외 (비밀번호를 다시 확인해주세요.)
        }
        // 올바른 생년월일 양식인지?
        if (!isRegexBirth(postSignUpReq.getBirthday())) {
            throw new BaseException(POST_USERS_INVALID_BIRTHDAY); //2013 :(생년월일 양식을 yyyy.MM.dd로 설정해주세요.)
        }
        if (!isRegexPhone(postSignUpReq.getPhone())) {
            throw new BaseException(POST_USERS_INVALID_PHONENUM); // 2014 : (전화번호는 - 없이 숫자만 입력해주세요.)
        }
        // 중복 아이디 체크
        if (customerDao.checkUid(postSignUpReq.getUid()) == 1) {
            throw new BaseException(POST_USERS_EXISTS_ID); // 2018 : 중복 아이디
        }

        // 비밀번호 암호화
        String salt;
        try{
            salt = SHA256.createSalt(postSignUpReq.getPassword()); // 비밀번호를 이용하여 salt 생성
            String pwd = new SHA256().encrypt(postSignUpReq.getPassword(), salt); // 비밀번호 암호화
            postSignUpReq.setPassword(pwd);
        } catch (Exception ignored) {
            throw new BaseException(PASSWORD_ENCRYPTION_ERROR); // 4011 : 비밀번호 암호화에 실패하였습니다
        }

        try {
            // 유저 고유식별번호
            int customerIdx = customerDao.signup(postSignUpReq, salt);

            PostSignUpRes postSignUpres = customerDao.signUpComplete(customerIdx);
            postSignUpres.setCompleteDate(postSignUpres.getCompleteDate().substring(0, 10)); //2020년10월02

            return postSignUpres;

        } catch (Exception e) {
            System.out.println(e);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    @Transactional(rollbackFor = BaseException.class)
    public PostLoginRes login(PostLoginReq postLoginReq) throws BaseException{
        if(postLoginReq.getUid().length() == 0 || postLoginReq.getPassword().length() == 0){
            throw new BaseException(REQUEST_ERROR); // 2000 : 입력값 전체 빈 값일때
        }

        // 1) 아이디가 존재하는지 확인, 회원정보 우선 조회
        Customer customer;
        try{
            customer = customerDao.login(postLoginReq);
        }catch(Exception exception){
            throw new BaseException(FAILED_TO_LOGIN);
        } new BaseException(FAILED_TO_LOGIN);

        // 2) 비밀 번호 암호화
        try{
            String salt = customer.getSalt();
            String pwd = new SHA256().encrypt(postLoginReq.getPassword(), salt);
            //System.out.println("{SellerService.Class} pwd : " + pwd);
            if (postLoginReq.getUid().equals(customer.getUid()) && pwd.equals(customer.getPassword())){
                String jwt = jwtTokenProvider.createJwt(customer.getCustomerIdx(), "Customer");
                return new PostLoginRes(jwt, customer.getName());
            }
            else{
                throw new BaseException(FAILED_TO_LOGIN);
            }
        }catch(Exception exception){
            throw new BaseException(FAILED_TO_LOGIN);
        }
    }

    @Transactional(rollbackFor = BaseException.class)
    public PostSignUpAuthyRes userAuthy(PostSignUpAuthyReq signUpAuthy) throws BaseException{
        // 1) 회원가입 가능한지?? 이미 등록된 회원인지??
        int duplicateUser;
        try{
            duplicateUser = customerDao.userAuthy(signUpAuthy);
        }catch(Exception exception){
            throw new BaseException(MODIFY_FAIL_USERPASSWORD); // 4015 : 유저 비밀번호 수정 실패
        }

        if (duplicateUser == 1){
            try{
                // 4) 랜덤 인증번호 생성 (번호)
                Random rand  = new Random();
                String certificationNum = "";
                for(int i=0; i<6; i++) {
                    String ran = Integer.toString(rand.nextInt(10));
                    certificationNum+=ran;
                }

                // 인증 메시지 생성
                Message message = new Message();
                message.setFrom("01043753181");
                message.setTo(signUpAuthy.getPhoneNum());
                message.setText("회원가입 본인인증 확인입니다.\n["+certificationNum+"]");

                // coolSMS API 사용하여 사용자 핸드폰에 전송
                SingleMessageSentResponse response = this.messageService.sendOne(new SingleMessageSendingRequest(message));
                log.info("coolSMS API요청 :{}", response);

                // DB에 전송 인증정보 저장
                int smsSendRes = smsDao.smsAuthy(signUpAuthy, certificationNum, "S");
                return new PostSignUpAuthyRes(smsSendRes);

            }catch(Exception exception){
                throw new BaseException(COOLSMS_API_ERROR); //  5010 : SMS 인증번호 발송을 실패하였습니다.
            }
        }
        else{
            throw new BaseException(POST_USERS_ALREADY_EXISTS);
        }
    }

    public PostSignUpAuthyRes userAuthyPass(PostSignUpAuthyReq passReq) throws BaseException{
        try{
            int userPass = smsDao.smsAuthyPass(passReq);
            System.out.println(userPass);
            return new PostSignUpAuthyRes(userPass);
        }catch(Exception exception){
            throw new BaseException(COOLSMS_API_ERROR); // 5010 : SMS 인증번호 발송을 실패하였습니다.
        }
    }

    @Transactional(rollbackFor = BaseException.class)
    public int lostIdAndPw(SmsCertificateIdReq smsCertificateIdReq, SmsCertificatePwReq smsCertificatePwReq) throws BaseException{
        String findType;
        SmsCertificateReq smsCertificateReq = new SmsCertificateReq();
        // 1) 올바른 바디 value로 요청되지 않았을때
        if(smsCertificatePwReq == null && smsCertificateIdReq != null){ // 아이디 찾기 일때
            // 폰번호 & 이름이 같이 들어와야함
            if(smsCertificateIdReq.getPhoneNum() == null || smsCertificateIdReq.getName() == null){
                throw new BaseException(REQUEST_ERROR); // 2000 : 입력값을 확인해주세요
            }else{
                smsCertificateReq.setPhoneNum(smsCertificateIdReq.getPhoneNum());
                smsCertificateReq.setName(smsCertificateIdReq.getName());
                findType = "IC";
            }
        }else if (smsCertificateIdReq == null && smsCertificatePwReq != null){ // 비번 찾기 일때
            // 폰번호 & 아이디가 같이 들어와야함
            if(smsCertificatePwReq.getPhoneNum() == null || smsCertificatePwReq.getUid() == null){
                throw new BaseException(REQUEST_ERROR); // 2000 : 입력값을 확인해주세요
            }else{
                smsCertificateReq.setPhoneNum(smsCertificatePwReq.getPhoneNum());
                smsCertificateReq.setUid(smsCertificatePwReq.getUid());
                findType = "PC";
            }
        }else{
            throw new BaseException(REQUEST_ERROR); // 2000 : 입력값을 확인해주세요
        }

        // 2) 가입하지 않은 구매자일때
        int customerValidCheck;
        try {
            customerValidCheck = customerDao.validLoginInfo(smsCertificateReq, findType);
        }catch (Exception e){
            throw new BaseException(DATABASE_ERROR); // 2000 : 입력값을 확인해주세요
        }

        if (customerValidCheck == 0){
            throw new BaseException(POST_USERS_NOT_FOUND); // 2021 : 가입되지 않은 회원입니다.
        }

        // 3) 랜덤 인증번호 생성 (번호)
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
            message.setText((findType == "IC"? "Jatteoli 아이디 찾기\n인증번호" : "Jatteoli 비밀번호 찾기\n인증번호") + " : ["+certificationNum+"]");

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

    public List<UidRecovRes> idFind(UidRecovReq receivedNumConfReq) throws BaseException{
        try{
            int smsIdx = smsDao.smsCheckIdCustom(receivedNumConfReq);

            if (smsIdx == 1){
                return customerDao.idFind(receivedNumConfReq);
            }
            else{
                throw new BaseException(SMS_CERTIFICATE_FAILED); // 4022 : SMS 인증 실패
            }

        }catch(Exception exception){
            throw new BaseException(SMS_DATA_FIND_ERROR); // 4021 : 유효하지 않은 SMS 인증번호 요청입니다.
        }
    }

    public PwRecovRes pwFind(PwRecovReq receivedNumConfReq) throws BaseException{
        try{
            int smsIdx = smsDao.smsCheckPwCustom(receivedNumConfReq);

            if (smsIdx == 1){
                int customerIdx = customerDao.JwtForRestorePw(receivedNumConfReq);
                String jwt = jwtTokenProvider.createJwt(customerIdx, "Customer");
                return new PwRecovRes(jwt, receivedNumConfReq.getUid(), 1);
            }
            else{
                throw new BaseException(SMS_CERTIFICATE_FAILED); // 4021 : 비밀번호 암호화에 실패하였습니다
            }
        }catch(Exception exception){
            throw new BaseException(SMS_DATA_FIND_ERROR); // 4022 : SMS 인증 실패
        }
    }

    @Transactional(rollbackFor = BaseException.class)
    public RestorePwRes pwRestore(RestorePwReq restorePwReq, int customerIdx) throws BaseException{
        if (!restorePwReq.getPw().equals(restorePwReq.getPwCheck())){
            System.out.println(1);
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
            customerDao.pwRestore(customerIdx, salt, pwd);
            return new RestorePwRes(0,1);
        }catch(Exception exception){
            System.out.println(exception);
            throw new BaseException(MODIFY_FAIL_USERPASSWORD); // 4015 : 유저 비밀번호 수정 실패
        }
    }
}
