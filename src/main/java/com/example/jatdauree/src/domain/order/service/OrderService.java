package com.example.jatdauree.src.domain.order.service;

import com.example.jatdauree.config.BaseException;
import com.example.jatdauree.config.BaseResponseStatus;
import com.example.jatdauree.src.domain.order.dao.OrderDao;
import com.example.jatdauree.src.domain.order.dto.GetOrderProRes;
import com.example.jatdauree.src.domain.order.dto.GetOrderRes;
import com.example.jatdauree.src.domain.order.dto.PostOrderCancelReq;
import com.example.jatdauree.src.domain.order.dto.PostPickupReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private final OrderDao orderDao;

    @Autowired
    public OrderService(OrderDao orderDao) {
        this.orderDao = orderDao;
    }

    public List<GetOrderRes> getOrdersBySellerId(int sellerIdx)throws BaseException {
        try {
            int storeIdx = orderDao.getStoreIdxBySellerIdx(sellerIdx);
            List<GetOrderRes> getOrdersResList = orderDao.getOrdersByStoreIdx(storeIdx);
            return getOrdersResList;
        }
        catch (Exception exception){
            System.out.println(exception);
            throw new BaseException(BaseResponseStatus.RESPONSE_ERROR);
        }
    }

    public void postOrderBySellerId(int sellerIdx, PostOrderCancelReq postOrderCancelReq) throws BaseException{
        try {
            int storeIdx = orderDao.getStoreIdxBySellerIdx(sellerIdx);
            orderDao.updateOrderStatus(storeIdx, postOrderCancelReq.getOrderIdx(), postOrderCancelReq.getStatus());
        }
        catch (Exception exception){
            throw new BaseException(BaseResponseStatus.RESPONSE_ERROR);
        }
    }

    public List<GetOrderProRes> getOrderProBySellerIdx(int sellerIdx)throws BaseException {
        try {
            int storeIdx = orderDao.getStoreIdxBySellerIdx(sellerIdx);
            List<GetOrderProRes> getOrderProResList = orderDao.getOrderProByStoreIdx(storeIdx);
            return getOrderProResList;
        }
        catch (Exception exception){
            throw new BaseException(BaseResponseStatus.RESPONSE_ERROR);
        }
    }

    public void postPickupBysellerIdx(int sellerIdx, PostPickupReq postPickupReq) throws BaseException {
        try{
            int storeIdx = orderDao.getStoreIdxBySellerIdx(sellerIdx);
            orderDao.updateOrderPickup(storeIdx, postPickupReq.getOrderIdx(), postPickupReq.getStatus());
        }
        catch(Exception exception){
            throw new BaseException(BaseResponseStatus.RESPONSE_ERROR);
        }
    }


}
