package xyz.vandijck.safety.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import xyz.vandijck.safety.backend.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>
{
    /**
     * Find a user by its email address.
     *
     * @param email The email address.
     * @return The user instance if found.
     */
    User findByEmail(String email);

}
