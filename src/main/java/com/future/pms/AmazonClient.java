package com.future.pms;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Service public class AmazonClient {

    private AmazonS3 s3client;

    @Value("${amazonProperties.endpointUrl}") private String endpointUrl;
    @Value("${amazonProperties.bucketName}") private String bucketName;
    @Value("${amazonProperties.accessKey}") private String accessKey;
    @Value("${amazonProperties.secretKey}") private String secretKey;

    @PostConstruct private void initializeAmazon() {
        AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);
        this.s3client = new AmazonS3Client(credentials);
    }

    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }

    public String convertMultiPartToFileQR(ByteArrayOutputStream file, String filename)
        throws IOException {
        String fileUrl;
        File convFile = new File(filename);
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.toByteArray());
        fos.close();
        fileUrl = endpointUrl + "/" + bucketName + "/" + filename;
        uploadFileTos3bucket(filename, convFile);
        convFile.delete();
        return fileUrl;
    }

    private void uploadFileTos3bucket(String fileName, File file) {
        s3client.putObject(new PutObjectRequest(bucketName, fileName, file)
            .withCannedAcl(CannedAccessControlList.PublicRead));
    }

    public String uploadFile(MultipartFile multipartFile, String fileName) {
        String fileUrl = "";
        try {
            File file = convertMultiPartToFile(multipartFile);
            fileUrl = endpointUrl + "/" + bucketName + "/" + fileName;
            uploadFileTos3bucket(fileName, file);
            file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileUrl;
    }
}
