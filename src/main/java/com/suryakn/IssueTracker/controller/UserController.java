package com.suryakn.IssueTracker.controller;

import com.suryakn.IssueTracker.dto.CreateUserRequest;
import com.suryakn.IssueTracker.dto.UserProjection;
import com.suryakn.IssueTracker.dto.UserUpdateRequest;
import com.suryakn.IssueTracker.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
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
    public ResponseEntity<UserProjection> createUser(@Valid @RequestBody CreateUserRequest request) {
        return userService.createUser(request);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserProjection> updateUser(@PathVariable Integer id, @Valid @RequestBody CreateUserRequest request) {
        return userService.updateUser(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
        return userService.deleteUser(id);
    }

    @PutMapping("/profile")
    public ResponseEntity<UserProjection> updateProfile(@Valid @RequestBody UserUpdateRequest request) {
        return userService.updateUserProfile(request);
    }
}
