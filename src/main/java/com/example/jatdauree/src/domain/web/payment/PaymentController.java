package com.example.jatdauree.src.domain.web.payment;

import com.example.jatdauree.config.BaseException;
import com.example.jatdauree.config.BaseResponse;
import com.example.jatdauree.src.domain.web.payment.dto.DeleteBillingReq;
import com.example.jatdauree.src.domain.web.payment.dto.DeleteBillingRes;
import com.example.jatdauree.src.domain.web.payment.dto.IssueBillingReq;
import com.example.jatdauree.src.domain.web.payment.dto.IssueBillingRes;
import com.example.jatdauree.utils.jwt.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/jat/bill")
public class PaymentController {


    @Autowired
    private final PaymentService paymentService;
    @Autowired
    private final JwtService jwtService;

    public PaymentController(PaymentService paymentService, JwtService jwtService) {
        this.paymentService = paymentService;
        this.jwtService = jwtService;
    }

    @ResponseBody
    @PostMapping("/issue-billing")
    public BaseResponse<IssueBillingRes> issueBilling(@RequestBody IssueBillingReq issueBillingReq){
        try{
            //int customerIdx = jwtService.getUserIdx();
            IssueBillingRes issueBillingRes = paymentService.issueBilling(issueBillingReq);
            return new BaseResponse<>(issueBillingRes);
        }catch (BaseException baseException){
            return new BaseResponse<>(baseException.getStatus());
        }
    }

    @ResponseBody
    @DeleteMapping("/issue-billing")
    public BaseResponse<DeleteBillingRes> deleteBilling(@RequestBody DeleteBillingReq deleteBillingReq){
        try{
            DeleteBillingRes deleteBillingRes = paymentService.deleteBilling(deleteBillingReq);
            return new BaseResponse<>(deleteBillingRes);
        }catch (BaseException baseException){
            return new BaseResponse<>(baseException.getStatus());
        }
    }


}
