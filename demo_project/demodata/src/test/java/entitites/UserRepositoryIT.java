package entitites;

import entities.User;
import entities.UserRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/integration-test-context.xml")
@Transactional
public class UserRepositoryIT {

    @Autowired
    UserRepository userRepository;

    @PersistenceContext(unitName = "demoPU")
    EntityManager entityManager;

    @Autowired
    DataSource dataSource;
    
    @Test
    public void should_be_emtpy() {

        entityManager.persist(new User(3, "myusername"));
        entityManager.flush();
        entityManager.clear();

        Assert.assertNotNull(userRepository.getUserName(3));
    }

    @AfterTransaction
    public void afterTransaction() {
        SimpleJdbcTemplate jdbc = new SimpleJdbcTemplate(dataSource);
        int recs = jdbc.queryForInt("select count(*) from UserData");
        Assert.assertEquals(0, recs);
    }
}
