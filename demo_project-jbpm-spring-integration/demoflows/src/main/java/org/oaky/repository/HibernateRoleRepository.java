package org.oaky.repository;

import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

@Transactional
public class HibernateRoleRepository extends HibernateDaoSupport implements RoleRepository {

	public HibernateRoleRepository(SessionFactory sessionFactory) {
		Assert.notNull(sessionFactory);
		super.setSessionFactory(sessionFactory);
	}

	public Role getRole(int id) {
		return (Role) super.getSession(false).get(Role.class, id);
	}
}
