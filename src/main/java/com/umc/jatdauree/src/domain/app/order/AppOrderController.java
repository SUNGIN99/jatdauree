package com.umc.jatdauree.src.domain.app.order;

import com.umc.jatdauree.config.BaseException;
import com.umc.jatdauree.config.BaseResponse;
import com.umc.jatdauree.src.domain.app.order.dto.OrderDeleteReq;
import com.umc.jatdauree.src.domain.app.order.dto.OrderDeleted;
import com.umc.jatdauree.src.domain.app.order.dto.OrderDetailRes;
import com.umc.jatdauree.src.domain.app.order.service.AppOrderService;
import com.umc.jatdauree.src.domain.app.order.dto.GetOrderListRes;
import com.umc.jatdauree.utils.jwt.JwtService;
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

    @ResponseBody
    @GetMapping("/details")
    BaseResponse<OrderDetailRes> getOrderDetail(@RequestParam("orderIdx") int orderIdx){
        try{
            int customerIdx = jwtService.getUserIdx();
            OrderDetailRes orderDetail = appOrderService.getOrderDetail(orderIdx);

            return new BaseResponse<>(orderDetail);
        }catch (BaseException e){
            return new BaseResponse(e.getStatus());
        }
    }

    @ResponseBody
    @PatchMapping("")
    BaseResponse<OrderDeleted> orderDelete(@RequestBody OrderDeleteReq delReq){
        try{
            int customerIdx = jwtService.getUserIdx();
            OrderDeleted orderList = appOrderService.orderDelete(delReq);

            return new BaseResponse<>(orderList);
        }catch (BaseException e){
            return new BaseResponse(e.getStatus());
        }
    }


}
