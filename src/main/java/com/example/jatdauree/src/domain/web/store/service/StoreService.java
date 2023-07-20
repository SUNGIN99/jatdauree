package com.example.jatdauree.src.domain.web.store.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.jatdauree.config.BaseException;
import com.example.jatdauree.config.secret.SmsSecret;
import com.example.jatdauree.src.domain.web.store.dao.StoreDao;
import com.example.jatdauree.src.domain.web.store.dto.*;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.example.jatdauree.config.BaseResponseStatus.*;
import static com.example.jatdauree.utils.UtilFileImgUrl.absolutePath;
import static com.example.jatdauree.utils.UtilFileImgUrl.checkFileIsNullThenName;


@Slf4j
@Service
public class StoreService {
    private final StoreDao storeDao;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    private final AmazonS3 s3Client;

    private final DefaultMessageService messageService;

    @Autowired
    public StoreService(StoreDao storeDao, AmazonS3 s3Client) {
        this.storeDao = storeDao;
        this.s3Client = s3Client;
        this.messageService = NurigoApp.INSTANCE.initialize(SmsSecret.APIKey, SmsSecret.Secret, "https://api.coolsms.co.kr");
    }

    public StoreNameDupRes storeNameDuplicate(String storeName) {
        int check = storeDao.storeNameDuplicate(storeName);
        return new StoreNameDupRes(check);
    }


    @Transactional(rollbackFor = {BaseException.class, IOException.class})
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

            fileNames[0] = checkFileIsNullThenName(postStoreReq.getBusinessCertificateFile()); // 사업자 등록증 이미지파일
            fileNames[1] = checkFileIsNullThenName(postStoreReq.getSellerCertificateFile()); // 영업자 등록증 이미지 파일
            fileNames[2] = checkFileIsNullThenName(postStoreReq.getCopyAccountFile()); // 통장 사본 이미지 파일
            fileNames[3] = checkFileIsNullThenName(postStoreReq.getStoreLogoFile()); // 가게로고 이미지파일
            fileNames[4] = checkFileIsNullThenName(postStoreReq.getSignFile()); // 가게 간판 이미지 파일

            // 보낼 이미지 생성
            for (int i = 0; i<5 ; i++)
                if (fileNames[i] != null) {
                    files[i] = new File(absolutePath + fileNames[i]);
                    System.out.println(fileNames[i] +": "+ files[i]);
                }
        }catch (Exception e){
            throw new BaseException(STORE_URL_POST_ERROR); // 4034: 등록하는 이미지의 형태가 잘못 처리되었습니다.
        }

        // 실제 파일 생성
        try{
            if (files[0] != null)
                postStoreReq.getBusinessCertificateFile().transferTo(files[0]);
            if (files[1] != null)
                postStoreReq.getSellerCertificateFile().transferTo(files[1]);
            if (files[2] != null)
                postStoreReq.getCopyAccountFile().transferTo(files[2]);
            if (files[3] != null)
                postStoreReq.getStoreLogoFile().transferTo(files[3]);
            if (files[4] != null)
                postStoreReq.getSignFile().transferTo(files[4]);
        }catch (Exception e){
            throw new BaseException(STORE_URL_POST_ERROR); // 4034: 등록하는 이미지의 형태가 잘못 처리되었습니다.
        }

        // 2) 가게 등록에 필요한 각 이미지 url 생성
        String[] urls = new String[]{null, null, null, null, null};
        try{
            for (int i = 0; i<5; i++) {
                if (fileNames[i] != null) {
                    s3Client.putObject(new PutObjectRequest(bucketName, fileNames[i], files[i]));
                    urls[i] = "" + s3Client.getUrl(bucketName, fileNames[i]);
                    files[i].delete();
                }
            }
        } catch (Exception e){
            throw new BaseException(S3_ACCESS_API_ERROR); // 5030 : 이미지 url 생성에 실패하였습니다.
        }

        try{ // url 저장
           return new PostStoreRes(storeDao.storeRegister(sellerIdx, postStoreReq, fileNames));
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
            GetStoreInfoRes getStoreInfoRes =  storeDao.getStoreInfo(storeIdx);
            String s3StoreLogoFileName, s3StoreSignFileName ;

            if(!getStoreInfoRes.getStoreLogoUrl().equals("")){
                s3StoreLogoFileName = ""+s3Client.getUrl(bucketName, getStoreInfoRes.getStoreLogoUrl());
                getStoreInfoRes.setStoreLogoUrl(s3StoreLogoFileName);
                getStoreInfoRes.setStoreLogoUrl(s3StoreLogoFileName);
            }

            if(!getStoreInfoRes.getStoreLogoUrl().equals("")){
                s3StoreSignFileName = ""+s3Client.getUrl(bucketName, getStoreInfoRes.getSignUrl());
                getStoreInfoRes.setSignUrl(s3StoreSignFileName);
            }

            return  getStoreInfoRes;
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

        // 2) 파일 url 존재 확인
        SavedFileNames savedFileNames;
        try{
            // 서버에 등록되어있는 로고와 간판 이미지 파일 확인
            savedFileNames = storeDao.getS3FileNames(storeIdx);

            // *이미 서버에 로고와 간판 파일이 등록되어 있지 않다면? 새로 등록해야함 (새로운 파일이름)*
            // 1- 로고 수정요청이 있고, 서버에 등록이 되지 않은 경우 = 새로운 파일명 생성
            if(patchStoreInfoReq.getStoreLogoUrl()!= null && savedFileNames.getLogoFileName() == null){
                savedFileNames.setLogoFileName(checkFileIsNullThenName(patchStoreInfoReq.getStoreLogoUrl()));
            }

            // 2- 간판 수정 요청이 있고, 서버에 등록이 되지 않은 경우 = 새로운 파일명 생성
            // 간판 새로운 이미지 등록 수정 요청일 경우 && 서버에 수정요청이 이미지를 새로운 등록을 하는 경우
            if(patchStoreInfoReq.getSignUrl()!= null && savedFileNames.getSignFileName() == null){
                savedFileNames.setSignFileName(checkFileIsNullThenName(patchStoreInfoReq.getSignUrl()));
            }

            // 2- 로고 수정요청이 있고, 서버에 이미 등록이 되어있는 경우 = 기존 서버에 저장된 파일명 이용
            // 3- 로고 수정요청이 없고, 서버에 이미 등록이 되어있는 경우 = 기존 서버에 저장된 파일명 이용
            // 4- 로고 수정요청이 없고, 서버에 등록이 되지 않은 경우  = 기존 서버에 저장된 파일 명 이용
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }

        // 파일명 생성 (checkFileIsNullThenName 에서 파일이 null 이면 가게정보 수정요청들어와도 파일이름 null)
        File logoFile = null, signFile = null;
        try{
            if(savedFileNames.getLogoFileName() != null)
                logoFile = new File(absolutePath + savedFileNames.getLogoFileName());
            if(savedFileNames.getSignFileName() != null)
                signFile = new File(absolutePath + savedFileNames.getSignFileName());
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }

        // s3 파일 보낼준비 및 S3 URL 생성 요청
        try{
            // 파일명을 만들고,
            if(logoFile != null && patchStoreInfoReq.getStoreLogoUrl() != null) {
                System.out.println("logoFile Updated");
                patchStoreInfoReq.getStoreLogoUrl().transferTo(logoFile);
                if(s3Client.doesObjectExist(bucketName, savedFileNames.getLogoFileName()))
                    s3Client.deleteObject(bucketName, savedFileNames.getLogoFileName());

                s3Client.putObject(new PutObjectRequest(bucketName, savedFileNames.getLogoFileName(), logoFile));
                logoFile.delete();
            }
            if(signFile != null && patchStoreInfoReq.getSignUrl() != null) {
                System.out.println("signFile Updated");
                patchStoreInfoReq.getSignUrl().transferTo(signFile);
                if(s3Client.doesObjectExist(bucketName, savedFileNames.getSignFileName()))
                    s3Client.deleteObject(bucketName, savedFileNames.getSignFileName());
                s3Client.putObject(new PutObjectRequest(bucketName, savedFileNames.getSignFileName(), signFile));
                signFile.delete();
            }
        } catch (Exception e){
            throw new BaseException(S3_ACCESS_API_ERROR); // 5030 : 이미지 url 생성에 실패하였습니다.
        }


        // 2) 가게 정보 수정
        try {
            storeDao.storeUpdate(storeIdx, patchStoreInfoReq, savedFileNames);
            return new PatchStoreInfoRes(storeIdx);
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    //가게영업종료
    //1.등록된 가게가 맞는지 error ---> 2030 : 사용자의 가게가 등록되어있지 않습니다.
    //(뺸 조건)가게 주인이 맞는지 ---> storeIdx로 sellerIdx를 조회해서 떨이 메뉴 테이블에서의 storeIdx와 동일한지 확인  error---> 사장님의 가게가 아닙니다. 다시 한번 선택한 가게를
    //생각해보니 2번은 할 필요가 없는게 애초에 sellerIdx를 조회하면 storeIdx 조회 가능하니까 로그인한 사장님의 가게가 아닐 수 없음
    //2.이미 영업종료가 됐는데 한번 더 눌렀을때--> 이미 영업 종료 되었습니다

    public PatchStoreEndRes storeEnd(int sellerIdx) throws BaseException{
        // 1) 사용자 가게 조회
        int storeIdx;
        try{
            storeIdx = storeDao.storeIdxBySellerIdx(sellerIdx);
        } catch (Exception e) {
            throw new BaseException(POST_STORES_NOT_REGISTERD); // 2030 : 사용자의 가게가 등록되어있지 않습니다.
        }

        // 2) 가게에 등록된 오늘의 떨이 메뉴 모두 비활성화
        try {
            return new PatchStoreEndRes(storeDao.storeEnd(storeIdx));
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR); //값을 바꾸는데 실패
        }
    }


    public GetStroeInfoAdmin getStoreInfoAdmin(int storeIdx) throws BaseException {
        // 1) 가게정보가져오기
        GetStroeInfoAdmin storeInfoA;
        try{
            storeInfoA = storeDao.getStoreInfoAdmin(storeIdx);
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR); //값을 바꾸는데 실패
        }

        // 2) 이미지 url 가져오기
        try{
            if(storeInfoA != null){
                if(storeInfoA.getBusinessCertificateUrl() != null){
                    storeInfoA.setBusinessCertificateUrl(""+s3Client.getUrl(bucketName, storeInfoA.getBusinessCertificateUrl()));
                }
                if(storeInfoA.getSellerCertificateUrl() != null){
                    storeInfoA.setSellerCertificateUrl(""+s3Client.getUrl(bucketName, storeInfoA.getSellerCertificateUrl()));
                }
                if(storeInfoA.getCopyAccountUrl() != null){
                    storeInfoA.setCopyAccountUrl(""+s3Client.getUrl(bucketName, storeInfoA.getCopyAccountUrl()));
                }
                if(storeInfoA.getStoreLogoUrl() != null){
                    storeInfoA.setStoreLogoUrl(""+s3Client.getUrl(bucketName, storeInfoA.getStoreLogoUrl()));
                }
                if(storeInfoA.getSignUrl() != null){
                    storeInfoA.setSignUrl(""+s3Client.getUrl(bucketName, storeInfoA.getSignUrl()));
                }
            }
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR); //값을 바꾸는데 실패
        }

        return storeInfoA;
    }

    public List<GetStoreListAdmin> getStoreListAdmin() throws BaseException{
        try{
            return storeDao.getStoreListAdmin();
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR); //값을 바꾸는데 실패
        }
    }

    public StorePermitRes storePermit(StorePermit storePermit) throws BaseException {
        // 가게 승인(상태값 변경)
        int updated1 = 0, updated2 = 0;
        try{
            updated1 = storeDao.storePermit(storePermit);
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR); //값을 바꾸는데 실패
        }

        // 판매자 전화번호 조회
        StorePermitRes permitRes;
        try{
            permitRes = storeDao.storeSellersPhone(storePermit.getStoreIdx());
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR); //값을 바꾸는데 실패
        }

        try{
            updated2 = storeDao.sellerPermit(permitRes.getSellerIdx());
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR); //값을 바꾸는데 실패
        }

        if(updated1 == 1 && updated2 == 1) {
            // 가게 승인 완료 메시지 전송
            try {
                Message message = new Message();
                message.setFrom("01043753181");
                message.setTo(permitRes.getSellerPhone());
                message.setText("[재떨이]:판매자 가게 승인 완료\n" +
                        permitRes.getSellerName() + "님의" +
                        permitRes.getStoreName() + "의 판매 승인이 완료되었습니다!\n" +
                        "어서 재떨이 서비스를 이용하세요!");
                SingleMessageSentResponse response = this.messageService.sendOne(new SingleMessageSendingRequest(message));
                log.info("coolSMS API요청 :{}", response);

            } catch (Exception e) {
                throw new BaseException(DATABASE_ERROR); //값을 바꾸는데 실패
            }

            return permitRes;
        }else{
            throw new BaseException(DATABASE_ERROR); //값을 바꾸는데 실패
        }
    }
}
