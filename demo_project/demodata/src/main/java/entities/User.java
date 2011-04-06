package entities;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * TODO: describe purpose of class/interface
 */
@Entity(name="UserData")
public class User {

    @Id
    private int user_id;
    private String user_name;

    public User() {
    }

    public User(int user_id, String user_name) {
        this.user_id = user_id;
        this.user_name = user_name;
    }

    public int getUser_id() {
        return user_id;
    }

    public String getUser_name() {
        return user_name;
    }
}
