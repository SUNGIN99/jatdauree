package com.umc.jatdauree.src.domain.app.order.service;

import com.amazonaws.services.s3.AmazonS3;
import com.umc.jatdauree.config.BaseException;
import com.umc.jatdauree.src.domain.app.order.dao.AppOrderDao;
import com.umc.jatdauree.src.domain.app.order.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.umc.jatdauree.config.BaseResponseStatus.DATABASE_ERROR;
import static com.umc.jatdauree.config.BaseResponseStatus.REQUEST_ERROR;

@Service
public class AppOrderService {
    private final AppOrderDao appOrderDao;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    private final AmazonS3 s3Client; //공부하기,자바 static

    @Autowired
    public AppOrderService(AppOrderDao appOrderDao, AmazonS3 s3Client) {
        this.appOrderDao = appOrderDao;
        this.s3Client = s3Client;
    }


    public List<GetOrderListRes> getOrderList(int customerIdx) throws BaseException {
        String[] weekDays = new String[]{"일", "월", "화", "수", "목", "금", "토"};
        // 주문 내역 가져오기
        List<GetOrderListRes> orderList;
        try{
            orderList = appOrderDao.getOrderList(customerIdx, weekDays);
        }catch (Exception e){
            throw new BaseException(DATABASE_ERROR);
        }
        // 주문내역 s3
        try{
            for(GetOrderListRes order : orderList){
                if(order.getMenuUrl() != null && !order.getMenuUrl().equals("")){
                    order.setMenuUrl(""+s3Client.getUrl(bucketName, order.getMenuUrl()));
                }
            }
        }catch (Exception e){
            throw new BaseException(DATABASE_ERROR);
        }

        return orderList;
    }

    public OrderDetailRes getOrderDetail(int orderIdx) throws BaseException{

        // 1. 주문 상세 기본정보 가져오기
        OrderDetailRes orderDetails;
        try{
            orderDetails = appOrderDao.getOrderDetail(orderIdx);
        }catch (Exception e){
            throw new BaseException(DATABASE_ERROR);
        }

        // 2. 주문에 딸린 메뉴 상세 정봅 가져오기
        try{
            List<OrderMenuItem> orderItems = appOrderDao.getOrderItems(orderIdx);

            orderDetails.setOrderMenus(orderItems);

            return orderDetails;
        }catch (Exception e){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public OrderDeleted orderDelete(OrderDeleteReq delReq) throws BaseException{
        String status;
        try{
            status = appOrderDao.orderStatusCheck(delReq.getOrderIdx());
        }catch (Exception e){
            throw new BaseException(DATABASE_ERROR);
        }

        try{
            if(status.equals("A")){
                int removed = appOrderDao.orderDelete(delReq);
                //appOrderDao.orderListDelete(delReq);
                return new OrderDeleted(removed);
            }
            else{
                throw new Exception();
            }
        }catch (Exception e){
            throw new BaseException(REQUEST_ERROR);
        }
    }
}
