package com.example.jatdauree.src.domain.menu.service;

import com.example.jatdauree.config.BaseException;
import com.example.jatdauree.src.domain.menu.dao.MenuDao;
import com.example.jatdauree.src.domain.menu.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.jatdauree.config.BaseResponseStatus.RESPONSE_ERROR;

@Service
public class MenuService {
    private MenuDao menuDao;

    @Autowired
    public MenuService(MenuDao menuDao) {
        this.menuDao = menuDao;
    }

    //작성자 : 이윤채  메뉴등록 M으로 status 등록하면 메인메뉴, S로 등록하면 서브메뉴
    public PostMenuRes menuRegister(PostMenuReq postMenuReq) throws BaseException {
        try {
            return new PostMenuRes(menuDao.menuRegisterDao(postMenuReq));

        } catch (Exception e) {
            //System.out.println(e);
            throw new BaseException(RESPONSE_ERROR);
        }
    }

    //원산지 등록
    public PostIngredientRes ingredientRegister(PostIngredientReq postIngredientReq) throws BaseException {
        try {
            return new PostIngredientRes(menuDao.ingredientDao(postIngredientReq));
        } catch (Exception e) {
            //System.out.println(e);
            throw new BaseException(RESPONSE_ERROR);
        }
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



    public List<PostModifyRes> menuModify(int storeIdx) throws BaseException {
        try {
            return menuDao.menuModifyDao(storeIdx);
        } catch (Exception e) {
            System.out.println(e);
            throw new BaseException(RESPONSE_ERROR);
        }
    }
}



    //메뉴수정

