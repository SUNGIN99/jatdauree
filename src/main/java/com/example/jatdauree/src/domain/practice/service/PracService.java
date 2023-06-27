package com.example.jatdauree.src.domain.practice.service;

import com.example.jatdauree.config.BaseException;
import com.example.jatdauree.src.domain.practice.dao.PracDao;
import com.example.jatdauree.src.domain.practice.dto.GetItemRes;
import com.example.jatdauree.src.domain.practice.dto.PostItemsReq;
import com.example.jatdauree.src.domain.practice.dto.PostItemsRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class PracService {
    @Autowired
    private final PracDao pracDao;

    public PracService(PracDao pracDao) {
        this.pracDao = pracDao;
    }

    public PostItemsRes inputItems(PostItemsReq postItemsReq) throws BaseException {
        return pracDao.inputItems(postItemsReq);

    }

    public ArrayList<GetItemRes> outputItems() throws BaseException {
        ArrayList<PostItemsReq> itemArray = pracDao.outputItems();

        ArrayList<GetItemRes> getItemRes = new ArrayList<>();
        for (PostItemsReq item : itemArray) {
            getItemRes.add(new GetItemRes(item.getItemIdx(), item.getTitle(), item.getContents()));
        }

        return getItemRes;

    }

    public GetItemRes outputItemsByParamId(int id) throws BaseException {
        ArrayList<PostItemsReq> itemArray = pracDao.outputItems();

        for (PostItemsReq item : itemArray) {
            if (item.getItemIdx() == id) {
                return new GetItemRes(item.getItemIdx(), item.getTitle(), item.getContents());
            }
        }
        return null;
    }
}
