package com.example.jatdauree.src.domain.menu;

import com.example.jatdauree.config.BaseException;
import com.example.jatdauree.config.BaseResponse;
import com.example.jatdauree.src.domain.menu.dto.*;
import com.example.jatdauree.src.domain.menu.service.MenuService;
import com.example.jatdauree.src.domain.todaymenu.dto.GetMenusSearchRes;
import com.example.jatdauree.utils.jwt.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/jat/menus")
public class MenuController {
    private final MenuService menuService;
    private final JwtService jwtService;


    @Autowired
    public MenuController(MenuService menuService, JwtService jwtService) {
        this.menuService = menuService;
        this.jwtService = jwtService;
    }

    /**
     * MenuController - 1
     * 23.07.07 작성자 : 이윤채, 김성인
     * menuRegiste 메뉴등록 POST
     * 메뉴등록(메인:status "M", 서브:status "S") //메뉴등록 할때 원산지도 같이
     *
     * Post /jat/menus
     * @param @RequestBody PostMenuReq
     * @return BaseResponse<PostMenuRes>
     */
    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostMenuRes> menuRegister(@RequestBody PostMenuReq postMenuReq) {
        try {
            int sellerIdx = jwtService.getUserIdx();

            PostMenuRes postMenuRes = menuService.menuRegister(sellerIdx, postMenuReq);
            return new BaseResponse<>(postMenuRes);
        } catch (BaseException baseException) {
            return new BaseResponse<>(baseException.getStatus());
        }
    }

    /**
     * 23.07.07 작성자 : 정주현, 김성인
     * 등록된 메뉴 조회
     * GET /jat/menus
     * @return BaseResponse<ArrayList<GetMenusSearchRes>> </ArrayList<GetMenusSearchRes>>
     */
    @ResponseBody
    @GetMapping("")
    public BaseResponse<ArrayList<GetMenusSearchRes>> searchMenu(){
        try{
            int sellerIdx = jwtService.getUserIdx();

            ArrayList<GetMenusSearchRes> getMenusSearchRes = menuService.searchMenu(sellerIdx);
            return new BaseResponse<>(getMenusSearchRes);
        }catch (BaseException baseException){
            return new BaseResponse<>(baseException.getStatus());
        }
    }


//상태값수정
    /**
     * MenuController - 3
     * 23.07.06 작성자 : 이윤채
     * statusChange 상태값 수정 POST
     * POST/jat/menus/statusChange
     * @param @RequestBody PostStatusUpReq
     *  @return BaseResponse<postStatusUpRes>
     */
    @ResponseBody
    @PostMapping("/statusChange")
    public BaseResponse<PostStatusUpRes> statusChange(@RequestBody PostStatusUpReq postStatusUpReq){
        try {
            PostStatusUpRes postStatusUpRes =menuService.statusChange(postStatusUpReq);
            return new BaseResponse<>(postStatusUpRes);
        }catch (BaseException baseException){
            return new BaseResponse<>(baseException.getStatus());
        }
    }


    //메뉴 수정 /jat/menu/menuUpdate
    /**
     * MenuController - 4
     * 23.07.06 작성자 : 이윤채
     * menuUpdate 메뉴 수정  POST
     * POST/jat/menu/menuUpdate
     * @param @RequestBody PostMenuUpReq
     * @return BaseResponse<postMenuUpRes>
     *
     */
    @ResponseBody
    @PostMapping("/menuUpdate")
    public BaseResponse<PostMenuUpRes> menuUpdate(@RequestBody PostMenuUpReq postMenuUpReq){
        try {
            PostMenuUpRes postMenuUpRes =menuService.menuUpdate(postMenuUpReq);
            return new BaseResponse<>(postMenuUpRes);
        }catch (BaseException baseException){
            return new BaseResponse<>(baseException.getStatus());
        }
    }

}
