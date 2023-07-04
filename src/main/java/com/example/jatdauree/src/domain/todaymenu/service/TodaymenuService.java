package com.example.jatdauree.src.domain.todaymenu.service;

import com.example.jatdauree.config.BaseException;
import com.example.jatdauree.src.domain.todaymenu.dao.MenuDao;
import com.example.jatdauree.src.domain.todaymenu.dto.GetMenusSearchRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class TodaymenuService {

    private final MenuDao menuDao;


    @Autowired
    public TodaymenuService(MenuDao menuDao) {
        this.menuDao = menuDao;
    }

    public ArrayList<GetMenusSearchRes> search(int sellerIdx) throws BaseException {
        int storeIdx = menuDao.getStroeIdxbySellerIdx(sellerIdx);

        return menuDao.Search(storeIdx);
    }
/*
    public ArrayList<PostTodayMenuRegRes> Register(PostTodayMenuRegReq) throws BaseException{
        return
    }
*/


}
