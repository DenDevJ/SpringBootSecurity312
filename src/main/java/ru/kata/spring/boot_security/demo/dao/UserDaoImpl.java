package ru.kata.spring.boot_security.demo.dao;
import ru.kata.spring.boot_security.demo.model.User;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.NoResultException;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public class UserDaoImpl implements UserDao {
    @PersistenceContext
    private EntityManager entityManager;
    @Override
    public void save(User user) {
        entityManager.persist(user);
    }
    @Override
    public void update(User user) {
        entityManager.merge(user);
    }
    @Override
    public void delete(Long id) {
        User user = findById(id);
        if (user != null) {
            entityManager.remove(user);
        }
    }
    @Override
    public User findById(Long id) {
        return entityManager.find(User.class, id);
    }
    @Override
    public User findByEmail(String email) {
        try {
        return entityManager.createQuery(
                        "SELECT u FROM User u WHERE u.email = :email", User.class)
                .setParameter("email", email)
                .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
    @Override
    public List<User> findAll() {
        return entityManager.createQuery("SELECT u FROM User u", User.class)
                .getResultList();
    }
}