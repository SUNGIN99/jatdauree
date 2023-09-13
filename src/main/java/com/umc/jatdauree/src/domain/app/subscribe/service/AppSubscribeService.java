package com.umc.jatdauree.src.domain.app.subscribe.service;
import com.amazonaws.services.s3.AmazonS3;
import com.umc.jatdauree.config.BaseException;
import com.umc.jatdauree.src.domain.app.store.dao.AppStoreDao;
import com.umc.jatdauree.src.domain.app.subscribe.dto.AppSubscriptionReq;
import com.umc.jatdauree.src.domain.app.subscribe.dto.AppSubscriptionRes;
import com.umc.jatdauree.src.domain.app.subscribe.dto.GetAppSubscriptionRes;
import com.umc.jatdauree.src.domain.kakao.LocationValue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Service;
import com.umc.jatdauree.src.domain.app.subscribe.dao.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import static com.umc.jatdauree.config.BaseResponseStatus.DATABASE_ERROR;
import static com.umc.jatdauree.config.BaseResponseStatus.POST_STORES_NOT_REGISTERD;

@Service
public class AppSubscribeService {
    private final AppSubscribeDao appSubscribeDao;
    private final AppStoreDao appStoreDao;
    private final LocationValue locationValue;
    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    private final AmazonS3 s3Client;


    public AppSubscribeService(AppSubscribeDao appSubscribeDao, AppStoreDao appStoreDao, AmazonS3 s3Client) {
        this.appSubscribeDao = appSubscribeDao;
        this.appStoreDao = appStoreDao;
        this.locationValue = new LocationValue();
        this.s3Client = s3Client;
    }

    @Transactional(rollbackFor = BaseException.class)
    public AppSubscriptionRes subscription(int customerIdx, AppSubscriptionReq appSubscriptionReq) throws BaseException {
        // 1. 가게존재 여부확인
        int existStoreCheck;
        try {
            existStoreCheck = appSubscribeDao.existStore(appSubscriptionReq.getStoreIdx());
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
        if (existStoreCheck ==0){
            throw new BaseException(POST_STORES_NOT_REGISTERD); //없는가게입니다
        }

        // 2. 구독 기록 확인
        int subIdx;
        try {
            subIdx = appSubscribeDao.sudIdxByCustomerIdxStoreIdx(customerIdx, appSubscriptionReq.getStoreIdx());
        } catch (IncorrectResultSizeDataAccessException error) {
            subIdx = 0;
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }

        // 3. 구독 상태 처리
        try{
            if(appSubscriptionReq.getYn() == 1){
                // 구독
                if (subIdx != 0) {// 재 구독
                    appSubscribeDao.reSubscribe(customerIdx, appSubscriptionReq.getStoreIdx());
                }else{ // 신규 구독
                    subIdx = appSubscribeDao.firstSubscribe(customerIdx, appSubscriptionReq.getStoreIdx());
                }
            }else{
                // 구독 취소
                if (subIdx != 0) {
                    appSubscribeDao.cancelSubscription(customerIdx, appSubscriptionReq.getStoreIdx());
                }else{
                    throw new BaseException(DATABASE_ERROR);
                }
            }
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }

        // 4. 구독 상태값 확인 후 결과 응답
        try{
            String status = appSubscribeDao.checkSubscribeStatus(customerIdx, appSubscriptionReq.getStoreIdx());
            return new AppSubscriptionRes(subIdx, status);
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }

    }


    public List<GetAppSubscriptionRes> getSubscriptionList(int customerIdx, Double longitude, Double latitude) throws BaseException {

        List<GetAppSubscriptionRes> getAppSubscriptionRes;
        try {
            getAppSubscriptionRes = appSubscribeDao.getSubscriptionList(customerIdx);
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
        for (GetAppSubscriptionRes subscription : getAppSubscriptionRes) {

            if (subscription.getStoreLogoUrl() != null && subscription.getStoreLogoUrl().length() != 0) {
                subscription.setStoreLogoUrl("" + s3Client.getUrl(bucketName, subscription.getStoreLogoUrl()));
            }
            if (subscription.getStoreSignUrl() != null && subscription.getStoreSignUrl().length() != 0) {
                subscription.setStoreSignUrl("" + s3Client.getUrl(bucketName, subscription.getStoreSignUrl()));
            }

            int distance = (int) locationValue.getDistance(latitude, longitude, subscription.getY(),  subscription.getX());
            int duration = locationValue.getDuration(distance);

            subscription.setDistance(distance);
            subscription.setDuration(duration);
        }

        return getAppSubscriptionRes;
    }

}

