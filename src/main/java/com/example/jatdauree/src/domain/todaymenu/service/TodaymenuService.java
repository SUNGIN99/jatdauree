package com.example.jatdauree.src.domain.todaymenu.service;

import com.example.jatdauree.config.BaseException;
import com.example.jatdauree.src.domain.todaymenu.dao.MenuDao;
import com.example.jatdauree.src.domain.todaymenu.dao.TodayMenuDao;
import com.example.jatdauree.src.domain.todaymenu.dto.GetMenusSearchRes;
import com.example.jatdauree.src.domain.todaymenu.dto.PostTodayMenuRegReq;
import com.example.jatdauree.src.domain.todaymenu.dto.PostTodayMenuRegRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class TodaymenuService {

    private final MenuDao menuDao;
    private final TodayMenuDao todayMenuDao;


    @Autowired
    public TodaymenuService(MenuDao menuDao,TodayMenuDao todayMenuDao) {
        this.menuDao = menuDao;
        this.todayMenuDao= todayMenuDao;
    }


    public ArrayList<GetMenusSearchRes> search(int sellerIdx) throws BaseException {
        int storeIdx = menuDao.getStroeIdxbySellerIdx(sellerIdx);

        return menuDao.Search(storeIdx);
    }



    public ArrayList<PostTodayMenuRegRes> registerTodayMenu(PostTodayMenuRegReq postTodayMenuRegReq) throws BaseException{

        int todayMenuIdx = todayMenuDao.RegisterTodayMenu(postTodayMenuRegReq); //가게 idx

        return todayMenuDao.searchTodayMenuTable(todayMenuIdx); // todayMenutable 조회

    }


}
