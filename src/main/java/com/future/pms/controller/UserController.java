package com.future.pms.controller;

import com.future.pms.model.User;
import com.future.pms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("**") @RestController @RequestMapping public class UserController {
    @Autowired UserService userService;

    @GetMapping("/api/user") public ResponseEntity loadAll() {
        return ResponseEntity.ok(userService.loadAll());
    }

    @GetMapping("/api3/user/me") public ResponseEntity getUser() {
        return new ResponseEntity<>("Authorized", HttpStatus.OK);
    }

    @PostMapping("/api/user") public ResponseEntity createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @PutMapping("/api/user/{id}")
    public ResponseEntity<User> updateUser(@PathVariable("id") String id, @RequestBody User user) {
        return userService.updateUser(id, user);
    }
}
