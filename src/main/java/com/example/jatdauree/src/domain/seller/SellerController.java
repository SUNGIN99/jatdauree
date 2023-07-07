package com.example.jatdauree.src.domain.seller;


import com.example.jatdauree.config.BaseException;
import com.example.jatdauree.config.BaseResponse;
import com.example.jatdauree.src.domain.seller.dto.*;
import com.example.jatdauree.src.domain.seller.service.SellerService;
import com.example.jatdauree.utils.jwt.JwtService;
import com.example.jatdauree.utils.jwt.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//@CrossOrigin(origins="*")
@Slf4j
@RestController
@RequestMapping("/jat/sellers")
public class SellerController {

    @Autowired
    private final SellerService sellerService;
    @Autowired
    private final JwtTokenProvider jwtTokenProvider;
    @Autowired
    private final JwtService jwtService;

    public SellerController(SellerService sellerService, JwtTokenProvider jwtTokenProvider, JwtService jwtService) {
        this.sellerService = sellerService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.jwtService = jwtService;
    }

    /**
     * 23.06.29 작성자 : 김성인
     * 회원가입 요청 후, 판매자 회원가입 정보 반환
     * Post /jat/sellers
     * @param @RequestBody postSigunUpReq
     * @return BaseResponse<PostSignUpRes>
     */
    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostSignUpRes> signUp(@RequestBody PostSignUpReq postSigunUpReq){
        try{
            PostSignUpRes postSignUpRes = sellerService.signUp(postSigunUpReq);
            return new BaseResponse<>(postSignUpRes);
        }catch (BaseException baseException){
            return new BaseResponse<>(baseException.getStatus());
        }
    }

    /**
     * 23.06.29 작성자 : 김성인
     * 로그인 요청 후 jwt 반환 (sellerIdx, Role(추가예정) 포함)
     * Post /jat/sellers/login
     * @param @RequestBody PostLoginReq
     * @return BaseResponse<PostLoginRes>
     */
    @ResponseBody
    @PostMapping("/login")
    public BaseResponse<PostLoginRes> login(@RequestBody PostLoginReq postLoginReq) {
        try{
            PostLoginRes user = sellerService.login(postLoginReq);
            return new BaseResponse<>(user);
        }catch(BaseException baseException){
            return new BaseResponse<>(baseException.getStatus());
        }
    }

    /**
     * 23.07.02 작성자 : 김성인
     * 아이디 중복확인
     * Post /jat/sellers/id-duplicate
     * @param @RequestBody IdDuplicateReq
     * @return BaseResponse<IdDuplicateRes>
     */
    @ResponseBody
    @PostMapping("/id-duplicate")
    public BaseResponse<IdDuplicateRes> checkUid(@RequestBody IdDuplicateReq idDuplicateReq){
        try{
            IdDuplicateRes idDuplicateRes = sellerService.checkUid(idDuplicateReq);
            return new BaseResponse<>(idDuplicateRes);
        }catch (BaseException baseException){
            return new BaseResponse<>(baseException.getStatus());
        }
    }

    /**
     * 23.07.02 작성자 : 김성인
     * 아이디/비밀번호 찾기, 인증번호 전송 api
     * Post /jat/sellers/id-lost
     * @param @RequestBody SmsCertificateReq
     * @return BaseResponse<SmsCertificateRes>
     */
    @ResponseBody
    @PostMapping("/lost")
    public BaseResponse<SmsCertificateRes> lostIdAndPw(@RequestBody SmsCertificateReq smsCertificateReq){
        try{
            SmsCertificateRes certificateRes = new SmsCertificateRes(sellerService.lostIdAndPw(smsCertificateReq));
            return new BaseResponse<>(certificateRes);
        }catch (BaseException baseException){
            return new BaseResponse<>(baseException.getStatus());
        }
    }

    /**
     * 23.07.02 작성자 : 김성인
     * 인증번호 확인, 유저가 가입한 모든 ID정보 리스트 반환
     * Post /jat/sellers/id-find
     * @param @RequestBody ReceivedNumConfReq
     * @return BaseResponse<List<ReceivedNumConfRes>>
     */
    @ResponseBody
    @PostMapping("/id-find")
    public BaseResponse<List<ReceivedNumConfRes>> idFind(@RequestBody ReceivedNumConfReq receivedNumConfReq){
        try{
            List<ReceivedNumConfRes> receivedNumConfRes = sellerService.idFind(receivedNumConfReq);
            return new BaseResponse<>(receivedNumConfRes);
        }catch (BaseException baseException){
            return new BaseResponse<>(baseException.getStatus());
        }
    }

    /**
     * 23.07.02 작성자 : 김성인
     * 인증번호 확인, 가입한 ID와 비밀번호 재설정이 가능한지 반환
     * Post /jat/sellers/pw-find
     * @param @RequestBody ReceivedNumConfReq
     * @return BaseResponse<List<ReceivedNumConfPwRes>>
     */
    @ResponseBody
    @PostMapping("/pw-find")
    public BaseResponse<ReceivedNumConfPwRes> pwFind(@RequestBody ReceivedNumConfReq receivedNumConfReq){
        try{
            ReceivedNumConfPwRes receivedNumConfPwRes = sellerService.pwFind(receivedNumConfReq);
            return new BaseResponse<>(receivedNumConfPwRes);
        }catch (BaseException baseException){
            return new BaseResponse<>(baseException.getStatus());
        }
    }

    /**
     * 23.07.02 작성자 : 김성인
     * 비밀번호찾기 -> 인증번호 확인 -> 비밀번호 재설정
     * Post /jat/sellers/pw-restore
     * @param @RequestBody RestorePwReq
     * @return BaseResponse<RestorePwRes>
     */
    @ResponseBody
    @PostMapping("/pw-restore")
    public BaseResponse<RestorePwRes> pwRestore(@RequestBody RestorePwReq restorePwReq){
        try{
            int sellerIdx = jwtService.getUserIdx();
            RestorePwRes restorePwRes = sellerService.pwRestore(restorePwReq, sellerIdx);
            return new BaseResponse<>(restorePwRes);
        }catch (BaseException baseException){
            return new BaseResponse<>(baseException.getStatus());
        }
    }

    @GetMapping("/test")
    public int a(){
        return 123;
    }

}
