package com.example.jatdauree.src.domain.menu.service;

import com.example.jatdauree.config.BaseException;
import com.example.jatdauree.src.domain.menu.dao.MenuDao;
import com.example.jatdauree.src.domain.menu.dto.*;
import com.example.jatdauree.src.domain.seller.dao.SellerDao;
import com.example.jatdauree.src.domain.store.dao.StoreDao;
import com.example.jatdauree.src.domain.menu.dto.GetMenuItemsRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        // 0) 사용자 메뉴 등록 여부 확인
        // (index)  0 :first_login, 1: menu_register
        int[] loginStatus = sellerDao.checkRegisterd(sellerIdx);
        if(loginStatus[0] == 0 && loginStatus[1] == 0){
            throw new BaseException(STORE_MENU_ALREADY_SAVED); // 4034 : 메뉴 등록이 이미 이루어진 판매자 입니다.
        }
        if(loginStatus[0] == 1){
            throw new BaseException(STORE_REGISTER_NOT_PERMITTED); // 4035 : 관리자에게 가게 승인되지 않은 판매자 계정입니다.
        }
        // first_login == 1, menu_register == 0 일때 메뉴등록가능

        // 1)사용자 가게 조회
        int storeIdx;
        try{
            storeIdx = storeDao.storeIdxBySellerIdx(sellerIdx);
        } catch (Exception e) {
            throw new BaseException(POST_STORES_NOT_REGISTERD); // 2030 : 사용자의 가게가 등록되어있지 않습니다.
        }

        // 2) 가게 메뉴/원산지 등록
        int mainMenuItemCount = 0, sideMenuItemCount = 0, ingredientCount = 0;
        try { // 2-1) 메인 메뉴 등록
            if (postMenuReq.getPostMenuItems() != null && postMenuReq.getPostMenuItems().size() != 0)
                mainMenuItemCount =  menuDao.mainMenuRegister(storeIdx, postMenuReq.getPostMenuItems());
            else
                throw new BaseException(STORE_MAINMENU_DATA_UNVALID); // 2031 : 메인 메뉴 등록 정보가 올바르지 않습니다.
        } catch (Exception e) {
            throw new BaseException(STORE_MAINMENU_SAVE_ERROR); // 4031 : 메인메뉴 등록에 실패하였습니다.
        }

        try { // 2-2) 사이드 메뉴 등록
            if (postMenuReq.getPostSideMenuItems() != null && postMenuReq.getPostSideMenuItems().size() != 0)
                sideMenuItemCount = menuDao.sideMenuRegister(storeIdx, postMenuReq.getPostSideMenuItems());
            else
                throw new BaseException(STORE_SIDEMENU_DATA_UNVALID); // 2032 : 사이드 메뉴 등록 정보가 올바르지 않습니다.
        } catch (Exception e) {
            throw new BaseException(STORE_SIDEMENU_SAVE_ERROR); // 4032: 사이드메뉴 등록에 실패하였습니다.
        }

        try { // 2-3) 원산지 등록
            if (postMenuReq.getPostIngredientItems() != null && postMenuReq.getPostIngredientItems().size() != 0)
                ingredientCount = menuDao.ingredientRegister(storeIdx, postMenuReq.getPostIngredientItems());
            else
                throw new BaseException(STORE_INGREDIENT_DATA_UNVALID); // 2033 : 원산지 등록 정보가 올바르지 않습니다.
        } catch (Exception e) {
            throw new BaseException(STORE_INGREDIENT_SAVE_ERROR); // 4033 : 원산지 등록에 실패하였습니다.
        }

        // 3) 판매자 최초로그인,
        // 판매자 가게등록여부 필드 변경 : 1, 1 -> 0, 0
        try {
            sellerDao.registerMenuNIngredient(sellerIdx);
            storeDao.convertStoreOpen(storeIdx);
        } catch(Exception e){
            throw new BaseException(SELLER_ALL_REGISTER_COMPLETE_ERROR); // 4030 : 판매자의 최초로그인, 메뉴/원산지 등록이 완료되지 못하였습니다.
        }

        return new PostMenuRes(storeIdx, mainMenuItemCount, sideMenuItemCount, ingredientCount);
    }

    //떨이 메뉴 조회
    public GetMenuItemsRes getStoreMenuList(int sellerIdx) throws BaseException {
        int[] loginStatus = sellerDao.checkRegisterd(sellerIdx);
        if(loginStatus[0] != 0 && loginStatus[1] != 0){
            throw new BaseException(STORE_REGISTER_NOT_PERMITTED); // 4035 : 관리자에게 가게 승인되지 않은 판매자 계정입니다.
        }

        // 1) 사용자 가게 조회
        int storeIdx;
        try{
            storeIdx = storeDao.storeIdxBySellerIdx(sellerIdx);
        } catch (Exception e) {
            throw new BaseException(POST_STORES_NOT_REGISTERD); // 2030 : 사용자의 가게가 등록되어있지 않습니다.
        }


        List<GetMenuItem> mainMenuList, sideMenuList;
        // 2) 메인 메뉴 조회
        try{
            mainMenuList = menuDao.getStoreMenuList(storeIdx, "M");
        }catch (Exception e) {
            mainMenuList = null;
        }

        // 3) 사이드 메뉴 조회
        try{
            sideMenuList = menuDao.getStoreMenuList(storeIdx, "S");
        }catch (Exception e) {
            sideMenuList = null;
        }

        return new GetMenuItemsRes(storeIdx, mainMenuList, sideMenuList);

    }

    /*public PostMenuUpRes menuUpdate(PostMenuUpReq postMenuUpReq) throws BaseException {
        try {
            return new PostMenuUpRes(menuDao.menuUpdate(postMenuUpReq));
        } catch (Exception e) {

            throw new BaseException(RESPONSE_ERROR);
        }
    }*/
}


