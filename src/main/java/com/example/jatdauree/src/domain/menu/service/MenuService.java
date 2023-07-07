package com.example.jatdauree.src.domain.menu.service;

import com.example.jatdauree.config.BaseException;
import com.example.jatdauree.src.domain.menu.dao.MenuDao;
import com.example.jatdauree.src.domain.menu.dto.*;
import com.example.jatdauree.src.domain.seller.dao.SellerDao;
import com.example.jatdauree.src.domain.store.dao.StoreDao;
import com.example.jatdauree.src.domain.todaymenu.dto.GetMenusSearchRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

import static com.example.jatdauree.config.BaseResponseStatus.*;

@Service
public class MenuService {
    private final MenuDao menuDao;
    private final StoreDao storeDao;
    private final SellerDao sellerDao;

    @Autowired
    public MenuService(MenuDao menuDao, StoreDao storeDao, SellerDao sellerDao) {
        this.menuDao = menuDao;
        this.storeDao = storeDao;
        this.sellerDao = sellerDao;
    }

    // 초기 메뉴 등록
    @Transactional(rollbackFor = BaseException.class)
    public PostMenuRes menuRegister(int sellerIdx, PostMenuReq postMenuReq) throws BaseException {
        // 1)사용자 가게 조회
        int storeIdx;
        try{
            storeIdx = storeDao.storeIdxBySellerIdx(sellerIdx);
        } catch (Exception e) {
            throw new BaseException(POST_STORES_NOT_REGISTERD); // 2030 : 사용자의 가게가 등록되어있지 않습니다.
        }
        // 2) 가게 메뉴/원산지 등록
        int mainMenuItemCount = 0, sideMenuItemCount = 0, ingredientCount = 0;
        try { // 메인 메뉴 등록
            if (postMenuReq.getMainMenuItems() != null && postMenuReq.getMainMenuItems().size() != 0)
                mainMenuItemCount =  menuDao.mainMenuRegister(storeIdx, postMenuReq.getMainMenuItems());
        } catch (Exception e) {
            throw new BaseException(STORE_MAINMENU_SAVE_ERROR); // 4031 : 메인메뉴 등록에 실패하였습니다.
        }
        try { // 사이드 메뉴 등록
            if (postMenuReq.getSideMenuItems() != null && postMenuReq.getSideMenuItems().size() != 0)
                sideMenuItemCount = menuDao.sideMenuRegister(storeIdx, postMenuReq.getSideMenuItems());
        } catch (Exception e) {
            throw new BaseException(STORE_SIDEMENU_SAVE_ERROR); // 4032: 사이드메뉴 등록에 실패하였습니다.
        }
        try { // 원산지 등록
            if (postMenuReq.getIngredientItems() != null && postMenuReq.getIngredientItems().size() != 0)
                ingredientCount = menuDao.ingredientRegister(storeIdx, postMenuReq.getIngredientItems());
        } catch (Exception e) {
            throw new BaseException(STORE_INGREDIENT_SAVE_ERROR); // 4033 : 원산지 등록에 실패하였습니다.
        }

        // 3) 판매자 최초로그인, 가게등록여부 필드 변경 : 1, 1 -> 0, 0
        try {
            sellerDao.registerMenuNIngredient(sellerIdx);
        } catch(Exception e){
            throw new BaseException(SELLER_ALL_REGISTER_COMPLETE_ERROR); // 4030 : 판매자의 최초로그인, 메뉴/원산지 등록이 완료되지 못하였습니다.
        }

        return new PostMenuRes(storeIdx, mainMenuItemCount, sideMenuItemCount, ingredientCount);
    }

    //떨이 메뉴 조회
    public ArrayList<GetMenusSearchRes> searchMenu(int sellerIdx) throws BaseException {
        int storeIdx = menuDao.getStroeIdxbySellerIdx(sellerIdx);

        return menuDao.searchMenu(storeIdx);
    }


    //상태변경
    public PostStatusUpRes statusChange(PostStatusUpReq postStatusUpReq) throws BaseException {
        try {
            return new PostStatusUpRes(menuDao.statusChangeDao(postStatusUpReq));
        } catch (Exception e) {
            throw new BaseException(RESPONSE_ERROR);

        }

    }

    public PostMenuUpRes menuUpdate(PostMenuUpReq postMenuUpReq) throws BaseException {
        try {
            return new PostMenuUpRes(menuDao.menuUpdate(postMenuUpReq));
        } catch (Exception e) {

            throw new BaseException(RESPONSE_ERROR);
        }
    }

    /*public List<PostModifyRes> menuModify(int storeIdx) throws BaseException {
        try {
            return menuDao.menuModifyDao(storeIdx);
        } catch (Exception e) {
            System.out.println(e);
            throw new BaseException(RESPONSE_ERROR);
        }
    }*/
}



    //메뉴수정

