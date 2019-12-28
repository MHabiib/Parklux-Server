package com.future.pms.service;

import com.future.pms.model.User;
import org.springframework.http.ResponseEntity;

public interface UserService {
    ResponseEntity<User> loadAll();

    ResponseEntity createUser(User user);

    ResponseEntity<User> updateUser(String id, User user);

    ResponseEntity deleteUser(String id);
}
