package com.future.pms.controller;

import com.future.pms.model.User;
import com.future.pms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@CrossOrigin("**") @RestController @RequestMapping public class UserController {
    @Autowired UserService userService;

    @GetMapping("/api3/user") public ResponseEntity loadAll(Integer page, Principal principal) {
        return ResponseEntity.ok(userService.loadAll(page, principal));
    }

    @GetMapping("/api3/user/me") public ResponseEntity getUser() {
        return new ResponseEntity<>("Authorized", HttpStatus.OK);
    }

    @GetMapping("/api3/user/email") public String getEmail(Principal principal) {
        return principal.getName();
    }

    @GetMapping("/api3/{id}/user") public ResponseEntity getUserSA(@PathVariable("id") String id) {
        return userService.getUserSA(id);
    }

    @PostMapping("/api3/user") public ResponseEntity createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @PutMapping("/api3/user")
    public ResponseEntity updateUser(@RequestBody User user, Principal principal) {
        return userService.updateUser(user, principal);
    }

    @PutMapping("/api3/{id}/user")
    public ResponseEntity updateUserFromList(@PathVariable("id") String id,
        @RequestBody User user) {
        return userService.updateUserFromList(id, user);
    }

    @DeleteMapping("/api3/{id}/user")
    public String deleteSuperAdmin(@PathVariable("id") String id, Principal principal) {
        return userService.deleteSuperAdmin(id, principal);
    }
}
