package com.example.jatdauree.src.domain.review.service;

import com.example.jatdauree.config.BaseException;
import com.example.jatdauree.config.BaseResponseStatus;
import com.example.jatdauree.src.domain.review.dao.ReviewDao;
import com.example.jatdauree.src.domain.review.dto.GetReviewRes;
import com.example.jatdauree.src.domain.review.dto.PostReviewAnswerReq;
import com.example.jatdauree.src.domain.review.dto.PostReviewAnswerRes;
import com.example.jatdauree.src.domain.review.dto.ReviewItems;
import com.example.jatdauree.src.domain.store.dao.StoreDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.jatdauree.config.BaseResponseStatus.POST_STORES_NOT_REGISTERD;

@Service
public class ReviewService {

    private final StoreDao storeDao;
    private final ReviewDao reviewDao;

    @Autowired
    public ReviewService(StoreDao storeDao, ReviewDao reviewDao){
        this.storeDao = storeDao;
        this.reviewDao= reviewDao;
    }

    //리뷰 조회
    public GetReviewRes getReview(int sellerIdx) throws BaseException {
        // 1) 사용자 가게 조회
        int storeIdx;
        try{
            storeIdx = storeDao.storeIdxBySellerIdx(sellerIdx);
        } catch (Exception e) {
            throw new BaseException(POST_STORES_NOT_REGISTERD); // 2030 : 사용자의 가게가 등록되어있지 않습니다.
        }

        // 2) 리뷰 조회
        List<ReviewItems> reviewItems;
        try {
            reviewItems = reviewDao.reviewItems(storeIdx);
        }catch (Exception e) {
            throw new BaseException(BaseResponseStatus.RESPONSE_ERROR);    // 리뷰 조회에 실패하였습니다.
        }
        //return new GetReviewRes(storeIdx,reviewItems);


        // 3) 리뷰 주문 내의 메뉴 목록 조회
        try{
            for (ReviewItems item : reviewItems){
                item.setOrderTodayMenu(reviewDao.orderTodayMenus(storeIdx, item.getOrderIdx()));
            }
            return new GetReviewRes(storeIdx, reviewItems);
        }catch (Exception e) {
            throw new BaseException(BaseResponseStatus.RESPONSE_ERROR);    // 리뷰 조회에 실패하였습니다.
        }
    }

/*
    //리뷰 답글 달기
    public PostReviewAnswerRes postReviewAnswerReq(int sellerIdx, PostReviewAnswerReq postReviewAnswerReq) throws BaseException {
        // 1) 사용자 가게 조회
        int storeIdx;
        try{
            storeIdx = storeDao.storeIdxBySellerIdx(sellerIdx);
        } catch (Exception e) {
            throw new BaseException(POST_STORES_NOT_REGISTERD); // 2030 : 사용자의 가게가 등록되어있지 않습니다.
        }

    }

*/

}

