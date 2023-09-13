package com.umc.jatdauree.src.domain.web.order;

import com.umc.jatdauree.config.BaseException;
import com.umc.jatdauree.config.BaseResponse;
import com.umc.jatdauree.src.domain.web.order.dto.*;
import com.umc.jatdauree.src.domain.web.order.service.OrderService;
import com.umc.jatdauree.utils.jwt.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
     * 23.07.15 작성자 : 김성인
     * 주문화면 -대기상태
     * GET /jat/orders/wait
     * JWT 적용해서 sellerIdx 가져오기
     */
    @ResponseBody
    @GetMapping("/wait")
    public BaseResponse<GetOrderListRes> getWaitedOrder(){
        try{
            int sellerIdx = jwtService.getUserIdx();
            GetOrderListRes getOrdersRes = ordersService.getOrdersBySellerIdx(sellerIdx, "W");

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
     * POST /jat/orders/bills
     * */
    @ResponseBody
    @PostMapping("/bills")
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
     * 23.07.15 작성자: 김성인
     * 주문 처리 중 확인
     * GET /jat/orders/process
     * */
    @ResponseBody
    @GetMapping("/process")
    public BaseResponse<GetOrderListRes> getProcessOrder(){
        try{
            int sellerIdx = jwtService.getUserIdx();
            GetOrderListRes getOrderListRes = ordersService.getOrdersBySellerIdx(sellerIdx, "P");

            return new BaseResponse<>(getOrderListRes);
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

    /**
     * OrderController -4
     * 23.07.15 작성자: 김성인
     * 주문 처리 중 확인
     * GET /jat/orders/complete
     * */
    @ResponseBody
    @GetMapping("/complete")
    public BaseResponse<GetOrderListRes> getCompleteOrder(){
        try{
            int sellerIdx = jwtService.getUserIdx();
            GetOrderListRes getOrderListRes = ordersService.getOrdersBySellerIdx(sellerIdx, "A");

            return new BaseResponse<>(getOrderListRes);
        }catch (BaseException baseException){
            return new BaseResponse<>(baseException.getStatus());
        }
    }

}
