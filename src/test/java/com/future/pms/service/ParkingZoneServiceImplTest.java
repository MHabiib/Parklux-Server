package com.future.pms.service;

import com.future.pms.model.parking.ParkingLevel;
import com.future.pms.model.parking.ParkingSection;
import com.future.pms.model.parking.ParkingSlot;
import com.future.pms.model.parking.ParkingZone;
import com.future.pms.repository.ParkingLevelRepository;
import com.future.pms.repository.ParkingSectionRepository;
import com.future.pms.repository.ParkingSlotRepository;
import com.future.pms.repository.ParkingZoneRepository;
import com.future.pms.service.impl.ParkingZoneServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;

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
    private static final ParkingSlot PARKING_SLOT =
        ParkingSlot.builder().idSlot("idSlot").idLevel("idLevel").idParkingZone("idParkingZone")
            .name("name").slotNumberInLayout(1).status(SLOT_TAKEN).status(SLOT_SCAN_ME).build();
    private static final ParkingSection PARKING_SECTION = ParkingSection.builder().build();
    private static final ParkingZone PARKING_ZONE =
        ParkingZone.builder().idParkingZone("idParkingZone").address("address")
            .emailAdmin("emailAdmin").imageUrl("imageUrl").name("name").phoneNumber("phoneNumber")
            .build();
    private static final List<ParkingZone> LIST_OF_PARKING_ZONE =
        Collections.singletonList(PARKING_ZONE);
    private static final Page<ParkingZone> PAGE_OF_PARKING_ZONE =
        new PageImpl<>(LIST_OF_PARKING_ZONE);

    @InjectMocks ParkingZoneServiceImpl parkingZoneServiceImpl;
    @Mock ParkingZoneRepository parkingZoneRepository;
    @Mock ParkingLevelRepository parkingLevelRepository;
    @Mock ParkingSectionRepository parkingSectionRepository;
    @Mock ParkingSlotRepository parkingSlotRepository;
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

    @Test public void updateParkingSectionSlotNotFound() {
        Mockito.when(parkingZoneRepository.findParkingZoneByEmailAdmin(principal.getName()))
            .thenReturn(null);

        ResponseEntity responseEntity = parkingZoneServiceImpl.updateParkingSection(ID);

        assertThat(responseEntity).isNotNull();
    }
}
