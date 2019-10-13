package com.future.pms.service.impl;

import com.future.pms.model.User;
import com.future.pms.repository.UserRepository;
import com.future.pms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public ResponseEntity loadAll() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @Override
    public ResponseEntity createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return null;
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
