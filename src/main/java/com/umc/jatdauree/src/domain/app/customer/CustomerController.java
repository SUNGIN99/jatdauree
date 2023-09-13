package com.umc.jatdauree.src.domain.app.customer;

import com.umc.jatdauree.config.BaseException;
import com.umc.jatdauree.config.BaseResponse;
import com.umc.jatdauree.config.BaseResponseStatus;
import com.umc.jatdauree.src.domain.app.customer.dto.*;
import com.umc.jatdauree.src.domain.app.customer.dto.PostLoginRes;
import com.umc.jatdauree.src.domain.app.customer.dto.PostSignUpReq;
import com.umc.jatdauree.src.domain.app.customer.dto.PostSignUpRes;
import com.umc.jatdauree.src.domain.app.customer.service.CustomerService;
import com.umc.jatdauree.src.domain.web.seller.dto.*;
import com.umc.jatdauree.src.domain.web.seller.dto.PostSignUpAuthyReq;
import com.umc.jatdauree.utils.jwt.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.umc.jatdauree.config.BaseResponseStatus.SMS_DATA_FIND_ERROR;

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


    @ResponseBody
    @PostMapping("/pw-recovery")
    public BaseResponse<PwRecovRes> pwFind(@RequestBody PwRecovReq receivedNumConfReq){
        try{
            PwRecovRes receivedNumConfPwRes = customerService.pwFind(receivedNumConfReq);
            return new BaseResponse<>(receivedNumConfPwRes);
        }catch (BaseException baseException){
            return new BaseResponse<>(baseException.getStatus());
        }
    }


    @ResponseBody
    @PatchMapping("/pw-restore")
    public BaseResponse<RestorePwRes> pwRestore(@RequestBody RestorePwReq restorePwReq){
        try{
            int customerIdx = jwtService.getUserIdx();
            RestorePwRes restorePwRes = customerService.pwRestore(restorePwReq, customerIdx);
            return new BaseResponse<>(restorePwRes);
        }catch (BaseException baseException){
            return new BaseResponse<>(baseException.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/address")
    public BaseResponse<?> userAddress(
            @RequestParam(value = "location", required = false) String location,
            @RequestParam(value = "longitude", required = false) Double longitude,
            @RequestParam(value = "latitude", required = false) Double latitude){
        try{
            //int customerIdx = jwtService.getUserIdx();

            if (location != null && longitude == null && latitude == null){
                AddressXY addressXY = customerService.userAddress(location);
                return new BaseResponse<>(addressXY);
            }else if (location == null && longitude != null && latitude != null){
                AddressNames addresss = customerService.addrName(longitude, latitude);
                return new BaseResponse<>(addresss);
            }else{
                return new BaseResponse<>(BaseResponseStatus.REQUEST_ERROR);
            }

        }catch (BaseException baseException){
            return new BaseResponse<>(baseException.getStatus());
        }
    }

    /**
     * 23.08.03 작성자 : 정주현
     * 닉네임(이름) 조회
     * Post /jat/app/users/my-tteoli
     *
     * @return BaseResponse<GetNicknameRes>
     */
    @ResponseBody
    @GetMapping("/my-tteoli")
    public BaseResponse<GetNicknameRes> nicknameSearch() {
        try {
            int customerIdx = jwtService.getUserIdx();
            GetNicknameRes getNicknameRes = customerService.nicknameSearch(customerIdx);
            return new BaseResponse<>(getNicknameRes);
        } catch (BaseException baseException) {
            return new BaseResponse<>(baseException.getStatus());
        }
    }

    /**
     * 23.08.03 작성자 : 정주현
     * 내 정보 조회
     * Post /jat/app/users/info
     *
     * @return BaseResponse<GetMyInfoRes>
     */
    @ResponseBody
    @GetMapping("/info")
    public BaseResponse<GetMyInfoRes> myInfoSearch() {
        try {
            int customerIdx = jwtService.getUserIdx();
            GetMyInfoRes getMyInfoRes = customerService.myInfoSearch(customerIdx);
            return new BaseResponse<>(getMyInfoRes);
        } catch (BaseException baseException) {
            return new BaseResponse<>(baseException.getStatus());
        }
    }
}
