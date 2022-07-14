package xyz.vandijck.safety.repository;

import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import xyz.vandijck.safety.entity.note.Note;
import xyz.vandijck.safety.entity.tag.Tag;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {

	List<Note> findByTagsIn(Collection<Tag> tags);
	
}
