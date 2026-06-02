package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.dao.RoleDao;
import ru.kata.spring.boot_security.demo.model.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private RoleDao roleDao;

    @InjectMocks
    private RoleServiceImpl roleService;

    private Role adminRole;
    private Role userRole;

    @BeforeEach
    void setUp() {
        adminRole = new Role("ROLE_ADMIN");
        adminRole.setId(1L);

        userRole = new Role("ROLE_USER");
        userRole.setId(2L);
    }

    @Test
    void save_ShouldSaveRole() {
        // when
        roleService.save(adminRole);

        // then
        verify(roleDao, times(1)).save(adminRole);
    }

    @Test
    void update_ShouldUpdateRole() {
        // given
        adminRole.setName("ROLE_SUPER_ADMIN");

        // when
        roleService.update(adminRole);

        // then
        verify(roleDao, times(1)).update(adminRole);
    }

    @Test
    void delete_ShouldDeleteRole() {
        // given
        Long roleId = 1L;

        // when
        roleService.delete(roleId);

        // then
        verify(roleDao, times(1)).delete(roleId);
    }

    @Test
    void findById_ShouldReturnRole() {
        // given
        Long roleId = 1L;
        when(roleDao.findById(roleId)).thenReturn(adminRole);

        // when
        Role found = roleService.findById(roleId);

        // then
        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(1L);
        assertThat(found.getName()).isEqualTo("ROLE_ADMIN");
        verify(roleDao, times(1)).findById(roleId);
    }

    @Test
    void findById_WhenRoleNotFound_ShouldReturnNull() {
        // given
        Long roleId = 999L;
        when(roleDao.findById(roleId)).thenReturn(null);

        // when
        Role found = roleService.findById(roleId);

        // then
        assertThat(found).isNull();
        verify(roleDao, times(1)).findById(roleId);
    }

    @Test
    void findByName_ShouldReturnRole() {
        // given
        String roleName = "ROLE_ADMIN";
        when(roleDao.findByName(roleName)).thenReturn(adminRole);

        // when
        Role found = roleService.findByName(roleName);

        // then
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("ROLE_ADMIN");
        verify(roleDao, times(1)).findByName(roleName);
    }

    @Test
    void findAll_ShouldReturnListOfRoles() {
        // given
        List<Role> roles = Arrays.asList(adminRole, userRole);
        when(roleDao.findAll()).thenReturn(roles);

        // when
        List<Role> result = roleService.findAll();

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Role::getName)
                .containsExactly("ROLE_ADMIN", "ROLE_USER");
        verify(roleDao, times(1)).findAll();
    }

    @Test
    void findAllByIds_ShouldReturnSetOfRoles() {
        // given
        Set<Long> ids = new HashSet<>(Arrays.asList(1L, 2L));
        Set<Role> roles = new HashSet<>(Arrays.asList(adminRole, userRole));
        when(roleDao.findAllByIds(ids)).thenReturn(roles);

        // when
        Set<Role> result = roleService.findAllByIds(ids);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Role::getName)
                .containsExactlyInAnyOrder("ROLE_ADMIN", "ROLE_USER");
        verify(roleDao, times(1)).findAllByIds(ids);
    }
}