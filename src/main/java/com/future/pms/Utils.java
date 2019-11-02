package com.future.pms;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Date;

import static com.future.pms.Constants.UPLOADED_FOLDER;


@Service
public final class Utils {

    public static Long getTotalTime(Long dateIn, Long dateOut) {
        Date dateInConv = new Date(dateIn);
        Date dateOutConv = new Date(dateOut);
        return (((dateOutConv.getTime() / 1000) - (dateInConv.getTime() / 1000)) / 60);
    }

    public static boolean checkImageFile(MultipartFile file) {
        if (null != file) {
            String fileName = file.getOriginalFilename();
            if (StringUtils.isEmpty(fileName)) {
                return false;
            }
            return file.getContentType().equals("image/png") || file.getContentType()
                    .equals("image/jpg") || file.getContentType().equals("image/jpeg") || file
                    .getContentType().equals("image/bmp");
        }
        return false;
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
