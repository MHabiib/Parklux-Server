package com.future.pms.service.impl;

import com.future.pms.model.QR;
import com.future.pms.model.parking.ParkingSlot;
import com.future.pms.model.parking.ParkingZone;
import com.future.pms.repository.ParkingSlotRepository;
import com.future.pms.repository.ParkingZoneRepository;
import com.future.pms.service.GenerateQRService;
import net.glxn.qrgen.core.image.ImageType;
import net.glxn.qrgen.javase.QRCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Random;

@Service
public class GenerateQRServiceImpl implements GenerateQRService {

    @Autowired
    ParkingZoneRepository parkingZoneRepository;

    @Autowired
    ParkingSlotRepository parkingSlotRepository;

    @Override
    public ResponseEntity generateQR(String idParkingZone) {

        ParkingZone parkingZoneExist = parkingZoneRepository.findParkingZoneByIdParkingZone(idParkingZone);
        ParkingSlot parkingSlot = parkingSlotRepository
                .findFirstByIdParkingZoneAndStatus(parkingZoneExist.getIdParkingZone(),"AVAILABLE");
        parkingSlot.setStatus("SCAN_ME");
        parkingSlotRepository.save(parkingSlot);
            QR qr = new QR();
            qr.setSlotName(parkingSlot.getName());
            qr.setIdParkingZone(parkingSlot.getIdParkingZone());
            ByteArrayOutputStream bout =
                    QRCode.from(String.valueOf(qr))
                            .withSize(250, 250)
                            .to(ImageType.PNG)
                            .stream();
            try {
                OutputStream out = new FileOutputStream("../tmp/"
                        + parkingZoneExist.getName()
                        + " - "+parkingSlot.getName()
                        + ".png");
                bout.writeTo(out);
                out.flush();
                out.close();

            } catch (IOException e){
                e.printStackTrace();
            }
        return new ResponseEntity<>("Generated", HttpStatus.OK);
    }

}
