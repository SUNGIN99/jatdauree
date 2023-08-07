package com.example.jatdauree.src.domain.app.order.service;

import com.amazonaws.services.s3.AmazonS3;
import com.example.jatdauree.config.BaseException;
import com.example.jatdauree.src.domain.app.order.dao.AppOrderDao;
import com.example.jatdauree.src.domain.app.order.dto.GetOrderListRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.jatdauree.config.BaseResponseStatus.DATABASE_ERROR;

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

        try{
            return appOrderDao.getOrderList(customerIdx, weekDays);
        }catch (Exception e){
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
