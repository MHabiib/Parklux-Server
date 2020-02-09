package com.future.pms.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.future.pms.AmazonClient;
import com.future.pms.config.MongoTokenStore;
import com.future.pms.model.Booking;
import com.future.pms.model.User;
import com.future.pms.model.parking.*;
import com.future.pms.model.request.LevelDetailsRequest;
import com.future.pms.model.request.ListLevelRequest;
import com.future.pms.model.request.SectionDetailRequest;
import com.future.pms.model.request.UpdateParkingZoneRequest;
import com.future.pms.repository.*;
import com.future.pms.service.ParkingZoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import static com.future.pms.Constants.*;
import static com.future.pms.Utils.checkImageFile;

@Service public class ParkingZoneServiceImpl implements ParkingZoneService {
    @Autowired ParkingZoneRepository parkingZoneRepository;
    @Autowired ParkingLevelRepository parkingLevelRepository;
    @Autowired ParkingSectionRepository parkingSectionRepository;
    @Autowired ParkingSlotRepository parkingSlotRepository;
    @Autowired CustomerRepository customerRepository;
    @Autowired UserRepository userRepository;
    @Autowired BookingRepository bookingRepository;
    @Autowired PasswordEncoder passwordEncoder;
    @Autowired AmazonClient amazonClient;
    @Autowired MongoTokenStore mongoTokenStore;

    @Override public ResponseEntity loadAll(Integer page, String name) {
        PageRequest request = PageRequest.of(page, 10, new Sort(Sort.Direction.ASC, "name"));
        return ResponseEntity
            .ok(parkingZoneRepository.findParkingZoneByNameContainingAllIgnoreCase(request, name));
    }

    @Override public ResponseEntity getParkingZoneDetail(Principal principal) {
        if (parkingZoneRepository.findParkingZoneByEmailAdmin(principal.getName()) != null) {
            return new ResponseEntity<>(
                parkingZoneRepository.findParkingZoneByEmailAdmin(principal.getName()),
                HttpStatus.OK);
        } else {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
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
            return new ResponseEntity<>("Section not found !", HttpStatus.BAD_REQUEST);
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
                if (layout.get(i).contains(SLOT_TAKEN)
                    && slotsLayout.charAt(i) != (SLOT_TAKEN_CHAR)) {
                    ParkingSlot parkingSlot = parkingSlotRepository
                        .findByIdParkingZoneAndSlotNumberInLayout(parkingLevel.getIdParkingZone(),
                            i);
                    Booking booking = bookingRepository
                        .findBookingByIdSlotAndDateOutNull(parkingSlot.getIdSlot());
                    BookingServiceImpl bookingService = new BookingServiceImpl();
                    bookingService.bookingCheckoutSetup(booking, parkingSlot, parkingSlotRepository,
                        bookingRepository);
                }
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
                        parkingSlot.setName(parkingLevel.getLevelName() + " " + slotName(i));
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

    private String slotName(Integer slotAt) {
        for (int i = 1; i <= TOTAL_SLOT_IN_ROW; i++) {
            if (slotAt < TOTAL_SLOT_IN_ROW * i) {
                return "(" + LETTER.get(i - 1) + "-" + ((slotAt % TOTAL_SLOT_IN_ROW) + 1) + ")";
            }
        }
        return "";
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
                if (layout.get(i).contains(SLOT_EMPTY)) {
                    ParkingSlot parkingSlot = parkingSlotRepository
                        .findByIdParkingZoneAndSlotNumberInLayout(parkingLevel.getIdParkingZone(),
                            i);
                    parkingSlot.setStatus(SLOT_READY);
                    parkingSlotRepository.save(parkingSlot);
                }
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

    @Override public ResponseEntity updateParkingZone(Principal principal, String parkingZoneJSON)
        throws IOException {
        UpdateParkingZoneRequest parkingZone =
            new ObjectMapper().readValue(parkingZoneJSON, UpdateParkingZoneRequest.class);
        ParkingZone parkingZoneDetail =
            parkingZoneRepository.findParkingZoneByEmailAdmin(principal.getName());
        if (parkingZoneDetail == null) {
            return new ResponseEntity<>(PARKING_ZONE_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }
        return updateParkingZone(parkingZone, parkingZoneDetail, principal.getName());
    }

    @Override public ResponseEntity updateAdmin(String id, String parkingZoneJSON)
        throws IOException {
        UpdateParkingZoneRequest parkingZone =
            new ObjectMapper().readValue(parkingZoneJSON, UpdateParkingZoneRequest.class);
        ParkingZone parkingZoneDetail = parkingZoneRepository.findParkingZoneByIdParkingZone(id);
        if (parkingZoneDetail == null) {
            return new ResponseEntity<>(PARKING_ZONE_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }
        return updateParkingZone(parkingZone, parkingZoneDetail, parkingZoneDetail.getEmailAdmin());
    }

    private ResponseEntity updateParkingZone(UpdateParkingZoneRequest parkingZone,
        ParkingZone parkingZoneDetail, String emailAdmin) {
        ParkingZone parkingZoneExist = parkingZoneRepository
            .findParkingZoneByIdParkingZone(parkingZoneDetail.getIdParkingZone());
        User user = userRepository.findByEmail(emailAdmin);
        if (!"".equals(parkingZone.getPassword())) {
            user.setPassword(passwordEncoder.encode(parkingZone.getPassword()));
        }
        if (!parkingZoneExist.getEmailAdmin().equals(parkingZone.getEmailAdmin())) {
            if (!parkingZoneExist.getEmailAdmin().equals(parkingZone.getEmailAdmin())
                && userRepository.countByEmail(parkingZone.getEmailAdmin()) > 0) {
                return new ResponseEntity<>(PARKING_ZONE_NOT_FOUND, HttpStatus.BAD_REQUEST);
            } else {
                mongoTokenStore.revokeToken(parkingZoneExist.getEmailAdmin());
                parkingZoneExist.setEmailAdmin(parkingZone.getEmailAdmin());
            }
        }
        if (0.0 != parkingZone.getLatitude()) {
            parkingZoneExist.setLongitude(parkingZone.getLongitude());
            parkingZoneExist.setLatitude(parkingZone.getLatitude());
        }
        if (parkingZone.getImageUrl().equals("")) {
            parkingZone.setImageUrl(parkingZoneDetail.getImageUrl());
        }
        if (0.0 != (parkingZone.getPrice())) {
            parkingZoneExist.setPrice(parkingZone.getPrice());
        }
        if (!" - ".equals(parkingZone.getOpenHour())) {
            parkingZoneExist.setOpenHour(parkingZone.getOpenHour());
        }
        parkingZoneExist.setName(parkingZone.getName());
        parkingZoneExist.setAddress(parkingZone.getAddress());
        parkingZoneExist.setPhoneNumber(parkingZone.getPhoneNumber());
        parkingZoneExist.setImageUrl(parkingZone.getImageUrl());
        parkingZoneExist.setLatitude(parkingZone.getLatitude());
        parkingZoneExist.setLongitude(parkingZone.getLongitude());
        user.setEmail(parkingZone.getEmailAdmin());
        parkingZoneRepository.save(parkingZoneExist);
        userRepository.save(user);
        return new ResponseEntity<>(parkingZoneExist, HttpStatus.OK);
    }

    @Override public ResponseEntity updateParkingLevel(LevelDetailsRequest levelDetailsRequest,
        Principal principal) {
        ParkingLevel parkingLevel =
            parkingLevelRepository.findByIdLevel(levelDetailsRequest.getIdLevel());
        List<ParkingSlot> parkingSlotList =
            parkingSlotRepository.findAllByIdLevel(parkingLevel.getIdLevel());
        switch (levelDetailsRequest.getStatus()) {
            case LEVEL_AVAILABLE: {
                if (levelDetailsRequest.getLevelName().equals("")) {
                    levelDetailsRequest.setLevelName(parkingLevel.getLevelName());
                }
                if (!parkingLevel.getStatus().equals(levelDetailsRequest.getStatus())
                    && null != parkingSlotList) {
                    for (ParkingSlot parkingSlot : parkingSlotList) {
                        parkingSlot.setStatus(parkingSlot.getStatus()
                            .substring(parkingSlot.getStatus().length() - 1));
                        parkingSlotRepository.save(parkingSlot);
                    }
                }
                if (levelDetailsRequest.getLevelName().equals(parkingLevel.getLevelName())
                    && parkingLevel.getLevelName().contains(" - Unavailable")) {
                    levelDetailsRequest.setLevelName(levelDetailsRequest.getLevelName()
                        .substring(0, levelDetailsRequest.getLevelName().length() - 14));
                }
                parkingLevel.setLevelName(levelDetailsRequest.getLevelName());
                parkingLevel.setStatus(LEVEL_AVAILABLE);
                parkingLevelRepository.save(parkingLevel);
                return new ResponseEntity<>("Success", HttpStatus.OK);
            }
            case LEVEL_UNAVAILABLE: {
                if (levelDetailsRequest.getLevelName().equals("")) {
                    levelDetailsRequest.setLevelName(parkingLevel.getLevelName());
                }
                if (!levelDetailsRequest.getLevelName().contains(" - Unavailable")) {
                    parkingLevel
                        .setLevelName(levelDetailsRequest.getLevelName() + " - Unavailable");
                } else {
                    parkingLevel.setLevelName(levelDetailsRequest.getLevelName());
                }
                if (!parkingLevel.getStatus().equals(levelDetailsRequest.getStatus())
                    && null != parkingSlotList) {
                    for (ParkingSlot parkingSlot : parkingSlotList) {
                        if (parkingSlot.getStatus().equals(SLOT_TAKEN) || parkingSlot.getStatus()
                            .equals(SLOT_SCAN_ME)) {
                            return new ResponseEntity<>("There are ongoing parking",
                                HttpStatus.BAD_REQUEST);
                        }
                        parkingSlot.setStatus(
                            String.format("%s-%s", LEVEL_UNAVAILABLE, parkingSlot.getStatus()));
                        parkingSlotRepository.save(parkingSlot);
                    }
                }
                parkingLevel.setStatus(LEVEL_UNAVAILABLE);
                parkingLevelRepository.save(parkingLevel);
                return new ResponseEntity<>("Success", HttpStatus.OK);
            }
            case LEVEL_TAKE_OUT: {
                for (ParkingSlot parkingSlot : parkingSlotList) {
                    if (parkingSlot.getStatus().equals(SLOT_TAKEN) || parkingSlot.getStatus()
                        .equals(SLOT_SCAN_ME)) {
                        return new ResponseEntity<>("There are ongoing parking",
                            HttpStatus.BAD_REQUEST);
                    }
                }
                parkingSectionRepository.deleteAll(parkingSectionRepository
                    .findParkingSectionByIdLevel(parkingLevel.getIdLevel()));
                parkingSlotRepository.deleteAll(parkingSlotList);
                parkingLevelRepository.delete(parkingLevel);
                return new ResponseEntity<>("Success", HttpStatus.OK);
            }
        }
        return new ResponseEntity<>("Failed", HttpStatus.BAD_REQUEST);
    }

    @Override public ResponseEntity getLevels(Principal principal) {
        List<ParkingLevel> parkingLevel = parkingLevelRepository
            .findByIdParkingZoneOrderByLevelName(
                parkingZoneRepository.findParkingZoneByEmailAdmin(principal.getName())
                    .getIdParkingZone());
        ArrayList<ListLevelRequest> listLevelRequest = new ArrayList<>();
        for (ParkingLevel level : parkingLevel) {
            ListLevelRequest listLevel = new ListLevelRequest();
            listLevel.setIdLevel(level.getIdLevel());
            listLevel.setLevelName(level.getLevelName());
            listLevel.setLevelStatus(level.getStatus());
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

    @Override public ResponseEntity editModeParkingLevel(String idLevel, String mode) {
        ParkingLevel parkingLevel = parkingLevelRepository.findByIdLevel(idLevel);
        if (null != parkingLevel) {
            List<ParkingSlot> parkingSlotList = parkingSlotRepository.findAllByIdLevel(idLevel);
            if (mode.equals(EDIT_MODE)) {
                if (null != parkingSlotList) {
                    for (ParkingSlot parkingSlot : parkingSlotList) {
                        parkingSlot.setStatus(
                            String.format("%s-%s", LEVEL_ON_EDIT, parkingSlot.getStatus()));
                        parkingSlotRepository.save(parkingSlot);
                    }
                }
                parkingLevel.setStatus(LEVEL_ON_EDIT);
            } else {
                if (mode.equals(EXIT_EDIT_MODE)) {
                    for (ParkingSlot parkingSlot : parkingSlotList) {
                        parkingSlot.setStatus(parkingSlot.getStatus()
                            .substring(parkingSlot.getStatus().length() - 1));
                        parkingSlotRepository.save(parkingSlot);
                    }
                }
                parkingLevel.setStatus(LEVEL_AVAILABLE);
            }
            parkingLevelRepository.save(parkingLevel);
            return new ResponseEntity<>("Success change level mode", HttpStatus.OK);
        } else
            return new ResponseEntity<>("Parking zone not found", HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity updateParkingZonePicture(Principal principal, MultipartFile file) {
        ParkingZone parkingZoneExist =
            parkingZoneRepository.findParkingZoneByEmailAdmin(principal.getName());
        if (checkImageFile(file)) {
            String fileName = parkingZoneExist.getEmailAdmin().replaceAll("\\s+", "") + ".png";
            parkingZoneExist.setImageUrl(amazonClient.uploadFile(file, fileName));
            parkingZoneRepository.save(parkingZoneExist);
            return new ResponseEntity<>("Image saved", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Some error occured. Failed to add image",
                HttpStatus.BAD_REQUEST);
        }
    }

    @Override public ResponseEntity getAdminSA(String id) {
        ParkingZone parkingZone = parkingZoneRepository.findParkingZoneByIdParkingZone(id);
        if (parkingZone != null) {
            return new ResponseEntity<>(parkingZoneRepository.findParkingZoneByIdParkingZone(id),
                HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Admin not found", HttpStatus.NOT_FOUND);
        }
    }

    @Override public ResponseEntity getLatLng() {
        List<ParkingZone> parkingZoneList =
            parkingZoneRepository.findParkingZoneByLatitudeNotNullAndLongitudeNotLike(0.0);
        List<ParkingZoneLatLng> parkingZoneLatLngList = new ArrayList<>();
        for (ParkingZone parkingZone : parkingZoneList) {
            ParkingZoneLatLng parkingZoneLatLng = new ParkingZoneLatLng();
            parkingZoneLatLng.setName(parkingZone.getName());
            parkingZoneLatLng.setAddress(parkingZone.getAddress());
            parkingZoneLatLng.setPhoneNumber(parkingZone.getPhoneNumber());
            parkingZoneLatLng.setOpenHour(parkingZone.getOpenHour());
            parkingZoneLatLng.setPrice(parkingZone.getPrice());
            parkingZoneLatLng.setLatitude(parkingZone.getLatitude());
            parkingZoneLatLng.setLongitude(parkingZone.getLongitude());
            parkingZoneLatLng.setImageUrl(parkingZone.getImageUrl());
            parkingZoneLatLngList.add(parkingZoneLatLng);
        }
        return new ResponseEntity<>(parkingZoneLatLngList, HttpStatus.OK);
    }
}
