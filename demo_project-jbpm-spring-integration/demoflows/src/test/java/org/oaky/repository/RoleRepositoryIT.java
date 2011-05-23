package org.oaky.repository;

import org.junit.Before;
import org.oaky.repository.RoleRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.io.Serializable;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/integration-test-context.xml")
@Transactional
public class RoleRepositoryIT implements Serializable {

    @Autowired
	RoleRepository roleRepository;

    @Autowired
    @Qualifier("demoDS")
    public void setDataSource(DataSource ds) {
	    jdbc = new SimpleJdbcTemplate(ds);
    }

    private SimpleJdbcTemplate jdbc;

    @Before
    public void setUp() {
        jdbc.update("insert into ROLE(role_id,role_name) VALUES(?,?)", 999, "TESTROLE");
    }
    
    @Test
    public void should_read_correct_role() throws Exception {
        Assert.assertEquals("TESTROLE", roleRepository.getRole(999).getName());
    }

    @BeforeTransaction
    public void beforeTransaction() {
	    jdbc.update("insert into ROLE(role_id,role_name) VALUES(?,?)", 3, "SysAdmin");
    }

    @AfterTransaction
    public void afterTransaction() {
        int recs = jdbc.queryForInt("select count(*) from ROLE");
        Assert.assertEquals(1, recs);
    }
}
