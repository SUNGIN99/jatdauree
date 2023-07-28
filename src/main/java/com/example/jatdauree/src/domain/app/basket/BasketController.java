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
            //int userIdx = jwtService.getCustomerIdx();
            BasketStoreCheckRes basketRes = basketService.storeCheck(0, checkReq);
            return new BaseResponse<>(basketRes);
        }catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }

    @ResponseBody
    @GetMapping ("/count")
    BaseResponse<GetBasketCountRes> getBasketCount(){
        try{
            //int userIdx = jwtService.getCustomerIdx();
            GetBasketCountRes basketcount = basketService.getBasketCount(0);
            return new BaseResponse<>(basketcount);
        }catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }

    @ResponseBody
    @PostMapping("/")
    BaseResponse<PostBasketRes> postBasket(@RequestBody PostBasketReq basketReq){
        try{
            //int userIdx = jwtService.getCustomerIdx();
            PostBasketRes basketRes = basketService.postBasket(0, basketReq);
            return new BaseResponse<>(basketRes);
        }catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }

    @ResponseBody
    @GetMapping ("/")
    BaseResponse<GetBasketRes> getBasket(){
        try{
            //int userIdx = jwtService.getCustomerIdx();
            GetBasketRes basketRes = basketService.getBasket(0);
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
