package com.example.jatdauree.src.domain.todaymenu;


import com.example.jatdauree.config.BaseException;
import com.example.jatdauree.config.BaseResponse;
import com.example.jatdauree.src.domain.todaymenu.service.TodaymenuService;
import com.example.jatdauree.src.domain.todaymenu.dto.GetMenusSearchRes;
import com.example.jatdauree.utils.jwt.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/jat/menus")
public class TodayManuController {

    private final TodaymenuService todaymenuService;

    private final JwtService jwtService;

    @Autowired
    public TodayManuController(TodaymenuService todaymenuService, JwtService jwtService)
    {
        this.todaymenuService = todaymenuService;
        this.jwtService = jwtService;
    }

    /**
     * 23.07.04 작성자 : 정주현
     * 등록된 메뉴 조회
     * GET /jat/menus
     * @param
     * @return BaseResponse<menusSeachRes></menusSeachRes>
     */
    @ResponseBody
    @GetMapping("")
    public BaseResponse<ArrayList<GetMenusSearchRes>> Search(){
        try{
            int sellerIdx = jwtService.getUserIdx();
            ArrayList<GetMenusSearchRes> getMenusSearchRes = todaymenuService.search(sellerIdx);
            return new BaseResponse<>(getMenusSearchRes);
        }catch (BaseException baseException){
            return new BaseResponse<>(baseException.getStatus());
        }
    }
}

/*

     * 23.07.04 작성자 : 정주현
     * 오늘의 떨이 메뉴 등록
     * Post /jat/today
     * @param @RequestBody postSigunUpReq
     * @return BaseResponse<PostSignUpRes>


    @ResponseBody
    @PostMapping("/today")
    public BaseResponse<ArrayList<PostTodayMenuRegRes>> Search(@RequestBody PostTodayMenuRegReq postTodayMenuRegReq){
        try{
            ArrayList<PostTodayMenuRegRes> menusSearchRes = todaymenuService.Register(postTodayMenuRegReq);
            return new BaseResponse<>(postTodayMenuRegReq);
        }catch (BaseException baseException){
            return new BaseResponse<>(baseException.getStatus());
        }
    }


}
*/