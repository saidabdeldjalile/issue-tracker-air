package com.suryakn.IssueTracker.controller;

import com.suryakn.IssueTracker.dto.CreateUserRequest;
import com.suryakn.IssueTracker.dto.UserProjection;
import com.suryakn.IssueTracker.dto.UserUpdateRequest;
import com.suryakn.IssueTracker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserProjection>> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserProjection> getUserById(@PathVariable Integer id) {
        return userService.getUserById(id);
    }

    @GetMapping("/by-email/{email}")
    public ResponseEntity<UserProjection> getUserByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email);
    }

    @PostMapping
    public ResponseEntity<UserProjection> createUser(@RequestBody CreateUserRequest request) {
        return userService.createUser(request);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserProjection> updateUser(@PathVariable Integer id, @RequestBody CreateUserRequest request) {
        return userService.updateUser(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
        return userService.deleteUser(id);
    }

    @PutMapping("/profile")
    public ResponseEntity<UserProjection> updateProfile(@RequestBody UserUpdateRequest request) {
        return userService.updateUserProfile(request);
    }
}
