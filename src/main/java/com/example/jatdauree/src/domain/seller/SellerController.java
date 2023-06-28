package com.example.jatdauree.src.domain.seller;


import com.example.jatdauree.config.BaseException;
import com.example.jatdauree.config.BaseResponse;
import com.example.jatdauree.src.domain.seller.dto.PostLoginRequest;
import com.example.jatdauree.src.domain.seller.dto.PostSignUpRes;
import com.example.jatdauree.src.domain.seller.dto.PostSignUpReq;
import com.example.jatdauree.src.domain.seller.dto.SellerAuthentication;
import com.example.jatdauree.src.domain.seller.service.SellerService;
import com.example.jatdauree.utils.JwtService;
import com.example.jatdauree.utils.JwtTokenProvider;
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
     * 회원가입 요청
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
            System.out.println(baseException);
            return new BaseResponse<>(baseException.getStatus());
        }
    }

    @PostMapping("/login")
    public String login(@RequestBody PostLoginRequest postLoginRequest){
        SellerAuthentication user = (SellerAuthentication) sellerService.loadUserByUserIdx(1L);
        return jwtTokenProvider.createJwt(user.getSellerIdx().intValue());
    }

    @GetMapping("/tests")
    public int authenticationTest() throws BaseException {
        int userId = jwtService.getUserIdx();
        log.info("userIdx = {} " , userId);
        return userId;

    }
}
