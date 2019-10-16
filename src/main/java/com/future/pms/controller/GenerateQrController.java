package com.future.pms.controller;

import com.future.pms.model.QR;
import net.glxn.qrgen.core.image.ImageType;
import net.glxn.qrgen.core.vcard.VCard;
import net.glxn.qrgen.javase.QRCode;

import java.io.*;
import java.util.Random;

public class GenerateQrController {
    public static void main(String... args){
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

        } catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
