package com.future.pms.controller;

import com.future.pms.model.User;
import com.future.pms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@CrossOrigin("**") @RestController @RequestMapping("/api/user") public class UserController {
    @Autowired UserService userService;

    @GetMapping public ResponseEntity loadAll() {
        return ResponseEntity.ok(userService.loadAll());
    }

    @GetMapping("/me") public Principal getUser(Principal principal) {
        return principal;
    }

    @GetMapping("/customer/detail") public ResponseEntity getUserDetail(Principal principal) {
        return ResponseEntity.ok(userService.getUserDetail(principal));
    }

    @PostMapping public ResponseEntity createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable("id") String id, @RequestBody User user) {
        return userService.updateUser(id, user);
    }
}
