package com.example.jatdauree.src.domain.todaymenu.service;

import com.example.jatdauree.config.BaseException;
import com.example.jatdauree.src.domain.store.dao.StoreDao;
import com.example.jatdauree.src.domain.todaymenu.dao.TodayMenuDao;
import com.example.jatdauree.src.domain.todaymenu.dto.GetMainPageItem;
import com.example.jatdauree.src.domain.todaymenu.dto.GetMainPageMenu;
import com.example.jatdauree.src.domain.todaymenu.dto.PostMainPageTMenu;
import com.example.jatdauree.src.domain.todaymenu.dto.PostMainPageTMenuRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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



    public GetMainPageMenu getTodayMenuList(int sellerIdx) throws BaseException{
        // 1) 사용자 가게 조회
        int storeIdx;
        try{
            storeIdx = storeDao.storeIdxBySellerIdx(sellerIdx);
        } catch (Exception e) {
            throw new BaseException(POST_STORES_NOT_REGISTERD); // 2030 : 사용자의 가게가 등록되어있지 않습니다.
        }

        List<GetMainPageItem> mainTodaymenuItems, sideTodaymenuItems;
        try{
            mainTodaymenuItems = todayMenuDao.getTodayMenuList(storeIdx, "M");
        }catch (Exception e) {
            throw new BaseException(POST_STORES_NOT_REGISTERD); // 2030 : 사용자의 가게가 등록되어있지 않습니다.
        }

        try{
            sideTodaymenuItems = todayMenuDao.getTodayMenuList(storeIdx, "S");
        }catch (Exception e) {
            throw new BaseException(POST_STORES_NOT_REGISTERD); // 2030 : 사용자의 가게가 등록되어있지 않습니다.
        }

        return new GetMainPageMenu(storeIdx, mainTodaymenuItems, sideTodaymenuItems);
    }


    // 떨이메뉴 등록
    @Transactional(rollbackFor = BaseException.class)
    public PostMainPageTMenuRes registerTodayMenu(int sellerIdx, PostMainPageTMenu postTodayMenuReq) throws BaseException {
        // 1) 사용자 가게 조회
        int storeIdx;
        try{
            storeIdx = storeDao.storeIdxBySellerIdx(sellerIdx);
        } catch (Exception e) {
            throw new BaseException(POST_STORES_NOT_REGISTERD); // 2030 : 사용자의 가게가 등록되어있지 않습니다.
        }

        // -1 : 미등록 상태 메뉴
        //  0 : 등록 되어있지만 수정 안하는 떨이메뉴
        //  1 : 등록되어 있고, 수정을 하는 떨이메뉴
        //  2 : 등록 안되어 있다가 새로 등록하는 떨이메뉴
        // 2-1) 메인 메뉴 떨이메뉴 수정/등록 확인
        List<GetMainPageItem> newTodayMain = new ArrayList<>(), updateTodayMain = new ArrayList<>();
        int newTodayMainCnt = 0, updateTodayMainCnt = 0;
        if (postTodayMenuReq.getMainMenus() != null) {
            for (GetMainPageItem pageItem : postTodayMenuReq.getMainMenus()) {
                if (pageItem.getIsUpdated() == 2) { // 2: 새로 등록하는 오늘의 떨이 메뉴 일 경우!
                    newTodayMain.add(pageItem);
                }
                else if(pageItem.getIsUpdated() == 1){ // 1: 등록되어있는 메뉴 중 수정하는 떨이 메뉴일 경우!
                    updateTodayMain.add(pageItem);
                }
            }
        }
        // 2-2) 메인 떨이메뉴 등록/수정
        try{
            newTodayMainCnt = todayMenuDao.registerTodayMenu(storeIdx, newTodayMain, "M");
            updateTodayMainCnt = todayMenuDao.updateTodayMenu(updateTodayMain);
        } catch (Exception e) {
            throw new BaseException(POST_TODAY_MAINMENU_SAVE_ERROR); // 2030 : 사용자의 가게가 등록되어있지 않습니다.
        }

        // 3-1) 사이드 메뉴 떨이메뉴 수정/등록 확인
        List<GetMainPageItem> newTodaySide = new ArrayList<>(), updateTodaySide = new ArrayList<>();
        int newTodaySideCnt = 0, updateTodaySideCnt = 0;
        if (postTodayMenuReq.getSideMenus() != null) {
            for (GetMainPageItem pageItem : postTodayMenuReq.getSideMenus()) {
                if (pageItem.getIsUpdated() == 2) { // 2: 새로 등록하는 오늘의 떨이 메뉴 일 경우!
                    newTodaySide.add(pageItem);
                }
                else if(pageItem.getIsUpdated() == 1){
                    updateTodaySide.add(pageItem);
                }
            }
        }
        // 3-2) 사이드 떨이메뉴 등록/수정
        try{
            newTodaySideCnt = todayMenuDao.registerTodayMenu(storeIdx, newTodaySide, "S");
            updateTodaySideCnt = todayMenuDao.updateTodayMenu(updateTodaySide);
        } catch (Exception e) {
            throw new BaseException(POST_TODAY_MAINMENU_SAVE_ERROR); // 2030 : 사용자의 가게가 등록되어있지 않습니다.
        }

        return new PostMainPageTMenuRes(storeIdx, newTodayMainCnt, updateTodayMainCnt, newTodaySideCnt, updateTodaySideCnt);
    }


}

