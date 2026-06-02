package ru.kata.spring.boot_security.demo.service;
import ru.kata.spring.boot_security.demo.dao.UserDao;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.model.Role;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Set;
import java.util.List;
import java.util.HashSet;
@Service
public class UserServiceImpl implements UserService, UserDetailsService {
    private final UserDao userDao;
    private final BCryptPasswordEncoder passwordEncoder;
    private final RoleService roleService;
    @PersistenceContext
    private EntityManager entityManager;
    public UserServiceImpl(UserDao userDao, BCryptPasswordEncoder passwordEncoder, RoleService roleService) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
    }
    @Override
    @Transactional
    public void save(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userDao.save(user);
    }
    @Override
    @Transactional
    public void update(User user) {
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            User existingUser = findById(user.getId());
            user.setPassword(existingUser.getPassword());
        }
        userDao.update(user);
    }
    @Override
    @Transactional
    public void delete(Long id) {
        userDao.delete(id);
    }
    @Override
    @Transactional(readOnly = true)
    public User findById(Long id) {
        return entityManager.createQuery(
                        "SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.id = :id", User.class)
                .setParameter("id", id)
                .getResultList()
                .stream()
                .findFirst()
                .orElse(null);
    }
    @Override
    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        return entityManager.createQuery(
                        "SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.email = :email", User.class)
                .setParameter("email", email)
                .getResultList()
                .stream()
                .findFirst()
                .orElse(null);
    }
    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return entityManager.createQuery(
                        "SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.roles", User.class)
                .getResultList();
    }
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
        return user;
    }
    @Override
    @Transactional
    public void addRoleToUser(Long userId, Long roleId) {
        User user = findById(userId);
        Role role = roleService.findById(roleId);
        if (user != null && role != null) {
            user.getRoles().add(role);
            userDao.update(user);
        }
    }

    @Override
    @Transactional
    public void removeRoleFromUser(Long userId, Long roleId) {
        User user = findById(userId);
        Role role = roleService.findById(roleId);
        if (user != null && role != null) {
            user.getRoles().remove(role);
            userDao.update(user);
        }
    }

    @Override
    @Transactional
    public void setUserRoles(Long userId, Set<Long> roleIds) {
        User user = findById(userId);
        if (user != null) {
            Set<Role> roles = new HashSet<>(roleService.findAllByIds(roleIds));
            user.setRoles(roles);
            userDao.update(user);
        }
    }
}