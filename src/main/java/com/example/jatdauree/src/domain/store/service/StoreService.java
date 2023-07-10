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


    @Transactional(rollbackFor = BaseException.class)
    public PostStoreRes storeRegister(int sellerIdx, PostStoreReq postStoreReq) throws BaseException, IOException {
        try{ // 0) 가게 중복 등록 방지
            int storeRegistredCheck = storeDao.storeAlreadyRegister(sellerIdx);
            if (storeRegistredCheck == 1){
                throw new BaseException(DATABASE_ERROR);
            }
        }catch (Exception e){
            throw new BaseException(DATABASE_ERROR);
        }

        // 1) 가게 이미지 등록정보 URL 발생
        ArrayList<String> fileNames = new ArrayList<>();
        ArrayList<File> files = new ArrayList<>();
        HashMap<String, File> fileHash = new HashMap<>();
        try{
            String absolutePath = "/home/ubuntu/s3tempImg/";
            String fileNameTemp;
            if (postStoreReq.getBusinessCertificateFile() != null) {//사업자 등록증 이미지파일
                fileNameTemp = postStoreReq.getBusinessCertificateFile().getOriginalFilename();
                fileHash.put(fileNameTemp, new File(absolutePath + fileNameTemp));
                postStoreReq.getBusinessCertificateFile().transferTo(fileHash.get(fileNameTemp));
            }
            if (postStoreReq.getSellerCertificateFile() != null) {// 영업자 등록증 이미지 파일
                fileNameTemp = postStoreReq.getSellerCertificateFile().getOriginalFilename();
                fileHash.put(fileNameTemp, new File(absolutePath + fileNameTemp));
                postStoreReq.getSellerCertificateFile().transferTo(fileHash.get(fileNameTemp));
            }
            if (postStoreReq.getCopyAccountFile() != null) {// 통장 사본 이미지 파일
                fileNameTemp = postStoreReq.getCopyAccountFile().getOriginalFilename();
                fileHash.put(fileNameTemp, new File(absolutePath + fileNameTemp));
                postStoreReq.getCopyAccountFile().transferTo(fileHash.get(fileNameTemp));
            }
            if (postStoreReq.getStoreLogoFile() != null) {// 가게로고 이미지파일
                fileNameTemp = postStoreReq.getStoreLogoFile().getOriginalFilename();
                fileHash.put(fileNameTemp, new File(absolutePath + fileNameTemp));
                postStoreReq.getStoreLogoFile().transferTo(fileHash.get(fileNameTemp));
            }
            if (postStoreReq.getSignFile() != null) {// 가게 간판 이미지 파일
                fileNameTemp = postStoreReq.getSignFile().getOriginalFilename();
                fileHash.put(fileNameTemp, new File(absolutePath + fileNameTemp));
                postStoreReq.getSignFile().transferTo(fileHash.get(fileNameTemp));
            }
        }catch (Exception e){
            throw new BaseException(DATABASE_ERROR);
        }

        // 2) 가게 등록에 필요한 각 이미지 url 생성
        ArrayList<String> urls = new ArrayList<>();
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
