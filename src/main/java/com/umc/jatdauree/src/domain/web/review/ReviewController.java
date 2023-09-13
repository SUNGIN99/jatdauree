package com.umc.jatdauree.src.domain.web.review;

import com.umc.jatdauree.config.BaseException;
import com.umc.jatdauree.config.BaseResponse;
import com.umc.jatdauree.src.domain.web.review.dto.*;
import com.umc.jatdauree.src.domain.web.review.service.ReviewService;
import com.umc.jatdauree.src.domain.web.review.dto.ReviewReport;
import com.umc.jatdauree.src.domain.web.review.dto.ReviewReportAdmit;
import com.umc.jatdauree.utils.jwt.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public BaseResponse<GetReviewRes> review() {
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
    public BaseResponse<ReviewAnswerRes> reviewAnswerModify(@RequestBody ReviewAnswerReq reviewAnswerReq){
        try{
            int sellerIdx = jwtService.getUserIdx();

            ReviewAnswerRes reviewAnswerRes  = reviewService.reviewAnswer(sellerIdx,reviewAnswerReq);
            return new BaseResponse<>(reviewAnswerRes);
        }catch(BaseException baseException){
            return new BaseResponse<>(baseException.getStatus());
        }
    }

    /**
     * 23.07.13 작성자 : 정주현
     * 리뷰 총 리뷰 현황
     * Patch /jat/review/average
     * @return BaseResponse<GetAverageReviewRes>
     */
    @ResponseBody
    @GetMapping("/average")
    public BaseResponse<GetReviewStarTotalRes> reviewStarTotal()  {
        try{
            int sellerIdx = jwtService.getUserIdx();
            GetReviewStarTotalRes getReviewStarTotalRes  = reviewService.reviewStarTotal(sellerIdx);
            return new BaseResponse<>(getReviewStarTotalRes);
        }catch(BaseException baseException){
            return new BaseResponse<>(baseException.getStatus());
        }
    }

    @ResponseBody
    @PostMapping("/report")
    public BaseResponse<ReviewReportRes> reviewReport(@RequestBody ReviewReportReq reportReq) {
        try{
            int sellerIdx = jwtService.getUserIdx();
            ReviewReportRes reportRes = reviewService.reviewReport(sellerIdx, reportReq);
            return new BaseResponse<>(reportRes);
        }catch(BaseException baseException){
            return new BaseResponse<>(baseException.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/admin")
    public BaseResponse<List<ReviewReport>> reviewReportAdmin(){
        try{
            int sellerIdx = jwtService.getUserIdx();
            List<ReviewReport> reportList = reviewService.reviewReportAdmin();

            return new BaseResponse<>(reportList);
        }catch(BaseException baseException){
            return new BaseResponse<>(baseException.getStatus());
        }
    }

    @ResponseBody
    @PatchMapping("/admin")
    public BaseResponse<ReviewReportReq> reviewReportDone(@RequestBody ReviewReportAdmit reportAdmit){
        try{
            int sellerIdx = jwtService.getUserIdx();
            ReviewReportReq reportDone = reviewService.reviewReportDone(reportAdmit);

            return new BaseResponse<>(reportDone);
        }catch(BaseException baseException){
            return new BaseResponse<>(baseException.getStatus());
        }
    }



}
