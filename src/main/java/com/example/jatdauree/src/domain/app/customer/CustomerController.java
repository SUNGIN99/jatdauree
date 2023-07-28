package com.example.jatdauree.src.domain.app.customer;

import com.example.jatdauree.config.BaseException;
import com.example.jatdauree.config.BaseResponse;
import com.example.jatdauree.src.domain.app.customer.dto.PostSignUpReq;
import com.example.jatdauree.src.domain.app.customer.dto.PostSignUpRes;
import com.example.jatdauree.src.domain.app.customer.service.CustomerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/jat/app/users")
public class CustomerController {

    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
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
}
