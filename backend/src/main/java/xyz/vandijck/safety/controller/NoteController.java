/*
 * Copyright 2014-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package xyz.vandijck.safety.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import javax.validation.Valid;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import xyz.vandijck.safety.entity.note.Note;
import xyz.vandijck.safety.entity.note.NoteInput;
import xyz.vandijck.safety.entity.note.NoteRepresentationModelAssembler;
import xyz.vandijck.safety.entity.note.NoteRepresentationModelAssembler.NoteModel;
import xyz.vandijck.safety.entity.tag.TagRepresentationModelAssembler;
import xyz.vandijck.safety.entity.tag.TagRepresentationModelAssembler.TagModel;
import xyz.vandijck.safety.service.NoteSevice;


@RestController
@RequestMapping("/notes")
public class NoteController {

	private final NoteSevice noteSevice;
	
	private final NoteRepresentationModelAssembler noteAssembler;
	
	private final TagRepresentationModelAssembler tagAssembler;

	NoteController(NoteSevice noteSevice, NoteRepresentationModelAssembler noteAssembler,
				   TagRepresentationModelAssembler tagAssembler) {
		this.noteSevice = noteSevice;
		this.noteAssembler = noteAssembler;
		this.tagAssembler = tagAssembler;
	}

	@RequestMapping(method = RequestMethod.GET)
	CollectionModel<NoteModel> all() {
		return noteAssembler.toCollectionModel(noteSevice.findAll());
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	NoteModel get(@PathVariable("id") long id) {
		return noteAssembler.toModel(noteSevice.findNoteById(id));
	}

	@ResponseStatus(HttpStatus.CREATED)
	@RequestMapping(method = RequestMethod.POST)
	HttpHeaders create(@Valid @RequestBody NoteInput noteInput) {
		Note note = noteSevice.create(noteInput);

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setLocation(linkTo(NoteController.class).slash(note.getId()).toUri());

		return httpHeaders;
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	void delete(@PathVariable("id") long id) {
		noteSevice.deleteById(id);
	}

	@RequestMapping(value = "/{id}/tags", method = RequestMethod.GET)
	public CollectionModel<TagModel> noteTags(@PathVariable("id") long id) {
		return tagAssembler.toCollectionModel(noteSevice.findNoteById(id).getTags());
	}

}
