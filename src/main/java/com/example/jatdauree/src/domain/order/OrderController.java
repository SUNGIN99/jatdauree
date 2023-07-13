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
    private final JwtService jwtService;


    @Autowired
    public OrderController(OrderService ordersService, JwtService jwtService){
        this.ordersService = ordersService;
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
     * PATCH /jat/orders/receive
     * JWT 적용해서 sellerIdx 가져오고 UPDATE를 통해서 status 수정하기
     */
    @ResponseBody
    @PatchMapping("/receive")
    public BaseResponse<PatchReceRes> patchRecBySellerIdx(@RequestBody PatchReceReq patchReceReq){
        try{
            int sellerIdx = jwtService.getUserIdx();
            PatchReceRes patchReceRes = ordersService.patchRecBySellerIdx(sellerIdx,patchReceReq);
            return new BaseResponse<>(patchReceRes);
        }catch (BaseException baseException){
            return new BaseResponse<>(baseException.getStatus());
        }
    }

    /**
     * OrderController -3
     * 23.07.11 작성자 : 윤다은
     * 주문표 인쇄하기
     * GET /jat/orders/bills
     * */
    @ResponseBody
    @GetMapping("/bills")
    public BaseResponse<GetBillRes> getBillsByOrderIdx(@RequestBody GetBillReq getBillReq){
        try{
            int sellerIdx = jwtService.getUserIdx();
            GetBillRes getBillRes = ordersService.getBillsByOrderIdx(sellerIdx, getBillReq);
            return new BaseResponse<>(getBillRes);
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
    public BaseResponse<GetOrderProRes> getProcessOrder(){
        try{
            int sellerIdx = jwtService.getUserIdx();
            GetOrderProRes getOrderProRes = ordersService.getProcessOrder(sellerIdx);
            return new BaseResponse<>(getOrderProRes);
        }catch (BaseException baseException){
            return new BaseResponse<>(baseException.getStatus());
        }
    }


    /**
     * OrderController -4
     * 23.07.14 작성자 : 이윤채
     * 주문 처리 중 확인
     * Patch /jat/orders/pickup
     * */
    @ResponseBody
    @PatchMapping("/pickup")
    public BaseResponse<PatchPickupRes> pickupOrder(@RequestBody PatchPickupReq pathchPickupReq){
        try{
            int sellerIdx = jwtService.getUserIdx();

            PatchPickupRes patchPickupRes = ordersService.pickupOrder(sellerIdx,pathchPickupReq);
            return new BaseResponse<>(patchPickupRes);
        }catch (BaseException baseException){
            return new BaseResponse<>(baseException.getStatus());
        }
    }


}
