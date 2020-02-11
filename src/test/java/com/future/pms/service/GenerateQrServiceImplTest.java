package com.future.pms.service;

import com.future.pms.AmazonClient;
import com.future.pms.model.parking.ParkingLevel;
import com.future.pms.model.parking.ParkingSlot;
import com.future.pms.model.parking.ParkingZone;
import com.future.pms.repository.ParkingLevelRepository;
import com.future.pms.repository.ParkingSlotRepository;
import com.future.pms.repository.ParkingZoneRepository;
import com.future.pms.service.impl.GenerateQRServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.security.Principal;
import java.util.*;

import static com.future.pms.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.Silent.class) public class GenerateQrServiceImplTest {
    private static final ParkingSlot PARKING_SLOT =
        ParkingSlot.builder().idSlot("idSlot").idLevel("idLevel").idParkingZone("idParkingZone")
            .name("name").slotNumberInLayout(1).status(SLOT_TAKEN).status(SLOT_SCAN_ME).build();
    private static final ParkingZone PARKING_ZONE =
        ParkingZone.builder().idParkingZone("idParkingZone").address("address")
            .emailAdmin("emailAdmin").imageUrl("imageUrl").name("name").phoneNumber("phoneNumber")
            .build();
    private static final ParkingLevel PARKING_LEVEL = ParkingLevel.builder().idLevel("idLevel")
        .slotsLayout(new ArrayList<>(Arrays.asList("_1", "_2"))).build();

    private static final List<ParkingSlot> LIST_OF_PARKING_SLOT =
        Collections.singletonList(PARKING_SLOT);
    private static final String ID = "id";
    private static final String IMAGE_NAME = "imageName";

    @InjectMocks GenerateQRServiceImpl generateQRServiceImpl;
    @Mock ParkingSlotRepository parkingSlotRepository;
    @Mock ParkingZoneRepository parkingZoneRepository;
    @Mock ParkingLevelRepository parkingLevelRepository;
    @Mock Timer mockedTimer;
    @Mock AmazonClient amazonClient;
    @Mock private Principal principal;

    @Test public void generateQRListParkingSlotNull() throws IOException {
        Mockito.when(parkingSlotRepository.findByIdSlot(ID)).thenReturn(PARKING_SLOT);
        Mockito.when(parkingZoneRepository.findParkingZoneByEmailAdmin(principal.getName()))
            .thenReturn(PARKING_ZONE);

        ResponseEntity responseEntity = generateQRServiceImpl.generateQR(principal, "");

        assertThat(responseEntity).isNotNull();

        Mockito.verify(parkingZoneRepository).findParkingZoneByEmailAdmin(principal.getName());
        Mockito.verifyNoMoreInteractions(parkingZoneRepository);
    }

    @Test public void generateQRSlotFull() throws IOException, InterruptedException {
        PARKING_SLOT.setStatus(SLOT_EMPTY);
        Mockito.when(parkingSlotRepository.findByIdSlot(ID)).thenReturn(PARKING_SLOT);
        Mockito.when(parkingZoneRepository.findParkingZoneByEmailAdmin(principal.getName()))
            .thenReturn(PARKING_ZONE);
        Mockito.when(parkingSlotRepository
            .findAllByIdParkingZoneAndStatus(PARKING_ZONE.getIdParkingZone(), SLOT_EMPTY))
            .thenReturn(LIST_OF_PARKING_SLOT);
        Mockito.when(parkingLevelRepository.findByIdLevel(PARKING_SLOT.getIdLevel()))
            .thenReturn(PARKING_LEVEL);
        Mockito.when(parkingSlotRepository.findByIdSlot(PARKING_SLOT.getIdSlot()))
            .thenReturn(PARKING_SLOT);

        ResponseEntity responseEntity = generateQRServiceImpl.generateQR(principal, "");

        Thread.sleep(23000);

        assertThat(responseEntity).isNotNull();

        Mockito.verify(parkingZoneRepository).findParkingZoneByEmailAdmin(principal.getName());
        Mockito.verify(parkingLevelRepository, Mockito.times(2))
            .findByIdLevel(PARKING_SLOT.getIdLevel());
        Mockito.verify(parkingLevelRepository, Mockito.times(2)).save(PARKING_LEVEL);
        Mockito.verifyNoMoreInteractions(parkingZoneRepository);
        Mockito.verifyNoMoreInteractions(parkingLevelRepository);
    }

    @Test public void generateQRSlotEmpty() throws IOException {
        Mockito.when(parkingSlotRepository.findByIdSlot(ID)).thenReturn(PARKING_SLOT);
        Mockito.when(parkingZoneRepository.findParkingZoneByEmailAdmin(principal.getName()))
            .thenReturn(PARKING_ZONE);
        Mockito.when(parkingSlotRepository
            .findAllByIdParkingZoneAndStatus(PARKING_ZONE.getIdParkingZone(), SLOT_EMPTY))
            .thenReturn(LIST_OF_PARKING_SLOT);

        ResponseEntity responseEntity = generateQRServiceImpl.generateQR(principal, "");

        assertThat(responseEntity).isNotNull();

        Mockito.verify(parkingZoneRepository).findParkingZoneByEmailAdmin(principal.getName());
        Mockito.verify(parkingSlotRepository)
            .findAllByIdParkingZoneAndStatus(PARKING_ZONE.getIdParkingZone(), SLOT_EMPTY);
        Mockito.verifyNoMoreInteractions(parkingZoneRepository);
        Mockito.verifyNoMoreInteractions(parkingSlotRepository);
    }
}
