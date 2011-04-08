package entities;


import org.springframework.transaction.annotation.Transactional;

import javax.persistence.PersistenceContext;

@Transactional
public class JpaRoleRepository implements RoleRepository {

    @PersistenceContext
    private javax.persistence.EntityManager entityManager;

    public Role getRole(int id) {
		return entityManager.find(Role.class, id);
//        Map<String,Object> args = new HashMap();
//        args.put("user_id", 1);
//        Query q = entityManager.createQuery("select u from UserData u where u.user_id=:user_id");
//        q.setParameter("user_id", id);
//        return ((Role)q.getSingleResult()).getName();
//        return jdbc.queryForObject("SELECT user_name FROM Role WHERE user_id=:user_id", String.class, args);
    }
}
