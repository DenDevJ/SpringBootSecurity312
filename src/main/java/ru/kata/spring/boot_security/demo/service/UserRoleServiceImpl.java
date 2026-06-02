package ru.kata.spring.boot_security.demo.service;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashSet;
import java.util.Set;

@Service
public class UserRoleServiceImpl implements UserRoleService {
    private final UserService userService;
    private final RoleService roleService;

    public UserRoleServiceImpl(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @Override
    @Transactional
    public void addRoleToUser(Long userId, Long roleId) {
        User user = userService.findById(userId);
        Role role = roleService.findById(roleId);
        if (user != null && role != null) {
            user.getRoles().add(role);
            userService.update(user);
        }
    }

    @Override
    @Transactional
    public void removeRoleFromUser(Long userId, Long roleId) {
        User user = userService.findById(userId);
        Role role = roleService.findById(roleId);
        if (user != null && role != null) {
            user.getRoles().remove(role);
            userService.update(user);
        }
    }

    @Override
    @Transactional
    public void setUserRoles(Long userId, Set<Long> roleIds) {
        User user = userService.findById(userId);
        if (user != null && roleIds != null) {
            Set<Role> roles = new HashSet<>(roleService.findAllByIds(roleIds));
            user.setRoles(roles);
            userService.update(user);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Set<Role> getUserRoles(Long userId) {
        User user = userService.findById(userId);
        return user != null ? user.getRoles() : new HashSet<>();
    }
}