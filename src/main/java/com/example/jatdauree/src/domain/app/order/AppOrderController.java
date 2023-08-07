package com.example.jatdauree.src.domain.app.order;

import com.example.jatdauree.config.BaseException;
import com.example.jatdauree.config.BaseResponse;
import com.example.jatdauree.src.domain.app.order.service.AppOrderService;
import com.example.jatdauree.src.domain.app.order.dto.GetOrderListRes;
import com.example.jatdauree.utils.jwt.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/jat/app/orders")
public class AppOrderController {
    @Autowired
    private final AppOrderService appOrderService;

    @Autowired
    private final JwtService jwtService;

    public AppOrderController(AppOrderService appOrderService, JwtService jwtService) {
        this.appOrderService = appOrderService;
        this.jwtService = jwtService;
    }

    @ResponseBody
    @GetMapping("")
    BaseResponse<List<GetOrderListRes>> getOrderList(){
        try{
            int customerIdx = jwtService.getUserIdx();
            List<GetOrderListRes> orderList = appOrderService.getOrderList(customerIdx);

            return new BaseResponse<>(orderList);
        }catch (BaseException e){
            return new BaseResponse(e.getStatus());
        }
    }


}
