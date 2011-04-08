package entities;

import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * TODO: describe purpose of class/interface
 */
@Transactional
public class HibernateRoleRepository extends HibernateDaoSupport implements RoleRepository {

	public HibernateRoleRepository(SessionFactory sessionFactory) {
		Assert.notNull(sessionFactory);
		super.setSessionFactory(sessionFactory);
	}

	public Role getRole(int id) {
		return super.getHibernateTemplate().get(Role.class, 1);
	}
}
