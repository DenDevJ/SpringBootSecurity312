package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.model.Role;
import java.util.Set;

public interface UserRoleService {
    void addRoleToUser(Long userId, Long roleId);
    void removeRoleFromUser(Long userId, Long roleId);
    void setUserRoles(Long userId, Set<Long> roleIds);
    Set<Role> getUserRoles(Long userId);
}