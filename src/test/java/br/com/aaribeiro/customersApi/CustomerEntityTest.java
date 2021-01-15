package br.com.aaribeiro.customersApi;

import br.com.aaribeiro.customersApi.controller.CustomerController;
import br.com.aaribeiro.customersApi.controller.CustomerControllerAdvice;
import br.com.aaribeiro.customersApi.entity.CustomerEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.time.LocalDate;

public class CustomerEntityTest extends CustomerEntityApplicationTest {

    private MockMvc mockMvc;

    @Autowired
    private CustomerController customerController;

    @Autowired
    private CustomerControllerAdvice customerControllerAdvice;

    @Before
    public void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(this.customerController).setControllerAdvice(customerControllerAdvice).build();
    }

    private String getCustomerJson() throws JsonProcessingException {
        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setName("Andrei");
        customerEntity.setAge(30);
        customerEntity.setCpf("06624150947");
        customerEntity.setDateOfBirth(LocalDate.of(1990, 05, 03));

        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(customerEntity);
    }

    @Test
    public void mustRegisterCorrectCustomer_ReturnStatusCode201() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                .post("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.getCustomerJson())
        )
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    public void mustRegisterIncorrectCustomer_ReturnStatusCode400() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                .post("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}")
        )
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void mustUpdateExistingtCustomer_ReturnStatusCode200() throws Exception {
        /*POST Customer*/
        ObjectMapper mapper = new ObjectMapper();
        MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders
                .post("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.getCustomerJson())).andReturn();

        CustomerEntity customerEntity = mapper.readValue(result.getResponse().getContentAsString(), CustomerEntity.class);
        customerEntity.setName("New customer");

        /*PUT Customer*/
        this.mockMvc.perform(MockMvcRequestBuilders
                .put("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(customerEntity))
        )
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void mustConsultCustomerThatExists_ReturnStatusCode200() throws Exception {
        /*POST Customer*/
        this.mockMvc.perform(MockMvcRequestBuilders
                .post("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.getCustomerJson())
        );

        /*GET Customer*/
        this.mockMvc.perform(MockMvcRequestBuilders
                .get("/customers/1")
        )
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void mustConsultCustomerThatDoesNotExists_ReturnStatusCode404() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                .get("/customers/99")
        ).andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void mustDeleteAnCustomerThatExists_ReturnStatusCode200() throws Exception {
        /*POST Customer*/
        this.mockMvc.perform(MockMvcRequestBuilders
                .post("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.getCustomerJson())
        );

        this.mockMvc.perform(MockMvcRequestBuilders
                .post("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.getCustomerJson())
        );

        /*GET Customer*/
        this.mockMvc.perform(MockMvcRequestBuilders
                .delete("/customers/2")
        ).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void mustDeleteCustomerThatDoesNotExist_RetornarStatusCode404() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                .delete("/customers/99")
        ).andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void mustChangeExistingtCustomer_ReturnStatusCode200() throws Exception {
        /*POST Customer*/
        this.mockMvc.perform(MockMvcRequestBuilders
                .post("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.getCustomerJson())
        );

        String patch = "[{\"op\":\"replace\", \"path\":\"/name\", \"value\":\"Andrei Antonio Ribeiro\"}]";

        /*PATCH Customer*/
        this.mockMvc.perform(MockMvcRequestBuilders
                .patch("/customers/1")
                .contentType("application/json-patch+json")
                .content(patch)
        ).andExpect(MockMvcResultMatchers.status().isOk());
    }
}
