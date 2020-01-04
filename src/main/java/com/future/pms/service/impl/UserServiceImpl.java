package com.future.pms.service.impl;

import com.future.pms.model.Customer;
import com.future.pms.model.User;
import com.future.pms.model.parking.ParkingZone;
import com.future.pms.repository.CustomerRepository;
import com.future.pms.repository.ParkingZoneRepository;
import com.future.pms.repository.UserRepository;
import com.future.pms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;

import static com.future.pms.Constants.*;

@Service public class UserServiceImpl implements UserService {
    @Autowired UserRepository userRepository;
    @Autowired CustomerRepository customerRepository;
    @Autowired ParkingZoneRepository parkingZoneRepository;
    @Autowired PasswordEncoder passwordEncoder;

    @Override public ResponseEntity loadAll() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @Override public ResponseEntity loadAll(Integer page) {
        PageRequest request = new PageRequest(page, 10, new Sort(Sort.Direction.ASC, "name"));
        return ResponseEntity.ok(userRepository.findAllByRole(SUPER_ADMIN, request));
    }

    @Override public ResponseEntity createUser(User user) {
        if (null != userRepository.findByEmail(user.getEmail()))
            return new ResponseEntity<>("Email already registered !", HttpStatus.BAD_REQUEST);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (ADMIN.equals(user.getRole())) {
            ParkingZone parkingZone = new ParkingZone();
            parkingZone.setName("PARKING ZONE NAME");
            parkingZone.setEmailAdmin(user.getEmail());
            parkingZoneRepository.save(parkingZone);
            addUser(user);
        } else if (CUSTOMER.equals(user.getRole())) {
            Customer customer = new Customer();
            customer.setName(CUSTOMER);
            customer.setEmail(user.getEmail());
            customerRepository.save(customer);
            addUser(user);
        } else if (SUPER_ADMIN.equals(user.getRole())) {
            addUser(user);
        } else {
            new ResponseEntity<>("Failed create user", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Create user successful !", HttpStatus.OK);
    }

    private void addUser(User user) {
        userRepository.save(user);
    }

    @Override public ResponseEntity updateUser(User user, Principal principal) {
        User userExist = userRepository.findByEmail(principal.getName());
        if (null != userRepository.findByEmail(user.getEmail()) || null == userExist)
            return new ResponseEntity<>("Email already registered !", HttpStatus.MULTI_STATUS);
        userExist.setPassword(passwordEncoder.encode(user.getPassword()));
        userExist.setEmail(user.getEmail());
        userRepository.save(userExist);
        return new ResponseEntity<>("Create user successful !", HttpStatus.OK);
    }

    @Override public ResponseEntity deleteUser(String id) {
        return null;
    }

    @Override public Object getUserSA(String id) {
        User user = userRepository.findByIdUser(id);
        if (user != null) {
            return new ResponseEntity<>(userRepository.findByIdUser(id), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
    }
}
