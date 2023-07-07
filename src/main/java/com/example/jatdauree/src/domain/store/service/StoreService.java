package com.example.jatdauree.src.domain.store.service;

import com.example.jatdauree.config.BaseException;
import com.example.jatdauree.src.domain.store.dao.StoreDao;
import com.example.jatdauree.src.domain.store.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.jatdauree.config.BaseResponseStatus.DATABASE_ERROR;
import static com.example.jatdauree.config.BaseResponseStatus.RESPONSE_ERROR;


@Service
public class StoreService {
    private StoreDao storesDao;
    @Autowired
    public StoreService(StoreDao storesDao) {
        this.storesDao = storesDao;
    }


    @Transactional
    public PostStoreRes storeRegister(int sellerIdx, PostStoreReq postStoreReq) throws BaseException {
        try{
           return new PostStoreRes(storesDao.storeRegister(sellerIdx, postStoreReq));
        } catch (Exception e){
            System.out.println(e);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public PostStoreUpdateRes storeUpdate(PostStoreUpdateReq postStoreUpdateReq) throws BaseException {
        try {
            return new PostStoreUpdateRes(storesDao.storeUpdate(postStoreUpdateReq));
        } catch (Exception e) {

            throw new BaseException(RESPONSE_ERROR);
        }
    }


    /*public List<PostStoreModifyRes> storeModify(int storeIdx) throws BaseException {
        try {
            return storesDao.storeModifyDao(storeIdx);
        } catch (Exception e) {
            System.out.println(e);
            throw new BaseException(RESPONSE_ERROR);
        }
    }*/

}
