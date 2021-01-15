package br.com.aaribeiro.customersApi.service;

import br.com.aaribeiro.customersApi.entity.CustomerEntity;
import br.com.aaribeiro.customersApi.repository.CustomerRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    public Optional<CustomerEntity> getCustomer(int id) throws Exception {
        return customerRepository.findById(id);
    }

    public Page<CustomerEntity> getCustomers(Pageable pageable) throws Exception {
        return customerRepository.findAll(pageable);
    }

    public Page<CustomerEntity> find(String name, String cpf, Pageable pageable) throws Exception {
        if ((name != null && !name.isEmpty()) && (cpf != null && !cpf.isEmpty())) {
            return customerRepository.findByNameAndCpfCustom(name.toUpperCase(), cpf, pageable);
        } else if (cpf != null && !cpf.isEmpty()) {
            return customerRepository.findByCpfContaining(cpf, pageable);
        } else if (name != null && !name.isEmpty()) {
            return customerRepository.findByNameIgnoreCaseContainingOrderByNameAsc(name, pageable);
        }
        return null;
    }

    public CustomerEntity updateCustomer(CustomerEntity customerEntity) throws Exception {
        return customerRepository.save(customerEntity);
    }

    public void deleteMovie(int id) throws Exception {
        customerRepository.deleteById(id);
    }

    public CustomerEntity setCustomer(CustomerEntity movieJson) throws Exception {
        this.calcularIdade(movieJson);
        return customerRepository.save(movieJson);
    }

    private void calcularIdade(CustomerEntity movieJson) throws Exception {
        try {
            movieJson.setAge(LocalDate.now().getYear() - movieJson.getDateOfBirth().getYear());
        } catch (Exception e){
            throw new Exception("An error occurred while calculating age.");
        }
    }

    public CustomerEntity applyPatchToCustomer(JsonPatch patch, CustomerEntity targetCustomer) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode patched = patch.apply(mapper.convertValue(targetCustomer, JsonNode.class));
        return mapper.treeToValue(patched, CustomerEntity.class);
    }
}