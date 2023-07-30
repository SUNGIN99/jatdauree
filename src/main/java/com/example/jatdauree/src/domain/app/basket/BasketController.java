package com.example.jatdauree.src.domain.app.basket;

import com.example.jatdauree.config.BaseException;
import com.example.jatdauree.config.BaseResponse;
import com.example.jatdauree.src.domain.app.basket.dto.*;
import com.example.jatdauree.src.domain.app.basket.service.BasketService;
import com.example.jatdauree.utils.jwt.JwtService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/jat/app/basket")
public class BasketController {
    private final JwtService jwtService;
    private final BasketService basketService;

    public BasketController(JwtService jwtService, BasketService basketService) {
        this.jwtService = jwtService;
        this.basketService = basketService;
    }

    @ResponseBody
    @PostMapping("/same-store")
    BaseResponse<BasketStoreCheckRes> storeCheck(@RequestBody BasketStoreCheckReq checkReq){
        try{
            int userIdx = jwtService.getUserIdx();
            BasketStoreCheckRes basketRes = basketService.storeCheck(userIdx, checkReq);
            return new BaseResponse<>(basketRes);
        }catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }

    @ResponseBody
    @GetMapping ("/count")
    BaseResponse<GetBasketCountRes> getBasketCount(){
        try{
            int userIdx = jwtService.getUserIdx();
            GetBasketCountRes basketcount = basketService.getBasketCount(userIdx);
            return new BaseResponse<>(basketcount);
        }catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }

    @ResponseBody
    @PostMapping("/")
    BaseResponse<PostBasketRes> postBasket(@RequestBody PostBasketReq basketReq){
        try{
            int userIdx = jwtService.getUserIdx();
            PostBasketRes basketRes = basketService.postBasket(userIdx, basketReq);
            return new BaseResponse<>(basketRes);
        }catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }

    @ResponseBody
    @GetMapping ("/")
    BaseResponse<GetBasketRes> getBasket(){
        try{
            int userIdx = jwtService.getUserIdx();
            GetBasketRes basketRes = basketService.getBasket(userIdx);
            return new BaseResponse<>(basketRes);
        }catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }

    /*@ResponseBody
    @PatchMapping("/")
    BaseResponse<PatchBasketRes> patchBasket(@RequestBody GetBasketRes basketReq){
        return null;
    }*/

}
