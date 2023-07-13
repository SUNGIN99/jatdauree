package com.example.jatdauree.src.domain.review.service;

import com.example.jatdauree.config.BaseException;
import com.example.jatdauree.config.BaseResponseStatus;
import com.example.jatdauree.src.domain.review.dao.ReviewDao;
import com.example.jatdauree.src.domain.review.dto.*;
import com.example.jatdauree.src.domain.store.dao.StoreDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.jatdauree.config.BaseResponseStatus.*;

@Service
public class ReviewService {

    private final StoreDao storeDao;
    private final ReviewDao reviewDao;

    @Autowired
    public ReviewService(StoreDao storeDao, ReviewDao reviewDao) {
        this.storeDao = storeDao;
        this.reviewDao = reviewDao;
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
            System.out.println("exception1: " + e);
            throw new BaseException(BaseResponseStatus.RESPONSE_ERROR);    // 리뷰 조회에 실패하였습니다.
        }

        // 3) 리뷰 주문 내의 메뉴 목록 조회
        try {
            for (ReviewItems item : reviewItems) {
                item.setOrderTodayMenu(reviewDao.orderTodayMenus(storeIdx, item.getOrderIdx()));
            }
            return new GetReviewRes(storeIdx, reviewItems);
        } catch (Exception e) {
            System.out.println("exception2: " + e);
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
        if (reviewAnswerReq.getComment() == null || reviewAnswerReq.getComment().length() == 0 ){
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

}




