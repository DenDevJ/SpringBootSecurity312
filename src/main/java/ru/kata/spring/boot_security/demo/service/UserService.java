package ru.kata.spring.boot_security.demo.service;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.model.Role;
import org.springframework.security.core.userdetails.UserDetailsService;
import java.util.List;
import java.util.Set;
public interface UserService extends UserDetailsService {
    void save(User user);
    void update(User user);
    void delete(Long id);
    User findById(Long id);
    User findByEmail(String email);
    List<User> findAll();
    void addRoleToUser(Long userId, Long roleId);
    void removeRoleFromUser(Long userId, Long roleId);
    void setUserRoles(Long userId, Set<Long> roleIds);
}