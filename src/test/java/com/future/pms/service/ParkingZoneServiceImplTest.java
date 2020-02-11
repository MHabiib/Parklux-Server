package com.future.pms.service;

import com.future.pms.AmazonClient;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
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
        new PageRequest(0, 10, new Sort(Sort.Direction.ASC, "dateIn"));
    private static final Pageable PAGEABLE_NAME =
        new PageRequest(0, 10, new Sort(Sort.Direction.ASC, "name"));
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
            .imageUrl("").latitude(0.1).build();
    private static final User USER =
        User.builder().idUser("idUser").email(null).password("passwordUser").role("roleUser")
            .build();
    private static final String PARKING_ZONE_JSON =
        "{\n" + "  \"address\": \"address\",\n" + "  \"emailAdmin\": \"sana2@mail.com\",\n"
            + "  \"name\": \"Plaza Indonesia\",\n" + "  \"latitude\": 0.1,\n"
            + "  \"openHour\": \"string\",\n" + "  \"phoneNumber\": \"string\",\n"
            + "  \"price\": 1000,\n" + "  \"password\": \"password\",\n" + "  \"imageUrl\": \"\"\n"
            + "}";
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
    private static ParkingLevel PARKING_LEVEL =
        ParkingLevel.builder().idLevel("idLevel").slotsLayout(new ArrayList<>(SLOTS)).build();
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
    @Mock AmazonClient amazonClient;
    @Mock MultipartFile multipartFile;
    @Mock Principal principal;

    @Test public void loadAll() {
        Mockito.when(
            parkingZoneRepository.findParkingZoneByNameContainingAllIgnoreCase(PAGEABLE_NAME, NAME))
            .thenReturn(PAGE_OF_PARKING_ZONE);

        ResponseEntity responseEntity = parkingZoneServiceImpl.loadAll(0, NAME);

        assertThat(responseEntity).isNotNull();

        Mockito.verify(parkingZoneRepository)
            .findParkingZoneByNameContainingAllIgnoreCase(PAGEABLE_NAME, NAME);
        Mockito.verifyNoMoreInteractions(parkingZoneRepository);
    }

    @Test public void getParkingZoneDetailSuccess() {
        Mockito.when(parkingZoneRepository.findParkingZoneByEmailAdmin(principal.getName()))
            .thenReturn(null);

        ResponseEntity responseEntity = parkingZoneServiceImpl.getParkingZoneDetail(principal);

        assertThat(responseEntity).isNotNull();

        Mockito.verify(parkingZoneRepository).findParkingZoneByEmailAdmin(principal.getName());
        Mockito.verifyNoMoreInteractions(parkingZoneRepository);
    }

    @Test public void getParkingZoneDetailUserNotFound() {
        Mockito.when(parkingZoneRepository.findParkingZoneByEmailAdmin(principal.getName()))
            .thenReturn(PARKING_ZONE);

        ResponseEntity responseEntity = parkingZoneServiceImpl.getParkingZoneDetail(principal);

        assertThat(responseEntity).isNotNull();

        Mockito.verify(parkingZoneRepository, Mockito.times(2))
            .findParkingZoneByEmailAdmin(principal.getName());
        Mockito.verifyNoMoreInteractions(parkingZoneRepository);
    }

    @Test public void addParkingLevel() {
        Mockito.when(parkingZoneRepository.findParkingZoneByEmailAdmin(principal.getName()))
            .thenReturn(PARKING_ZONE);
        Mockito.when(parkingLevelRepository.findByIdLevel(null)).thenReturn(PARKING_LEVEL);

        ResponseEntity responseEntity = parkingZoneServiceImpl.addParkingLevel(NAME, principal);

        assertThat(responseEntity).isNotNull();

        PARKING_LEVEL.setIdLevel(null);
        PARKING_LEVEL.setIdParkingZone("idParkingZone");
        PARKING_LEVEL.setLevelName("am");
        PARKING_LEVEL.setStatus("A");
        Mockito.verify(parkingZoneRepository).findParkingZoneByEmailAdmin(principal.getName());
        Mockito.verify(parkingLevelRepository).save(PARKING_LEVEL);
        Mockito.verify(parkingLevelRepository).findByIdLevel(null);
        Mockito.verifyNoMoreInteractions(parkingZoneRepository);
        Mockito.verifyNoMoreInteractions(parkingLevelRepository);
    }

    @Test public void addParkingLevelParkingZoneNotFound() {
        Mockito.when(parkingZoneRepository.findParkingZoneByEmailAdmin(principal.getName()))
            .thenReturn(null);

        ResponseEntity responseEntity = parkingZoneServiceImpl.addParkingLevel(NAME, principal);

        assertThat(responseEntity).isNotNull();

        Mockito.verify(parkingZoneRepository).findParkingZoneByEmailAdmin(principal.getName());
        Mockito.verifyNoMoreInteractions(parkingZoneRepository);
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

        Mockito.verify(parkingLevelRepository).findByIdLevel(ID);
        Mockito.verify(parkingSectionRepository)
            .findParkingSectionByIdSection(ID.substring(1, ID.length() - 1));
        Mockito.verifyNoMoreInteractions(parkingLevelRepository);
        Mockito.verifyNoMoreInteractions(parkingSectionRepository);
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

        Mockito.verify(parkingSectionRepository)
            .findParkingSectionByIdSection(ID.substring(1, ID.length() - 1));
        Mockito.verify(parkingSectionRepository).save(PARKING_SECTION);
        Mockito.verify(parkingLevelRepository).findByIdLevel(ID);
        Mockito.verify(parkingLevelRepository).save(PARKING_LEVEL);
        Mockito.verify(parkingSlotRepository)
            .findByIdParkingZoneAndSlotNumberInLayout(PARKING_LEVEL.getIdParkingZone(), 0);
        Mockito.verify(parkingSlotRepository).save(PARKING_SLOT);
        Mockito.verifyNoMoreInteractions(parkingSectionRepository);
        Mockito.verifyNoMoreInteractions(parkingLevelRepository);
        Mockito.verifyNoMoreInteractions(parkingSlotRepository);
    }

    @Test public void updateParkingSectionActive() {
        PARKING_SECTION.setStatus(ACTIVE);
        PARKING_SECTION.setSectionName("1");
        PARKING_SECTION.setIdLevel(ID);
        Mockito.when(parkingSectionRepository
            .findParkingSectionByIdSection(ID.substring(1, ID.length() - 1)))
            .thenReturn(PARKING_SECTION);
        ArrayList<String> SLOTS2 = SLOTS;
        for (int i = 0; i < SLOTS2.size(); i++) {
            SLOTS2.set(i, SLOTS2.get(0).replace('S', '_'));
        }
        PARKING_LEVEL.setSlotsLayout(SLOTS2);
        Mockito.when(parkingLevelRepository.findByIdLevel(ID)).thenReturn(PARKING_LEVEL);
        Mockito.when(parkingSlotRepository
            .findByIdParkingZoneAndSlotNumberInLayout(PARKING_LEVEL.getIdParkingZone(), 0))
            .thenReturn(PARKING_SLOT);

        ResponseEntity responseEntity = parkingZoneServiceImpl.updateParkingSection(ID);

        assertThat(responseEntity).isNotNull();

        Mockito.verify(parkingLevelRepository).findByIdLevel(ID);
        Mockito.verify(parkingLevelRepository).save(PARKING_LEVEL);
        Mockito.verifyNoMoreInteractions(parkingLevelRepository);
        Mockito.verifyNoMoreInteractions(parkingSlotRepository);
    }

    @Test public void updateParkingSectionActiveFailed() {
        PARKING_SECTION.setStatus(ACTIVE);
        PARKING_SECTION.setSectionName("A");
        PARKING_SECTION.setIdLevel(ID);
        Mockito.when(parkingSectionRepository
            .findParkingSectionByIdSection(ID.substring(1, ID.length() - 1)))
            .thenReturn(PARKING_SECTION);
        Mockito.when(parkingLevelRepository.findByIdLevel(ID)).thenReturn(PARKING_LEVEL);

        ResponseEntity responseEntity = parkingZoneServiceImpl.updateParkingSection(ID);

        assertThat(responseEntity).isNotNull();

        Mockito.verify(parkingSectionRepository)
            .findParkingSectionByIdSection(ID.substring(1, ID.length() - 1));
        Mockito.verify(parkingLevelRepository).findByIdLevel(ID);
        Mockito.verifyNoMoreInteractions(parkingSectionRepository);
        Mockito.verifyNoMoreInteractions(parkingLevelRepository);
    }

    @Test public void updateParkingSectionDefault() {
        PARKING_SECTION.setStatus("");
        Mockito.when(parkingSectionRepository
            .findParkingSectionByIdSection(ID.substring(1, ID.length() - 1)))
            .thenReturn(PARKING_SECTION);

        ResponseEntity responseEntity = parkingZoneServiceImpl.updateParkingSection(ID);

        assertThat(responseEntity).isNotNull();

        Mockito.verify(parkingSectionRepository)
            .findParkingSectionByIdSection(ID.substring(1, ID.length() - 1));
        Mockito.verifyNoMoreInteractions(parkingSectionRepository);
    }

    @Test public void updateParkingSectionSlotNotFound() {
        Mockito.when(parkingZoneRepository.findParkingZoneByEmailAdmin(principal.getName()))
            .thenReturn(null);

        ResponseEntity responseEntity = parkingZoneServiceImpl.updateParkingSection(ID);

        assertThat(responseEntity).isNotNull();

        Mockito.verifyNoMoreInteractions(parkingZoneRepository);
    }

    @Test public void updateLevel() {
        ArrayList<String> SLOTS2 = SLOTS;
        SLOTS2.set(0, SLOTS2.get(0).replace('O', 'T'));

        PARKING_LEVEL.setSlotsLayout(SLOTS2);

        Mockito.when(parkingLevelRepository.findByIdLevel(ID)).thenReturn(PARKING_LEVEL);
        Mockito.when(parkingSlotRepository
            .findByIdParkingZoneAndSlotNumberInLayout(PARKING_LEVEL.getIdParkingZone(), 0))
            .thenReturn(PARKING_SLOT);
        Mockito.when(parkingSlotRepository
            .findByIdParkingZoneAndSlotNumberInLayout(PARKING_LEVEL.getIdParkingZone(), 8))
            .thenReturn(PARKING_SLOT);
        Mockito.when(bookingRepository.findBookingByIdSlotAndDateOutNull(PARKING_SLOT.getIdSlot()))
            .thenReturn(BOOKING);

        StringBuilder slotLayout = new StringBuilder("T__TD_TTTE__EE____TT");
        for (int i = 0; i < TOTAL_SLOT_IN_LEVEL - 18; i++) {
            slotLayout.append("T");
        }

        ResponseEntity responseEntity =
            parkingZoneServiceImpl.updateLevel(ID, slotLayout.toString());

        assertThat(responseEntity).isNotNull();

        Mockito.verify(bookingRepository)
            .findBookingByIdSlotAndDateOutNull(PARKING_SLOT.getIdSlot());
        Mockito.verify(bookingRepository).save(BOOKING);
        Mockito.verifyNoMoreInteractions(bookingRepository);
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
        Mockito.when(userRepository.countByEmail("sana2@mail.com")).thenReturn(0);

        ResponseEntity responseEntity =
            parkingZoneServiceImpl.updateParkingZone(principal, PARKING_ZONE_JSON);

        assertThat(responseEntity).isNotNull();

        Mockito.verify(userRepository).findByEmail("emailAdmin");
        Mockito.verify(userRepository).countByEmail("sana2@mail.com");
        Mockito.verify(userRepository).save(USER);
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Test public void updateParkingZoneDetailNull() throws IOException {
        UsernamePasswordAuthenticationToken principal =
            new UsernamePasswordAuthenticationToken(PARKING_ZONE.getEmailAdmin(), "password");
        Mockito.when(parkingZoneRepository.findParkingZoneByEmailAdmin(principal.getName()))
            .thenReturn(null);
        Mockito.when(parkingZoneRepository.findParkingZoneByIdParkingZone("idParkingZone"))
            .thenReturn(null);
        Mockito.when(userRepository.findByEmail(PARKING_ZONE.getEmailAdmin())).thenReturn(USER);

        ResponseEntity responseEntity =
            parkingZoneServiceImpl.updateParkingZone(principal, PARKING_ZONE_JSON);

        assertThat(responseEntity).isNotNull();

        Mockito.verify(parkingZoneRepository).findParkingZoneByEmailAdmin(principal.getName());
        Mockito.verifyNoMoreInteractions(parkingZoneRepository);
        Mockito.verifyNoMoreInteractions(userRepository);
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

        Mockito.verify(parkingZoneRepository).findParkingZoneByIdParkingZone(ID);
        Mockito.verify(parkingZoneRepository).findParkingZoneByIdParkingZone("idParkingZone");
        Mockito.verify(userRepository).findByEmail(PARKING_ZONE.getEmailAdmin());
        Mockito.verify(userRepository).countByEmail("sana2@mail.com");
        Mockito.verifyNoMoreInteractions(parkingZoneRepository);
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Test public void updateAdminParkingZoneDetailNull() throws IOException {
        Mockito.when(parkingZoneRepository.findParkingZoneByIdParkingZone(ID)).thenReturn(null);
        Mockito.when(userRepository.findByEmail(PARKING_ZONE.getEmailAdmin())).thenReturn(USER);
        Mockito.when(userRepository.countByEmail("sana2@mail.com")).thenReturn(1);
        Mockito.when(parkingZoneRepository.findParkingZoneByIdParkingZone("idParkingZone"))
            .thenReturn(null);

        ResponseEntity responseEntity = parkingZoneServiceImpl.updateAdmin(ID, PARKING_ZONE_JSON);

        assertThat(responseEntity).isNotNull();

        Mockito.verify(parkingZoneRepository).findParkingZoneByIdParkingZone(ID);
        Mockito.verifyNoMoreInteractions(parkingZoneRepository);
        Mockito.verifyNoMoreInteractions(userRepository);
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

        Mockito.verify(parkingLevelRepository).findByIdLevel(LEVEL_DETAILS_REQUEST.getIdLevel());
        Mockito.verify(parkingLevelRepository).save(PARKING_LEVEL);
        Mockito.verify(parkingSlotRepository).findAllByIdLevel(PARKING_LEVEL.getIdLevel());
        Mockito.verify(parkingSlotRepository).save(PARKING_SLOT);
        Mockito.verifyNoMoreInteractions(parkingLevelRepository);
        Mockito.verifyNoMoreInteractions(parkingSlotRepository);
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

        Mockito.verify(parkingLevelRepository).findByIdLevel(LEVEL_DETAILS_REQUEST.getIdLevel());
        Mockito.verify(parkingLevelRepository).save(PARKING_LEVEL);
        Mockito.verify(parkingSlotRepository).findAllByIdLevel(PARKING_LEVEL.getIdLevel());
        Mockito.verify(parkingSlotRepository).save(PARKING_SLOT);
        Mockito.verifyNoMoreInteractions(parkingLevelRepository);
        Mockito.verifyNoMoreInteractions(parkingSlotRepository);
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

        Mockito.verify(parkingLevelRepository).findByIdLevel(LEVEL_DETAILS_REQUEST.getIdLevel());
        Mockito.verify(parkingSlotRepository).findAllByIdLevel(PARKING_LEVEL.getIdLevel());
        Mockito.verifyNoMoreInteractions(parkingLevelRepository);
        Mockito.verifyNoMoreInteractions(parkingSlotRepository);
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

        Mockito.verify(parkingLevelRepository).findByIdLevel(LEVEL_DETAILS_REQUEST.getIdLevel());
        Mockito.verify(parkingSlotRepository).findAllByIdLevel(PARKING_LEVEL.getIdLevel());
        Mockito.verifyNoMoreInteractions(parkingLevelRepository);
        Mockito.verifyNoMoreInteractions(parkingSlotRepository);
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

        Mockito.verify(parkingLevelRepository).findByIdLevel(LEVEL_DETAILS_REQUEST.getIdLevel());
        Mockito.verify(parkingLevelRepository).delete(PARKING_LEVEL);
        Mockito.verify(parkingSlotRepository).findAllByIdLevel(PARKING_LEVEL.getIdLevel());
        Mockito.verify(parkingSlotRepository).deleteAll(LIST_OF_PARKING_SLOT3);
        Mockito.verifyNoMoreInteractions(parkingLevelRepository);
        Mockito.verifyNoMoreInteractions(parkingSlotRepository);
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

        Mockito.verify(parkingLevelRepository).findByIdLevel(LEVEL_DETAILS_REQUEST.getIdLevel());
        Mockito.verify(parkingSlotRepository).findAllByIdLevel(PARKING_LEVEL.getIdLevel());
        Mockito.verifyNoMoreInteractions(parkingLevelRepository);
        Mockito.verifyNoMoreInteractions(parkingSlotRepository);
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

        Mockito.verify(parkingLevelRepository).findByIdLevel(LEVEL_DETAILS_REQUEST.getIdLevel());
        Mockito.verify(parkingSlotRepository).findAllByIdLevel(PARKING_LEVEL.getIdLevel());
        Mockito.verifyNoMoreInteractions(parkingLevelRepository);
        Mockito.verifyNoMoreInteractions(parkingSlotRepository);
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

        Mockito.verify(parkingZoneRepository).findParkingZoneByEmailAdmin(principal.getName());
        Mockito.verify(parkingLevelRepository)
            .findByIdParkingZoneOrderByLevelName(PARKING_ZONE.getIdParkingZone());
        Mockito.verifyNoMoreInteractions(parkingZoneRepository);
        Mockito.verifyNoMoreInteractions(parkingLevelRepository);
    }

    @Test public void getParkingBookingLayout() {
        Mockito.when(bookingRepository.findBookingByIdBooking(ID)).thenReturn(BOOKING);
        Mockito.when(parkingSlotRepository.findByIdSlot(BOOKING.getIdSlot()))
            .thenReturn(PARKING_SLOT3);
        Mockito.when(parkingLevelRepository.findByIdLevel(PARKING_SLOT.getIdLevel()))
            .thenReturn(PARKING_LEVEL);
        ResponseEntity responseEntity = parkingZoneServiceImpl.getParkingBookingLayout(ID);

        assertThat(responseEntity).isNotNull();

        Mockito.verify(bookingRepository).findBookingByIdBooking(ID);
        Mockito.verify(parkingSlotRepository).findByIdSlot(BOOKING.getIdSlot());
        Mockito.verify(parkingLevelRepository).findByIdLevel(PARKING_SLOT.getIdLevel());
        Mockito.verifyNoMoreInteractions(bookingRepository);
        Mockito.verifyNoMoreInteractions(parkingSlotRepository);
        Mockito.verifyNoMoreInteractions(parkingLevelRepository);
    }

    @Test public void getParkingBookingLayoutFailed() {
        ResponseEntity responseEntity = parkingZoneServiceImpl.getParkingBookingLayout(ID);

        assertThat(responseEntity).isNotNull();
    }

    @Test public void getParkingLevelLayout() {
        Mockito.when(parkingLevelRepository.findByIdLevel(ID)).thenReturn(PARKING_LEVEL);

        ResponseEntity responseEntity = parkingZoneServiceImpl.getParkingLevelLayout(ID);

        assertThat(responseEntity).isNotNull();

        Mockito.verify(parkingLevelRepository).findByIdLevel(ID);
        Mockito.verifyNoMoreInteractions(parkingLevelRepository);
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

        Mockito.verify(parkingSectionRepository)
            .findParkingSectionBySectionNameAndIdLevel(SECTION_ONE, ID);
        Mockito.verify(parkingSectionRepository)
            .findParkingSectionBySectionNameAndIdLevel(SECTION_TWO, ID);
        Mockito.verify(parkingSectionRepository)
            .findParkingSectionBySectionNameAndIdLevel(SECTION_THREE, ID);
        Mockito.verify(parkingSectionRepository)
            .findParkingSectionBySectionNameAndIdLevel(SECTION_FOUR, ID);
        Mockito.verify(parkingLevelRepository).findByIdLevel(ID);
        Mockito.verifyNoMoreInteractions(parkingSectionRepository);
        Mockito.verifyNoMoreInteractions(parkingLevelRepository);
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

        Mockito.verify(parkingSectionRepository)
            .findParkingSectionBySectionNameAndIdLevel(SECTION_ONE, ID);
        Mockito.verify(parkingSectionRepository)
            .findParkingSectionBySectionNameAndIdLevel(SECTION_TWO, ID);
        Mockito.verify(parkingSectionRepository)
            .findParkingSectionBySectionNameAndIdLevel(SECTION_THREE, ID);
        Mockito.verify(parkingSectionRepository)
            .findParkingSectionBySectionNameAndIdLevel(SECTION_FOUR, ID);
        Mockito.verify(parkingLevelRepository).findByIdLevel(ID);
        Mockito.verifyNoMoreInteractions(parkingSectionRepository);
        Mockito.verifyNoMoreInteractions(parkingLevelRepository);
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

        Mockito.verify(parkingSectionRepository)
            .findParkingSectionBySectionNameAndIdLevel(SECTION_ONE, ID);
        Mockito.verify(parkingSectionRepository)
            .findParkingSectionBySectionNameAndIdLevel(SECTION_TWO, ID);
        Mockito.verify(parkingSectionRepository)
            .findParkingSectionBySectionNameAndIdLevel(SECTION_THREE, ID);
        Mockito.verify(parkingSectionRepository)
            .findParkingSectionBySectionNameAndIdLevel(SECTION_FOUR, ID);
        Mockito.verify(parkingLevelRepository).findByIdLevel(ID);
        Mockito.verifyNoMoreInteractions(parkingSectionRepository);
        Mockito.verifyNoMoreInteractions(parkingLevelRepository);
    }

    @Test public void getSectionDetailsSlotScanMe() {
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
        SLOTS.set(0, SLOTS.get(0).replace('_', 'S'));
        PARKING_LEVEL.setSlotsLayout(SLOTS);
        Mockito.when(parkingLevelRepository.findByIdLevel(ID)).thenReturn(PARKING_LEVEL);

        ResponseEntity responseEntity = parkingZoneServiceImpl.getSectionDetails(ID);

        assertThat(responseEntity).isNotNull();


        Mockito.verify(parkingSectionRepository)
            .findParkingSectionBySectionNameAndIdLevel(SECTION_ONE, ID);
        Mockito.verify(parkingSectionRepository)
            .findParkingSectionBySectionNameAndIdLevel(SECTION_TWO, ID);
        Mockito.verify(parkingSectionRepository)
            .findParkingSectionBySectionNameAndIdLevel(SECTION_THREE, ID);
        Mockito.verify(parkingSectionRepository)
            .findParkingSectionBySectionNameAndIdLevel(SECTION_FOUR, ID);
        Mockito.verify(parkingLevelRepository).findByIdLevel(ID);
        Mockito.verifyNoMoreInteractions(parkingSectionRepository);
        Mockito.verifyNoMoreInteractions(parkingLevelRepository);
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

        Mockito.verify(parkingSectionRepository)
            .findParkingSectionBySectionNameAndIdLevel(SECTION_ONE, ID);
        Mockito.verify(parkingSectionRepository)
            .findParkingSectionBySectionNameAndIdLevel(SECTION_TWO, ID);
        Mockito.verify(parkingSectionRepository)
            .findParkingSectionBySectionNameAndIdLevel(SECTION_THREE, ID);
        Mockito.verify(parkingSectionRepository)
            .findParkingSectionBySectionNameAndIdLevel(SECTION_FOUR, ID);
        Mockito.verify(parkingLevelRepository).findByIdLevel(ID);
        Mockito.verifyNoMoreInteractions(parkingSectionRepository);
        Mockito.verifyNoMoreInteractions(parkingLevelRepository);
    }

    @Test public void editModeParkingLevelEditMode() {
        LIST_OF_PARKING_SLOT4.add(PARKING_SLOT3);
        LIST_OF_PARKING_SLOT4.add(PARKING_SLOT3);
        LIST_OF_PARKING_SLOT4.add(PARKING_SLOT3);
        Mockito.when(parkingLevelRepository.findByIdLevel(ID)).thenReturn(PARKING_LEVEL);
        Mockito.when(parkingSlotRepository.findAllByIdLevel(ID)).thenReturn(LIST_OF_PARKING_SLOT4);
        ResponseEntity responseEntity = parkingZoneServiceImpl.editModeParkingLevel(ID, EDIT_MODE);

        assertThat(responseEntity).isNotNull();
        PARKING_SLOT.setStatus("X-X-X-E");

        Mockito.verify(parkingLevelRepository).findByIdLevel(ID);
        Mockito.verify(parkingLevelRepository).save(PARKING_LEVEL);
        Mockito.verify(parkingSlotRepository).findAllByIdLevel(ID);
        Mockito.verify(parkingSlotRepository, Mockito.times(3)).save(PARKING_SLOT);
        Mockito.verifyNoMoreInteractions(parkingLevelRepository);
        Mockito.verifyNoMoreInteractions(parkingSlotRepository);
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

        PARKING_SLOT.setStatus("E");
        Mockito.verify(parkingLevelRepository).findByIdLevel(ID);
        Mockito.verify(parkingLevelRepository).save(PARKING_LEVEL);
        Mockito.verify(parkingSlotRepository).findAllByIdLevel(ID);
        Mockito.verify(parkingSlotRepository, Mockito.times(6)).save(PARKING_SLOT);
        Mockito.verifyNoMoreInteractions(parkingLevelRepository);
        Mockito.verifyNoMoreInteractions(parkingSlotRepository);
    }

    @Test public void editModeParkingNullParkingZone() {
        Mockito.when(parkingLevelRepository.findByIdLevel(ID)).thenReturn(null);

        ResponseEntity responseEntity =
            parkingZoneServiceImpl.editModeParkingLevel(ID, EXIT_EDIT_MODE);

        assertThat(responseEntity).isNotNull();

        Mockito.verify(parkingLevelRepository).findByIdLevel(ID);
        Mockito.verifyNoMoreInteractions(parkingLevelRepository);
    }

    @Test public void updateParkingZonePictureSuccess() throws IOException {
        Mockito.when(parkingZoneRepository.findParkingZoneByEmailAdmin(principal.getName()))
            .thenReturn(PARKING_ZONE);

        FileInputStream inputFile = new FileInputStream("./erd.png");
        MockMultipartFile file =
            new MockMultipartFile("file", "NameOfTheFile.png", "image/png", inputFile);

        ResponseEntity responseEntity =
            parkingZoneServiceImpl.updateParkingZonePicture(principal, file);

        assertThat(responseEntity).isNotNull();

        Mockito.verify(parkingZoneRepository).findParkingZoneByEmailAdmin(principal.getName());
        Mockito.verify(parkingZoneRepository).save(PARKING_ZONE);
        Mockito.verifyNoMoreInteractions(parkingZoneRepository);
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

        Mockito.verify(parkingZoneRepository, Mockito.times(2)).findParkingZoneByIdParkingZone(ID);
        Mockito.verifyNoMoreInteractions(parkingZoneRepository);
    }

    @Test public void getAdminSAFailed() {
        Mockito.when(parkingZoneRepository.findParkingZoneByIdParkingZone(ID)).thenReturn(null);

        ResponseEntity responseEntity = parkingZoneServiceImpl.getAdminSA(ID);

        assertThat(responseEntity).isNotNull();

        Mockito.verify(parkingZoneRepository).findParkingZoneByIdParkingZone(ID);
        Mockito.verifyNoMoreInteractions(parkingZoneRepository);
    }

    @Test public void getLatLng() {
        Mockito.when(parkingZoneRepository.findParkingZoneByLatitudeNotNullAndLongitudeNotLike(0.0))
            .thenReturn(LIST_OF_PARKING_ZONE);

        ResponseEntity responseEntity = parkingZoneServiceImpl.getLatLng();

        assertThat(responseEntity).isNotNull();

        Mockito.verify(parkingZoneRepository)
            .findParkingZoneByLatitudeNotNullAndLongitudeNotLike(0.0);
        Mockito.verifyNoMoreInteractions(parkingZoneRepository);
    }

}
