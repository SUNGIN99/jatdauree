package com.example.jatdauree.src.domain.app.customer;

import com.example.jatdauree.config.BaseException;
import com.example.jatdauree.config.BaseResponse;
import com.example.jatdauree.src.domain.app.customer.dto.*;
import com.example.jatdauree.src.domain.app.customer.service.CustomerService;
import com.example.jatdauree.src.domain.web.seller.dto.PostLoginReq;
import com.example.jatdauree.src.domain.web.seller.dto.PostSignUpAuthyReq;
import com.example.jatdauree.src.domain.web.seller.dto.PostSignUpAuthyRes;
import com.example.jatdauree.src.domain.web.seller.dto.SmsCertificateRes;
import com.example.jatdauree.utils.jwt.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.jatdauree.config.BaseResponseStatus.SMS_DATA_FIND_ERROR;

@Slf4j
@RestController
@RequestMapping("/jat/app/users")
public class CustomerController {
    @Autowired
    private final CustomerService customerService;
    @Autowired
    private final JwtService jwtService;
    @Autowired
    public CustomerController(CustomerService customerService, JwtService jwtService) {
        this.customerService = customerService;
        this.jwtService = jwtService;
    }

    /**
     * 23.07.20 작성자 : 정주현
     * 회원가입 요청 후, 판매자 회원가입 정보 반환
     * Post /jat/app/users
     * @param @RequestBody postSigunUpReq
     * @return BaseResponse<PostSignUpRes>
     */
    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostSignUpRes> signUp(@RequestBody PostSignUpReq postSigunUpReq){
        try{
            PostSignUpRes postSignUpRes = customerService.signUp(postSigunUpReq);
            return new BaseResponse<>(postSignUpRes);
        }catch (BaseException baseException){
            return new BaseResponse<>(baseException.getStatus());
        }
    }

    @ResponseBody
    @PostMapping("/authy")
    public BaseResponse<PostSignUpAuthyRes> userAuthy(@RequestBody PostSignUpAuthyReq signUpAuthy){
        try{
            PostSignUpAuthyRes signUpAuthyRes = customerService.userAuthy(signUpAuthy);
            return new BaseResponse<>(signUpAuthyRes);
        }catch (BaseException baseException){
            return new BaseResponse<>(baseException.getStatus());
        }
    }

    @ResponseBody
    @PostMapping("/authy-pass")
    public BaseResponse<PostSignUpAuthyRes> userAuthyPass(@RequestBody PostSignUpAuthyReq passReq){
        try{
            PostSignUpAuthyRes passRes = customerService.userAuthyPass(passReq);

            if (passRes.getValidIdentify() == 1){
                return new BaseResponse<>(passRes);
            }else {
                return new BaseResponse<>( SMS_DATA_FIND_ERROR, passRes);
            }
        }catch (BaseException baseException){
            return new BaseResponse<>(baseException.getStatus());
        }
    }


    @ResponseBody
    @PostMapping("/login")
    public BaseResponse<PostLoginRes> login(@RequestBody PostLoginReq postLoginReq) {
        try{
            PostLoginRes user = customerService.login(postLoginReq);
            return new BaseResponse<>(user);
        }catch(BaseException baseException){
            return new BaseResponse<>(baseException.getStatus());
        }
    }

    @ResponseBody
    @PostMapping("/uid-lost")
    public BaseResponse<SmsCertificateRes> lostId(@RequestBody SmsCertificateIdReq smsCertificateReq){
        try{
            SmsCertificateRes certificateRes = new SmsCertificateRes(customerService.lostIdAndPw(smsCertificateReq, null));
            return new BaseResponse<>(certificateRes);
        }catch (BaseException baseException){
            return new BaseResponse<>(baseException.getStatus());
        }
    }

    @ResponseBody
    @PostMapping("/pw-lost")
    public BaseResponse<SmsCertificateRes> lostPw(@RequestBody SmsCertificatePwReq smsCertificateReq){
        try{
            SmsCertificateRes certificateRes = new SmsCertificateRes(customerService.lostIdAndPw(null, smsCertificateReq));
            return new BaseResponse<>(certificateRes);
        }catch (BaseException baseException){
            return new BaseResponse<>(baseException.getStatus());
        }
    }


    @ResponseBody
    @PostMapping("/uid-recovery")
    public BaseResponse<List<UidRecovRes>> idFind(@RequestBody UidRecovReq receivedNumConfReq){
        try{
            List<UidRecovRes> receivedNumConfRes = customerService.idFind(receivedNumConfReq);
            return new BaseResponse<>(receivedNumConfRes);
        }catch (BaseException baseException){
            return new BaseResponse<>(baseException.getStatus());
        }
    }

/*
    @ResponseBody
    @PostMapping("/pw-find")
    public BaseResponse<ReceivedNumConfPwRes> pwFind(@RequestBody ReceivedNumConfReq receivedNumConfReq){
        try{
            ReceivedNumConfPwRes receivedNumConfPwRes = customerService.pwFind(receivedNumConfReq);
            return new BaseResponse<>(receivedNumConfPwRes);
        }catch (BaseException baseException){
            return new BaseResponse<>(baseException.getStatus());
        }
    }


    @ResponseBody
    @PatchMapping("/pw-restore")
    public BaseResponse<RestorePwRes> pwRestore(@RequestBody RestorePwReq restorePwReq){
        try{
            int sellerIdx = jwtService.getUserIdx();
            RestorePwRes restorePwRes = customerService.pwRestore(restorePwReq, sellerIdx);
            return new BaseResponse<>(restorePwRes);
        }catch (BaseException baseException){
            return new BaseResponse<>(baseException.getStatus());
        }
    }*/
}
