package com.example.jatdauree.src.domain.todaymenu;


import com.example.jatdauree.config.BaseException;
import com.example.jatdauree.config.BaseResponse;
import com.example.jatdauree.src.domain.todaymenu.dto.GetMainPageMenu;
import com.example.jatdauree.src.domain.todaymenu.dto.PostMainPageTMenu;
import com.example.jatdauree.src.domain.todaymenu.service.TodayMenuService;
import com.example.jatdauree.utils.jwt.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/jat/today")
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
     * 23.07.17 작성자 : 정주현, 김성인
     * 등록된 메뉴 조회
     * GET /jat/today
     * @return BaseResponse<GetMainPageMenu>
     */
    @ResponseBody
    @GetMapping("")
    public BaseResponse<GetMainPageMenu> getTodayMenuList(){
        try{
            int sellerIdx = jwtService.getUserIdx();

            GetMainPageMenu getMenuItemsRes = todaymenuService.getTodayMenuList(sellerIdx);
            // 미등록 : -1, 등록 : 0, 수정 : 1, 삭제 : 2
            return new BaseResponse<>(getMenuItemsRes);
        }catch (BaseException baseException){
            return new BaseResponse<>(baseException.getStatus());
        }
    }


     /**
     * 23.07.07 작성자 : 정주현, 김성인
     * 오늘의 떨이 메뉴 등록
     * Post /jat/today
     */
    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostMainPageTMenu> registerTodayMenu(@RequestBody PostMainPageTMenu postTodayMenuReg) {
        try {
            int sellerIdx = jwtService.getUserIdx();
            PostMainPageTMenu postTodayMenuRegRes = todaymenuService.registerTodayMenu(sellerIdx, postTodayMenuReg);
            return new BaseResponse<>(postTodayMenuRegRes);
        } catch (BaseException baseException) {
            return new BaseResponse<>(baseException.getStatus());
        }
    }


}
























