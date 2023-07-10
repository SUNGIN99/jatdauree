package com.example.jatdauree.src.domain.order;

import com.example.jatdauree.config.BaseException;
import com.example.jatdauree.config.BaseResponse;
import com.example.jatdauree.src.domain.order.dto.*;
import com.example.jatdauree.src.domain.order.service.OrderService;
import com.example.jatdauree.utils.jwt.JwtService;
import com.example.jatdauree.utils.jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/jat/orders")
public class OrderController {

    @Autowired
    private final OrderService ordersService;
    @Autowired
    private final JwtTokenProvider jwtTokenProvider;
    @Autowired
    private final JwtService jwtService;


    @Autowired
    public OrderController(OrderService ordersService, JwtTokenProvider jwtTokenProvider, JwtService jwtService){
        this.ordersService = ordersService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.jwtService = jwtService;
    }

    /**
     * OrderController-1
     * 23.07.06 작성자 : 윤다은
     * 주문화면 -대기상태
     * GET /jat/orders/wait
     * JWT 적용해서 sellerIdx 가져오기
     */
    @ResponseBody
    @GetMapping("/wait")
    public BaseResponse<List<GetOrderRes>> outputToday(){
        try{
            int sellerIdx = jwtService.getUserIdx();
            List<GetOrderRes> getOrdersRes = ordersService.getOrdersBySellerId(sellerIdx);
            return new BaseResponse<>(getOrdersRes);
        }catch (BaseException baseException){
            return new BaseResponse<>(baseException.getStatus());
        }
    }

    /**
     * OrderController-2
     * 23.07.06 작성자 : 윤다은
     * 주문화면 - 주문취소/처리 중
     * Post /jat/orders/cancel
     * JWT 적용해서 sellerIdx 가져오고 UPDATE를 통해서 status 수정하기
     */
    @ResponseBody
    @PostMapping("/cancel")
    public BaseResponse<String>updateOrder(@RequestBody PostOrderCancelReq postOrderCancelReq){
        try{
            int sellerIdx = jwtService.getUserIdx();
            ordersService.postOrderBySellerId(sellerIdx,postOrderCancelReq);
            return new BaseResponse<>("Order update successfuly.");
        }catch (BaseException baseException){
            return new BaseResponse<>(baseException.getStatus());
        }
    }

    /**
     * OrderController -4
     * 23.07.07 작성자 : 윤다은
     * 주문 처리 중 확인
     * GET /jat/orders/process
     * */
    @ResponseBody
    @GetMapping("/process")
    public BaseResponse<List<GetOrderProRes>> outputProcess(@RequestBody GetOrderReq getOrderReq){
        try{
            int sellerIdx = jwtService.getUserIdx();
            List<GetOrderProRes> getOrderProRes = ordersService.getOrderProBySellerIdx(sellerIdx);
            return new BaseResponse<>(getOrderProRes);
        }catch (BaseException baseException){
            return new BaseResponse<>(baseException.getStatus());
        }
    }

    /**
     * OrderController -5
     * 23.07.07 작성자 : 윤다은
     * 픽업 완료( main page -3 처리 중 상태를 픽업 완료)
     * POST /jat/orders/pickup
     */
    @ResponseBody
    @PostMapping("/pickup")
    public BaseResponse<String> pickupOrder(@RequestBody PostPickupReq postPickupReq){
        try{
            int sellerIdx = jwtService.getUserIdx();
            ordersService.postPickupBysellerIdx(sellerIdx,postPickupReq);
            return new BaseResponse<>("pickup successfuly");
        }catch (BaseException baseException){
            return new BaseResponse<>(baseException.getStatus());
        }
    }


}
