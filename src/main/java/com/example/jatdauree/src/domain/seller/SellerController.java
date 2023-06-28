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

@Slf4j
@RestController
@RequestMapping("/jat/users")
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
     * Post /jat/users
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
     * Post /jat/users/login
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

    @GetMapping("/tests")
    public int authenticationTest() throws BaseException {
        int userId = jwtService.getUserIdx();
        log.info("userIdx = {} " , userId);
        return userId;

    }
}
