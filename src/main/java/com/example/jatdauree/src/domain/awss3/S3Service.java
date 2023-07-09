package com.example.jatdauree.src.domain.awss3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.jatdauree.config.BaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URL;

@Service
public class S3Service {
    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Autowired
    private AmazonS3 s3Client;


    public S3UrlResult uploadFile(MultipartFile file) throws BaseException, IOException {
        File fileObj = new File("C:\\Users\\d\\Desktop\\s3\\"+file.getOriginalFilename());
        file.transferTo(fileObj);
        String fileName =  file.getOriginalFilename();
        s3Client.putObject(new PutObjectRequest(bucketName, fileName, fileObj));
        //fileObj.delete();

        URL convertedImgUrl = s3Client.getUrl(bucketName, fileName);
        return new S3UrlResult(""+convertedImgUrl);
    }
}
