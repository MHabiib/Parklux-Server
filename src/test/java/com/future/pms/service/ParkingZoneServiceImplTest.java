package com.future.pms.service;

import com.future.pms.config.MongoTokenStore;
import com.future.pms.model.Booking;
import com.future.pms.model.User;
import com.future.pms.model.parking.ParkingLevel;
import com.future.pms.model.parking.ParkingSection;
import com.future.pms.model.parking.ParkingSlot;
import com.future.pms.model.parking.ParkingZone;
import com.future.pms.model.request.LevelDetailsRequest;
import com.future.pms.repository.*;
import com.future.pms.service.impl.ParkingZoneServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.future.pms.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.Silent.class) public class ParkingZoneServiceImplTest {
    private static final String NAME = "name";
    private static final String ID = "123456789ASDGHJQWE";
    private static final Pageable PAGEABLE =
        new PageRequest(0, 10, new Sort(Sort.Direction.DESC, "dateIn"));
    private static final ParkingLevel PARKING_LEVEL =
        ParkingLevel.builder().idLevel("idLevel").slotsLayout(new ArrayList<>(SLOTS)).build();
    private static final LevelDetailsRequest LEVEL_DETAILS_REQUEST =
        LevelDetailsRequest.builder().levelName("levelName - Unavailable").idLevel("idLevel")
            .build();
    private static final ParkingSlot PARKING_SLOT =
        ParkingSlot.builder().idSlot("idSlot").idLevel("idLevel").idParkingZone("idParkingZone")
            .name("name").slotNumberInLayout(1).status(SLOT_TAKEN).status(SLOT_SCAN_ME).build();
    private static final ParkingSlot PARKING_SLOT2 =
        ParkingSlot.builder().idSlot("idSlot").idLevel("idLevel").idParkingZone("idParkingZone")
            .name("name").slotNumberInLayout(1).status(SLOT_TAKEN).build();
    private static final ParkingSlot PARKING_SLOT3 =
        ParkingSlot.builder().idSlot("idSlot").idLevel("idLevel").idParkingZone("idParkingZone")
            .name("name").slotNumberInLayout(1).status(SLOT_EMPTY).build();
    private static final ParkingSection PARKING_SECTION = ParkingSection.builder().build();
    private static final ParkingZone PARKING_ZONE =
        ParkingZone.builder().idParkingZone("idParkingZone").address("address")
            .emailAdmin("emailAdmin").imageUrl("imageUrl").name("name").phoneNumber("phoneNumber")
            .imageUrl("").build();
    private static final User USER =
        User.builder().idUser("idUser").email(null).password("passwordUser").role("roleUser")
            .build();
    private static final String PARKING_ZONE_JSON =
        "{\n" + "  \"address\": \"address\",\n" + "  \"emailAdmin\": \"sana2@mail.com\",\n"
            + "  \"name\": \"Plaza Indonesia\",\n" + "  \"openHour\": \"string\",\n"
            + "  \"phoneNumber\": \"string\",\n" + "  \"price\": 1000,\n"
            + "  \"password\": \"password\",\n" + "  \"imageUrl\": \"\"\n" + "}";
    private static final Booking BOOKING =
        Booking.builder().idBooking("idBooking").address("address").customerName("customerName")
            .customerPhone("customerPhone").dateIn(8L).dateOut(9L).idParkingZone("idParkingZone")
            .idSlot("idSlot").idUser("isUser").imageUrl("imageUrl").levelName("levelName")
            .parkingZoneName("parkingZoneName").price(100D).slotName("slotName").totalPrice("1000")
            .totalTime("12312").build();
    private static final List<ParkingZone> LIST_OF_PARKING_ZONE =
        Collections.singletonList(PARKING_ZONE);
    private static final List<ParkingLevel> LIST_OF_PARKING_LEVEL = new ArrayList<>();
    private static final List<ParkingSlot> LIST_OF_PARKING_SLOT =
        Collections.singletonList(PARKING_SLOT);
    private static final List<ParkingSlot> LIST_OF_PARKING_SLOT2 =
        Collections.singletonList(PARKING_SLOT2);
    private static final List<ParkingSlot> LIST_OF_PARKING_SLOT3 =
        Collections.singletonList(PARKING_SLOT3);
    private static final List<ParkingSlot> LIST_OF_PARKING_SLOT4 = new ArrayList<>();
    private static final Page<ParkingZone> PAGE_OF_PARKING_ZONE =
        new PageImpl<>(LIST_OF_PARKING_ZONE);

    @InjectMocks ParkingZoneServiceImpl parkingZoneServiceImpl;
    @Mock ParkingZoneRepository parkingZoneRepository;
    @Mock ParkingLevelRepository parkingLevelRepository;
    @Mock ParkingSectionRepository parkingSectionRepository;
    @Mock ParkingSlotRepository parkingSlotRepository;
    @Mock UserRepository userRepository;
    @Mock BookingRepository bookingRepository;
    @Mock BookingService bookingService;
    @Mock PasswordEncoder passwordEncoder;
    @Mock MongoTokenStore mongoTokenStore;
    @Mock MultipartFile multipartFile;
    @Mock Principal principal;

    @Test public void loadAll() {
        Mockito.when(
            parkingZoneRepository.findParkingZoneByNameContainingAllIgnoreCase(PAGEABLE, NAME))
            .thenReturn(PAGE_OF_PARKING_ZONE);

        ResponseEntity responseEntity = parkingZoneServiceImpl.loadAll(0, NAME);

        assertThat(responseEntity).isNotNull();
    }

    @Test public void getParkingZoneDetailSuccess() {
        Mockito.when(parkingZoneRepository.findParkingZoneByEmailAdmin(principal.getName()))
            .thenReturn(null);

        ResponseEntity responseEntity = parkingZoneServiceImpl.getParkingZoneDetail(principal);

        assertThat(responseEntity).isNotNull();
    }

    @Test public void getParkingZoneDetailUserNotFound() {
        Mockito.when(parkingZoneRepository.findParkingZoneByEmailAdmin(principal.getName()))
            .thenReturn(PARKING_ZONE);

        ResponseEntity responseEntity = parkingZoneServiceImpl.getParkingZoneDetail(principal);

        assertThat(responseEntity).isNotNull();
    }

    @Test public void addParkingLevel() {
        Mockito.when(parkingZoneRepository.findParkingZoneByEmailAdmin(principal.getName()))
            .thenReturn(PARKING_ZONE);
        Mockito.when(parkingLevelRepository.findByIdLevel(null)).thenReturn(PARKING_LEVEL);

        ResponseEntity responseEntity = parkingZoneServiceImpl.addParkingLevel(NAME, principal);

        assertThat(responseEntity).isNotNull();
    }

    @Test public void addParkingLevelParkingZoneNotFound() {
        Mockito.when(parkingZoneRepository.findParkingZoneByEmailAdmin(principal.getName()))
            .thenReturn(null);

        ResponseEntity responseEntity = parkingZoneServiceImpl.addParkingLevel(NAME, principal);

        assertThat(responseEntity).isNotNull();
    }

    @Test public void updateParkingSectionNotActiveFailed() {
        PARKING_SECTION.setStatus(NOT_ACTIVE);
        PARKING_SECTION.setSectionName("A");
        PARKING_SECTION.setIdLevel(ID);
        Mockito.when(parkingSectionRepository
            .findParkingSectionByIdSection(ID.substring(1, ID.length() - 1)))
            .thenReturn(PARKING_SECTION);
        Mockito.when(parkingLevelRepository.findByIdLevel(ID)).thenReturn(PARKING_LEVEL);

        ResponseEntity responseEntity = parkingZoneServiceImpl.updateParkingSection(ID);

        assertThat(responseEntity).isNotNull();
    }

    @Test public void updateParkingSectionNotActive() {
        PARKING_SECTION.setStatus(NOT_ACTIVE);
        PARKING_SECTION.setSectionName("1");
        PARKING_SECTION.setIdLevel(ID);
        SLOTS.set(0, SLOTS.get(0).replace('_', 'E'));
        PARKING_LEVEL.setSlotsLayout(SLOTS);
        Mockito.when(parkingSectionRepository
            .findParkingSectionByIdSection(ID.substring(1, ID.length() - 1)))
            .thenReturn(PARKING_SECTION);
        Mockito.when(parkingLevelRepository.findByIdLevel(ID)).thenReturn(PARKING_LEVEL);
        Mockito.when(parkingSlotRepository
            .findByIdParkingZoneAndSlotNumberInLayout(PARKING_LEVEL.getIdParkingZone(), 0))
            .thenReturn(PARKING_SLOT);

        ResponseEntity responseEntity = parkingZoneServiceImpl.updateParkingSection(ID);

        assertThat(responseEntity).isNotNull();
    }

    @Test public void updateParkingSectionActive() {
        PARKING_SECTION.setStatus(ACTIVE);
        PARKING_SECTION.setIdLevel(ID);
        PARKING_SECTION.setSectionName("1");
        Mockito.when(parkingSectionRepository
            .findParkingSectionByIdSection(ID.substring(1, ID.length() - 1)))
            .thenReturn(PARKING_SECTION);
        Mockito.when(parkingLevelRepository.findByIdLevel(ID)).thenReturn(PARKING_LEVEL);

        ResponseEntity responseEntity = parkingZoneServiceImpl.updateParkingSection(ID);

        assertThat(responseEntity).isNotNull();
    }

    @Test public void updateParkingSectionDefault() {
        PARKING_SECTION.setStatus("");
        Mockito.when(parkingSectionRepository
            .findParkingSectionByIdSection(ID.substring(1, ID.length() - 1)))
            .thenReturn(PARKING_SECTION);

        ResponseEntity responseEntity = parkingZoneServiceImpl.updateParkingSection(ID);

        assertThat(responseEntity).isNotNull();
    }

    @Test public void updateParkingSectionSlotNotFound() {
        Mockito.when(parkingZoneRepository.findParkingZoneByEmailAdmin(principal.getName()))
            .thenReturn(null);

        ResponseEntity responseEntity = parkingZoneServiceImpl.updateParkingSection(ID);

        assertThat(responseEntity).isNotNull();
    }

    @Test public void updateLevel() {
        PARKING_LEVEL.setSlotsLayout(SLOTS);
        Mockito.when(parkingLevelRepository.findByIdLevel(ID)).thenReturn(PARKING_LEVEL);
        Mockito.when(parkingSlotRepository
            .findByIdParkingZoneAndSlotNumberInLayout(PARKING_LEVEL.getIdParkingZone(), 0))
            .thenReturn(PARKING_SLOT);
        Mockito.when(parkingSlotRepository
            .findByIdParkingZoneAndSlotNumberInLayout(PARKING_LEVEL.getIdParkingZone(), 8))
            .thenReturn(PARKING_SLOT);
        Mockito.when(bookingRepository.findBookingByIdSlotAndDateOutNull(PARKING_SLOT.getIdSlot()))
            .thenReturn(BOOKING);
        SLOTS.set(0, SLOTS.get(0).replace('O', 'T'));

        ResponseEntity responseEntity = parkingZoneServiceImpl.updateLevel(ID,
            "T__TD_TTTE__EE____TT______________________________________________________________________________________________________________________________________________________________________________________________________________________________________________");

        assertThat(responseEntity).isNotNull();
    }

    @Test public void updateLevelNotFound() {
        ResponseEntity responseEntity = parkingZoneServiceImpl.updateLevel(ID, "_AEE");

        assertThat(responseEntity).isNotNull();
    }

    @Test public void updateParkingZone() throws IOException {
        UsernamePasswordAuthenticationToken principal =
            new UsernamePasswordAuthenticationToken(PARKING_ZONE.getEmailAdmin(), "password");
        Mockito.when(parkingZoneRepository.findParkingZoneByEmailAdmin(principal.getName()))
            .thenReturn(PARKING_ZONE);
        Mockito.when(parkingZoneRepository.findParkingZoneByIdParkingZone("idParkingZone"))
            .thenReturn(PARKING_ZONE);
        Mockito.when(userRepository.findByEmail(PARKING_ZONE.getEmailAdmin())).thenReturn(USER);

        ResponseEntity responseEntity =
            parkingZoneServiceImpl.updateParkingZone(principal, PARKING_ZONE_JSON);

        assertThat(responseEntity).isNotNull();
    }

    @Test public void updateAdmin() throws IOException {
        Mockito.when(parkingZoneRepository.findParkingZoneByIdParkingZone(ID))
            .thenReturn(PARKING_ZONE);
        Mockito.when(userRepository.findByEmail(PARKING_ZONE.getEmailAdmin())).thenReturn(USER);
        Mockito.when(userRepository.countByEmail("sana2@mail.com")).thenReturn(1);
        Mockito.when(parkingZoneRepository.findParkingZoneByIdParkingZone("idParkingZone"))
            .thenReturn(PARKING_ZONE);

        ResponseEntity responseEntity = parkingZoneServiceImpl.updateAdmin(ID, PARKING_ZONE_JSON);

        assertThat(responseEntity).isNotNull();
    }

    @Test public void updateParkingLevelStatusAvailable() {
        Mockito.when(parkingLevelRepository.findByIdLevel(LEVEL_DETAILS_REQUEST.getIdLevel()))
            .thenReturn(PARKING_LEVEL);
        Mockito.when(parkingSlotRepository.findAllByIdLevel(PARKING_LEVEL.getIdLevel()))
            .thenReturn(LIST_OF_PARKING_SLOT);
        LEVEL_DETAILS_REQUEST.setStatus(LEVEL_AVAILABLE);
        LEVEL_DETAILS_REQUEST.setLevelName("");
        PARKING_LEVEL.setStatus("");
        PARKING_LEVEL.setLevelName("levelName - Unavailable");

        ResponseEntity responseEntity =
            parkingZoneServiceImpl.updateParkingLevel(LEVEL_DETAILS_REQUEST, principal);

        assertThat(responseEntity).isNotNull();
    }

    @Test public void updateParkingLevelStatusUnavailable() {
        Mockito.when(parkingLevelRepository.findByIdLevel(LEVEL_DETAILS_REQUEST.getIdLevel()))
            .thenReturn(PARKING_LEVEL);
        Mockito.when(parkingSlotRepository.findAllByIdLevel(PARKING_LEVEL.getIdLevel()))
            .thenReturn(LIST_OF_PARKING_SLOT);

        LEVEL_DETAILS_REQUEST.setLevelName("");
        LEVEL_DETAILS_REQUEST.setStatus(LEVEL_UNAVAILABLE);
        PARKING_LEVEL.setStatus("");
        ResponseEntity responseEntity =
            parkingZoneServiceImpl.updateParkingLevel(LEVEL_DETAILS_REQUEST, principal);

        assertThat(responseEntity).isNotNull();
    }

    @Test public void updateParkingLevelStatusUnavailableHaveOngoing() {
        Mockito.when(parkingLevelRepository.findByIdLevel(LEVEL_DETAILS_REQUEST.getIdLevel()))
            .thenReturn(PARKING_LEVEL);
        Mockito.when(parkingSlotRepository.findAllByIdLevel(PARKING_LEVEL.getIdLevel()))
            .thenReturn(LIST_OF_PARKING_SLOT2);

        LEVEL_DETAILS_REQUEST.setLevelName("");
        LEVEL_DETAILS_REQUEST.setStatus(LEVEL_UNAVAILABLE);
        PARKING_LEVEL.setStatus("");
        PARKING_LEVEL.setLevelName("Level Name - Unavailable");
        ResponseEntity responseEntity =
            parkingZoneServiceImpl.updateParkingLevel(LEVEL_DETAILS_REQUEST, principal);

        assertThat(responseEntity).isNotNull();
    }

    @Test
    public void updateParkingLevelStatusUnavailableHaveOngoingLevelNameNotContainsUnavailable() {
        Mockito.when(parkingLevelRepository.findByIdLevel(LEVEL_DETAILS_REQUEST.getIdLevel()))
            .thenReturn(PARKING_LEVEL);
        Mockito.when(parkingSlotRepository.findAllByIdLevel(PARKING_LEVEL.getIdLevel()))
            .thenReturn(LIST_OF_PARKING_SLOT2);

        LEVEL_DETAILS_REQUEST.setLevelName("");
        LEVEL_DETAILS_REQUEST.setStatus(LEVEL_UNAVAILABLE);
        PARKING_LEVEL.setStatus("");
        PARKING_LEVEL.setLevelName("Level Name");
        ResponseEntity responseEntity =
            parkingZoneServiceImpl.updateParkingLevel(LEVEL_DETAILS_REQUEST, principal);

        assertThat(responseEntity).isNotNull();
    }

    @Test public void updateParkingLevelStatusTakeOut() {
        Mockito.when(parkingLevelRepository.findByIdLevel(LEVEL_DETAILS_REQUEST.getIdLevel()))
            .thenReturn(PARKING_LEVEL);
        Mockito.when(parkingSlotRepository.findAllByIdLevel(PARKING_LEVEL.getIdLevel()))
            .thenReturn(LIST_OF_PARKING_SLOT3);
        LEVEL_DETAILS_REQUEST.setLevelName("");
        LEVEL_DETAILS_REQUEST.setStatus(LEVEL_TAKE_OUT);

        ResponseEntity responseEntity =
            parkingZoneServiceImpl.updateParkingLevel(LEVEL_DETAILS_REQUEST, principal);

        assertThat(responseEntity).isNotNull();
    }

    @Test public void updateParkingLevelStatusTakeOutHaveOngoing() {
        Mockito.when(parkingLevelRepository.findByIdLevel(LEVEL_DETAILS_REQUEST.getIdLevel()))
            .thenReturn(PARKING_LEVEL);
        Mockito.when(parkingSlotRepository.findAllByIdLevel(PARKING_LEVEL.getIdLevel()))
            .thenReturn(LIST_OF_PARKING_SLOT2);
        LEVEL_DETAILS_REQUEST.setLevelName("");
        LEVEL_DETAILS_REQUEST.setStatus(LEVEL_TAKE_OUT);

        ResponseEntity responseEntity =
            parkingZoneServiceImpl.updateParkingLevel(LEVEL_DETAILS_REQUEST, principal);

        assertThat(responseEntity).isNotNull();
    }

    @Test public void updateParkingLevelStatusFailed() {
        Mockito.when(parkingLevelRepository.findByIdLevel(LEVEL_DETAILS_REQUEST.getIdLevel()))
            .thenReturn(PARKING_LEVEL);
        Mockito.when(parkingSlotRepository.findAllByIdLevel(PARKING_LEVEL.getIdLevel()))
            .thenReturn(LIST_OF_PARKING_SLOT2);
        LEVEL_DETAILS_REQUEST.setStatus("");

        ResponseEntity responseEntity =
            parkingZoneServiceImpl.updateParkingLevel(LEVEL_DETAILS_REQUEST, principal);

        assertThat(responseEntity).isNotNull();
    }

    @Test public void getLevels() {

        LIST_OF_PARKING_LEVEL.add(PARKING_LEVEL);
        LIST_OF_PARKING_LEVEL.add(PARKING_LEVEL);
        LIST_OF_PARKING_LEVEL.add(PARKING_LEVEL);
        UsernamePasswordAuthenticationToken principal =
            new UsernamePasswordAuthenticationToken(PARKING_ZONE.getEmailAdmin(), "password");

        Mockito.when(parkingZoneRepository.findParkingZoneByEmailAdmin(principal.getName()))
            .thenReturn(PARKING_ZONE);
        Mockito.when(parkingLevelRepository
            .findByIdParkingZoneOrderByLevelName(PARKING_ZONE.getIdParkingZone()))
            .thenReturn(LIST_OF_PARKING_LEVEL);

        ResponseEntity responseEntity = parkingZoneServiceImpl.getLevels(principal);

        assertThat(responseEntity).isNotNull();
    }

    @Test public void getParkingBookingLayout() {
        Mockito.when(bookingRepository.findBookingByIdBooking(ID)).thenReturn(BOOKING);
        Mockito.when(parkingSlotRepository.findByIdSlot(BOOKING.getIdSlot()))
            .thenReturn(PARKING_SLOT3);
        Mockito.when(parkingLevelRepository.findByIdLevel(PARKING_SLOT.getIdLevel()))
            .thenReturn(PARKING_LEVEL);
        ResponseEntity responseEntity = parkingZoneServiceImpl.getParkingBookingLayout(ID);

        assertThat(responseEntity).isNotNull();
    }

    @Test public void getParkingBookingLayoutFailed() {
        ResponseEntity responseEntity = parkingZoneServiceImpl.getParkingBookingLayout(ID);

        assertThat(responseEntity).isNotNull();
    }

    @Test public void getParkingLevelLayout() {
        Mockito.when(parkingLevelRepository.findByIdLevel(ID)).thenReturn(PARKING_LEVEL);

        ResponseEntity responseEntity = parkingZoneServiceImpl.getParkingLevelLayout(ID);

        assertThat(responseEntity).isNotNull();
    }

    @Test public void getParkingLevelLayoutFailed() {
        ResponseEntity responseEntity = parkingZoneServiceImpl.getParkingLevelLayout(ID);

        assertThat(responseEntity).isNotNull();
    }

    @Test public void getSectionDetails() {
        Mockito.when(
            parkingSectionRepository.findParkingSectionBySectionNameAndIdLevel(SECTION_ONE, ID))
            .thenReturn(PARKING_SECTION);
        Mockito.when(
            parkingSectionRepository.findParkingSectionBySectionNameAndIdLevel(SECTION_TWO, ID))
            .thenReturn(PARKING_SECTION);
        Mockito.when(
            parkingSectionRepository.findParkingSectionBySectionNameAndIdLevel(SECTION_THREE, ID))
            .thenReturn(PARKING_SECTION);
        Mockito.when(
            parkingSectionRepository.findParkingSectionBySectionNameAndIdLevel(SECTION_FOUR, ID))
            .thenReturn(PARKING_SECTION);
        Mockito.when(parkingLevelRepository.findByIdLevel(ID)).thenReturn(PARKING_LEVEL);

        ResponseEntity responseEntity = parkingZoneServiceImpl.getSectionDetails(ID);

        assertThat(responseEntity).isNotNull();
    }

    @Test public void getSectionDetailsSlotTaken() {
        Mockito.when(
            parkingSectionRepository.findParkingSectionBySectionNameAndIdLevel(SECTION_ONE, ID))
            .thenReturn(PARKING_SECTION);
        Mockito.when(
            parkingSectionRepository.findParkingSectionBySectionNameAndIdLevel(SECTION_TWO, ID))
            .thenReturn(PARKING_SECTION);
        Mockito.when(
            parkingSectionRepository.findParkingSectionBySectionNameAndIdLevel(SECTION_THREE, ID))
            .thenReturn(PARKING_SECTION);
        Mockito.when(
            parkingSectionRepository.findParkingSectionBySectionNameAndIdLevel(SECTION_FOUR, ID))
            .thenReturn(PARKING_SECTION);
        SLOTS.set(0, SLOTS.get(0).replace('_', 'T'));
        PARKING_LEVEL.setSlotsLayout(SLOTS);
        Mockito.when(parkingLevelRepository.findByIdLevel(ID)).thenReturn(PARKING_LEVEL);

        ResponseEntity responseEntity = parkingZoneServiceImpl.getSectionDetails(ID);

        assertThat(responseEntity).isNotNull();
    }

    @Test public void getSectionDetailsSlotEmpty() {
        Mockito.when(
            parkingSectionRepository.findParkingSectionBySectionNameAndIdLevel(SECTION_ONE, ID))
            .thenReturn(PARKING_SECTION);
        Mockito.when(
            parkingSectionRepository.findParkingSectionBySectionNameAndIdLevel(SECTION_TWO, ID))
            .thenReturn(PARKING_SECTION);
        Mockito.when(
            parkingSectionRepository.findParkingSectionBySectionNameAndIdLevel(SECTION_THREE, ID))
            .thenReturn(PARKING_SECTION);
        Mockito.when(
            parkingSectionRepository.findParkingSectionBySectionNameAndIdLevel(SECTION_FOUR, ID))
            .thenReturn(PARKING_SECTION);
        SLOTS.set(0, SLOTS.get(0).replace('_', 'E'));
        PARKING_LEVEL.setSlotsLayout(SLOTS);
        Mockito.when(parkingLevelRepository.findByIdLevel(ID)).thenReturn(PARKING_LEVEL);

        ResponseEntity responseEntity = parkingZoneServiceImpl.getSectionDetails(ID);

        assertThat(responseEntity).isNotNull();
    }

    @Test public void getSectionDetailsSlotDisable() {
        Mockito.when(
            parkingSectionRepository.findParkingSectionBySectionNameAndIdLevel(SECTION_ONE, ID))
            .thenReturn(PARKING_SECTION);
        Mockito.when(
            parkingSectionRepository.findParkingSectionBySectionNameAndIdLevel(SECTION_TWO, ID))
            .thenReturn(PARKING_SECTION);
        Mockito.when(
            parkingSectionRepository.findParkingSectionBySectionNameAndIdLevel(SECTION_THREE, ID))
            .thenReturn(PARKING_SECTION);
        Mockito.when(
            parkingSectionRepository.findParkingSectionBySectionNameAndIdLevel(SECTION_FOUR, ID))
            .thenReturn(PARKING_SECTION);
        SLOTS.set(0, SLOTS.get(0).replace('_', 'D'));
        PARKING_LEVEL.setSlotsLayout(SLOTS);
        Mockito.when(parkingLevelRepository.findByIdLevel(ID)).thenReturn(PARKING_LEVEL);

        ResponseEntity responseEntity = parkingZoneServiceImpl.getSectionDetails(ID);

        assertThat(responseEntity).isNotNull();
    }

    @Test public void editModeParkingLevelEditMode() {
        LIST_OF_PARKING_SLOT4.add(PARKING_SLOT3);
        LIST_OF_PARKING_SLOT4.add(PARKING_SLOT3);
        LIST_OF_PARKING_SLOT4.add(PARKING_SLOT3);
        Mockito.when(parkingLevelRepository.findByIdLevel(ID)).thenReturn(PARKING_LEVEL);
        Mockito.when(parkingSlotRepository.findAllByIdLevel(ID)).thenReturn(LIST_OF_PARKING_SLOT4);
        ResponseEntity responseEntity = parkingZoneServiceImpl.editModeParkingLevel(ID, EDIT_MODE);

        assertThat(responseEntity).isNotNull();
    }

    @Test public void editModeParkingLevelAvailable() {
        LIST_OF_PARKING_SLOT4.add(PARKING_SLOT3);
        LIST_OF_PARKING_SLOT4.add(PARKING_SLOT3);
        LIST_OF_PARKING_SLOT4.add(PARKING_SLOT3);

        Mockito.when(parkingLevelRepository.findByIdLevel(ID)).thenReturn(PARKING_LEVEL);
        Mockito.when(parkingSlotRepository.findAllByIdLevel(ID)).thenReturn(LIST_OF_PARKING_SLOT4);

        ResponseEntity responseEntity =
            parkingZoneServiceImpl.editModeParkingLevel(ID, EXIT_EDIT_MODE);

        assertThat(responseEntity).isNotNull();
    }

    @Test public void editModeParkingNullParkingZone() {
        Mockito.when(parkingLevelRepository.findByIdLevel(ID)).thenReturn(null);

        ResponseEntity responseEntity =
            parkingZoneServiceImpl.editModeParkingLevel(ID, EXIT_EDIT_MODE);

        assertThat(responseEntity).isNotNull();
    }

    @Test public void updateParkingZonePictureFailed() {

        ResponseEntity responseEntity =
            parkingZoneServiceImpl.updateParkingZonePicture(principal, multipartFile);

        assertThat(responseEntity).isNotNull();
    }

    @Test public void getAdminSASuccess() {
        Mockito.when(parkingZoneRepository.findParkingZoneByIdParkingZone(ID))
            .thenReturn(PARKING_ZONE);

        ResponseEntity responseEntity = parkingZoneServiceImpl.getAdminSA(ID);

        assertThat(responseEntity).isNotNull();
    }

    @Test public void getAdminSAFailed() {
        Mockito.when(parkingZoneRepository.findParkingZoneByIdParkingZone(ID)).thenReturn(null);

        ResponseEntity responseEntity = parkingZoneServiceImpl.getAdminSA(ID);

        assertThat(responseEntity).isNotNull();
    }

}
