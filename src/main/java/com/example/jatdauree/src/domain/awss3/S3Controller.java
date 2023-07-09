package com.example.jatdauree.src.domain.awss3;


import com.example.jatdauree.config.BaseException;
import com.example.jatdauree.config.BaseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequestMapping("/sss")
@RestController
public class S3Controller {

    @Autowired
    private final S3Service s3Service;

    public S3Controller(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    @PostMapping("")
    @ResponseBody
    public BaseResponse<S3UrlResult> getImgUrlFromS3(@RequestParam(value="file")MultipartFile file){
        try{
            S3UrlResult s3UrlResult = s3Service.uploadFile(file);

            return new BaseResponse<>(s3UrlResult);
        }catch (BaseException baseException){
            return new BaseResponse<>(baseException.getStatus());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
