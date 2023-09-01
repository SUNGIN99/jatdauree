package com.example.jatdauree.src.domain.web.order.service;

import com.example.jatdauree.config.BaseException;
import com.example.jatdauree.config.BaseResponseStatus;
import com.example.jatdauree.src.domain.web.order.dao.OrderDao;
import com.example.jatdauree.src.domain.web.order.dto.*;
import com.example.jatdauree.src.domain.web.store.dao.StoreDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

import static com.example.jatdauree.config.BaseResponseStatus.*;

@Service
public class OrderService {

    private final OrderDao orderDao;
    private final StoreDao storeDao;

    @Autowired
    public OrderService(OrderDao orderDao, StoreDao storeDao) {
        this.orderDao = orderDao;
        this.storeDao = storeDao;
    }

    public GetOrderListRes getOrdersBySellerIdx(int sellerIdx, String status) throws BaseException {
        // 1) 사용자 가게 조회
        int storeIdx;
        try{
            storeIdx = storeDao.storeIdxBySellerIdx(sellerIdx);
        } catch (Exception e) {
            throw new BaseException(POST_STORES_NOT_REGISTERD); // 2030 : 사용자의 가게가 등록되어있지 않습니다.
        }

        // 2) 주문관련된 정보 모두 가져오기 (접수 대기중 W)
        // 주문Idx, 주문/픽업 시간, 요청사항, 총 결제금액, 결제상태, 메뉴이름, 총 가격
        List<GetOrderItemRes> getOrdersResList;
        try {
            getOrdersResList = orderDao.getOrdersByStoreIdx(storeIdx, status);
        } catch (Exception e){
            throw new BaseException(BaseResponseStatus.RESPONSE_ERROR); // 주문 대기 조회에 실패하였습니다.
        }
        // 3) 주문관련 정보 조합하기
        HashMap<Integer, GetOrderRes> orderHash = new LinkedHashMap<>();
        try{
            for(GetOrderItemRes orders : getOrdersResList){
                if (!orderHash.containsKey(orders.getOrderIdx())){
                    GetOrderRes orderInfo = new GetOrderRes(
                            orders.getOrderIdx(),
                            orders.getOrderSequence(),
                            orders.getOrderTime(),
                            orders.getPickUpTime(),
                            orders.getRequest(),
                            1, // 주문당 메뉴 종류 개수 1 로 시작
                            orders.getTotalPrice(),
                            orders.getPayStatus(),
                            new ArrayList<>()
                    );
                    // 메뉴 목록 리스트 추가
                    orderInfo.getOrderItem().add(new OrderMenuCntPrirce(orders.getMenuName(), orders.getMenuCount(), orders.getDiscountedPrice()));

                    orderHash.put(orders.getOrderIdx(), orderInfo);

                }else{
                    // 주문 내 메뉴 개수 1 증가
                    orderHash.get(orders.getOrderIdx())
                            .setMenuDiverse(orderHash.get(orders.getOrderIdx()).getMenuDiverse() + 1);

                    // 총 주문 가격 합산
                    orderHash.get(orders.getOrderIdx())
                            .setTotalPrice(orderHash.get(orders.getOrderIdx()).getTotalPrice() + orders.getTotalPrice());

                    // 메뉴 이름, 개수목록 추가
                    orderHash.get(orders.getOrderIdx())
                            .getOrderItem().add(new OrderMenuCntPrirce(orders.getMenuName(), orders.getMenuCount(), orders.getDiscountedPrice()));
                }
            }
        }catch (Exception e){
            throw new BaseException(BaseResponseStatus.RESPONSE_ERROR); // 주문 대기 목록을 처리하는데 실패하였습니다.
        }

        return new GetOrderListRes<>(storeIdx, new ArrayList<>(orderHash.values()));
    }

    @Transactional(rollbackFor = BaseException.class)
    public PatchReceRes patchRecBySellerIdx(int sellerIdx, PatchReceReq patchReceReq) throws BaseException{
        // 0)
        int storeIdx;
        try {
            storeIdx = storeDao.storeIdxBySellerIdx(sellerIdx);
        }
        catch (Exception e){
            throw new BaseException(POST_STORES_NOT_REGISTERD); // 존재하지 않은 가게입니다.
        }

        // 1) 올바른 접수/조회 요청 인지
        if (!patchReceReq.getStatus().equals("P") && !patchReceReq.getStatus().equals("D"))
            throw new BaseException(DATABASE_ERROR); // 올바르지 않은 주문 접수/취소 요청입니다.

        // 2) 조회해서 상태가 대기 상태인지 확인
        String status;
        try{
            status = orderDao.checkOrderStatus(patchReceReq.getOrderIdx());
        }catch (Exception e){
            throw new BaseException(DATABASE_ERROR); // 주문 정보가 옳바르지 않습니다.
        }
        if(!"W".equals(status)){
            throw new BaseException(DATABASE_ERROR); // 이미 처리 되거나, 처리 되지 않은 주문입니다.
        }

        // 4) 주문번호 확인
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(new Date());
        int orderSequence;
        try{
            orderSequence = orderDao.getOrderSequence(storeIdx, date);
            orderSequence += 1;
        }catch (IncorrectResultSizeDataAccessException error) { // 쿼리문에 해당하는 결과가 없거나 2개 이상일 때
            orderSequence = 1;
        }catch (Exception e){
            throw new BaseException(DATABASE_ERROR);// 데이터를 업데이트 하는데 오류가 있음
        }

        // 5) 주문 접수 혹은 취소
        // ******************** 여기서 구매정보 발생 시켜서 처리해야함 ********************
        int updated = 0;
        try{
            if (patchReceReq.getStatus().equals("D"))
                updated = orderDao.orderDenied(storeIdx, patchReceReq.getOrderIdx(), patchReceReq.getStatus());
            else if (patchReceReq.getStatus().equals("P"))
                updated = orderDao.orderAccepted(storeIdx, patchReceReq.getOrderIdx(), patchReceReq.getStatus(), orderSequence);
        }catch (Exception e){
            //System.out.println("1: "+ e);
            throw new BaseException(DATABASE_ERROR);// 데이터를 업데이트 하는데 오류가 있음
        }


        // *********** 주문번호 처리 로직 필요 ***********
        if(updated == 1){
            return new PatchReceRes(storeIdx, orderSequence);
        }else{
            //System.out.println("2: ");
            throw new BaseException(DATABASE_ERROR);
        }
    }


    public GetBillRes getBillsByOrderIdx(int sellerIdx, GetBillReq getBillReq) throws BaseException {
        //1. 가게의 존재 여부
        int storeIdx;
        try {
            storeIdx = storeDao.storeIdxBySellerIdx(sellerIdx);
        } catch (Exception e) {
            throw new BaseException(BaseResponseStatus.POST_STORES_NOT_REGISTERD); //가게가 등록되어 있지 않다.
        }
        // 2. 상태가 W,D인 경우에는 주문표가 존재하지 않는다. (주문 접수처리가 된 주문표)
        String status;
        try{
            status = orderDao.checkOrderStatus(getBillReq.getOrderIdx());
        }catch (Exception e){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR); //현재 상태가 존재하지 않는 경우이다.
        }
        if ((!status.equals("P") || !status.equals("W")) && status.equals("D")){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR); // 현재 옳바르지 않은 상태이다.
        }

        // 3. 주문 정보 조회하기
        GetBillRes orderBills;
        try{
            orderBills = orderDao.getOrderBills(storeIdx, getBillReq.getOrderIdx());
        }catch (Exception e){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }

        // 4. 주문에 포함된 메뉴 정보 조회하기
        try{
            orderBills.setOrderItem(orderDao.getOrderMenus(getBillReq.getOrderIdx()));

            return orderBills;
        }catch (Exception e){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }


    }

    @Transactional(rollbackFor = BaseException.class)
    public PatchPickupRes pickupOrder(int sellerIdx, PatchPickupReq patchPickupReq) throws BaseException {
        //1)사용자 가게 조회
        int storeIdx;
        try {
            storeIdx = storeDao.storeIdxBySellerIdx(sellerIdx);
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }

        // 2) 해당 주문의 상태값을 조회
        String status;
        try {
            status = orderDao.checkOrderStatus(patchPickupReq.getOrderIdx());
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR); // 해당 주문에대한 상태값을 찾을 수 없습니다.
        }

        if (!"P".equals(status)) {// string으로 status를가져와야..... //"P" != "status" 이렇게 하면 결과가항상 참이 나온다 실패
            throw new BaseException(DATABASE_ERROR); // 해당 주문이 대기/취소/완료된 작업입니다.
        }

        // 주문 상태 확인 후 업데이트 세분화 필요
        int resultCount;
        try {
            resultCount = orderDao.orderPickupUpdate(patchPickupReq.getOrderIdx());
        } catch (BaseException e) {
            throw new BaseException(DATABASE_ERROR);
        }

        if (resultCount == 1)
            return new PatchPickupRes(storeIdx);
        else
            throw new BaseException(DATABASE_ERROR);
    }



}
