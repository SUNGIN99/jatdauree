package com.umc.jatdauree.src.domain.app.subscribe;
import com.umc.jatdauree.config.BaseException;
import com.umc.jatdauree.src.domain.app.subscribe.service.AppSubscribeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.umc.jatdauree.utils.jwt.JwtService;
import com.umc.jatdauree.config.BaseResponse;
import com.umc.jatdauree.src.domain.app.subscribe.dto.*;

import java.util.List;


@RestController
@RequestMapping("/jat/app/subscription")
public class AppSubscribeController {
    @Autowired
    private final AppSubscribeService appSubscribeService;
    @Autowired
    private final JwtService jwtService;

    public AppSubscribeController(AppSubscribeService appSubscribeService, JwtService jwtService){this.appSubscribeService=appSubscribeService;this.jwtService = jwtService;}


    /**
     * 23.08.03 작성자 : 이운채
     * 식당 구독 하기 / 구독취소
     * POST /jat/app/subscription
     *
     */
    @ResponseBody
    @PostMapping("")
    public BaseResponse<AppSubscriptionRes> subscription(@RequestBody AppSubscriptionReq appSubscriptionReq){
        try{
            int customerIdx = jwtService.getUserIdx();
            AppSubscriptionRes appsubscriptionRes = appSubscribeService.subscription(customerIdx,appSubscriptionReq);
            return new BaseResponse<>(appsubscriptionRes);
        }catch (BaseException baseException){
            return new BaseResponse<>(baseException.getStatus());
        }
    }


    /**
     * 23.08.03 작성자 : 이운채
     * 구독 한 가게 리스트
     * GET /jat/app/subscription
     */
    @ResponseBody
    @GetMapping("")
    public BaseResponse<List<GetAppSubscriptionRes>>getSubscription(
            @RequestParam("longitude") Double longitude,
            @RequestParam("latitude") Double latitude){
        try {
            int customerIdx = jwtService.getUserIdx();
            List<GetAppSubscriptionRes> getAppSubscriptionRes =appSubscribeService.getSubscriptionList(customerIdx, longitude, latitude);
            return new BaseResponse<>(getAppSubscriptionRes);

        }catch (BaseException baseException) {
            return new BaseResponse<>(baseException.getStatus());
        }
    }



}
