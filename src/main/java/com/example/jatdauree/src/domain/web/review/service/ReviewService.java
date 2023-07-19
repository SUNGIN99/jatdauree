package com.example.jatdauree.src.domain.web.review.service;

import com.amazonaws.services.s3.AmazonS3;
import com.example.jatdauree.config.BaseException;
import com.example.jatdauree.config.BaseResponseStatus;
import com.example.jatdauree.src.domain.web.review.dao.ReviewDao;
import com.example.jatdauree.src.domain.web.store.dao.StoreDao;
import com.example.jatdauree.src.domain.web.review.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.example.jatdauree.config.BaseResponseStatus.*;

@Service
public class ReviewService {

    private final StoreDao storeDao;
    private final ReviewDao reviewDao;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;
    private AmazonS3 s3Client;

    @Autowired
    public ReviewService(StoreDao storeDao, ReviewDao reviewDao, AmazonS3 s3Client) {
        this.storeDao = storeDao;
        this.reviewDao = reviewDao;
        this.s3Client = s3Client;
    }

    //리뷰 조회
    public GetReviewRes getReview(int sellerIdx) throws BaseException {
        // 1) 사용자 가게 조회
        int storeIdx;
        try {
            storeIdx = storeDao.storeIdxBySellerIdx(sellerIdx);
        } catch (Exception e) {
            throw new BaseException(POST_STORES_NOT_REGISTERD); // 2030 : 사용자의 가게가 등록되어있지 않습니다.
        }

        // 2) 리뷰 조회
        List<ReviewItems> reviewItems;
        try {
            reviewItems = reviewDao.reviewItems(storeIdx);
        } catch (Exception e) {
            //System.out.println("exception1: " + e);
            throw new BaseException(BaseResponseStatus.RESPONSE_ERROR);    // 리뷰 조회에 실패하였습니다.
        }

        // 리뷰에 사진 정보 있으면 s3에서 url 가져오기
        try{
            for (ReviewItems item : reviewItems) {
                if(item.getReview_url() != null )
                    item.setReview_url(""+s3Client.getUrl(bucketName, item.getReview_url()));
            }
        }catch (Exception e) {
            //System.out.println("exception1: " + e);
            throw new BaseException(BaseResponseStatus.RESPONSE_ERROR);    // 리뷰 이미지 url 실패하였습니다.
        }

        // 3) 리뷰 주문 내의 메뉴 목록 조회
        try {
            for (ReviewItems item : reviewItems) {
                item.setOrderTodayMenu(reviewDao.orderTodayMenus(storeIdx, item.getOrderIdx()));
            }
            return new GetReviewRes(storeIdx, reviewItems);
        } catch (Exception e) {
            //System.out.println("exception2: " + e);
            throw new BaseException(BaseResponseStatus.RESPONSE_ERROR);    // 리뷰 조회에 실패하였습니다.
        }
    }



    //리뷰 답글 달기
    public ReviewAnswerRes reviewAnswer(int sellerIdx, ReviewAnswerReq reviewAnswerReq) throws BaseException {
        // 1) 사용자 가게 조회
        int storeIdx;
        try {
            storeIdx = storeDao.storeIdxBySellerIdx(sellerIdx);
        } catch (Exception e) {
            throw new BaseException(POST_STORES_NOT_REGISTERD); // 2030 : 사용자의 가게가 등록되어있지 않습니다.
        }

        //2) 해당 가게에 해당 리뷰가 존재하는지 조회
        int reviewIdxCheck;
        try{
            reviewIdxCheck = reviewDao.checkReviewIdx(storeIdx,reviewAnswerReq.getReviewIdx());
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR); // 가게의 리뷰 조회에 실패
        }

        // 3) 답글달 가게의 리뷰 존재여부
        if (reviewIdxCheck == 0){
            throw new BaseException(POST_REVIEW_EXISTS_REVIEW); //2050 : 가게에 해당 리뷰가 존재하지 않습니다.
        }

        // 4) 리뷰에 대한 입력값 수정
        if (reviewAnswerReq.getComment() == null){
            throw new BaseException(POST_REVIEW_COMMENT_DATA_UNVALID); // 2051 : 리뷰 답글을 작성하지 않았습니다.
        }

        //3) 해당 리뷰인덱스에 "comment" 넣기(등록,수정)
        try{
            reviewDao.reviewAnswer(reviewAnswerReq);
            return new ReviewAnswerRes(storeIdx, reviewAnswerReq.getReviewIdx(), reviewAnswerReq.getComment());
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }

    }

    //리뷰 총 별점
    public GetReviewStarTotalRes reviewStarTotal(int sellerIdx) throws BaseException {

        // 1) 사용자 가게 조회
        int storeIdx;
        try {
            storeIdx = storeDao.storeIdxBySellerIdx(sellerIdx);
        } catch (Exception e) {
            throw new BaseException(POST_STORES_NOT_REGISTERD); // 2030 : 사용자의 가게가 등록되어있지 않습니다.
        }

        // 2)리뷰 개수,별점 평균,별점 가지고 오기
        try{
            GetReviewStarRes reviewStar = reviewDao.reviewStarTotal(storeIdx);

            List<StarCountRatio> starCountRatios = new ArrayList<>();
            starCountRatios.add(new StarCountRatio("별 5개", reviewStar.getStar5(), (int) (reviewStar.getStar5() * 1.0 / reviewStar.getReviews_total() * 100)));
            starCountRatios.add(new StarCountRatio("별 4개", reviewStar.getStar4(), (int) (reviewStar.getStar4() * 1.0  / reviewStar.getReviews_total() * 100)));
            starCountRatios.add(new StarCountRatio("별 3개", reviewStar.getStar3(), (int) (reviewStar.getStar3() * 1.0  / reviewStar.getReviews_total() * 100)));
            starCountRatios.add(new StarCountRatio("별 2개", reviewStar.getStar2(), (int) (reviewStar.getStar2() * 1.0  / reviewStar.getReviews_total() * 100)));
            starCountRatios.add(new StarCountRatio("별 1개", reviewStar.getStar1(), (int) (reviewStar.getStar1() * 1.0  / reviewStar.getReviews_total() * 100)));

            return new GetReviewStarTotalRes(storeIdx, reviewStar.getStar_average(), reviewStar.getReviews_total(), starCountRatios);
        }catch (Exception e){
            throw new BaseException(DATABASE_ERROR);
        }

    }

    public ReviewReportRes reviewReport(int sellerIdx, ReviewReportReq reportReq) throws BaseException {
        int reportDone;
        try{
            reportDone = reviewDao.reviewReport(reportReq);
        }catch (Exception e){
            throw new BaseException(DATABASE_ERROR);

        }if(reportDone == 1){
            ReviewReportRes reportRes = new ReviewReportRes(reportReq.getReviewIdx(), reportDone);
            return reportRes;
        }else{
            throw new BaseException(DATABASE_ERROR);
        }
    }
}




