package com.umc.jatdauree.src.domain.app.menu;

import com.umc.jatdauree.config.BaseException;
import com.umc.jatdauree.config.BaseResponse;
import com.umc.jatdauree.src.domain.app.menu.dto.*;
import com.umc.jatdauree.src.domain.app.menu.service.AppMenuService;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/jat/app/menus")
public class AppMenuController {
    private final AppMenuService appMenuService;

    public AppMenuController(AppMenuService appMenuService) {
        this.appMenuService = appMenuService;
    }

    /**
     * 23.07.20 작성자 : 이운채
     * 메뉴클릭 시 메뉴품목상세
     * GET/jat/app/menus/detail ? todayIdx =
     * @return BaseResponse<GetAppMenuClickRes>
     */

    @ResponseBody
    @GetMapping("/detail")
    public BaseResponse<GetMenuDetailInfoRes> getMenuDetailInfo(@RequestParam("todaymenuIdx") int todaymenuIdx) {
        try {
            GetMenuDetailInfoRes getMenuDetailInfoRes = appMenuService.getMenuDetailInfo(todaymenuIdx);
            return new BaseResponse<>(getMenuDetailInfoRes);

        } catch (BaseException baseException) {
            return new BaseResponse<>(baseException.getStatus());
        }
    }
}
