package xyz.vandijck.safety.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import xyz.vandijck.safety.backend.entity.Type;

public interface TypeRepository extends JpaRepository<Type, Long>
{

    Type findByName(String name);

}
