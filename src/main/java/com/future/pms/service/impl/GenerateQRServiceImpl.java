package com.future.pms.service.impl;

import com.future.pms.model.QR;
import com.future.pms.service.GenerateQRService;
import net.glxn.qrgen.core.image.ImageType;
import net.glxn.qrgen.javase.QRCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Random;

@Service
public class GenerateQRServiceImpl implements GenerateQRService {
    @Override
    public ResponseEntity generateQR(String... args) {
        QR qr = new QR();
        Random rnd = new Random(12);
        qr.setIdBooking("idBooking");
        qr.setRandomTestSlot(String.valueOf(rnd.nextInt()));

        ByteArrayOutputStream bout =
                QRCode.from(String.valueOf(qr))
                        .withSize(250, 250)
                        .to(ImageType.PNG)
                        .stream();
        try {
            OutputStream out = new FileOutputStream("../tmp/qr-code-vcard.png");
            bout.writeTo(out);
            out.flush();
            out.close();

        } catch (IOException e){
            e.printStackTrace();
        }
        return new ResponseEntity<>("Generated", HttpStatus.OK);
    }
}
