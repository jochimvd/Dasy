package xyz.vandijck.safety.entity.tag;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;
import xyz.vandijck.safety.controller.TagController;
import xyz.vandijck.safety.entity.tag.TagRepresentationModelAssembler.TagModel;

@Component
public class TagRepresentationModelAssembler extends RepresentationModelAssemblerSupport<Tag, TagModel> {

	TagRepresentationModelAssembler() {
		super(TagController.class, TagModel.class);
	}

	@Override
	public TagModel toModel(Tag entity) {
		TagModel model = new TagModel(entity);
		model.add(WebMvcLinkBuilder.linkTo(methodOn(TagController.class).get(entity.getId())).withSelfRel(),
			linkTo(methodOn(TagController.class).tagNotes(entity.getId())).withRel("tagged-notes"));
		return model;
	}
	
	@Override
	protected TagModel instantiateModel(Tag entity) {
		return new TagModel(entity);
	}

	@Relation(collectionRelation = "tags", itemRelation = "tag")
	public static class TagModel extends RepresentationModel<TagModel> {
		
		private final Tag tag;
		
		TagModel(Tag tag) {
			this.tag = tag;
		}
		
		public String getName() {
			return tag.getName();
		}
		
	}

}
