package entities;


import org.springframework.transaction.annotation.Transactional;

import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.HashMap;
import java.util.Map;

@Transactional
public class JpaUserRepository implements UserRepository {

    @PersistenceContext
    private javax.persistence.EntityManager entityManager;

    public JpaUserRepository() {
    }

    public String getUserName(int id) {
        Map<String,Object> args = new HashMap();
        args.put("user_id", 1);
        Query q = entityManager.createQuery("select u from UserData u where u.user_id=:user_id");
        q.setParameter("user_id", id);
        return ((User)q.getSingleResult()).getUser_name();
//        return jdbc.queryForObject("SELECT user_name FROM User WHERE user_id=:user_id", String.class, args);
    }
}
