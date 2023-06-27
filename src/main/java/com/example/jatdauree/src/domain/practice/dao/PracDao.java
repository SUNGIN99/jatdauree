package com.example.jatdauree.src.domain.practice.dao;

import com.example.jatdauree.src.domain.practice.dto.GetItemRes;
import com.example.jatdauree.src.domain.practice.dto.PostItemsReq;
import com.example.jatdauree.src.domain.practice.dto.PostItemsRes;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public class PracDao {

    public ArrayList<PostItemsReq> itemArray = new ArrayList<>();


    public PostItemsRes inputItems(PostItemsReq postItemsReq) {
        itemArray.add(postItemsReq);
        return new PostItemsRes(itemArray.size(), postItemsReq.getItemIdx());
    }

    public ArrayList<PostItemsReq> outputItems() {
        return itemArray;
    }
}
