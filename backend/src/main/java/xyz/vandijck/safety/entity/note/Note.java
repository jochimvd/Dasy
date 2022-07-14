package xyz.vandijck.safety.entity.note;

import java.util.List;
import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;
import xyz.vandijck.safety.entity.tag.Tag;

@Entity
@Getter
@Setter
public class Note {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	private String title;

	private String body;

	@ManyToMany
	private List<Tag> tags;

}
