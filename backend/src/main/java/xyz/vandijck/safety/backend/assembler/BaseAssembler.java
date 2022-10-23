package xyz.vandijck.safety.backend.assembler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Iterables;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.*;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import xyz.vandijck.safety.backend.assembler.link.GuardedLink;
import xyz.vandijck.safety.backend.controller.RootController;
import xyz.vandijck.safety.backend.controller.exceptions.InternalJsonProcessingException;
import xyz.vandijck.safety.backend.entity.Role;
import xyz.vandijck.safety.backend.entity.UniqueEntity;
import xyz.vandijck.safety.backend.entity.User;
import xyz.vandijck.safety.backend.policy.BasePolicy;
import xyz.vandijck.safety.backend.request.SearchRequest;
import xyz.vandijck.safety.backend.service.SearchDTO;
import xyz.vandijck.safety.backend.service.UserService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.LongFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


/**
 * An abstract class providing conversion methods between the database entity, the entityDTO and various representations.
 *
 * @param <Entity>    The database entity class
 * @param <EntityDTO> The DTO entity class
 */
public abstract class BaseAssembler<Entity, EntityDTO extends UniqueEntity> implements RepresentationModelAssembler<Entity, EntityModel<EntityDTO>> {

    private final Class<EntityDTO> dtoClass;
    protected final UserService userService;

    // JSON (de)serialization
    protected ObjectMapper objectMapper;
    protected ModelMapper modelMapper;

    // Policy for the entity to obtain roles
    protected BasePolicy policy;

    public BaseAssembler(BasePolicy policy, Class<EntityDTO> dtoClass, UserService userService,
                         ObjectMapper objectMapper, ModelMapper modelMapper) {
        this.policy = policy;
        this.userService = userService;
        this.dtoClass = dtoClass;
        this.objectMapper = objectMapper;
        this.modelMapper = modelMapper;
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    /**
     * Returns the current user
     *
     * @return The current user.
     */
    protected User getCurrentUser() {
        return userService.getCurrentUser();
    }

    /**
     * Extra links that have to be added to the collectionModel of this entity, such as POST
     */
    protected Stream<GuardedLink> collectionLinks() {
        return Stream.of(
                GuardedLink.guard(
                        policy.allowPublic(),
                        WebMvcLinkBuilder.linkTo(methodOn(RootController.class).index()).withRel("root")
                )
        );
    }

    /**
     * @return A link builder for searching
     */
    protected abstract WebMvcLinkBuilder findAllLinkBuilder();

    /**
     * Generates all links that grant access to useful operations on this entity like PATCH, DELETE, ...
     *
     * @param entity    The entity for which we want the links
     * @param extraInfo extra information that might be needed to construct links
     * @return All allowed links
     */
    protected abstract Stream<GuardedLink> entityLinks(EntityDTO entity, Map<String, Object> extraInfo);

    /**
     * Utility method converting an entity to a entityDTO
     *
     * @param entity The entity you want to convert
     * @return The entityDTO representation.
     */
    public abstract EntityDTO convertToDto(Entity entity);

    /**
     * Utility method converting an entityDTO to an entity
     *
     * @param dto The DTO you want to convert
     * @return The entity
     */
    public abstract Entity convertToEntity(EntityDTO dto);

    /**
     * Generates a HAL-JSON representation with all the needed links
     * Filters the dto based on the user's role.
     *
     * @param entity The entity with the needed information you want to have a representation of.
     * @return The HAL-JSON representation.
     */
    @Override
    public EntityModel<EntityDTO> toModel(Entity entity) {
        return toModel(entity, policy::roleFor, Collections.emptyMap());
    }

    /**
     * Same as {@link BaseAssembler#toModel} but with an extra roleFunction that allows you to overwrite the basic
     * {@link BasePolicy#roleFor} function call used to filter the fields.
     *
     * @param entity       The entity with the needed information you want to have a representation of.
     * @param roleFunction the function used to get the relevant role for filtering the fields
     * @param extraInfo    Extra information that might be needed while construction the links of this entity
     * @return The HAL-JSON representation.
     */
    public EntityModel<EntityDTO> toModel(Entity entity, LongFunction<Role> roleFunction, Map<String, Object> extraInfo) {
        EntityDTO dto = convertToDto(entity);
        EntityDTO filteredDTO = filterDto(convertToDto(entity), roleFunction);
        return EntityModel.of(filteredDTO, GuardedLink.filter(entityLinks(dto, extraInfo)));
    }

    /**
     * Same as {@link BaseAssembler#filterDto} but overwrite the basic {@link BaseAssembler#policy#roleFor} call
     *
     * @param dto          The dto you want to filter.
     * @param roleFunction the function used to get the relevant role for filtering the fields
     * @return The filtered dto.
     */
    public EntityDTO filterDto(EntityDTO dto, LongFunction<Role> roleFunction) {
        EntityDTO filteredDTO = dto;
        if (policy != null) { // Check if there is a policy in place
            Role role = roleFunction.apply(dto.getId());
            Class<?> view = Views.getMapping().getOrDefault(role, Views.Reader.class); // Get the corresponding view
            try {
                // Convert to string representation belonging to the correct view
                String filtered = objectMapper.writerWithView(view).writeValueAsString(dto);
                filteredDTO = objectMapper.readValue(filtered, dtoClass);
            } catch (JsonProcessingException ex) {
                throw new InternalJsonProcessingException(ex.getMessage());
            }
        }
        return filteredDTO;
    }

    /**
     * Creates a Collection model from an iterable of the entity
     *
     * @param entities The iterable of entities you want represented
     * @return A collectionModel representing all the given entities
     */
    @Override
    public CollectionModel<EntityModel<EntityDTO>> toCollectionModel(
            Iterable<? extends Entity> entities) {
        return toCollectionModelWithIterables(entities, null, Collections.emptyList());
    }

    /**
     * Same as {@link #toCollectionModel(Iterable)} but with extra links
     *
     * @param entities The entities you want represented
     * @param extra    The extra links you want to add to the representation
     * @return The representation
     */
    public CollectionModel<EntityModel<EntityDTO>> toCollectionModelWithIterables(
            Iterable<? extends Entity> entities,
            PagedModel.PageMetadata metadata,
            Iterable<Link> extra) {
        return toCollectionModelWithIterables(entities, metadata, extra, policy::roleFor);
    }

    /**
     * Same as {@link #toCollectionModelWithIterables(Iterable, Iterable)} but overwrite the default
     * {@link BasePolicy#roleFor} call
     *
     * @param entities     The entities you want represented
     * @param extra        The extra links you want to add to the collection representation
     * @param roleFunction The function that will return the relevant role used for filtering
     * @return The representation
     */
    public CollectionModel<EntityModel<EntityDTO>> toCollectionModelWithIterables(
            Iterable<? extends Entity> entities,
            PagedModel.PageMetadata metadata,
            Iterable<Link> extra,
            LongFunction<Role> roleFunction
    ) {
        return toCollectionModelWithIterables(entities, metadata, extra, roleFunction, Collections.emptyMap());
    }

    /**
     * Same as {@link #toCollectionModelWithIterables(Iterable, Iterable, LongFunction)} but with an extra map that will
     * eventually be passed to {@link #entityLinks(UniqueEntity, Map)}
     *
     * @param entities     The entities you want represented
     * @param extra        The extra links you want to add to the collection representation
     * @param roleFunction The function that will return the relevant role used for filtering
     * @param extraInfo    Extra information that will be used in the construction of the individual links of the entity
     * @return The representation
     */
    public CollectionModel<EntityModel<EntityDTO>> toCollectionModelWithIterables(
            Iterable<? extends Entity> entities,
            PagedModel.PageMetadata metadata,
            Iterable<Link> extra,
            LongFunction<Role> roleFunction,
            Map<String, Object> extraInfo
    ) {
        List<EntityModel<EntityDTO>> list = StreamSupport.stream(entities.spliterator(), false)
                .map(entity -> this.toModel(entity, roleFunction, extraInfo))
                .collect(Collectors.toList());
        Iterable<Link> links = Iterables.concat(GuardedLink.filter(collectionLinks()), extra);

        if (metadata != null) {
            return PagedModel.of(list, metadata, links);
        }

        return CollectionModel.of(list, links);
    }

    /**
     * Generates a collectionModel from a search DTO
     *
     * @param dto The DTO containing the needed information
     * @param sr  The search request
     * @return A collection model
     */
    public CollectionModel<EntityModel<EntityDTO>> toCollectionModelFromSearch(
            SearchDTO<Entity> dto, SearchRequest sr) {
        PagedModel.PageMetadata metadata = new PagedModel.PageMetadata(
                sr.getSize(), sr.getPage(), dto.getTotalElements(), dto.getTotalPages());

        return toCollectionModelFromSearch(dto, metadata, sr);
    }

    public CollectionModel<EntityModel<EntityDTO>> toCollectionModelFromSearch(
            SearchDTO<Entity> dto, SearchRequest sr, Pageable pageable) {
        PagedModel.PageMetadata metadata = new PagedModel.PageMetadata(
                pageable.getPageSize(), pageable.getPageNumber(), dto.getTotalElements(), dto.getTotalPages());

        return toCollectionModelFromSearch(dto, metadata, sr);
    }

    public CollectionModel<EntityModel<EntityDTO>> toCollectionModelFromSearch(
            SearchDTO<Entity> dto, PagedModel.PageMetadata metadata, SearchRequest sr) {
        List<Link> links = new ArrayList<>();

        WebMvcLinkBuilder findAll = findAllLinkBuilder();

        links.add(sr.createLink(IanaLinkRelations.SELF, findAll.toUriComponentsBuilder()));
        links.add(sr.createLink(IanaLinkRelations.FIRST, findAll.toUriComponentsBuilder(), 0));
        links.add(sr.createLink(IanaLinkRelations.LAST, findAll.toUriComponentsBuilder(), Math.max(0, dto.getTotalPages() - 1)));

        if (sr.getPage() < dto.getTotalPages() - 1)
            links.add(sr.createLink(IanaLinkRelations.NEXT, findAll.toUriComponentsBuilder(), sr.getPage() + 1));

        if (sr.getPage() > 0 && sr.getPage() < dto.getTotalPages())
            links.add(sr.createLink(IanaLinkRelations.PREV, findAll.toUriComponentsBuilder(), sr.getPage() - 1));

        return toCollectionModelWithIterables(dto.getList(), metadata, links);
    }

}
