package com.umc.jatdauree.src.domain.app.store;

import com.umc.jatdauree.config.BaseException;
import com.umc.jatdauree.config.BaseResponse;
import com.umc.jatdauree.src.domain.app.store.dto.*;
import com.umc.jatdauree.src.domain.app.store.service.AppStoreService;
import com.umc.jatdauree.utils.jwt.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
//import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.umc.jatdauree.config.BaseResponseStatus.GET_MENU_ERROR;


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
    public BaseResponse<?> getAppStoreDetails(
            @RequestParam("storeIdx") int storeIdx,
            @RequestParam("type") String type) {
        try {
            if (type.equalsIgnoreCase("menu")) {
                GetAppStoreDetailMenuRes getAppStoreDetailMenuRes = appStoreService.getAppStoreDetailMenu(storeIdx);
                return new BaseResponse<>(getAppStoreDetailMenuRes);
            } else if (type.equalsIgnoreCase("info")) {
                GetAppStoreDetailInfoRes getAppStoreDetailInfoRes = appStoreService.getAppStoreDetailInfo(storeIdx);
                return new BaseResponse<>(getAppStoreDetailInfoRes);
            } else if (type.equalsIgnoreCase("review")) {
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
    public BaseResponse<List<StoreListXY>> getStoreListByAddr(
            @RequestParam("max_lon") double maxX,
            @RequestParam("max_lat") double maxY,
            @RequestParam("min_lon") double minX,
            @RequestParam("min_lat") double minY){
        try{
            int customerIdx = jwtService.getUserIdx();
            List<StoreListXY> storeList = appStoreService.getStoreListByAddr(maxX, maxY, minX, minY);
            return new BaseResponse<>(storeList);
        }catch (BaseException baseException) {
            return new BaseResponse<>(baseException.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/preview")
    public BaseResponse<List<StorePreviewRes>> getStorePreview(
            @RequestParam("storeIdx") int storeIdx,
            @RequestParam("longitude") Double longitude,
            @RequestParam("latitude") Double latitude){
        try{
            int customerIdx = jwtService.getUserIdx();

            // 가게 Idx, 가게 이름, 가게 로고/간판 사진 url, 별점, 거리, 시간, 구독 여부
            List<StorePreviewRes> storeList = appStoreService.getStorePreview(customerIdx, storeIdx, longitude, latitude);
            return new BaseResponse<>(storeList);
        }catch (BaseException baseException) {
            return new BaseResponse<>(baseException.getStatus());
        }
    }

    /**
     * 23.07.29 작성자 : 이윤채
     * 식당 기본정보  (가게이름, 찜수, 별점, 위치정보)
     * GET//jat/app/stores/info ? storeIdx=
     * @return GetAppStoreInfoRes
     */

    @ResponseBody
    @GetMapping("/info")
    public BaseResponse<GetAppStoreInfoRes> getAppStoreInfo(
            @RequestParam("storeIdx") int storeIdx,
            @RequestParam("longitude") Double longitude,
            @RequestParam("latitude") Double latitude){

        try {
            int userIdx = jwtService.getUserIdx();
            GetAppStoreInfoRes getAppStoreInfoRes = appStoreService.getAppStoreInfo(userIdx, storeIdx, longitude, latitude);
            return new BaseResponse<>(getAppStoreInfoRes);
        } catch (BaseException baseException) {
            return new BaseResponse<>(baseException.getStatus());
        }
    }

    /**
     * 23.08.01작성자 : 이윤채
     * 식당 목록 보기(거리순)
     * GET /jat/app/stores
     * @return
     */
    @ResponseBody
    @GetMapping("")
    public BaseResponse<List<StorePreviewRes>>GetAppStore(
            @RequestParam("longitude") Double longitude,
            @RequestParam("latitude") Double latitude){
        try {
            int userIdx = jwtService.getUserIdx();
            List<StorePreviewRes> getAppStoreRes = appStoreService.getAppStoreList(userIdx, longitude, latitude);
            return new BaseResponse<>(getAppStoreRes);
        }catch (BaseException baseException) {
            return new BaseResponse<>(baseException.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/map")
    public BaseResponse<StorePoint> storePoint(@RequestParam("storeIdx") int storeIdx){
        try {
            int userIdx = jwtService.getUserIdx();
            StorePoint storeP = appStoreService.storePoint(storeIdx);
            return new BaseResponse<>(storeP);
        }catch (BaseException baseException) {
            return new BaseResponse<>(baseException.getStatus());
        }
    }




}
