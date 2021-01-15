package br.com.aaribeiro.customersApi.controller;

import br.com.aaribeiro.customersApi.entity.CustomerEntity;
import br.com.aaribeiro.customersApi.service.CustomerService;
import com.github.fge.jsonpatch.JsonPatch;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/customers")
@Api(tags = "Customers")
public class CustomerController {

    @Autowired
    CustomerService customerService;

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get customer.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return customer."),
            @ApiResponse(code = 404, message = "Customer not found."),
            @ApiResponse(code = 500, message = "General processing errors .")
    })
    public ResponseEntity<CustomerEntity> getCustomer(@PathVariable int id) throws Exception {
        try {
            Optional<CustomerEntity> customerEntity = customerService.getCustomer(id);
            if (!customerEntity.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            return ResponseEntity.status(HttpStatus.OK).body(customerEntity.get());
        } catch (Exception e) {
            throw new Exception(e.getLocalizedMessage());
        }
    }

    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get customers with pagination.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return all customers."),
            @ApiResponse(code = 500, message = "General processing errors.")
    })
    public ResponseEntity<Page<CustomerEntity>> getCustomers(
            @RequestParam(value = "page", defaultValue = "0", required = false) int page,
            @RequestParam(value = "size", defaultValue = "10", required = false) int size) throws Exception {
        try {
            PageRequest pageable = PageRequest.of(page, size, Sort.Direction.ASC, "name");
            return ResponseEntity.status(HttpStatus.OK).body(customerService.getCustomers(pageable));
        } catch (Exception e){
            throw new Exception(e.getLocalizedMessage());
        }
    }

    @GetMapping(path = "/find", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Search by name or cpf (or containing parts) with pagination.")
    public ResponseEntity<Page<CustomerEntity>> find(
        @RequestParam(value = "name", required = false) String name,
        @RequestParam(value = "cpf", required = false) String cpf,
        @RequestParam(value = "page", defaultValue = "0", required = false) int page,
        @RequestParam(value = "size", defaultValue = "10", required = false) int size) throws Exception {
        PageRequest pageable = PageRequest.of(page, size, Sort.Direction.ASC, "name");
        return ResponseEntity.status(HttpStatus.OK).body(customerService.find(name, cpf, pageable));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Set customer.")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Customer created with successful."),
            @ApiResponse(code = 400, message = "Something wrong with your request."),
            @ApiResponse(code = 500, message = "General processing errors.")
    })
    public ResponseEntity<CustomerEntity> setCustomer(@RequestBody @Valid CustomerEntity customerEntity) throws Exception {
        return ResponseEntity.status(HttpStatus.CREATED).body(customerService.setCustomer(customerEntity));
    }

    @PatchMapping(value = "/{id}", consumes = "application/json-patch+json", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Change customer.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Customer change success"),
            @ApiResponse(code = 400, message = "Something wrong with your request."),
            @ApiResponse(code = 500, message = "General processing errors.")
    })
    public ResponseEntity<CustomerEntity> changeCustomer(@PathVariable int id, @RequestBody JsonPatch patch) throws Exception {
        Optional<CustomerEntity> customerEntity = customerService.getCustomer(id);
        if (customerEntity.isPresent()){
            CustomerEntity customerPatched = customerService.applyPatchToCustomer(patch, customerEntity.get());
            customerService.updateCustomer(customerPatched);
            return ResponseEntity.ok(customerPatched);
        }
        return null;
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Update customer.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Update the customers. If the customer does not exist, a new customer will be created."),
            @ApiResponse(code = 400, message = "Something wrong with your request.")
    })
    public ResponseEntity<CustomerEntity> updateCustomer(@RequestBody @Valid CustomerEntity customerEntity) throws Exception {
        return ResponseEntity.status(HttpStatus.OK).body(customerService.updateCustomer(customerEntity));
    }

    @DeleteMapping(value = "/{id}")
    @ApiOperation(value = "Delete customer.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Customer deleted"),
            @ApiResponse(code = 404, message = "Customer not found")
    })
    public ResponseEntity deleteCustomer(@PathVariable int id){
        try {
            customerService.deleteMovie(id);
            return ResponseEntity.status(HttpStatus.OK).body(null);
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}