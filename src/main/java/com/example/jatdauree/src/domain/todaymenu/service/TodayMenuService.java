package com.example.jatdauree.src.domain.todaymenu.service;

import com.example.jatdauree.config.BaseException;
import com.example.jatdauree.src.domain.store.dao.StoreDao;
import com.example.jatdauree.src.domain.todaymenu.dao.TodayMenuDao;
import com.example.jatdauree.src.domain.todaymenu.dto.PostTodayMenuRegReq;
import com.example.jatdauree.src.domain.todaymenu.dto.PostTodayMenuRegRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.example.jatdauree.config.BaseResponseStatus.*;

@Service
public class TodayMenuService {

    private final TodayMenuDao todayMenuDao;
    private final StoreDao storeDao;

    @Autowired
    public TodayMenuService(TodayMenuDao todayMenuDao, StoreDao storeDao) {
        this.todayMenuDao = todayMenuDao;
        this.storeDao = storeDao;
    }

    // 떨이메뉴 등록
    public PostTodayMenuRegRes registerTodayMenu(int sellerIdx, PostTodayMenuRegReq postTodayMenuRegReq) throws BaseException {
        // 1) 사용자 가게 조회
        int storeIdx;
        try{
            storeIdx = storeDao.storeIdxBySellerIdx(sellerIdx);
        } catch (Exception e) {
            throw new BaseException(POST_STORES_NOT_REGISTERD); // 2030 : 사용자의 가게가 등록되어있지 않습니다.
        }

        // 2) 떨이 메인 메뉴 등록
        int mainTodayMenuItemCount = 0, sideTodayMenuItemCount = 0;
        try{
            if(postTodayMenuRegReq.getTodayMenuListMain() != null && postTodayMenuRegReq.getTodayMenuListMain().size() != 0)
                mainTodayMenuItemCount = todayMenuDao.registerTodayMenu(storeIdx, postTodayMenuRegReq.getTodayMenuListMain(), "M");
            else
                throw new BaseException(POST_TODAY_MAINMENU_DATA_UNVALID); // 2041 : 오늘의 떨이메뉴(메인) 등록 정보가 올바르지 않습니다.
        }catch (Exception e) {
            throw new BaseException(POST_TODAY_MAINMENU_SAVE_ERROR); // 4041 : 오늘의 떨이메뉴(메인) 등록에 실패하였습니다.
        }

        // 2) 떨이 사이드 메뉴 등록
        try{
            if(postTodayMenuRegReq.getTodayMenuListSide() != null && postTodayMenuRegReq.getTodayMenuListSide().size() != 0)
                sideTodayMenuItemCount = todayMenuDao.registerTodayMenu(storeIdx, postTodayMenuRegReq.getTodayMenuListSide(), "S");
            else
            throw new BaseException(POST_TODAY_SIDEMENU_DATA_UNVALID); // 2042 : 오늘의 떨이메뉴(사이드) 등록 정보가 올바르지 않습니다.
        }catch (Exception e) {
            throw new BaseException(POST_TODAY_SIDEMENU_SAVE_ERROR); // 4042 : 오늘의 떨이메뉴(사이드) 등록에 실패하였습니다.
        }

        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        return new PostTodayMenuRegRes(storeIdx, date, mainTodayMenuItemCount, sideTodayMenuItemCount);

    }


}

