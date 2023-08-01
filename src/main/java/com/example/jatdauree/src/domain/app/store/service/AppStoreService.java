package com.example.jatdauree.src.domain.app.store.service;

import com.amazonaws.services.s3.AmazonS3;
import com.example.jatdauree.config.BaseException;
import com.example.jatdauree.src.domain.app.store.dao.*;
import com.example.jatdauree.src.domain.app.store.dto.*;
import com.example.jatdauree.src.domain.kakao.address.LocationInfoRes;
import com.example.jatdauree.src.domain.kakao.LocationValue;
import com.example.jatdauree.src.domain.web.review.dto.*;
import com.example.jatdauree.src.domain.web.review.dao.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.example.jatdauree.config.BaseResponseStatus.*;

@Service
public class AppStoreService {
    private final AppStoreDao appStoreDao;
    private final ReviewDao reviewDao;

    private final LocationValue locationValue;
    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    private final AmazonS3 s3Client;

    @Autowired
    public AppStoreService(AppStoreDao appStoreDao, ReviewDao reviewDao, AmazonS3 s3Client) {
        this.appStoreDao = appStoreDao;
        this.reviewDao = reviewDao;
        this.locationValue = new LocationValue();
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
            try {
                reviewItems = appStoreDao.reviewItems(storeIdx);
                for (AppReviewItems item : reviewItems) {
                    if (item.getReview_url() != null)
                        item.setReview_url("" + s3Client.getUrl(bucketName, item.getReview_url()));
                    item.setOrderTodayMenu(appStoreDao.appOrderTodayMenus(storeIdx, item.getOrderIdx()));

                }
            } catch (Exception e) {
                throw new BaseException(RESPONSE_ERROR);    // 리뷰 조회에 실패하였습니다.
            }

            return new GetAppStoreDetailReviewRes(storeIdx, reviewStar.getStar_average(), reviewStar.getReviews_total(), reviewStar.getComment_total(),starCountRatios,reviewItems);
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }


    public List<StoreListXY> getStoreListByAddr(double maxX, double maxY, double minX, double minY) throws BaseException {
        // 카카오 위치 API 요청(사용자 현재 위치 주소 기반)
       /* ResponseEntity<LocationInfoRes> apiResponse;
        try{
            apiResponse = locationValue.kakaoLocalAPI(query);
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }

        // 카카오 위치 API 응답 실패
        if (apiResponse.getStatusCode() != HttpStatus.OK){
            throw new BaseException(DATABASE_ERROR);
        }

        // 응답 값이 1이상이면 결과가 존재함
        LocationInfoRes currentLoc = apiResponse.getBody();
        if (currentLoc.getDocuments().length == 0){
            throw new BaseException(DATABASE_ERROR);
        }

        // 현재 위/경도 값
        double nowX = currentLoc.getDocuments()[0].getX();
        double nowY = currentLoc.getDocuments()[0].getY();*/

        // 주변 반경내 1.5km 구하기.. (m단위 입력)
        //{minX, maxX, minY, maxY}
        double[] aroundXY = new double[]{minX, maxX, minY, maxY};

        // 주변 반경 내 현재 좌표로 가게 조회
        try{
            List<StoreListXY> storeListXY = appStoreDao.getStoreListByAddr(aroundXY);
            return storeListXY;
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<StorePreviewRes> getStorePreview(int customerIdx, StorePreviewReq previewReq) throws BaseException{
        // 1. 현재 사용자의 위치
        ResponseEntity<LocationInfoRes> apiResponse;
        try{
            apiResponse = locationValue.kakaoLocalAPI(previewReq.getCurrentAddress());
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
        // 카카오 위치 API 응답 실패
        if (apiResponse.getStatusCode() != HttpStatus.OK){
            throw new BaseException(DATABASE_ERROR);
        }
        // 응답 값이 1이상이면 결과가 존재함
        LocationInfoRes currentLoc = apiResponse.getBody();
        if (currentLoc.getDocuments().length == 0){
            throw new BaseException(DATABASE_ERROR);
        }
        // 현재 위/경도 값
        double nowX = currentLoc.getDocuments()[0].getX();
        double nowY = currentLoc.getDocuments()[0].getY();

        // 2. 선택한 가게 미리보기 정보 일단 가져오기
        StorePreviewDetails storePreview;
        try{
            storePreview = appStoreDao.getStorePreview(previewReq.getStoreIdx());
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }

        // 선택한 가게의 좌표
        double seletX = storePreview.getX();
        double selectY = storePreview.getY();

        // 3. 주변 반경내 300m 구하기.. (m단위 입력)
        // **선택한 가게에서 가까운 곳**
        //{minX, maxX, minY, maxY}
        double[] aroundXY = locationValue.aroundDist(seletX, selectY, 1500);

        // 가게에서 가장 가까운 최대 두 곳 더 미리보기
        List<StorePreviewDetails> aroundStoreList;
        try{
            aroundStoreList = appStoreDao.getAroundPreview(previewReq.getStoreIdx(),
                    seletX, selectY, aroundXY);
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }

        // 선택한 가게 까지 포함하기기
        aroundStoreList.add(0, storePreview);
        // 4. 반환할 미리보기 정보 만들기
        List<StorePreviewRes> storePreviewRes = new ArrayList<>();
        for(StorePreviewDetails prevDetail : aroundStoreList){
            // 가게 사진 URL
            if(prevDetail.getStoreLogoUrl() != null && prevDetail.getStoreLogoUrl().length() != 0){
                prevDetail.setStoreLogoUrl(""+s3Client.getUrl(bucketName, prevDetail.getStoreLogoUrl()));
            }

            if(prevDetail.getStoreSignUrl() != null && prevDetail.getStoreSignUrl().length() != 0){
                prevDetail.setStoreSignUrl(""+s3Client.getUrl(bucketName, prevDetail.getStoreSignUrl()));
            }
            // 가게평점
            double star = 0;
            try {
                star = appStoreDao.getStoreStar(prevDetail.getStoreIdx());
            }catch(NullPointerException nullerr){
                star = 0;
            }catch (IncorrectResultSizeDataAccessException error) { // 쿼리문에 해당하는 결과가 없거나 2개 이상일 때
                star = 0;
            }

            // ** 내 위치에서 해당 가게까지의 거리 **
            int distance = (int) locationValue.getDistance(nowX, nowY, prevDetail.getX(), prevDetail.getY());

            // 1Km (1000M) 16분 평군 소요
            // 100M : 1분 36초(96초) ,  10M : 9.6초
            // 거리 / 100M * 96초 / 1분 =
            int duration = (distance / 100 * 96 / 60); // 분

            // 구독 여부(예외처리 나중에 제대로 할것, EXISTS 써서 null 예외 처리 안해도될수도?)
            int subscribed;
            try{
                subscribed = appStoreDao.getStoreSubscribed(customerIdx, prevDetail.getStoreIdx());
            }catch (IncorrectResultSizeDataAccessException error) { // 쿼리문에 해당하는 결과가 없거나 2개 이상일 때
                subscribed  = 0;
            }catch(Exception e){
                throw new BaseException(DATABASE_ERROR);
            }

            StorePreviewRes prevRes = new StorePreviewRes(
                    prevDetail.getStoreIdx(),
                    prevDetail.getStoreName(),
                    prevDetail.getStoreLogoUrl(),
                    prevDetail.getStoreSignUrl(),
                    star, distance, duration, subscribed
            );

            storePreviewRes.add(prevRes);
        }

        return storePreviewRes;
    }
}