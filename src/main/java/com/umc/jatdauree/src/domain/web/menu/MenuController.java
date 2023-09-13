package com.umc.jatdauree.src.domain.web.menu;

import com.umc.jatdauree.config.BaseException;
import com.umc.jatdauree.config.BaseResponse;
import com.umc.jatdauree.src.domain.web.menu.dto.*;
import com.umc.jatdauree.src.domain.web.menu.service.MenuService;
import com.umc.jatdauree.utils.jwt.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    public BaseResponse<PostMenuRes> menuRegister(PostMenuReq postMenuReq) {
        try {
            int sellerIdx = jwtService.getUserIdx();

            PostMenuRes postMenuRes = menuService.menuRegister(sellerIdx, postMenuReq);
            return new BaseResponse<>(postMenuRes);
        } catch (BaseException baseException) {
            return new BaseResponse<>(baseException.getStatus());
        }
    }

    /**
     * 23.07.16 작성자 : 김성인
     * 등록된 메뉴 조회
     * GET /jat/menus
     * @return BaseResponse<GetMenuItemsRes>
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
     * 23.07.18 작성자 : 김성인
     * 메뉴수정
     * PATCH /jat/menus
     * @return BaseResponse<GetMenuItemsRes>
     */
    @ResponseBody
    @PatchMapping("")
    public BaseResponse<PatchMenuRes> menuUpdate(PatchMenuReq postMenuReq) {
        try {
            int sellerIdx = jwtService.getUserIdx();

            PatchMenuRes patchMenuRes = menuService.menuUpdate(sellerIdx, postMenuReq);
            return new BaseResponse<>(patchMenuRes);
        } catch (BaseException baseException) {
            return new BaseResponse<>(baseException.getStatus());
        }
    }




}
