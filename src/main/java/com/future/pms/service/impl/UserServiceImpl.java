package com.future.pms.service.impl;

import com.future.pms.config.MongoTokenStore;
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
    @Autowired MongoTokenStore mongoTokenStore;

    @Override public ResponseEntity loadAll() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @Override public ResponseEntity loadAll(Integer page, Principal principal) {
        PageRequest request = PageRequest.of(page, 10, new Sort(Sort.Direction.ASC, "name"));
        return ResponseEntity.ok(userRepository
            .findAllByRoleAndEmailIsNot(SUPER_ADMIN, request, principal.getName()));
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
        if (null != userRepository.findByEmail(user.getEmail())) {
            if (null != userRepository.findByEmail(user.getEmail()) && !user.getEmail()
                .equals(principal.getName())) {
                return new ResponseEntity<>("Email already registered !", HttpStatus.BAD_REQUEST);
            }
        }
        User userExist = userRepository.findByEmail(principal.getName());
        return updateUser(user, userExist);
    }

    @Override public ResponseEntity updateUserFromList(String id, User user) {
        User userExist = userRepository.findByIdUser(id);
        if (null != userRepository.findByEmail(user.getEmail()) && !user.getEmail()
            .equals(userExist.getEmail()))
            return new ResponseEntity<>("Email already registered !", HttpStatus.BAD_REQUEST);
        return updateUser(user, userExist);
    }

    private ResponseEntity updateUser(User user, User userExist) {
        if (!"".equals(user.getPassword()) && null != user.getPassword()) {
            userExist.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        userExist.setEmail(user.getEmail());
        userRepository.save(userExist);
        return new ResponseEntity<>("Update user successful !", HttpStatus.OK);
    }

    @Override public ResponseEntity getUserSA(String id) {
        User user = userRepository.findByIdUser(id);
        if (user != null) {
            return new ResponseEntity<>(userRepository.findByIdUser(id), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
    }

    @Override public String deleteSuperAdmin(String id, Principal principal) {
        User user = userRepository.findByIdUser(id);
        if (user != null && !user.getEmail().equals(principal.getName())) {
            userRepository.delete(user);
            return "Success delete Super Admin";
        } else {
            return "Failed delete Super Admin";
        }
    }
}
