package com.example.jatdauree.src.domain.order.service;

import com.example.jatdauree.config.BaseException;
import com.example.jatdauree.config.BaseResponseStatus;
import com.example.jatdauree.src.domain.order.dao.OrderDao;
import com.example.jatdauree.src.domain.order.dto.*;
import com.example.jatdauree.src.domain.store.dao.StoreDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.jatdauree.config.BaseResponseStatus.DATABASE_ERROR;
import static com.example.jatdauree.config.BaseResponseStatus.POST_STORES_NOT_REGISTERD;

@Service
public class OrderService {

    private final OrderDao orderDao;
    private final StoreDao storeDao;

    @Autowired
    public OrderService(OrderDao orderDao, StoreDao storeDao) {
        this.orderDao = orderDao;
        this.storeDao = storeDao;
    }

    public List<GetOrderRes> getOrdersBySellerId(int sellerIdx)throws BaseException {
        // 1) 사용자 가게 조회
        int storeIdx;
        try{
            storeIdx = storeDao.storeIdxBySellerIdx(sellerIdx);
        } catch (Exception e) {
            throw new BaseException(POST_STORES_NOT_REGISTERD); // 2030 : 사용자의 가게가 등록되어있지 않습니다.
        }

        try {
            List<GetOrderRes> getOrdersResList = orderDao.getOrdersByStoreIdx(storeIdx);
            return getOrdersResList;
        }
        catch (Exception exception){
            System.out.println(exception);
            throw new BaseException(BaseResponseStatus.RESPONSE_ERROR);
        }
    }

    @Transactional(rollbackFor = BaseException.class)
    public PatchReceRes patchRecBySellerIdx(int sellerIdx, PatchReceReq patchReceReq) throws BaseException{
        // 0)
        int storeIdx;
        try {
            storeIdx = storeDao.storeIdxBySellerIdx(sellerIdx);
        }
        catch (Exception e){
            System.out.println("1: " + e);
            throw new BaseException(POST_STORES_NOT_REGISTERD); // 존재하지 않은 가게입니다.
        }

        // 1) 올바른 접수/조회 요청 인지
        if (!patchReceReq.getStatus().equals("P") && !patchReceReq.getStatus().equals("D"))
            throw new BaseException(DATABASE_ERROR); // 올바르지 않은 주문 접수/취소 요청입니다.

        // 2) 조회해서 상태존재하는가
        String status;
        try{
            status = orderDao.checkOrderStatus(patchReceReq.getOrderIdx());
        }catch (Exception e){
            System.out.println("3: " + e);
            throw new BaseException(DATABASE_ERROR); // 주문 정보가 옳바르지 않습니다.
        }

        // 3) 현재상태가 W가 아닌 것들은
        if(!"W".equals(status)){
            throw new BaseException(DATABASE_ERROR); // 이미 처리 되거나, 처리 되지 않은 주문입니다.
        }

        // 4) 주문 접수 혹은 취소
        // ******************** 여기서 구매정보 발생 시켜서 처리해야함 ********************
        int updated;
        try{
            updated = orderDao.updateOrderStatus(storeIdx, patchReceReq.getOrderIdx(), patchReceReq.getStatus());
        }catch (Exception e){
            System.out.println("1: " + e);
            throw new BaseException(DATABASE_ERROR);// 데이터를 업데이트 하는데 오류가 있음
        }

        // *********** 주문번호 처리 로직 필요 ***********
        if(updated == 1){
            return new PatchReceRes(storeIdx, 0);
        }else{
            throw new BaseException(DATABASE_ERROR);
        }
    }


    public GetBillRes getBillsByOrderIdx(int sellerIdx, GetBillReq getBillReq) throws BaseException {
        //1. 가게의 존재 여부
        int storeIdx;
        try {
            storeIdx = storeDao.storeIdxBySellerIdx(sellerIdx);
        } catch (Exception exception) {
            throw new BaseException(BaseResponseStatus.POST_STORES_NOT_REGISTERD); //가게가 등록되어 있지 않다.
        }
        // 2. 상태가 W,D인 경우에는 주문표가 존재하지 않는다.
        String status;
        try{
            status = orderDao.checkOrderStatus(getBillReq.getOrderIdx());
        }catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR); //현재 상태가 존재하지 않는 경우이다.
        }
        if (status.equals("W") || status.equals("D")){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR); // 현재 옳바르지 않은 상태이다.
        }

        // 3. 주문 정보 조회하기
        GetBillRes orderBills;
        try{
            orderBills = orderDao.getOrderBills(storeIdx, getBillReq.getOrderIdx());
        }catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }

        // 4. 주문에 포함된 메뉴 정보 조회하기
        try{
            orderBills.setOrderItem(orderDao.getOrderMenus(storeIdx, getBillReq.getOrderIdx()));
            return orderBills;
        }catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }


    }

    public GetOrderProRes getProcessOrder(int sellerIdx) throws BaseException {
        //1. 가게를 조회해서 존재하는지를 확인한다.
        int storeIdx;
        try {
            storeIdx = storeDao.storeIdxBySellerIdx(sellerIdx);
        } catch (Exception exception) {
            throw new BaseException(BaseResponseStatus.POST_STORES_NOT_REGISTERD);
        }

        //2. orderList 조회하기
        List<GetOrderItem> orderLists;
        try {
            orderLists = orderDao.orderByStoreIdx(storeIdx);
        } catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }

        //3. orderList 안에 있는 orderItem 조회하기
        try {
            for (GetOrderItem order : orderLists){
                order.setOrderItems(orderDao.orderItem(storeIdx, order.getOrderIdx()));
            }
            return new GetOrderProRes(storeIdx ,orderLists);
        }catch (Exception exception){
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
