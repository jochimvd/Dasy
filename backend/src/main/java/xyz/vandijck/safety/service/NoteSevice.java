package xyz.vandijck.safety.service;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriTemplate;
import xyz.vandijck.safety.entity.note.Note;
import xyz.vandijck.safety.entity.note.NoteInput;
import xyz.vandijck.safety.entity.tag.Tag;
import xyz.vandijck.safety.repository.NoteRepository;
import xyz.vandijck.safety.repository.TagRepository;

@Service
public class NoteSevice {

    private static final UriTemplate TAG_URI_TEMPLATE = new UriTemplate("/tags/{id}");

    @Autowired
    NoteRepository noteRepository;

    @Autowired
    TagRepository tagRepository;


    public List<Note> findAll() {
        return noteRepository.findAll();
    }

    public Note findNoteById(long id) {
        return noteRepository.findById(id)
                .orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Note not found with id: " + id) );
    }

    public void deleteById(long id) {
        noteRepository.deleteById(id);
    }

    public Note create(NoteInput noteInput) {
        Note note = new Note();
        note.setTitle(noteInput.getTitle());
        note.setBody(noteInput.getBody());
        note.setTags(getTags(noteInput.getTagUris()));

        return noteRepository.save(note);
    }

    private List<Tag> getTags(List<URI> tagLocations) {
        List<Tag> tags = new ArrayList<>(tagLocations.size());
        for (URI tagLocation: tagLocations) {
            Tag tag = this.tagRepository.findById(extractTagId(tagLocation));
            if (tag == null) {
                throw new IllegalArgumentException("The tag '" + tagLocation
                        + "' does not exist");
            }
            tags.add(tag);
        }
        return tags;
    }

    private long extractTagId(URI tagLocation) {
        try {
            String idString = TAG_URI_TEMPLATE.match(tagLocation.toASCIIString()).get("id");
            return Long.valueOf(idString);
        }
        catch (RuntimeException ex) {
            throw new IllegalArgumentException("The tag '" + tagLocation + "' is invalid");
        }
    }

}
