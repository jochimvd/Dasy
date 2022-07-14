package xyz.vandijck.safety.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import javax.validation.Valid;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import xyz.vandijck.safety.entity.note.NoteRepresentationModelAssembler;
import xyz.vandijck.safety.entity.note.NoteRepresentationModelAssembler.NoteModel;
import xyz.vandijck.safety.entity.tag.Tag;
import xyz.vandijck.safety.entity.tag.TagInput;
import xyz.vandijck.safety.entity.tag.TagRepresentationModelAssembler;
import xyz.vandijck.safety.entity.tag.TagRepresentationModelAssembler.TagModel;
import xyz.vandijck.safety.exception.ResourceDoesNotExistException;
import xyz.vandijck.safety.repository.TagRepository;


@RestController
@RequestMapping("tags")
public class TagController {

	private final TagRepository repository;
	
	private final TagRepresentationModelAssembler tagAssembler;
	
	private final NoteRepresentationModelAssembler noteAssembler;

	TagController(TagRepository repository, TagRepresentationModelAssembler tagAssembler,
                  NoteRepresentationModelAssembler noteAssembler) {
		this.repository = repository;
		this.tagAssembler = tagAssembler;
		this.noteAssembler = noteAssembler;
	}

	@RequestMapping(method = RequestMethod.GET)
	CollectionModel<TagModel> all() {
		return this.tagAssembler.toCollectionModel(this.repository.findAll());
	}

	@ResponseStatus(HttpStatus.CREATED)
	@RequestMapping(method = RequestMethod.POST)
	HttpHeaders create(@Valid @RequestBody TagInput tagInput) {
		Tag tag = new Tag();
		tag.setName(tagInput.getName());

		this.repository.save(tag);

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setLocation(linkTo(TagController.class).slash(tag.getId()).toUri());

		return httpHeaders;
	}

	@ResponseStatus(HttpStatus.NO_CONTENT)
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	HttpHeaders update(@PathVariable("id") long id, @Valid @RequestBody TagInput tagInput) {
		Tag tag = findTagById(id);
		tag.setName(tagInput.getName());

		this.repository.save(tag);

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setLocation(linkTo(TagController.class).slash(tag.getId()).toUri());

		return httpHeaders;
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	void delete(@PathVariable("id") long id) {
		this.repository.deleteById(id);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public TagModel get(@PathVariable("id") long id) {
		return tagAssembler.toModel(findTagById(id));
	}

	@RequestMapping(value = "/{id}/notes", method = RequestMethod.GET)
	public CollectionModel<NoteModel> tagNotes(@PathVariable("id") long id) {
		return noteAssembler.toCollectionModel(findTagById(id).getNotes());
	}

	private Tag findTagById(long id) {
		Tag tag = this.repository.findById(id);
		if (tag == null) {
			throw new ResourceDoesNotExistException();
		}
		return tag;
	}
}
