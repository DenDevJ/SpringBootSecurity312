package ru.kata.spring.boot_security.demo.dao;
import ru.kata.spring.boot_security.demo.model.Role;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.NoResultException;
import java.util.List;
import java.util.Set;
@Repository
public class RoleDaoImpl implements RoleDao {
    @PersistenceContext
    private EntityManager entityManager;
    @Override
    public void save(Role role) {
        entityManager.persist(role);
    }
    @Override
    public void update(Role role) {
        entityManager.merge(role);
    }
    @Override
    public void delete(Long id) {
        Role role = findById(id);
        if (role != null) {
            entityManager.remove(role);
        }
    }
    @Override
    public Role findById(Long id) {
        return entityManager.find(Role.class, id);
    }
    @Override
    public Role findByName(String name) {
        try {
            return entityManager.createQuery(
                            "SELECT r FROM Role r WHERE r.name = :name", Role.class)
                    .setParameter("name", name)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
    @Override
    public List<Role> findAll() {
        return entityManager.createQuery("SELECT r FROM Role r", Role.class)
                .getResultList();
    }
    @Override
    public Set<Role> findAllByIds(Set<Long> ids) {
        return Set.copyOf(entityManager.createQuery(
                        "SELECT r FROM Role r WHERE r.id IN :ids", Role.class)
                .setParameter("ids", ids)
                .getResultList());
    }
}