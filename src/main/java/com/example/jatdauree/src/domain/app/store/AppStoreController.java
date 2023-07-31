package com.example.jatdauree.src.domain.app.store;

import com.example.jatdauree.config.BaseException;
import com.example.jatdauree.config.BaseResponse;
import com.example.jatdauree.src.domain.app.store.dto.*;
import com.example.jatdauree.src.domain.app.store.service.AppStoreService;
import com.example.jatdauree.utils.jwt.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
//import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.jatdauree.config.BaseResponseStatus.GET_MENU_ERROR;


@RestController
@RequestMapping("/jat/app/stores")
public class AppStoreController {
    @Autowired
    private final AppStoreService appStoreService;
    @Autowired
    private final JwtService jwtService;

    public AppStoreController(AppStoreService appStoreService, JwtService jwtService) {
        this.appStoreService = appStoreService;
        this.jwtService = jwtService;
    }


////이게 최종
    /**
     * 23.07.20 작성자 : 이윤채
     * 식당 세부정보 ( 메뉴, 정보, 리뷰) Query String
     * GET/jat/app/stores/info/details ? storeIdx= & type=
     * @return BaseResponse menu=<GetAppMenuItemRes>,info=<GetAppStoreDetailInfoRes>,review=< GetAppStoreReviewRes>
     */

    // jat/app/stores/info/details?storeIdx=2&type=menu
    // jat/app/stores/info/details?storeIdx=58&type=info
    //// jat/app/stores/info/details?storeIdx=1&type=review
    @ResponseBody
    @GetMapping("/info/details")
    public BaseResponse<?> getAppStoreDetails(@RequestParam("storeIdx") int storeIdx, @RequestParam("type") String type) {
        try {
            if ("menu".equalsIgnoreCase(type)) {
                GetAppStoreDetailMenuRes getAppStoreDetailMenuRes = appStoreService.getAppStoreDetailMenu(storeIdx);
                return new BaseResponse<>(getAppStoreDetailMenuRes);
            } else if ("info".equalsIgnoreCase(type)) {
                GetAppStoreDetailInfoRes getAppStoreDetailInfoRes = appStoreService.getAppStoreDetailInfo(storeIdx);
                return new BaseResponse<>(getAppStoreDetailInfoRes);
            } else if ("review".equalsIgnoreCase(type)) {
                GetAppStoreDetailReviewRes getAppStoreDetailReviewRes = appStoreService.getAppStoreDetailReview(storeIdx);
                return new BaseResponse<>(getAppStoreDetailReviewRes);
            }else {
                // 유효하지 않은 type 값일 경우 에러 처리
                return new BaseResponse<>(GET_MENU_ERROR);
            }
        } catch (BaseException baseException) {
            return new BaseResponse<>(baseException.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/address")
    public BaseResponse<List<StoreListXY>> getStoreListByAddr(@RequestParam("query") String query){
        try{
            int customerIdx = jwtService.getUserIdx();
            List<StoreListXY> storeList = appStoreService.getStoreListByAddr(query);
            return new BaseResponse<>(storeList);
        }catch (BaseException baseException) {
            return new BaseResponse<>(baseException.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/preview")
    public BaseResponse<List<StorePreviewRes>> getStorePreview(@RequestBody StorePreviewReq previewReq){
        try{
            int customerIdx = jwtService.getUserIdx();

            // 가게 Idx, 가게 이름, 가게 로고/간판 사진 url, 별점, 거리, 시간, 구독 여부
            List<StorePreviewRes> storeList = appStoreService.getStorePreview(customerIdx, previewReq);
            return new BaseResponse<>(storeList);
        }catch (BaseException baseException) {
            return new BaseResponse<>(baseException.getStatus());
        }
    }




}
