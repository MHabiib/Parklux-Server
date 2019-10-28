package com.future.pms.service.impl;

import com.future.pms.model.Customer;
import com.future.pms.model.parking.ParkingZone;
import com.future.pms.model.User;
import com.future.pms.repository.CustomerRepository;
import com.future.pms.repository.ParkingZoneRepository;
import com.future.pms.repository.UserRepository;
import com.future.pms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

import static com.future.pms.Constants.ADMIN;
import static com.future.pms.Constants.CUSTOMER;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    ParkingZoneRepository parkingZoneRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public ResponseEntity loadAll() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @Override
    public ResponseEntity createUser(User user) {
        if (userRepository.findByEmail(user.getEmail()) != null)
            return new ResponseEntity<>("Email already registered !", HttpStatus.BAD_REQUEST);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRole().equals(ADMIN)) {
            ParkingZone parkingZone = new ParkingZone();
            parkingZone.setName("PARKING ZONE NAME");
            parkingZone.setEmailParkingZone(user.getEmail());
            parkingZoneRepository.save(parkingZone);
        } else if (user.getRole().equals(CUSTOMER)) {
            Customer customer = new Customer();
            customer.setName(CUSTOMER);
            customer.setEmail(user.getEmail());
            customerRepository.save(customer);
        }
        userRepository.save(user);
        return new ResponseEntity<>("Create user successful !", HttpStatus.OK);
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
