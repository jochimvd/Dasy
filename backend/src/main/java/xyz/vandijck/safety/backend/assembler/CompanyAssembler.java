package xyz.vandijck.safety.backend.assembler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;
import xyz.vandijck.safety.backend.assembler.link.GuardedLink;
import xyz.vandijck.safety.backend.controller.CompanyController;
import xyz.vandijck.safety.backend.dto.CompanyDto;
import xyz.vandijck.safety.backend.entity.Company;
import xyz.vandijck.safety.backend.policy.CompanyPolicy;
import xyz.vandijck.safety.backend.request.CompanySearchRequest;
import xyz.vandijck.safety.backend.request.DeleteRequest;
import xyz.vandijck.safety.backend.service.UserService;

import java.util.Map;
import java.util.stream.Stream;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Assembler that converts Companies into CompanyDto's and vice versa
 * Also provides access to all allowed links
 */
@Component
public class CompanyAssembler extends BaseAssembler<Company, CompanyDto> {

    private final CompanyPolicy companyPolicy;

    @Autowired
    public CompanyAssembler(CompanyPolicy companyPolicy, UserService userService,
                            ObjectMapper objectMapper, ModelMapper modelMapper) {
        super(companyPolicy, CompanyDto.class, userService, objectMapper, modelMapper);
        this.companyPolicy = companyPolicy;
    }

    @Override
    protected Stream<GuardedLink> entityLinks(CompanyDto companyDto, Map<String, Object> extraInfo) {
        return Stream.of(
                GuardedLink.guard(
                        companyPolicy.allowGet(companyDto.getId()),
                        WebMvcLinkBuilder.linkTo(methodOn(CompanyController.class).findOne(companyDto.getId())).withSelfRel()
                ),
                GuardedLink.guard(
                        companyPolicy.allowDelete(companyDto.getId()),
                        linkTo(methodOn(CompanyController.class).archiveOrDelete(new DeleteRequest(), companyDto.getId())).withRel("delete")
                ),
                GuardedLink.guard(
                        companyPolicy.allowGetAll(new CompanySearchRequest()),
                        linkTo(methodOn(CompanyController.class).findAll(new CompanySearchRequest())).withRel("companies")
                )
        );
    }

    @Override
    protected WebMvcLinkBuilder findAllLinkBuilder() {
        return linkTo(methodOn(CompanyController.class).findAll(new CompanySearchRequest()));
    }

    @Override
    public Company convertToEntity(CompanyDto companyDto) {
        return modelMapper.map(companyDto, Company.class);
    }

    @Override
    public CompanyDto convertToDto(Company company) {
        return modelMapper.map(company, CompanyDto.class);
    }

}
