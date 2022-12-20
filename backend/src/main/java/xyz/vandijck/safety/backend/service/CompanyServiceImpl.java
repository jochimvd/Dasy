package xyz.vandijck.safety.backend.service;

import com.github.fge.jsonpatch.JsonPatch;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.vandijck.safety.backend.controller.exceptions.BadRequestException;
import xyz.vandijck.safety.backend.controller.exceptions.CompanyNotFoundException;
import xyz.vandijck.safety.backend.entity.Company;
import xyz.vandijck.safety.backend.repository.CompanyRepository;
import xyz.vandijck.safety.backend.request.CompanySearchRequest;
import xyz.vandijck.safety.backend.request.DeleteRequest;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Service
@NoArgsConstructor
public class CompanyServiceImpl implements CompanyService {

    @Autowired
    private CompanyRepository companyRepository;

    @PersistenceContext
    private EntityManager entityManager;


    public CompanyServiceImpl(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @Override
    public SearchDTO<Company> findAll(CompanySearchRequest request) {
        return SearchHelper.findAll(request, entityManager, Company.class);
    }

    @Override
    public List<Company> findAll() {
        return companyRepository.findAll();
    }

    @Override
    public Company findById(long id) {
        return getByIdOrThrow(id);
    }

    @Override
    public Company findElseCreate(String name) {
        Company company = companyRepository.findByName(name);
        if(company == null){
            company = companyRepository.save(new Company().setName(name));
        }
        return company;
    }

    @Override
    public Company save(Company company) {
        return companyRepository.save(company);
    }

    @Override
    public void deleteById(long id) {
        companyRepository.delete(getByIdOrThrow(id));
    }

    @Override
    public boolean archiveOrDeleteById(long id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Company patch(JsonPatch patch, long id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Company getByIdOrThrow(long id) throws BadRequestException {
        Optional<Company> company = companyRepository.findById(id);
        if (company.isEmpty()) {
            throw new CompanyNotFoundException();
        }
        return company.get();
    }

    @Override
    public boolean archiveOrDeleteById(DeleteRequest request, long id) {
        throw new UnsupportedOperationException();
    }

}
