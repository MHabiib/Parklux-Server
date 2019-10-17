/*
package com.future.pms.service.impl;

import com.future.pms.model.parking.ParkingZone;
import com.future.pms.model.User;
import com.future.pms.repository.ParkingZoneRepository;
import com.future.pms.repository.UserRepository;
import com.future.pms.service.ParkingZoneService;
import com.future.pms.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final
    ParkingZoneService parkingZoneService;

    private final
    UserRepository userRepository;

    private final
    ParkingZoneRepository parkingZoneRepository;

    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, ParkingZoneRepository parkingZoneRepository, PasswordEncoder passwordEncoder, ParkingZoneService parkingZoneService) {
        this.userRepository = userRepository;
        this.parkingZoneRepository = parkingZoneRepository;
        this.passwordEncoder = passwordEncoder;
        this.parkingZoneService = parkingZoneService;
    }

    @Override
    public ResponseEntity loadAll() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @Override
    public ResponseEntity createUser(User user) {
        if (userRepository.findByEmail(user.getEmail()) != null)
            return new ResponseEntity<>("Email already registered !",HttpStatus.BAD_REQUEST);

        if (user.getRole().equals("ADMIN")){
            if(parkingZoneRepository.findParkingZoneByName(user.getName())!=null)
                return new ResponseEntity<>("Name already registered !",HttpStatus.BAD_REQUEST);

            ParkingZone parkingZone = new ParkingZone();
            parkingZone.setName(user.getName());
            parkingZoneService.createParkingZone(parkingZone);
        }
        else
            user.setRole("CUSTOMER");

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return new ResponseEntity<>("Create user successful !",HttpStatus.OK);
    }

    @Override
    public ResponseEntity<User> updateUser(String id, User user) {
        return null;
    }

    @Override
    public ResponseEntity deleteUser(String id) {
        return null;
    }
}
*/
