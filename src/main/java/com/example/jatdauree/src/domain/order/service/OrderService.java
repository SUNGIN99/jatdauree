package com.example.jatdauree.src.domain.order.service;

import com.example.jatdauree.config.BaseException;
import com.example.jatdauree.config.BaseResponseStatus;
import com.example.jatdauree.src.domain.order.dao.OrderDao;
import com.example.jatdauree.src.domain.order.dto.GetOrderRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {
    private final OrderDao ordersDao;

    @Autowired
    public OrderService(OrderDao ordersDao) {this.ordersDao = ordersDao;}

    public List<GetOrderRes> getOrdersBySellerId(int sellerIdx)throws BaseException {
        try {
            int storeIdx = ordersDao.getStoreIdxBySellerIdx(sellerIdx);
            List<GetOrderRes> getOrdersResList = ordersDao.getOrdersByStoreIdx(storeIdx);
            return getOrdersResList;
        }
        catch (Exception exception){
            throw new BaseException(BaseResponseStatus.RESPONSE_ERROR);
        }
    }

}
