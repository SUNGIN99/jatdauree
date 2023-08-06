package com.example.jatdauree.src.domain.app.subscribe.service;
import com.amazonaws.services.s3.AmazonS3;
import com.example.jatdauree.config.BaseException;
import com.example.jatdauree.src.domain.app.subscribe.dto.AppSubscriptionReq;
import com.example.jatdauree.src.domain.app.subscribe.dto.AppSubscriptionRes;
import com.example.jatdauree.src.domain.app.subscribe.dto.GetAppSubscriptionRes;
import com.example.jatdauree.src.domain.kakao.LocationValue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.example.jatdauree.src.domain.app.subscribe.dao.*;
import org.springframework.transaction.annotation.Transactional;
import com.amazonaws.services.s3.AmazonS3;
import java.util.List;
import static com.example.jatdauree.config.BaseResponseStatus.DATABASE_ERROR;
import static com.example.jatdauree.config.BaseResponseStatus.POST_STORES_NOT_REGISTERD;

@Service
public class AppSubscribeService {
    private final AppSubscribeDao appSubscribeDao;
    private final LocationValue locationValue;
    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    private final AmazonS3 s3Client;


    public AppSubscribeService(AppSubscribeDao appSubscribeDao, AmazonS3 s3Client) {
        this.appSubscribeDao = appSubscribeDao;
        this.locationValue = new LocationValue();
        this.s3Client = s3Client;
    }

    @Transactional(rollbackFor = BaseException.class)
    public AppSubscriptionRes subscription(int customerIdx, AppSubscriptionReq appSubscriptionReq) throws BaseException {

        int existStoreCheck;
        try {
            existStoreCheck = appSubscribeDao.ExistStore(appSubscriptionReq.getStoreIdx());
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
        if (existStoreCheck ==0){
            throw new BaseException(POST_STORES_NOT_REGISTERD); //없는가게입니다
        }

        int subscribeCheck;
        try {
            subscribeCheck = appSubscribeDao.checkSubscribe(customerIdx, appSubscriptionReq.getStoreIdx());
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }

        if (subscribeCheck == 0) {
            try {
                appSubscribeDao.firstSubscribe(customerIdx, appSubscriptionReq.getStoreIdx());
            } catch (Exception e) {
                throw new BaseException(DATABASE_ERROR);
            }
        }

        int subIdx;
        try {
            subIdx = appSubscribeDao.sudIdxByCustomerIdxStoreIdx(customerIdx, appSubscriptionReq.getStoreIdx());
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }

        if (subscribeCheck == 1) {

            String status;
            try {
                status = appSubscribeDao.checkSubscribeStatus(customerIdx, appSubscriptionReq.getStoreIdx());
            } catch (Exception e) {
                throw new BaseException(DATABASE_ERROR);
            }

            if ("A".equals(status)) {
                try {
                    appSubscribeDao.cancelSubscription(customerIdx, appSubscriptionReq.getStoreIdx());
                } catch (Exception e) {
                    throw new BaseException(DATABASE_ERROR);
                }
            }

            if ("D".equals(status)) {
                try {
                    appSubscribeDao.reSubscribe(customerIdx, appSubscriptionReq.getStoreIdx());
                } catch (Exception e) {
                    throw new BaseException(DATABASE_ERROR);
                }
            }
        }
        return new AppSubscriptionRes(subIdx);
    }


    public List<GetAppSubscriptionRes> getSubscriptionList(int customerIdx) throws BaseException {

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
        }
        return getAppSubscriptionRes;
    }

}

