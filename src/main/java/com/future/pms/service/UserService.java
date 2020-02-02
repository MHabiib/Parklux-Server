package com.future.pms.service;

import com.future.pms.model.User;
import org.springframework.http.ResponseEntity;

import java.security.Principal;

public interface UserService {
    ResponseEntity<User> loadAll();

    ResponseEntity loadAll(Integer page, Principal principal);

    ResponseEntity createUser(User user);

    ResponseEntity updateUser(User user, Principal principal);

    ResponseEntity updateUserFromList(String id, User user);

    ResponseEntity getUserSA(String id);

    String deleteSuperAdmin(String id, Principal principal);
}
