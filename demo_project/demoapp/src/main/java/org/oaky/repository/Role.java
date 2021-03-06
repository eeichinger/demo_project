package org.oaky.repository;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Role {

    @Id
	@Column(name = "ROLE_ID")
    private int key;
	@Column(name = "ROLE_NAME")
    private String name;

    public Role() {
    }

    public Role(int user_id, String user_name) {
        this.key = user_id;
        this.name = user_name;
    }

    public int getKey() {
        return key;
    }

    public String getName() {
        return name;
    }
}
