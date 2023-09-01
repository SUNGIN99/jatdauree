package com.example.jatdauree.utils;

import jdk.swing.interop.SwingInterOpUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;

public class UtilFileImgUrl {
    private final static long currentTimeNanosOffset = (System.currentTimeMillis() * 1000000) - System.nanoTime();

    public static long currentTimeNanos() {
        return System.nanoTime() + currentTimeNanosOffset;
    }

    public static String absolutePath = "/home/ubuntu/s3tempImg/";
    //public static String absolutePath = "C:\\Users\\d\\Desktop\\s3\\s3Save\\";

    public static String checkFileIsNullThenName(MultipartFile files) {
        long nanos = UtilFileImgUrl.currentTimeNanos();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");

        String fileNanoName = sdf.format(nanos / 1000000L);

        //System.out.println(files);
        //System.out.println(files.getContentType());
        //System.out.println(files.getSize());
        if (files == null) {
            return null;
        } else if (!files.isEmpty()) {
            String contentType = files.getContentType();
            String originalFileExtension;

            if (ObjectUtils.isEmpty(contentType)) {
                return null;
            } else {
                if (contentType.contains("image/jpeg"))
                    originalFileExtension = ".jpg";
                else if (contentType.contains("image/png"))
                    originalFileExtension = ".png";
                else if (contentType.contains("image/gif"))
                    originalFileExtension = ".gif";
                else
                    return null;
                return fileNanoName + System.nanoTime() + originalFileExtension;
            }
        }
        return null;
    }
}

