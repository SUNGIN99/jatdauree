package com.example.jatdauree.src.domain.menu;

import com.example.jatdauree.config.BaseException;
import com.example.jatdauree.config.BaseResponse;
import com.example.jatdauree.src.domain.menu.dto.*;
import com.example.jatdauree.src.domain.menu.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/jat/menus")
public class MenuController {
    private final MenuService menuService;

    @Autowired
    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }


    //메뉴등록(메인:status "M", 서브:status "S") //메뉴등록 할때 원산지도 같이
    // /jat/menus
    /**
     * MenuController - 1
     * 23.07.06 작성자 : 이윤채
     * menuRegiste 메뉴등록 POST
     * post/jat/menus
     * @param @RequestBody PostMenuReq
     * @return BaseResponse<PostMenuRes>
     */
    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostMenuRes> menuRegister(@RequestBody PostMenuReq postMenuReq) {
        try {
            PostMenuRes postMenuRes = menuService.menuRegister(postMenuReq);
            return new BaseResponse<>(postMenuRes);

        } catch (BaseException baseException) {
            return new BaseResponse<>(baseException.getStatus());
        }
    }

    //원산지 등록
    // /jat/menus/ingredient
    /**
     * MenuController - 2
     * 23.07.06 작성자 : 이윤채
     * ingredientRegister 원산지 등록 POST
     * POST/jat/menus/ingrdient
     * @param @RequestBody PostIngredientReq
     *  @return BaseResponse<PostIngredientRes>
     */
    @ResponseBody
    @PostMapping("/ingredient")
    public BaseResponse<PostIngredientRes> ingredientRegister(@RequestBody PostIngredientReq postIngredientReq){
        try {
            PostIngredientRes postIngredientRes =menuService.ingredientRegister(postIngredientReq);
            return new BaseResponse<>(postIngredientRes);
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
