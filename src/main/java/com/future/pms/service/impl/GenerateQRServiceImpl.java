package com.future.pms.service.impl;

import com.future.pms.model.QR;
import com.future.pms.model.parking.ParkingLevel;
import com.future.pms.model.parking.ParkingSlot;
import com.future.pms.model.parking.ParkingZone;
import com.future.pms.repository.ParkingLevelRepository;
import com.future.pms.repository.ParkingSlotRepository;
import com.future.pms.repository.ParkingZoneRepository;
import com.future.pms.service.GenerateQRService;
import net.glxn.qrgen.core.image.ImageType;
import net.glxn.qrgen.javase.QRCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import static com.future.pms.Constants.*;

@Service public class GenerateQRServiceImpl implements GenerateQRService {
    @Autowired ParkingZoneRepository parkingZoneRepository;
    @Autowired ParkingSlotRepository parkingSlotRepository;
    @Autowired ParkingLevelRepository parkingLevelRepository;

    @Override public ResponseEntity generateQR(String idParkingZone) {
        ParkingZone parkingZoneExist =
            parkingZoneRepository.findParkingZoneByIdParkingZone(idParkingZone);
        List<ParkingSlot> listParkingSlot = parkingSlotRepository
            .findAllByIdParkingZoneAndStatus(parkingZoneExist.getIdParkingZone(), SLOT_EMPTY);
        if (null == listParkingSlot || listParkingSlot.size() == 0)
            return new ResponseEntity<>(
                "Parking Zone on " + parkingZoneExist.getName() + " already full !", HttpStatus.OK);
        else {
            ParkingSlot parkingSlot =
                listParkingSlot.get((int) (Math.random() * listParkingSlot.size()));
            if (SLOT_EMPTY.equals(parkingSlot.getStatus())) {
                parkingSlot.setStatus(SLOT_SCAN_ME);
                parkingSlotRepository.save(parkingSlot);
                ParkingLevel parkingLevel =
                    parkingLevelRepository.findByIdLevel(parkingSlot.getIdLevel());
                ArrayList<String> layout = parkingLevel.getSlotsLayout();
                layout.set(parkingSlot.getSlotNumberInLayout(),
                    SLOT_SCAN_ME + layout.get(parkingSlot.getSlotNumberInLayout()).substring(1));
                parkingLevel.setSlotsLayout(layout);
                parkingLevelRepository.save(parkingLevel);
                QR qr = new QR();
                qr.setIdSlot(parkingSlot.getIdSlot());
                ByteArrayOutputStream bout =
                    QRCode.from(String.valueOf(qr)).withSize(250, 250).to(ImageType.PNG).stream();
                try {
                    OutputStream out = new FileOutputStream(
                        FILE_LOCATION + parkingZoneExist.getName() + " - " + parkingSlot.getName()
                            + ".png");
                    bout.writeTo(out);
                    out.flush();
                    out.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                return new ResponseEntity<>("Slot taken !", HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>("Parking Location " + parkingSlot.getName(), HttpStatus.OK);
        }
    }
}
