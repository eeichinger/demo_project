package entities;

import org.springframework.transaction.annotation.Transactional;

/**
 * TODO: describe purpose of class/interface
 */
@Transactional
public interface UserRepository {
    String getUserName(int id);
}
