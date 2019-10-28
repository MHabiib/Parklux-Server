package com.future.pms.service;

import com.future.pms.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

public interface UserService {
    ResponseEntity<User> loadAll();
    ResponseEntity createUser(@RequestBody User user);
    ResponseEntity<User> updateUser(@PathVariable("id") String id, @RequestBody User user);
    ResponseEntity deleteUser(@PathVariable("id") String id);
}
