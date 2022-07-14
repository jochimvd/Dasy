package xyz.vandijck.safety.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import xyz.vandijck.safety.entity.tag.Tag;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

	Tag findById(long id); // TODO make optional

}
