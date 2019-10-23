package com.future.pms.service.impl;

import com.future.pms.model.Customer;
import com.future.pms.model.ParkingZone;
import com.future.pms.model.User;
import com.future.pms.repository.CustomerRepository;
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
    CustomerRepository customerRepository;

    private final
    ParkingZoneRepository parkingZoneRepository;

    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, ParkingZoneRepository parkingZoneRepository
            , PasswordEncoder passwordEncoder, ParkingZoneService parkingZoneService, CustomerRepository customerRepository) {
        this.userRepository = userRepository;
        this.parkingZoneRepository = parkingZoneRepository;
        this.passwordEncoder = passwordEncoder;
        this.parkingZoneService = parkingZoneService;
        this.customerRepository = customerRepository;
    }

    @Override
    public ResponseEntity loadAll() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @Override
    public ResponseEntity createUser(User user) {
        if (userRepository.findByEmail(user.getEmail()) != null)
            return new ResponseEntity<>("Email already registered !",HttpStatus.BAD_REQUEST);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if(user.getRole().equals("ADMIN")) {
            ParkingZone parkingZone = new ParkingZone();
            parkingZone.setEmailParkingZone(user.getEmail());
            parkingZoneRepository.save(parkingZone);
        }
        else if (user.getRole().equals("CUSTOMER")){
            Customer customer = new Customer();
            customer.setEmail(user.getEmail());
            customerRepository.save(customer);
        }
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
