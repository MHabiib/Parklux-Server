package com.future.pms.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.future.pms.model.Booking;
import com.future.pms.model.parking.ParkingLevel;
import com.future.pms.model.parking.ParkingSection;
import com.future.pms.model.parking.ParkingSlot;
import com.future.pms.model.parking.ParkingZone;
import com.future.pms.repository.*;
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
import java.util.ArrayList;

import static com.future.pms.Constants.*;
import static com.future.pms.Utils.checkImageFile;
import static com.future.pms.Utils.saveUploadedFile;

@Service public class ParkingZoneServiceImpl implements ParkingZoneService {
    @Autowired ParkingZoneRepository parkingZoneRepository;
    @Autowired ParkingLevelRepository parkingLevelRepository;
    @Autowired ParkingSectionRepository parkingSectionRepository;
    @Autowired ParkingSlotRepository parkingSlotRepository;
    @Autowired CustomerRepository customerRepository;
    @Autowired BookingRepository bookingRepository;
    @Autowired PasswordEncoder passwordEncoder;

    @Override public ResponseEntity loadAll(Integer page) {
        PageRequest request = new PageRequest(page, 5, new Sort(Sort.Direction.ASC, "name"));
        return ResponseEntity.ok(parkingZoneRepository.findAllBy(request));
    }

    @Override public ResponseEntity createParkingZone(ParkingZone parkingZone) {
        return null;
    }

    @Override
    public ResponseEntity addParkingLevel(@RequestBody String levelName, Principal principal) {
        ParkingZone parkingZoneExist =
            parkingZoneRepository.findParkingZoneByEmailAdmin(principal.getName());
        if (null != parkingZoneExist) {
            ParkingLevel parkingLevel = new ParkingLevel();
            parkingLevel.setIdParkingZone(parkingZoneExist.getIdParkingZone());
            parkingLevel.setSlotsLayout(SLOTS);
            parkingLevel.setLevelName(levelName);
            parkingLevel.setStatus(LEVEL_AVAILABLE);
            parkingLevelRepository.save(parkingLevel);
            addSection(parkingLevel.getIdLevel());
            return new ResponseEntity<>("Ok", HttpStatus.OK);
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
            parkingSection.setIdParkingZone(parkingLevel.getIdParkingZone());
            parkingSection.setSectionName(parkingLevel.getLevelName() + " Section " + i);
            parkingSection.setStatus(NOT_ACTIVE);
            parkingSectionRepository.save(parkingSection);
        }
    }

    @Override public ResponseEntity updateParkingSection(String idSection) {
        ParkingSection parkingSection =
            parkingSectionRepository.findParkingSectionByIdSection(idSection);
        if (null != parkingSection) {
            switch (parkingSection.getStatus()) {
                case NOT_ACTIVE: {
                    if (updateSlot(parkingSection, ACTIVE).equals(SUCCESS)) {
                        parkingSection.setStatus(ACTIVE);
                        parkingSectionRepository.save(parkingSection);
                        return new ResponseEntity<>(SLOT_UPDATED, HttpStatus.OK);
                    } else {
                        return new ResponseEntity<>("Can't update section !",
                            HttpStatus.BAD_REQUEST);
                    }
                }
                case ACTIVE: {
                    if (updateSlot(parkingSection, NOT_ACTIVE).equals(SUCCESS)) {
                        parkingSection.setStatus(NOT_ACTIVE);
                        parkingSectionRepository.save(parkingSection);
                        return new ResponseEntity<>(SLOT_UPDATED, HttpStatus.OK);
                    } else {
                        return new ResponseEntity<>("Can't update section !",
                            HttpStatus.BAD_REQUEST);
                    }
                }
                default: {
                    return new ResponseEntity<>("Can't update section !", HttpStatus.BAD_REQUEST);
                }
            }
        } else {
            return new ResponseEntity<>("Slot not found !", HttpStatus.BAD_REQUEST);
        }
    }

    @Override public ResponseEntity updateLevel(String idLevel, String slotsLayout) {
        ParkingLevel parkingLevel = parkingLevelRepository.findByIdLevel(idLevel);
        if (null != parkingLevel && slotsLayout.length() == TOTAL_SLOT_IN_LEVEL) {
            ArrayList<String> layout = parkingLevel.getSlotsLayout();
            for (int i = 1; i < layout.size(); i++) {
                layout.set(i, slotsLayout.charAt(i - 1) + layout.get(i).substring(1));
                if (layout.get(i).contains(SLOT_EMPTY)) {
                    ParkingSlot parkingSlot = new ParkingSlot();
                    parkingSlot.setStatus(SLOT_EMPTY);
                    parkingSlot.setIdLevel(parkingLevel.getIdLevel());
                    parkingSlot.setIdParkingZone(parkingLevel.getIdParkingZone());
                    parkingSlot.setSlotNumberInLayout(i);
                    parkingSlot.setName(parkingLevel.getLevelName() + " " + i);
                    parkingSlotRepository.save(parkingSlot);
                }
            }
            parkingLevel.setSlotsLayout(layout);
            parkingLevelRepository.save(parkingLevel);
            return new ResponseEntity<>("Success", HttpStatus.OK);
        }
        return new ResponseEntity<>("Level not found !", HttpStatus.BAD_REQUEST);
    }

    private String updateSlot(ParkingSection parkingSection, String status) {
        ParkingLevel parkingLevel =
            parkingLevelRepository.findByIdLevel(parkingSection.getIdLevel());
        ArrayList<String> layout = parkingLevel.getSlotsLayout();
        String sectionNumber =
            parkingSection.getSectionName().substring(parkingSection.getSectionName().length() - 1);
        String newLayoutValue;
        int totalUpdated = 0;
        if (status.equals(ACTIVE)) {
            newLayoutValue = SLOT_READY;
        } else {
            newLayoutValue = SLOT_NULL;
        }
        for (int i = 1; i < layout.size(); i++) {
            if (layout.get(i).contains(sectionNumber)) {
                layout.set(i, newLayoutValue + layout.get(i).substring(1));
                totalUpdated++;
                if (totalUpdated == TOTAL_SLOT_IN_SECTION) {
                    i = layout.size() + 1;
                }
            }
        }
        if (totalUpdated == TOTAL_SLOT_IN_SECTION) {
            parkingLevel.setSlotsLayout(layout);
            parkingLevelRepository.save(parkingLevel);
            return SUCCESS;
        } else {
            return FAILED;
        }
    }

    @Override public ResponseEntity updateParkingSlot(String idParkingSlot, String status) {
        ParkingSlot parkingSlot = parkingSlotRepository.findByIdSlot(idParkingSlot);
        if (null != parkingSlot) {
            switch (parkingSlot.getStatus()) {
                case SLOT_EMPTY: {
                    parkingSlot.setStatus(SLOT_DISABLE);
                    parkingSlotRepository.save(parkingSlot);
                    return new ResponseEntity<>(SLOT_UPDATED, HttpStatus.OK);
                }
                case SLOT_DISABLE: {
                    parkingSlot.setStatus(SLOT_EMPTY);
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

    @Override public ResponseEntity getParkingLayout(String idBooking) {
        Booking booking = bookingRepository.findBookingByIdBooking(idBooking);
        if (null != booking) {
            ParkingSlot parkingSlot = parkingSlotRepository.findByIdSlot(booking.getIdSlot());
            ParkingLevel parkingLevel =
                parkingLevelRepository.findByIdLevel(parkingSlot.getIdLevel());
            StringBuilder layoutInString = new StringBuilder();
            ArrayList<String> layout = parkingLevel.getSlotsLayout();
            for (int i = 0; i < layout.size(); i++) {
                if (i != parkingSlot.getSlotNumberInLayout()) {
                    layoutInString.append(layout.get(i).charAt(0));
                } else {
                    layoutInString.append(MY_SLOT);
                }
            }
            return new ResponseEntity<>(layoutInString, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Level not found !", HttpStatus.BAD_REQUEST);
        }
    }

    @Override public ResponseEntity getImage(String imageName) throws IOException {
        Path path = Paths.get(UPLOADED_FOLDER + imageName);
        File img = new File(String.valueOf(path));
        String mimetype = FileTypeMap.getDefaultFileTypeMap().getContentType(img);
        return ResponseEntity.ok().contentType(MediaType.valueOf(mimetype))
            .body(Files.readAllBytes(img.toPath()));
    }
}
