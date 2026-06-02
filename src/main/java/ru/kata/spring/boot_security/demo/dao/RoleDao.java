package ru.kata.spring.boot_security.demo.dao;
import ru.kata.spring.boot_security.demo.model.Role;
import java.util.List;
import java.util.Set;
public interface RoleDao {
    void save(Role role);
    void update(Role role);
    void delete(Long id);
    Role findById(Long id);
    Role findByName(String name);
    List<Role> findAll();
    Set<Role> findAllByIds(Set<Long> ids);
}