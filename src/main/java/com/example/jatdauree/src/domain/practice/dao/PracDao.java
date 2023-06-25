package com.example.jatdauree.src.domain.practice.dao;

import com.example.jatdauree.src.domain.practice.dto.GetItemRes;
import com.example.jatdauree.src.domain.practice.dto.PostItemsReq;
import com.example.jatdauree.src.domain.practice.dto.PostItemsRes;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public class PracDao {

    ArrayList<PostItemsReq> itemArray = new ArrayList<>();

    public PostItemsRes inputItems(PostItemsReq postItemsReq) {
        itemArray.add(postItemsReq);
        return new PostItemsRes(itemArray.size(), postItemsReq.getItemIdx());
    }

    public ArrayList<GetItemRes> outputItems() {
        ArrayList<GetItemRes> getItemRes = new ArrayList<>();
        for (PostItemsReq item : itemArray){
            getItemRes.add(new GetItemRes(item.getItemIdx(), item.getTitle(), item.getContents()));
        }

        return getItemRes;
    }
}
