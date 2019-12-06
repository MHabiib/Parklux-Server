package com.future.pms.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.future.pms.model.parking.ParkingLevel;
import com.future.pms.model.parking.ParkingSection;
import com.future.pms.model.parking.ParkingSlot;
import com.future.pms.model.parking.ParkingZone;
import com.future.pms.model.request.CreateParkingSlotRequest;
import com.future.pms.repository.ParkingLevelRepository;
import com.future.pms.repository.ParkingSectionRepository;
import com.future.pms.repository.ParkingSlotRepository;
import com.future.pms.repository.ParkingZoneRepository;
import com.future.pms.service.ParkingZoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import javax.activation.FileTypeMap;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;

import static com.future.pms.Constants.*;
import static com.future.pms.Utils.checkImageFile;
import static com.future.pms.Utils.saveUploadedFile;

@Service public class ParkingZoneServiceImpl implements ParkingZoneService {
    @Autowired ParkingZoneRepository parkingZoneRepository;
    @Autowired ParkingLevelRepository parkingLevelRepository;
    @Autowired ParkingSectionRepository parkingSectionRepository;
    @Autowired ParkingSlotRepository parkingSlotRepository;
    @Autowired PasswordEncoder passwordEncoder;

    @Override public ResponseEntity loadAll(Integer page) {
        PageRequest request = new PageRequest(page, 5, new Sort(Sort.Direction.ASC, "name"));
        return ResponseEntity.ok(parkingZoneRepository.findAllBy(request));
    }

    @Override public ResponseEntity createParkingZone(ParkingZone parkingZone) {
        return null;
    }

    @Override public ResponseEntity addParkingLevel(@RequestBody ParkingLevel parkingLevel) {
        ParkingZone parkingZoneExist =
            parkingZoneRepository.findParkingZoneByIdParkingZone(parkingLevel.getIdParkingZone());
        if (null != parkingZoneExist) {
            parkingLevel.setSlotsLayout(SLOTS);
            parkingLevelRepository.save(parkingLevel);
            addSection(parkingLevel.getIdLevel());
            return new ResponseEntity<>("ok", HttpStatus.OK);
        } else {
            return new ResponseEntity<>(PARKING_ZONE_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }
    }

    private void addSection(String idLevel) {
        ParkingLevel parkingLevel = parkingLevelRepository.findByIdLevel(idLevel);
        for (int i = 1; i < 5; i++) {
            ParkingSection parkingSection = new ParkingSection();
            parkingSection.setIdLevel(idLevel);
            parkingSection.setIdParkingZone(parkingLevel.getIdParkingZone());
            parkingSection.setSectionName(parkingLevel.getLevelName() + "section" + i);
            parkingSection.setStatus("NOT_ACTIVE");
            parkingSectionRepository.save(parkingSection);
        }
    }

   /* @Override public ResponseEntity addParkingSection(@RequestBody ParkingSection parkingSection) {
        ParkingLevel parkingLevelExist =
            parkingLevelRepository.findByIdLevel(parkingSection.getIdLevel());
        if (null != parkingLevelExist) {
            parkingSectionRepository.save(parkingSection);
            CreateParkingSlotRequest parkingSlotRequest = new CreateParkingSlotRequest();
            parkingSlotRequest.setIdParkingZone(parkingLevelExist.getIdParkingZone());
            parkingSlotRequest.setIdSection(parkingSection.getIdSection());
            for (int i = 1; i <= NUMBER_OF_SLOT; i++) {
                parkingSlotRequest
                    .setSlotName(String.format("%s - %s", parkingSection.getSectionName(), i));
                addSlot(parkingSlotRequest);
            }
            return new ResponseEntity<>(
                "Parking Section Created and Adds 20 Slot on " + parkingSection.getSectionName(),
                HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Parking Level Not Found", HttpStatus.BAD_REQUEST);
        }
    }*/

    private void addSlot(CreateParkingSlotRequest slotRequest) {
        ParkingSlot parkingSlot = new ParkingSlot();
        parkingSlot.setIdSection(slotRequest.getIdSection());
        parkingSlot.setIdParkingZone(slotRequest.getIdParkingZone());
        parkingSlot.setName(slotRequest.getSlotName());
        parkingSlotRepository.save(parkingSlot);
    }

    @Override public ResponseEntity updateParkingSlot(String idParkingSlot, String status) {
        ParkingSlot parkingSlot = parkingSlotRepository.findByIdSlot(idParkingSlot);
        if (null != parkingSlot) {
            switch (parkingSlot.getStatus()) {
                case AVAILABLE: {
                    parkingSlot.setStatus(DISABLE);
                    parkingSlotRepository.save(parkingSlot);
                    return new ResponseEntity<>(SLOT_UPDATED, HttpStatus.OK);
                }
                case DISABLE: {
                    parkingSlot.setStatus(AVAILABLE);
                    parkingSlotRepository.save(parkingSlot);
                    return new ResponseEntity<>(SLOT_UPDATED, HttpStatus.OK);
                }
                default: {
                    return new ResponseEntity<>("Can't update slot, there are ongoing booking !",
                        HttpStatus.BAD_REQUEST);
                }
            }
        } else {
            return new ResponseEntity<>("Slot not found !", HttpStatus.BAD_REQUEST);
        }
    }

    @Override public ResponseEntity deleteParkingZone(String id) {
        return null;
    }

    @Override public ResponseEntity updateParkingZone(Principal principal, MultipartFile file,
        String parkingZoneJSON) throws IOException {
        ParkingZone parkingZone = new ObjectMapper().readValue(parkingZoneJSON, ParkingZone.class);
        ParkingZone parkingZoneDetail =
            parkingZoneRepository.findParkingZoneByEmailAdmin(principal.getName());
        ParkingZone parkingZoneExist = parkingZoneRepository
            .findParkingZoneByIdParkingZone(parkingZoneDetail.getIdParkingZone());
        if (parkingZoneExist == null) {
            return new ResponseEntity<>(PARKING_ZONE_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }
        if (checkImageFile(file)) {
            try {
                if (parkingZoneExist.getImageUrl() != null) {
                    Path deletePath = Paths.get(UPLOADED_FOLDER + parkingZoneExist.getImageUrl());
                    Files.delete(deletePath);
                }
                String fileName = parkingZone.getEmailAdmin();
                saveUploadedFile(file, fileName);
                parkingZone.setImageUrl(UPLOADED_FOLDER + fileName);
            } catch (IOException e) {
                return new ResponseEntity<>("Some error occured. Failed to add image",
                    HttpStatus.BAD_REQUEST);
            }
        }
        parkingZoneExist.setName(parkingZone.getName());
        parkingZoneExist.setAddress(parkingZone.getAddress());
        parkingZoneExist.setOpenHour(parkingZone.getOpenHour());
        parkingZoneExist.setPhoneNumber(parkingZone.getPhoneNumber());
        parkingZoneExist.setPrice(parkingZone.getPrice());
        parkingZoneExist.setImageUrl(parkingZone.getImageUrl());
        parkingZoneRepository.save(parkingZoneExist);
        return new ResponseEntity<>("Parking Zone Updated", HttpStatus.OK);
    }

    @Override public ResponseEntity getImage(String imageName) throws IOException {
        Path path = Paths.get(UPLOADED_FOLDER + imageName);
        File img = new File(String.valueOf(path));
        String mimetype = FileTypeMap.getDefaultFileTypeMap().getContentType(img);
        return ResponseEntity.ok().contentType(MediaType.valueOf(mimetype))
            .body(Files.readAllBytes(img.toPath()));
    }
}
