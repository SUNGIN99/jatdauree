package com.example.jatdauree.src.domain.review;

import com.example.jatdauree.config.BaseException;
import com.example.jatdauree.config.BaseResponse;
import com.example.jatdauree.src.domain.review.dto.*;
import com.example.jatdauree.src.domain.review.service.ReviewService;
import com.example.jatdauree.src.domain.seller.dto.PostLoginReq;
import com.example.jatdauree.src.domain.seller.dto.PostLoginRes;
import com.example.jatdauree.utils.jwt.JwtService;
import com.example.jatdauree.utils.jwt.JwtTokenProvider;
import io.jsonwebtoken.Jwt;
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
     * Post /jat/review
     * @return BaseResponse<>(getReviewRes)
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
     * 23.07.12 작성자 : 정주현
     * 리뷰 답글 달기
     * Post /jat/review/comment
     * @return BaseResponse<GetReviewRes>
     */
    @ResponseBody
    @PostMapping("comment")
    public BaseResponse<PostReviewAnswerRes> reviewAnswer(@RequestBody PostReviewAnswerReq postReviewAnswerReq) throws BaseException {
        int sellerIdx = jwtService.getUserIdx();
        try{
            PostReviewAnswerRes postReviewAnswerRes  = reviewService.reviewAnswer(sellerIdx,postReviewAnswerReq);
            return new BaseResponse<>(postReviewAnswerRes);
        }catch(BaseException baseException){
            return new BaseResponse<>(baseException.getStatus());
        }
    }

    /**
     * 23.07.13 작성자 : 정주현
     * 리뷰 답글 수정
     * Post /jat/review/comment
     * @return BaseResponse<GetReviewRes>
     */
    @ResponseBody
    @PatchMapping("comment")
    public BaseResponse<PatchReviewRes> reviewAnswer(@RequestBody PatchReviewReg patchReviewReg) throws BaseException {
        int sellerIdx = jwtService.getUserIdx();
        try{
            PatchReviewRes patchReviewRes  = reviewService.reviewModify(sellerIdx,patchReviewReg);
            return new BaseResponse<>(patchReviewRes);
        }catch(BaseException baseException){
            return new BaseResponse<>(baseException.getStatus());
        }
    }

}
