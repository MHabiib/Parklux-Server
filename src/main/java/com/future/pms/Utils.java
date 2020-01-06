package com.future.pms;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Service public final class Utils {

    public static Long getTotalTime(Long dateIn, Long dateOut) {
        Date dateInConv = new Date(dateIn);
        Date dateOutConv = new Date(dateOut);
        return (((dateOutConv.getTime() / 1000) - (dateInConv.getTime() / 1000)) / 60);
    }

    public static boolean checkImageFile(MultipartFile file) {
        boolean isImage = false;
        if (null != file && !StringUtils.isEmpty(file.getOriginalFilename())) {
            isImage = file.getContentType().equals("image/png") || file.getContentType()
                .equals("image/jpg") || file.getContentType().equals("image/jpeg") || file
                .getContentType().equals("image/bmp");
        }
        return isImage;
    }
}
