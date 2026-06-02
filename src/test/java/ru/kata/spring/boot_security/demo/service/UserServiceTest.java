package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.dao.UserDao;
import ru.kata.spring.boot_security.demo.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.core.userdetails.UserDetails;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserDao userDao;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private EntityManager entityManager;

    @Mock
    private TypedQuery<User> query;

    @InjectMocks
    private UserServiceImpl userService;

    private User admin;
    private User regularUser;

    @BeforeEach
    void setUp() {
        admin = new User("Admin", "admin@example.com", "admin123", 30);
        admin.setId(1L);

        regularUser = new User("User", "user@example.com", "user123", 25);
        regularUser.setId(2L);
    }

    @Test
    void save_ShouldEncodePasswordAndSaveUser() {
        // given
        String rawPassword = "admin123";
        String encodedPassword = "encodedPassword123";
        admin.setPassword(rawPassword);
        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);

        // when
        userService.save(admin);

        // then
        assertThat(admin.getPassword()).isEqualTo(encodedPassword);
        verify(passwordEncoder, times(1)).encode(rawPassword);
        verify(userDao, times(1)).save(admin);
    }

    @Test
    void update_WhenPasswordProvided_ShouldEncodeAndUpdate() {
        // given
        String newPassword = "newPassword123";
        String encodedPassword = "encodedNewPassword";
        admin.setPassword(newPassword);
        when(passwordEncoder.encode(newPassword)).thenReturn(encodedPassword);

        // when
        userService.update(admin);

        // then
        assertThat(admin.getPassword()).isEqualTo(encodedPassword);
        verify(userDao, times(1)).update(admin);
    }

    @Test
    void update_WhenPasswordEmpty_ShouldKeepExistingPassword() {
        // given
        String existingPassword = "existingEncodedPassword";
        admin.setPassword("");
        when(userService.findById(admin.getId())).thenReturn(admin);
        admin.setPassword(existingPassword);

        // when
        userService.update(admin);

        // then
        verify(userDao, times(1)).update(admin);
    }

    @Test
    void delete_ShouldDeleteUser() {
        // given
        Long userId = 1L;

        // when
        userService.delete(userId);

        // then
        verify(userDao, times(1)).delete(userId);
    }

    @Test
    void findById_ShouldReturnUser() {
        // given
        Long userId = 1L;
        when(entityManager.createQuery(anyString(), eq(User.class))).thenReturn(query);
        when(query.setParameter("id", userId)).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList(admin));

        // when
        User found = userService.findById(userId);

        // then
        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(1L);
        assertThat(found.getName()).isEqualTo("Admin");
        assertThat(found.getEmail()).isEqualTo("admin@example.com");
    }

    @Test
    void findByEmail_ShouldReturnUser() {
        // given
        String email = "admin@example.com";
        when(entityManager.createQuery(anyString(), eq(User.class))).thenReturn(query);
        when(query.setParameter("email", email)).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList(admin));

        // when
        User found = userService.findByEmail(email);

        // then
        assertThat(found).isNotNull();
        assertThat(found.getEmail()).isEqualTo(email);
        assertThat(found.getName()).isEqualTo("Admin");
    }

    @Test
    void findByEmail_WhenUserNotFound_ShouldReturnNull() {
        // given
        String email = "notfound@example.com";
        when(entityManager.createQuery(anyString(), eq(User.class))).thenReturn(query);
        when(query.setParameter("email", email)).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList());

        // when
        User found = userService.findByEmail(email);

        // then
        assertThat(found).isNull();
    }

    @Test
    void findAll_ShouldReturnListOfUsers() {
        // given
        List<User> users = Arrays.asList(admin, regularUser);
        when(entityManager.createQuery(anyString(), eq(User.class))).thenReturn(query);
        when(query.getResultList()).thenReturn(users);

        // when
        List<User> result = userService.findAll();

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(User::getName)
                .containsExactly("Admin", "User");
        assertThat(result).extracting(User::getEmail)
                .containsExactly("admin@example.com", "user@example.com");
    }

    @Test
    void loadUserByUsername_ShouldReturnUserDetails() {
        // given
        String email = "admin@example.com";
        when(entityManager.createQuery(anyString(), eq(User.class))).thenReturn(query);
        when(query.setParameter("email", email)).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList(admin));

        // when
        UserDetails userDetails = userService.loadUserByUsername(email);

        // then
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(email);
        assertThat(userDetails.getAuthorities()).isNotNull();
    }
}