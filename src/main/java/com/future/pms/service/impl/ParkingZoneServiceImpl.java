package com.future.pms.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.future.pms.model.Booking;
import com.future.pms.model.parking.ParkingLevel;
import com.future.pms.model.parking.ParkingSection;
import com.future.pms.model.parking.ParkingSlot;
import com.future.pms.model.parking.ParkingZone;
import com.future.pms.model.request.ListLevelRequest;
import com.future.pms.model.request.SectionDetailRequest;
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
import java.util.List;

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

    @Override public ResponseEntity getParkingZoneDetail(Principal principal) {
        return ResponseEntity
            .ok(parkingZoneRepository.findParkingZoneByEmailAdmin(principal.getName()));
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
            parkingLevel.setLevelName(levelName.substring(1, levelName.length() - 1));
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
            parkingSection.setSectionName("Section " + i);
            parkingSection.setStatus(NOT_ACTIVE);
            parkingSectionRepository.save(parkingSection);
        }
    }

    @Override public ResponseEntity updateParkingSection(String idSection) {
        ParkingSection parkingSection = parkingSectionRepository
            .findParkingSectionByIdSection(idSection.substring(1, idSection.length() - 1));
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
        slotsLayout = slotsLayout.substring(1, slotsLayout.length() - 1);
        ParkingLevel parkingLevel = parkingLevelRepository.findByIdLevel(idLevel);
        if (null != parkingLevel && slotsLayout.length() == TOTAL_SLOT_IN_LEVEL) {
            ArrayList<String> layout = parkingLevel.getSlotsLayout();
            String existSlot = "";
            String successCreateSlot = "";
            for (int i = 0; i < layout.size(); i++) {
                layout.set(i, slotsLayout.charAt(i) + layout.get(i).substring(1));
                ParkingSlot parkingSlotExist = parkingSlotRepository
                    .findByIdParkingZoneAndSlotNumberInLayout(parkingLevel.getIdParkingZone(), i);
                if (null != parkingSlotExist) {
                    parkingSlotExist.setStatus(Character.toString(slotsLayout.charAt(i)));
                    parkingSlotRepository.save(parkingSlotExist);
                }
                if (layout.get(i).contains(SLOT_EMPTY)) {
                    if (null == parkingSlotRepository
                        .findByIdParkingZoneAndSlotNumberInLayout(parkingLevel.getIdParkingZone(),
                            i)) {
                        ParkingSlot parkingSlot = new ParkingSlot();
                        parkingSlot.setStatus(SLOT_EMPTY);
                        parkingSlot.setIdLevel(parkingLevel.getIdLevel());
                        parkingSlot.setIdParkingZone(parkingLevel.getIdParkingZone());
                        parkingSlot.setSlotNumberInLayout(i);
                        parkingSlot.setName(parkingLevel.getLevelName() + " " + i + 1);
                        parkingSlotRepository.save(parkingSlot);
                        successCreateSlot += (i + ", ");
                    } else {
                        existSlot += (i + ", ");
                    }
                }
            }
            parkingLevel.setSlotsLayout(layout);
            parkingLevelRepository.save(parkingLevel);
            return new ResponseEntity<>(
                "Success create slot " + successCreateSlot + ", not update slot " + existSlot,
                HttpStatus.OK);
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
        for (int i = 0; i < layout.size(); i++) {
            if (layout.get(i).contains(sectionNumber) && !layout.get(i).contains(SLOT_SCAN_ME)
                && !layout.get(i).contains(SLOT_TAKEN)) {
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

    @Override public ResponseEntity getLevels(Principal principal) {
        List<ParkingLevel> parkingLevel = parkingLevelRepository.findByIdParkingZone(
            parkingZoneRepository.findParkingZoneByEmailAdmin(principal.getName())
                .getIdParkingZone());
        ArrayList<ListLevelRequest> listLevelRequest = new ArrayList<>();
        for (ParkingLevel level : parkingLevel) {
            ListLevelRequest listLevel = new ListLevelRequest();
            listLevel.setIdLevel(level.getIdLevel());
            listLevel.setLevelName(level.getLevelName());
            listLevelRequest.add(listLevel);
        }
        return new ResponseEntity<>(listLevelRequest, HttpStatus.OK);
    }

    @Override public ResponseEntity getParkingBookingLayout(String idBooking) {
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

    @Override public ResponseEntity getParkingLevelLayout(String idLevel) {
        ParkingLevel parkingLevel = parkingLevelRepository.findByIdLevel(idLevel);
        if (null != parkingLevel) {
            StringBuilder layoutInString = new StringBuilder();
            ArrayList<String> layout = parkingLevel.getSlotsLayout();
            for (String s : layout) {
                layoutInString.append(s.charAt(0));
            }
            return new ResponseEntity<>(layoutInString, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Parking zone not found", HttpStatus.BAD_REQUEST);
        }
    }

    @Override public ResponseEntity getSectionDetails(String idLevel) {
        ArrayList<SectionDetailRequest> sectionDetailRequests = new ArrayList<>();
        SectionDetailRequest section1 = new SectionDetailRequest();
        SectionDetailRequest section2 = new SectionDetailRequest();
        SectionDetailRequest section3 = new SectionDetailRequest();
        SectionDetailRequest section4 = new SectionDetailRequest();
        section1.setSectionName(SECTION_ONE);
        section1.setIdSection(
            parkingSectionRepository.findParkingSectionBySectionNameAndIdLevel(SECTION_ONE, idLevel)
                .getIdSection());
        section2.setSectionName(SECTION_TWO);
        section2.setIdSection(
            parkingSectionRepository.findParkingSectionBySectionNameAndIdLevel(SECTION_TWO, idLevel)
                .getIdSection());
        section3.setSectionName(SECTION_THREE);
        section3.setIdSection(parkingSectionRepository
            .findParkingSectionBySectionNameAndIdLevel(SECTION_THREE, idLevel).getIdSection());
        section4.setSectionName(SECTION_FOUR);
        section4.setIdSection(parkingSectionRepository
            .findParkingSectionBySectionNameAndIdLevel(SECTION_FOUR, idLevel).getIdSection());

        ArrayList<String> layout = parkingLevelRepository.findByIdLevel(idLevel).getSlotsLayout();
        for (String s : layout) {
            if (Character.toString(s.charAt(1)).equals("1")) {
                sectionDetail(section1, s);
            }
            if (Character.toString(s.charAt(1)).equals("2")) {
                sectionDetail(section2, s);
            }
            if (Character.toString(s.charAt(1)).equals("3")) {
                sectionDetail(section3, s);
            }
            if (Character.toString(s.charAt(1)).equals("4")) {
                sectionDetail(section4, s);
            }
        }

        sectionDetailRequests.add(section1);
        sectionDetailRequests.add(section2);
        sectionDetailRequests.add(section3);
        sectionDetailRequests.add(section4);
        return new ResponseEntity<>(sectionDetailRequests, HttpStatus.OK);
    }

    private void sectionDetail(SectionDetailRequest section, String s) {
        if (!SLOT_NULL.equals(Character.toString(s.charAt(0)))) {
            section.setStatus(ACTIVE);
            switch (Character.toString(s.charAt(0))) {
                case SLOT_TAKEN:
                case SLOT_SCAN_ME:
                    section.setTotalTakenSlot(section.getTotalTakenSlot() + 1);
                    break;
                case SLOT_EMPTY:
                    section.setTotalEmptySlot(section.getTotalEmptySlot() + 1);
                    break;
                case SLOT_DISABLE:
                    section.setTotalDisableSlot(section.getTotalDisableSlot() + 1);
                    break;
            }
        } else {
            section.setStatus(NOT_ACTIVE);
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
