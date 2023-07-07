package com.example.jatdauree.src.domain.menu;

import com.example.jatdauree.config.BaseException;
import com.example.jatdauree.config.BaseResponse;
import com.example.jatdauree.src.domain.menu.dto.*;
import com.example.jatdauree.src.domain.menu.service.MenuService;
import com.example.jatdauree.src.domain.menu.dto.GetMenuItemsRes;
import com.example.jatdauree.utils.jwt.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
     * 23.07.07 작성자 : 이윤채, 김성인
     * 메뉴등록(메인:status "M", 서브:status "S")
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
     * @return BaseResponse<GetMenusSearchRes>
     */
    @ResponseBody
    @GetMapping("")
    public BaseResponse<GetMenuItemsRes> getStoreMenuList(){
        try{
            int sellerIdx = jwtService.getUserIdx();

            GetMenuItemsRes getMenuItemsRes = menuService.getStoreMenuList(sellerIdx);
            return new BaseResponse<>(getMenuItemsRes);
        }catch (BaseException baseException){
            return new BaseResponse<>(baseException.getStatus());
        }
    }

    /**
     * 23.07.06 작성자 : 이윤채
     * 메뉴 수정
     * Patch/jat/menu/menuUpdate
     * @param @RequestBody PostMenuUpReq
     * @return BaseResponse<postMenuUpRes>
     *
     */
    /*@ResponseBody
    @PatchMapping("")
    public BaseResponse<PostMenuUpRes> menuUpdate(@RequestBody PostMenuUpReq postMenuUpReq){
        try {
            PostMenuUpRes postMenuUpRes =menuService.menuUpdate(postMenuUpReq);
            return new BaseResponse<>(postMenuUpRes);
        }catch (BaseException baseException){
            return new BaseResponse<>(baseException.getStatus());
        }
    }*/

}
