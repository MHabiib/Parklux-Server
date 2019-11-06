package com.future.pms.controller;

import com.future.pms.model.User;
import com.future.pms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("**") @RestController @RequestMapping("/api/user") public class UserController {

    @Autowired UserService userService;

    @GetMapping public ResponseEntity loadAll() {
        return ResponseEntity.ok(userService.loadAll());
    }

    @PostMapping public ResponseEntity createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable("id") String id, @RequestBody User user) {
        return userService.updateUser(id, user);
    }
}
