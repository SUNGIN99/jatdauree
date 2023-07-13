package com.example.jatdauree.src.domain.review;

import com.example.jatdauree.config.BaseException;
import com.example.jatdauree.config.BaseResponse;
import com.example.jatdauree.src.domain.review.dto.*;
import com.example.jatdauree.src.domain.review.service.ReviewService;
import com.example.jatdauree.utils.jwt.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/jat/review")
public class ReviewController {

    private final JwtService jwtService;
    private final ReviewService reviewService;

    @Autowired
    public ReviewController(JwtService jwtService,ReviewService reviewService){
        this.jwtService = jwtService;
        this.reviewService = reviewService;

    }


    /**
     * 23.07.11 작성자 : 정주현
     * 등록되어 있는 리뷰 목록 조회
     * Get /jat/review
     * @return BaseResponse<GetReviewRes>
     */
    @ResponseBody
    @GetMapping("")
    public BaseResponse<GetReviewRes> review() throws BaseException {
        try{
            int sellerIdx = jwtService.getUserIdx();

            GetReviewRes getReviewRes = reviewService.getReview(sellerIdx);
            return new BaseResponse<>(getReviewRes);
        }catch(BaseException baseException){
            return new BaseResponse<>(baseException.getStatus());
        }
    }

    /**
     * 23.07.13 작성자 : 정주현, 김성인(service 조건문 수정)
     * 리뷰 답글 수정
     * Patch /jat/review/comment
     * @return BaseResponse<ReviewAnswerRes>
     */
    @ResponseBody
    @PatchMapping("/comment")
    public BaseResponse<ReviewAnswerRes> reviewAnswerModify(@RequestBody ReviewAnswerReq reviewAnswerReq) throws BaseException {
        try{
            int sellerIdx = jwtService.getUserIdx();

            ReviewAnswerRes reviewAnswerRes  = reviewService.reviewAnswer(sellerIdx,reviewAnswerReq);
            return new BaseResponse<>(reviewAnswerRes);
        }catch(BaseException baseException){
            return new BaseResponse<>(baseException.getStatus());
        }
    }

}
