package com.suryakn.IssueTracker.service;

import com.suryakn.IssueTracker.dto.CreateUserRequest;
import com.suryakn.IssueTracker.dto.UserProjection;
import com.suryakn.IssueTracker.dto.UserUpdateRequest;
import com.suryakn.IssueTracker.entity.Department;
import com.suryakn.IssueTracker.entity.Role;
import com.suryakn.IssueTracker.entity.Ticket;
import com.suryakn.IssueTracker.entity.UserEntity;
import com.suryakn.IssueTracker.repository.DepartmentRepository;
import com.suryakn.IssueTracker.repository.TicketRepository;
import com.suryakn.IssueTracker.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private UserEntity testUser;
    private Department testDepartment;

    @BeforeEach
    void setUp() {
        testDepartment = Department.builder()
                .id(1L)
                .name("IT Department")
                .build();

        testUser = UserEntity.builder()
                .id(1)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("encodedPassword")
                .role(Role.USER)
                .registrationNumber("REG001")
                .department(testDepartment)
                .build();
    }

    @Test
    void testGetAllUsers_ShouldReturnAllUsers() {
        List<UserEntity> users = new ArrayList<>();
        users.add(testUser);
        when(userRepository.findAll()).thenReturn(users);

        ResponseEntity<List<UserProjection>> response = userService.getAllUsers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testGetUserById_WhenExists_ShouldReturnUser() {
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));

        ResponseEntity<UserProjection> response = userService.getUserById(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("John", response.getBody().getFirstName());
        verify(userRepository, times(1)).findById(1);
    }

    @Test
    void testGetUserById_WhenNotExists_ShouldReturnNotFound() {
        when(userRepository.findById(999)).thenReturn(Optional.empty());

        ResponseEntity<UserProjection> response = userService.getUserById(999);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testGetUserByEmail_WhenExists_ShouldReturnUser() {
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(testUser));

        ResponseEntity<UserProjection> response = userService.getUserByEmail("john.doe@example.com");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("john.doe@example.com", response.getBody().getEmail());
    }

    @Test
    void testGetUserByEmail_WhenNotExists_ShouldReturnNotFound() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        ResponseEntity<UserProjection> response = userService.getUserByEmail("unknown@example.com");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testCreateUser_WithValidData_ShouldReturnCreatedUser() {
        CreateUserRequest request = CreateUserRequest.builder()
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@example.com")
                .password("password123")
                .role("USER")
                .registrationNumber("REG002")
                .departmentId(1L)
                .build();

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(userRepository.existsByRegistrationNumber(request.getRegistrationNumber())).thenReturn(false);
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(testDepartment));
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(UserEntity.class))).thenReturn(testUser);

        ResponseEntity<UserProjection> response = userService.createUser(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    void testCreateUser_WithDuplicateEmail_ShouldReturnConflict() {
        CreateUserRequest request = CreateUserRequest.builder()
                .firstName("Jane")
                .lastName("Smith")
                .email("john.doe@example.com")
                .password("password123")
                .role("USER")
                .build();

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        ResponseEntity<UserProjection> response = userService.createUser(request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        verify(userRepository, never()).save(any());
    }

    @Test
    void testCreateUser_WithDuplicateRegistrationNumber_ShouldReturnConflict() {
        CreateUserRequest request = CreateUserRequest.builder()
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@example.com")
                .password("password123")
                .role("USER")
                .registrationNumber("REG001")
                .build();

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(userRepository.existsByRegistrationNumber(request.getRegistrationNumber())).thenReturn(true);

        ResponseEntity<UserProjection> response = userService.createUser(request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        verify(userRepository, never()).save(any());
    }

    @Test
    void testUpdateUser_WithValidData_ShouldReturnUpdatedUser() {
        CreateUserRequest request = CreateUserRequest.builder()
                .firstName("John Updated")
                .lastName("Doe Updated")
                .email("john.updated@example.com")
                .password("newPassword")
                .role("ADMIN")
                .registrationNumber("REG001_UPDATED")
                .departmentId(1L)
                .build();

        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(userRepository.existsByRegistrationNumber(request.getRegistrationNumber())).thenReturn(false);
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(testDepartment));
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedNewPassword");
        when(userRepository.save(any(UserEntity.class))).thenReturn(testUser);

        ResponseEntity<UserProjection> response = userService.updateUser(1, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void testUpdateUser_WhenNotExists_ShouldReturnNotFound() {
        CreateUserRequest request = CreateUserRequest.builder()
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@example.com")
                .build();

        when(userRepository.findById(999)).thenReturn(Optional.empty());

        ResponseEntity<UserProjection> response = userService.updateUser(999, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testUpdateUser_WithDuplicateEmail_ShouldReturnConflict() {
        CreateUserRequest request = CreateUserRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .email("other.user@example.com")
                .build();

        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        ResponseEntity<UserProjection> response = userService.updateUser(1, request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    void testDeleteUser_WhenExists_ShouldDeleteUser() {
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(ticketRepository.findByCreatedByEmail(testUser.getEmail())).thenReturn(new ArrayList<>());
        when(ticketRepository.findByAssignedToEmail(testUser.getEmail())).thenReturn(new ArrayList<>());

        ResponseEntity<Void> response = userService.deleteUser(1);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(userRepository, times(1)).deleteById(1);
    }

    @Test
    void testDeleteUser_WhenNotExists_ShouldReturnNotFound() {
        when(userRepository.findById(999)).thenReturn(Optional.empty());

        ResponseEntity<Void> response = userService.deleteUser(999);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(userRepository, never()).deleteById(999);
    }

    @Test
    void testDeleteUser_WithTickets_ShouldClearTickets() {
        List<Ticket> createdTickets = new ArrayList<>();
        Ticket ticket = new Ticket("Test", "Desc", null, null);
        createdTickets.add(ticket);

        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(ticketRepository.findByCreatedByEmail(testUser.getEmail())).thenReturn(createdTickets);
        when(ticketRepository.findByAssignedToEmail(testUser.getEmail())).thenReturn(new ArrayList<>());

        ResponseEntity<Void> response = userService.deleteUser(1);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(ticketRepository, times(1)).save(ticket);
        assertNull(ticket.getCreatedBy());
    }

    @Test
    void testUpdateUserProfile_WithValidData_ShouldReturnUpdatedUser() {
        UserUpdateRequest request = new UserUpdateRequest();
        request.setEmail("john.doe@example.com");
        request.setFirstName("John Updated");
        request.setLastName("Doe Updated");
        request.setPassword("newPassword");
        request.setRegistrationNumber("REG001");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(testUser));
        when(userRepository.existsByRegistrationNumber(request.getRegistrationNumber())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedNewPassword");
        when(userRepository.save(any(UserEntity.class))).thenReturn(testUser);

        ResponseEntity<UserProjection> response = userService.updateUserProfile(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void testUpdateUserProfile_WhenNotExists_ShouldReturnNotFound() {
        UserUpdateRequest request = new UserUpdateRequest();
        request.setEmail("unknown@example.com");
        request.setFirstName("Unknown");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        ResponseEntity<UserProjection> response = userService.updateUserProfile(request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testUpdateUserProfile_WithDuplicateRegistrationNumber_ShouldReturnConflict() {
        UserUpdateRequest request = new UserUpdateRequest();
        request.setEmail("john.doe@example.com");
        request.setRegistrationNumber("REG999");

        UserEntity otherUser = UserEntity.builder()
                .id(2)
                .registrationNumber("REG999")
                .build();

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(testUser));
        when(userRepository.existsByRegistrationNumber(request.getRegistrationNumber())).thenReturn(true);
        when(userRepository.findByRegistrationNumber(request.getRegistrationNumber())).thenReturn(Optional.of(otherUser));

        ResponseEntity<UserProjection> response = userService.updateUserProfile(request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }
}