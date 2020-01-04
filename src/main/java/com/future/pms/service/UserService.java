package com.future.pms.service;

import com.future.pms.model.User;
import org.springframework.http.ResponseEntity;

import java.security.Principal;

public interface UserService {
    ResponseEntity<User> loadAll();

    ResponseEntity loadAll(Integer page);

    ResponseEntity createUser(User user);

    ResponseEntity updateUser(User user, Principal principal);

    ResponseEntity deleteUser(String id);

    Object getUserSA(String id);
}
