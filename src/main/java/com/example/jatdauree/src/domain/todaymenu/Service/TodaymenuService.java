package com.example.jatdauree.src.domain.todaymenu.Service;

import com.example.jatdauree.config.BaseException;
import com.example.jatdauree.src.domain.todaymenu.dao.MenuDao;
import com.example.jatdauree.src.domain.todaymenu.dto.GetMenusSearchRes;
import com.example.jatdauree.utils.jwt.JwtTokenProvider;
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

    public ArrayList<GetMenusSearchRes> Search(int SellerIdx) throws BaseException {
        return menuDao.Search(SellerIdx);
    }
/*
    public ArrayList<PostTodayMenuRegRes> Register(PostTodayMenuRegReq) throws BaseException{
        return
    }
*/


}
