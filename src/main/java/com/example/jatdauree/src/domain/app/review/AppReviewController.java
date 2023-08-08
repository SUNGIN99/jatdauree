package com.example.jatdauree.src.domain.app.review;


import com.example.jatdauree.config.BaseException;
import com.example.jatdauree.config.BaseResponse;
import com.example.jatdauree.src.domain.app.review.dto.*;
import com.example.jatdauree.src.domain.app.review.service.AppReviewService;
import com.example.jatdauree.utils.jwt.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/jat/app/reviews")
public class AppReviewController {

    @Autowired
    private final AppReviewService appReviewService;
    @Autowired
    private final JwtService jwtService;


    public AppReviewController(AppReviewService appReviewService, JwtService jwtService){
        this.appReviewService = appReviewService;
        this.jwtService = jwtService;
    }

    /**
     * Controller -1
     * 23.07.19 작성자 : 윤다은
     * 구매자가 리뷰 작성
     * POST /jat/app/reivews 리뷰 작성하기
     *
     */
    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostReviewRes> postReview(PostReviewReq postReviewReq){
        try{
            int customerIdx = jwtService.getUserIdx();
            PostReviewRes postReviewRes = appReviewService.postReview(customerIdx,postReviewReq);
            return new BaseResponse<> (postReviewRes);
        }catch (BaseException baseResponse){
            return new BaseResponse<>(baseResponse.getStatus());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Controller -2
     * 23.07.20 작성자 : 윤다은
     * 마이 떨이에서 모든 리뷰 조회
     * GET /jat/app/reivews 모든 리뷰 조회하기
     *
     */
    @ResponseBody
    @GetMapping("")
    public BaseResponse<GetReviewRes> myReviews(){
        try{
            int customerIdx = jwtService.getUserIdx();
            GetReviewRes getReviewRes = appReviewService.myReviews(customerIdx);
            return new BaseResponse<>(getReviewRes);
        }catch (BaseException baseResponse){
            return new BaseResponse<>(baseResponse.getStatus());
        }
    }

    /**
     * Controller -3
     * 23.07.20 작성자 : 윤다은
     * 리뷰 삭제하기
     * PATCH /jat/app/reivews
     */
    @ResponseBody
    @PatchMapping("")
    public BaseResponse<PatchReviewRes> deleteReview(@RequestBody PatchReviewReq patchReviewReq){
        try{
            int customerIdx = jwtService.getUserIdx();
            PatchReviewRes patchReviewRes = appReviewService.deleteReview(customerIdx, patchReviewReq);
            return new BaseResponse<>(patchReviewRes);
        }catch (BaseException baseException){
            return new BaseResponse<>(baseException.getStatus());
        }
    }
    /**
     * Controller -4
     * 23.07.20 작성자 : 윤다은
     * 리뷰 신고하기
     * POST /jat/app/reivews/report
     */
    @ResponseBody
    @PostMapping("/report")
    public BaseResponse<ReportReviewRes> reportReview(@RequestBody ReportReviewReq reportReviewReq){
        try{
            ReportReviewRes reportReviewRes = appReviewService.reportReview(reportReviewReq);
            return new BaseResponse<>(reportReviewRes);
        }catch (BaseException baseException){
            return new BaseResponse<>(baseException.getStatus());
        }
    }



}
