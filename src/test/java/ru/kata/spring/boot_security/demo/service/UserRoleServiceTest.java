package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRoleServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private RoleService roleService;

    @InjectMocks
    private UserRoleServiceImpl userRoleService;

    private User admin;
    private User regularUser;
    private Role adminRole;
    private Role userRole;

    @BeforeEach
    void setUp() {
        admin = new User("Admin", "admin@example.com", "admin123", 30);
        admin.setId(1L);
        admin.setRoles(new HashSet<>());

        regularUser = new User("User", "user@example.com", "user123", 25);
        regularUser.setId(2L);
        regularUser.setRoles(new HashSet<>());

        adminRole = new Role("ROLE_ADMIN");
        adminRole.setId(1L);

        userRole = new Role("ROLE_USER");
        userRole.setId(2L);
    }

    @Test
    void addRoleToUser_ShouldAddRole() {
        // given
        Long userId = 1L;
        Long roleId = 1L;
        when(userService.findById(userId)).thenReturn(admin);
        when(roleService.findById(roleId)).thenReturn(adminRole);

        // when
        userRoleService.addRoleToUser(userId, roleId);

        // then
        assertThat(admin.getRoles()).contains(adminRole);
        verify(userService, times(1)).update(admin);
    }

    @Test
    void addRoleToUser_WhenUserNotFound_ShouldNotAddRole() {
        // given
        Long userId = 999L;
        Long roleId = 1L;
        when(userService.findById(userId)).thenReturn(null);

        // when
        userRoleService.addRoleToUser(userId, roleId);

        // then
        verify(userService, never()).update(any());
    }

    @Test
    void addRoleToUser_WhenRoleNotFound_ShouldNotAddRole() {
        // given
        Long userId = 1L;
        Long roleId = 999L;
        when(userService.findById(userId)).thenReturn(admin);
        when(roleService.findById(roleId)).thenReturn(null);

        // when
        userRoleService.addRoleToUser(userId, roleId);

        // then
        assertThat(admin.getRoles()).doesNotContain(adminRole);
        verify(userService, never()).update(any());
    }

    @Test
    void removeRoleFromUser_ShouldRemoveRole() {
        // given
        Long userId = 1L;
        Long roleId = 1L;
        admin.getRoles().add(adminRole);
        when(userService.findById(userId)).thenReturn(admin);
        when(roleService.findById(roleId)).thenReturn(adminRole);

        // when
        userRoleService.removeRoleFromUser(userId, roleId);

        // then
        assertThat(admin.getRoles()).doesNotContain(adminRole);
        verify(userService, times(1)).update(admin);
    }

    @Test
    void setUserRoles_ShouldReplaceAllRoles() {
        // given
        Long userId = 1L;
        Set<Long> roleIds = new HashSet<>(Set.of(1L, 2L));
        Set<Role> roles = new HashSet<>(Set.of(adminRole, userRole));

        when(userService.findById(userId)).thenReturn(admin);
        when(roleService.findAllByIds(roleIds)).thenReturn(roles);

        // when
        userRoleService.setUserRoles(userId, roleIds);

        // then
        assertThat(admin.getRoles()).hasSize(2);
        assertThat(admin.getRoles()).containsExactlyInAnyOrder(adminRole, userRole);
        verify(userService, times(1)).update(admin);
    }

    @Test
    void getUserRoles_ShouldReturnUserRoles() {
        // given
        Long userId = 1L;
        admin.getRoles().addAll(Set.of(adminRole, userRole));
        when(userService.findById(userId)).thenReturn(admin);

        // when
        Set<Role> roles = userRoleService.getUserRoles(userId);

        // then
        assertThat(roles).hasSize(2);
        assertThat(roles).extracting(Role::getName)
                .containsExactlyInAnyOrder("ROLE_ADMIN", "ROLE_USER");
    }

    @Test
    void getUserRoles_WhenUserNotFound_ShouldReturnEmptySet() {
        // given
        Long userId = 999L;
        when(userService.findById(userId)).thenReturn(null);

        // when
        Set<Role> roles = userRoleService.getUserRoles(userId);

        // then
        assertThat(roles).isEmpty();
    }
}