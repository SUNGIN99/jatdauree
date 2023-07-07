package com.example.jatdauree.src.domain.todaymenu;


import com.example.jatdauree.config.BaseException;
import com.example.jatdauree.config.BaseResponse;
import com.example.jatdauree.src.domain.todaymenu.dto.GetMenusSearchRes;
import com.example.jatdauree.src.domain.todaymenu.dto.PostTodayMenuRegReq;
import com.example.jatdauree.src.domain.todaymenu.dto.PostTodayMenuRegRes;
import com.example.jatdauree.src.domain.todaymenu.service.TodayMenuService;
import com.example.jatdauree.utils.jwt.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/jat/menus")
public class TodayMenuController {

    private final TodayMenuService todaymenuService;

    private final JwtService jwtService;

    @Autowired
    public TodayMenuController(TodayMenuService todaymenuService, JwtService jwtService)
    {
        this.todaymenuService = todaymenuService;
        this.jwtService = jwtService;
    }

    /**
     * 23.07.04 작성자 : 정주현
     * 등록된 메뉴 조회
     * GET /jat/menus
     * @return BaseResponse<ArrayList<GetMenusSearchRes>> </ArrayList<GetMenusSearchRes>>
     */
    @ResponseBody
    @GetMapping("")
    public BaseResponse<ArrayList<GetMenusSearchRes>> searchMenu(){
        try{
            int sellerIdx = jwtService.getUserIdx();
            System.out.println("jwt SellerIdx : " + sellerIdx);
            ArrayList<GetMenusSearchRes> getMenusSearchRes = todaymenuService.searchMenu(sellerIdx);
            return new BaseResponse<>(getMenusSearchRes);
        }catch (BaseException baseException){
            return new BaseResponse<>(baseException.getStatus());
        }
    }



     /**
     * 23.07.06 작성자 : 정주현
     * 오늘의 떨이 메뉴 등록
     * Post /jat/menus/today
     */
    @ResponseBody
    @PostMapping("/today")
    public BaseResponse<ArrayList<PostTodayMenuRegRes>> registerTodayMenu(@RequestBody PostTodayMenuRegReq postTodayMenuRegReq) {
        try {
            int sellerIdx = jwtService.getUserIdx();
            ArrayList<PostTodayMenuRegRes> postTodayMenuRegRes = todaymenuService.registerTodayMenu(postTodayMenuRegReq);
            return new BaseResponse<>(postTodayMenuRegRes);
        } catch (BaseException baseException) {
            return new BaseResponse<>(baseException.getStatus());
        }
    }
}
























