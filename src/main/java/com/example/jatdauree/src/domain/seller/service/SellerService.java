package com.example.jatdauree.src.domain.seller.service;

import com.example.jatdauree.config.BaseException;
import com.example.jatdauree.src.domain.seller.dao.SellerDao;
import com.example.jatdauree.src.domain.seller.dto.*;
import com.example.jatdauree.utils.jwt.JwtTokenProvider;
import com.example.jatdauree.utils.SHA256;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

import static com.example.jatdauree.config.BaseResponseStatus.*;
import static com.example.jatdauree.utils.ValidationRegex.*;

@Service
public class SellerService {


    private final SellerDao sellerDao;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public SellerService(SellerDao sellerDao, @Lazy JwtTokenProvider jwtTokenProvider) {
        this.sellerDao = sellerDao;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public UserDetails loadUserByUserIdx(Long userId) {
        return new SellerAuthentication(1L, "id", "pw", Collections.singletonList("ROLE"));
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

        // 2) 비밀 번호 암호화
        try{
            String salt = seller.getSalt();
            String pwd = new SHA256().encrypt(postLoginReq.getPassword(), salt);
            System.out.println("{SellerService.Class} pwd : " + pwd);
            if (postLoginReq.getUid().equals(seller.getUid()) && pwd.equals(seller.getPassword())){
                String jwt = jwtTokenProvider.createJwt(seller.getSellerIdx());
                return new PostLoginRes(jwt,
                        seller.getSellerIdx(),
                        seller.getName(),
                        seller.getBirthday(),
                        seller.getPhone(),
                        seller.getEmail(),
                        seller.getFirst_login());
            }
            else{
                throw new BaseException(FAILED_TO_LOGIN);
            }
        }catch(Exception exception){
            throw new BaseException(FAILED_TO_LOGIN);
        }
    }
}
