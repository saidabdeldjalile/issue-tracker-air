package com.suryakn.IssueTracker.service;

import com.suryakn.IssueTracker.dto.NotificationDto;
import com.suryakn.IssueTracker.entity.Department;
import com.suryakn.IssueTracker.entity.Notification;
import com.suryakn.IssueTracker.entity.NotificationType;
import com.suryakn.IssueTracker.entity.UserEntity;
import com.suryakn.IssueTracker.repository.NotificationRepository;
import com.suryakn.IssueTracker.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private NotificationService notificationService;

    private UserEntity testUser;
    private Department testDepartment;
    private Notification testNotification;

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
                .password("password123")
                .role(com.suryakn.IssueTracker.entity.Role.USER)
                .department(testDepartment)
                .build();

        testNotification = Notification.builder()
                .id(1L)
                .type(NotificationType.TICKET_CREATED)
                .title("New Ticket")
                .message("A new ticket has been created")
                .user(testUser)
                .department(testDepartment)
                .relatedEntityId(1L)
                .isRead(false)
                .build();
    }

    @Test
    void testCreateNotification_ShouldSaveAndSendRealTimeNotification() {
        when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);

        Notification result = notificationService.createNotification(
            NotificationType.TICKET_CREATED,
            "New Ticket",
            "A new ticket has been created",
            testUser,
            testDepartment,
            1L
        );

        assertNotNull(result);
        assertEquals("New Ticket", result.getTitle());
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void testCreateNotificationsForUsers_ShouldCreateNotificationForEachUser() {
        List<UserEntity> users = new ArrayList<>();
        users.add(testUser);

        when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);

        notificationService.createNotificationsForUsers(
            NotificationType.TICKET_CREATED,
            "New Ticket",
            "A new ticket has been created",
            users,
            testDepartment,
            1L
        );

        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void testCreateNotificationsForUsers_WithEmptyList_ShouldNotCreateNotifications() {
        List<UserEntity> users = new ArrayList<>();

        notificationService.createNotificationsForUsers(
            NotificationType.TICKET_CREATED,
            "New Ticket",
            "A new ticket has been created",
            users,
            testDepartment,
            1L
        );

        verify(notificationRepository, never()).save(any());
    }

    @Test
    void testRegisterUserConnection_ShouldReturnSseEmitter() {
        SseEmitter emitter = notificationService.registerUserConnection(1);

        assertNotNull(emitter);
        verifyNoInteractions(notificationRepository);
    }

    @Test
    void testGetNotificationsForUser_WhenExists_ShouldReturnNotifications() {
        List<Notification> notifications = new ArrayList<>();
        notifications.add(testNotification);

        when(notificationRepository.findByUserIdOrderByCreatedAtDesc(1)).thenReturn(notifications);

        List<NotificationDto> result = notificationService.getNotificationsForUser(1);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("New Ticket", result.get(0).getTitle());
    }

    @Test
    void testGetNotificationsForUser_WhenNoNotifications_ShouldReturnEmptyList() {
        when(notificationRepository.findByUserIdOrderByCreatedAtDesc(1)).thenReturn(new ArrayList<>());

        List<NotificationDto> result = notificationService.getNotificationsForUser(1);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void testGetUnreadCount_ShouldReturnCount() {
        when(notificationRepository.countByUserIdAndIsReadFalse(1)).thenReturn(5L);

        Long result = notificationService.getUnreadCount(1);

        assertEquals(5L, result);
    }

    @Test
    void testGetUnreadCount_WhenNoUnread_ShouldReturnZero() {
        when(notificationRepository.countByUserIdAndIsReadFalse(1)).thenReturn(0L);

        Long result = notificationService.getUnreadCount(1);

        assertEquals(0L, result);
    }

    @Test
    void testMarkAsRead_WhenExists_ShouldMarkAsRead() {
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(testNotification));

        notificationService.markAsRead(1L);

        verify(notificationRepository, times(1)).save(testNotification);
        assertTrue(testNotification.getIsRead());
    }

    @Test
    void testMarkAsRead_WhenNotExists_ShouldDoNothing() {
        when(notificationRepository.findById(1L)).thenReturn(Optional.empty());

        notificationService.markAsRead(1L);

        verify(notificationRepository, never()).save(any());
    }

    @Test
    void testMarkAllAsRead_ShouldMarkAllAsRead() {
        List<Notification> notifications = new ArrayList<>();
        Notification n1 = Notification.builder().id(1L).isRead(false).build();
        Notification n2 = Notification.builder().id(2L).isRead(false).build();
        notifications.add(n1);
        notifications.add(n2);

        when(notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(1))
            .thenReturn(notifications);

        notificationService.markAllAsRead(1);

        verify(notificationRepository, times(1)).saveAll(notifications);
        assertTrue(n1.getIsRead());
        assertTrue(n2.getIsRead());
    }

    @Test
    void testMarkAllAsRead_WhenNoUnread_ShouldStillCallSaveAll() {
        when(notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(1))
            .thenReturn(new ArrayList<>());

        notificationService.markAllAsRead(1);

        // The service calls saveAll even with an empty list
        verify(notificationRepository, times(1)).saveAll(any());
    }

    @Test
    void testNotifyDepartmentUsers_WhenDepartmentIsNull_ShouldReturnEarly() {
        notificationService.notifyDepartmentUsers(
            NotificationType.PROJECT_CREATED,
            "New Project",
            "A new project has been created",
            null,
            1L
        );

        verify(notificationRepository, never()).save(any());
    }

    @Test
    void testNotifyDepartmentUsers_WhenDepartmentHasUsers_ShouldNotifyAll() {
        List<UserEntity> users = new ArrayList<>();
        users.add(testUser);
        testDepartment.setUsers(users);

        when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);

        notificationService.notifyDepartmentUsers(
            NotificationType.PROJECT_CREATED,
            "New Project",
            "A new project has been created",
            testDepartment,
            1L
        );

        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void testNotifyDepartmentUsers_WhenDepartmentHasNoUsers_ShouldNotNotify() {
        testDepartment.setUsers(new ArrayList<>());

        notificationService.notifyDepartmentUsers(
            NotificationType.PROJECT_CREATED,
            "New Project",
            "A new project has been created",
            testDepartment,
            1L
        );

        verify(notificationRepository, never()).save(any());
    }

    @Test
    void testNotifyDepartmentUsers_WhenDepartmentUsersNotInitialized_ShouldFetchFromRepository() {
        // Department with null users list
        Department deptWithoutUsers = Department.builder()
                .id(1L)
                .name("IT Department")
                .build();

        List<UserEntity> users = new ArrayList<>();
        users.add(testUser);
        when(userRepository.findAll()).thenReturn(users);
        when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);

        notificationService.notifyDepartmentUsers(
            NotificationType.PROJECT_CREATED,
            "New Project",
            "A new project has been created",
            deptWithoutUsers,
            1L
        );

        // Should fetch users from repository as fallback
        verify(userRepository, times(1)).findAll();
    }
}