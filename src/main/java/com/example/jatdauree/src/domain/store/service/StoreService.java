package com.example.jatdauree.src.domain.store.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.jatdauree.config.BaseException;
import com.example.jatdauree.src.domain.store.dao.StoreDao;
import com.example.jatdauree.src.domain.store.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import static com.example.jatdauree.config.BaseResponseStatus.*;


@Service
public class StoreService {
    private StoreDao storeDao;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    private AmazonS3 s3Client;

    @Autowired
    public StoreService(StoreDao storeDao, AmazonS3 s3Client) {
        this.storeDao = storeDao;
        this.s3Client = s3Client;
    }


    @Transactional(rollbackFor = BaseException.class)
    public PostStoreRes storeRegister(int sellerIdx, PostStoreReq postStoreReq) throws BaseException, IOException {
        try{
            int storeRegistredCheck = storeDao.storeAlreadyRegister(sellerIdx);
            if (storeRegistredCheck == 1){
                throw new BaseException(DATABASE_ERROR);
            }
        }catch (Exception e){
            System.out.println("1: "+ e);
            throw new BaseException(DATABASE_ERROR);
        }

        ArrayList<String> urls = new ArrayList<>(), fileNames = new ArrayList<>();
        ArrayList<File> files = new ArrayList<>();
        try{
            String absolutePath = "/home/ubuntu/s3tempImg/";
            fileNames.add(postStoreReq.getBusinessCertificateFile().getOriginalFilename());
            fileNames.add(postStoreReq.getSellerCertificateFile().getOriginalFilename());
            fileNames.add(postStoreReq.getCopyAccountFile().getOriginalFilename());
            fileNames.add(postStoreReq.getStoreLogoFile().getOriginalFilename());
            fileNames.add(postStoreReq.getSignFile().getOriginalFilename());

            for (int i = 0; i< 5; i++){
                files.add( new File(absolutePath + fileNames.get(i)));
            }

            postStoreReq.getBusinessCertificateFile().transferTo(files.get(0));
            postStoreReq.getSellerCertificateFile().transferTo(files.get(1));
            postStoreReq.getCopyAccountFile().transferTo(files.get(2));
            postStoreReq.getStoreLogoFile().transferTo(files.get(3));
            postStoreReq.getSignFile().transferTo(files.get(4));
        }catch (Exception e){
            System.out.println("2: "+ e);
            throw new BaseException(DATABASE_ERROR);
        }

        // 1) 가게 등록에 필요한 각 이미지 url 생성
        try{
            for (int i = 0; i<5; i++){
                s3Client.putObject(new PutObjectRequest(bucketName, fileNames.get(i), files.get(i)));
            }

            for (int i = 0; i< 5; i++){
                urls.add(""+s3Client.getUrl(bucketName, fileNames.get(i)));
            }

        } catch (Exception e){
            System.out.println("3: "+ e);
            throw new BaseException(DATABASE_ERROR);
        }

        try{
           return new PostStoreRes(storeDao.storeRegister(sellerIdx, postStoreReq, urls));
        } catch (Exception e){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public GetStoreInfoRes getStoreInfo(int sellerIdx) throws BaseException{
        // 1) 사용자 가게 조회
        int storeIdx;
        try{
            storeIdx = storeDao.storeIdxBySellerIdx(sellerIdx);
        } catch (Exception e) {
            throw new BaseException(POST_STORES_NOT_REGISTERD); // 2030 : 사용자의 가게가 등록되어있지 않습니다.
        }

        // 2) 판매자의 가게 Idx로 가게 기본정보 조회
        try{
            return storeDao.getStoreInfo(storeIdx);
        }catch (Exception e){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    @Transactional(rollbackFor = BaseException.class)
    public PatchStoreInfoRes storeUpdate(int sellerIdx, PatchStoreInfoReq patchStoreInfoReq) throws BaseException {
        // 1) 사용자 가게 조회
        int storeIdx;
        try{
            storeIdx = storeDao.storeIdxBySellerIdx(sellerIdx);
        } catch (Exception e) {
            throw new BaseException(POST_STORES_NOT_REGISTERD); // 2030 : 사용자의 가게가 등록되어있지 않습니다.
        }

        // 2) 가게 정보 수정
        try {
            storeDao.storeUpdate(storeIdx, patchStoreInfoReq);
            return new PatchStoreInfoRes(storeIdx);
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

}
