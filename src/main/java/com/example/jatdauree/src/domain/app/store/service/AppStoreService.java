package com.example.jatdauree.src.domain.app.store.service;

import com.amazonaws.services.s3.AmazonS3;
import com.example.jatdauree.config.BaseException;
import com.example.jatdauree.src.domain.app.store.dao.*;
import com.example.jatdauree.src.domain.app.store.dto.*;
import com.example.jatdauree.src.domain.web.review.dto.*;
import com.example.jatdauree.src.domain.web.review.dao.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.example.jatdauree.config.BaseResponseStatus.*;

@Service
public class AppStoreService {
    private final AppStoreDao appStoreDao;
    private final ReviewDao reviewDao;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    private final AmazonS3 s3Client;

    @Autowired
    public AppStoreService(AppStoreDao appStoreDao, ReviewDao reviewDao, AmazonS3 s3Client) {
        this.appStoreDao = appStoreDao;
        this.reviewDao = reviewDao;
        this.s3Client = s3Client;
    }



//쿼리 스트링 menu
    public GetAppStoreDetailMenuRes getAppStoreDetailMenu(int storeIdx) throws BaseException { //getAppStoreDetailMenu

        List<GetAppStoreDetailMenuItem> mainMenuList, sideMenuList;

        //메인메뉴조회
        try {
            mainMenuList = appStoreDao.getAppStoreDetailMenuList(storeIdx, "M");
        } catch (Exception e) {
            throw new BaseException(GET_MENU_ERROR);
        }
        // 메인 메뉴 url 가져오기
        try {
            for (GetAppStoreDetailMenuItem item : mainMenuList) {
                if (item.getMenuUrl() != null && !item.getMenuUrl().equals("")) {
                    item.setMenuUrl("" + s3Client.getUrl(bucketName, item.getMenuUrl()));
                }
            }
        } catch (Exception e) {
            throw new BaseException(GET_MENU_ERROR);
        }

        try {
            sideMenuList = appStoreDao.getAppStoreDetailMenuList(storeIdx, "S");
        } catch (Exception e) {
            throw new BaseException(GET_MENU_ERROR);
        }
        // 사이드 메뉴 url 가져오기
        try {
            for (GetAppStoreDetailMenuItem item : sideMenuList) {
                if (item.getMenuUrl() != null && !item.getMenuUrl().equals("")) {
                    item.setMenuUrl("" + s3Client.getUrl(bucketName, item.getMenuUrl()));
                }
            }
        } catch (Exception e) {
            throw new BaseException(GET_MENU_ERROR);
        }

        return new GetAppStoreDetailMenuRes(storeIdx, mainMenuList, sideMenuList);

    }

    //쿼리 스트링 info
    public GetAppStoreDetailInfoRes getAppStoreDetailInfo(int storeIdx) throws BaseException {
        GetAppStoreDetailStoreInfo detailStoreInfo; //detailStoreInfo
        GetAppStoreDetailStatisticsInfo detailStatisticsInfo;
        GetAppStoreDetailSellerInfo detailSellerInfo;//detailSellerInfo
        List<GetAppStoreDetailIngredientInfo> ingredientInfo; //detailIngredientInfoList ingredientInfo detailIngredientInfo
        String detailIngredientInfo;

        try {
            detailStoreInfo = appStoreDao.getAppStoreDetailStoreInfo(storeIdx);
        } catch (Exception e) {
            throw new BaseException(GET_MENU_ERROR);
        }
        try {

            int orderCount = appStoreDao.orderCount(storeIdx);
            int reviewCount = appStoreDao.reviewCount(storeIdx);
            int subscribeCount = appStoreDao.subscribeCount(storeIdx);
            detailStatisticsInfo =  new GetAppStoreDetailStatisticsInfo(orderCount,reviewCount, subscribeCount);

        } catch (Exception e) {
            //System.out.println("exception3: " + e);
            throw new BaseException(GET_MENU_ERROR);
        }
        try {
            detailSellerInfo = appStoreDao.getStoreAppDetailSellerInfo(storeIdx);
        } catch (Exception e) {
            //System.out.println("exception4: " + e);
            throw new BaseException(GET_MENU_ERROR);
        }
        try {
            ingredientInfo = appStoreDao.getStoreAppDetailIngredientInfo(storeIdx);
            StringBuilder combinedInfoBuilder = new StringBuilder();
            for(GetAppStoreDetailIngredientInfo info : ingredientInfo){
                combinedInfoBuilder.append(info.getIngredientInfo());
            }
         detailIngredientInfo = combinedInfoBuilder.toString();

        } catch (Exception e) {
          //  System.out.println("exception5: " + e);
            throw new BaseException(GET_MENU_ERROR);
        }

        return new GetAppStoreDetailInfoRes(storeIdx, detailStoreInfo,detailStatisticsInfo,detailSellerInfo,detailIngredientInfo);
    }

//쿼리 스트링 review
    public GetAppStoreDetailReviewRes getAppStoreDetailReview(int storeIdx) throws BaseException { //getAppStoreDetailReview
        // 리뷰 개수,별점 평균,별점 가지고 오기

        try {
            GetAppReviewStarRes reviewStar = appStoreDao.reviewStarTotal(storeIdx);

            List<StarCountRatio> starCountRatios = new ArrayList<>();
            starCountRatios.add(new StarCountRatio("별 5개", reviewStar.getStar5(), (int) (reviewStar.getStar5() * 1.0 / reviewStar.getReviews_total() * 100)));
            starCountRatios.add(new StarCountRatio("별 4개", reviewStar.getStar4(), (int) (reviewStar.getStar4() * 1.0  / reviewStar.getReviews_total() * 100)));
            starCountRatios.add(new StarCountRatio("별 3개", reviewStar.getStar3(), (int) (reviewStar.getStar3() * 1.0  / reviewStar.getReviews_total() * 100)));
            starCountRatios.add(new StarCountRatio("별 2개", reviewStar.getStar2(), (int) (reviewStar.getStar2() * 1.0  / reviewStar.getReviews_total() * 100)));
            starCountRatios.add(new StarCountRatio("별 1개", reviewStar.getStar1(), (int) (reviewStar.getStar1() * 1.0  / reviewStar.getReviews_total() * 100)));


            List<AppReviewItems> reviewItems;
           // List<String> appOrderTodayMenus;
            try {
                reviewItems = appStoreDao.reviewItems(storeIdx);
                for (AppReviewItems item : reviewItems) {
                    if (item.getReview_url() != null)
                        item.setReview_url("" + s3Client.getUrl(bucketName, item.getReview_url()));
                    item.setOrderTodayMenu(appStoreDao.appOrderTodayMenus(storeIdx, item.getOrderIdx()));

                }
            } catch (Exception e) {
                System.out.println("exception5: " + e);
                throw new BaseException(RESPONSE_ERROR);    // 리뷰 조회에 실패하였습니다.
            }

            return new GetAppStoreDetailReviewRes(storeIdx, reviewStar.getStar_average(), reviewStar.getReviews_total(), reviewStar.getComment_total(),starCountRatios,reviewItems);
        } catch (Exception e) {
            System.out.println("exception5: " + e);
            throw new BaseException(DATABASE_ERROR);
        }
    }







}