package xyz.vandijck.safety.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import xyz.vandijck.safety.backend.assembler.CompanyAssembler;
import xyz.vandijck.safety.backend.dto.CompanyDto;
import xyz.vandijck.safety.backend.entity.Company;
import xyz.vandijck.safety.backend.policy.CompanyPolicy;
import xyz.vandijck.safety.backend.request.CompanySearchRequest;
import xyz.vandijck.safety.backend.request.DeleteRequest;
import xyz.vandijck.safety.backend.service.CompanyService;
import xyz.vandijck.safety.backend.service.SearchDTO;

import javax.validation.Valid;

/**
 * Company controller, handles HTTP requests related to companies
 */
@RestController
@RequestMapping("/companies")
public class CompanyController implements BaseEntityController<CompanyDto, CompanySearchRequest> {

    @Autowired
    private CompanyService companyService;

    @Autowired
    private CompanyAssembler companyAssembler;

    @Autowired
    private CompanyPolicy companyPolicy;


    /**
     * Finds all companies that match the search parameters in CompanySearchRequest. In a normal HTTP request these parameters
     * wil come from all the parameters from a query link, eg "/companies?name=company1" will look for all the companies
     * with name "company1". See the openAPI specification for all the legal parameters.
     *
     * @param request A wrapper object containing all the needed information to build a query
     * @return A CollectionModel containing all the companies that match the search parameters
     */
    @GetMapping
    public CollectionModel<EntityModel<CompanyDto>> findAll(@Valid CompanySearchRequest request) {
        companyPolicy.authorizeGetAll(request);

        SearchDTO<Company> dto = companyService.findAll(request);
        return companyAssembler.toCollectionModelFromSearch(dto, request);
    }

    /**
     * Find a company by its id
     *
     * @param id The id you want to search for
     * @return An entitymodel of the company that matches that id.
     */
    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EntityModel<CompanyDto> findOne(@PathVariable long id) {
        companyPolicy.authorizeGet(id);
        return companyAssembler.toModel(companyService.findById(id));
    }


    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<String> archiveOrDelete(@RequestBody(required = false) @Valid DeleteRequest request, @PathVariable long id) {
        companyPolicy.authorizeDelete(id);
        companyService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}