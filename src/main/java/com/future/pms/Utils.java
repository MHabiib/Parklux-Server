package com.future.pms;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

import static com.future.pms.Constants.UPLOADED_FOLDER;

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

    public static void saveUploadedFile(MultipartFile file, String name) throws IOException {
        if (!file.isEmpty()) {
            byte[] bytes = file.getBytes();
            Path path = Paths.get(UPLOADED_FOLDER + name);
            System.out.println(UPLOADED_FOLDER + name);
            Files.write(path, bytes);
        }
    }
}
