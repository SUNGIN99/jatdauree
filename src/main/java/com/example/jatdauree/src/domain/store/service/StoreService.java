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
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

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

    private String checkFileIsNull(MultipartFile files){
        String absolutePath = "/home/ubuntu/s3tempImg/";

        return files != null ? absolutePath + files.getOriginalFilename() : null;
    }

    @Transactional(rollbackFor = BaseException.class)
    public PostStoreRes storeRegister(int sellerIdx, PostStoreReq postStoreReq) throws BaseException, IOException {
        //0) 가게 중복 등록 방지
        int storeRegistredCheck = storeDao.storeAlreadyRegister(sellerIdx);
        if (storeRegistredCheck == 1){
            throw new BaseException(STORE_ALREADY_REGISTERD);
        }

        // 1) 가게 이미지 등록정보 URL 발생
        String[] fileNames = new String[]{null, null, null, null, null};
        File[] files = new File[]{null, null, null, null, null};
        try{

            fileNames[0] = checkFileIsNull(postStoreReq.getBusinessCertificateFile()); // 사업자 등록증 이미지파일
            fileNames[1] = checkFileIsNull(postStoreReq.getSellerCertificateFile()); // 영업자 등록증 이미지 파일
            fileNames[2] = checkFileIsNull(postStoreReq.getCopyAccountFile()); // 통장 사본 이미지 파일
            fileNames[3] = checkFileIsNull(postStoreReq.getStoreLogoFile()); // 가게로고 이미지파일
            fileNames[4] = checkFileIsNull(postStoreReq.getSignFile()); // 가게 간판 이미지 파일

            // 보낼 이미지 생성
            for (int i = 0; i<5 ; i++)
                if (fileNames[i] != null)
                    files[i] = new File(fileNames[i]);

        }catch (Exception e){
            throw new BaseException(STORE_URL_POST_ERROR);
        }

        // 2) 가게 등록에 필요한 각 이미지 url 생성
        String[] urls = new String[]{null, null, null, null, null};
        try{
            for (int i = 0; i<5; i++) {
                if (fileNames[i] != null) {
                    s3Client.putObject(new PutObjectRequest(bucketName, fileNames[i], files[i]));
                    urls[i] = "" + s3Client.getUrl(bucketName, fileNames[i]);
                }
            }

        } catch (Exception e){
            throw new BaseException(S3_ACCESS_API_ERROR);
        }

        try{ // url 저장
           return new PostStoreRes(storeDao.storeRegister(sellerIdx, postStoreReq, urls));
        } catch (Exception e){
            System.out.println("2:" + e);
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
