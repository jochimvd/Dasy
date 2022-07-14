package xyz.vandijck.safety.entity.tag;

import java.util.List;
import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;
import xyz.vandijck.safety.entity.note.Note;

@Entity
@Getter
@Setter
public class Tag {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	private String name;

	@ManyToMany(mappedBy = "tags")
	private List<Note> notes;

}
