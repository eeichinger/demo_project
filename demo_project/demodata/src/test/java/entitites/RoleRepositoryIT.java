package entitites;

import entities.RoleRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.io.Serializable;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/integration-test-context.xml")
@Transactional
public class RoleRepositoryIT implements Serializable {

    @Autowired
	RoleRepository roleRepository;

//    @PersistenceContext(unitName = "demoPU")
//    EntityManager entityManager;

    @Autowired
    DataSource dataSource;
    
    @Test
    public void should_be_emtpy() {

//        entityManager.persist(new Role(3, "myusername"));
//        entityManager.flush();
//        entityManager.clear();

        Assert.assertNotNull(roleRepository.getRole(3));
    }

    @AfterTransaction
    public void afterTransaction() {
        SimpleJdbcTemplate jdbc = new SimpleJdbcTemplate(dataSource);
        int recs = jdbc.queryForInt("select count(*) from ROLE");
        Assert.assertEquals(7, recs);
    }
}
