package br.com.aaribeiro.customersApi.repository;

import br.com.aaribeiro.customersApi.entity.CustomerEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, Integer>{

    @Query(value = "SELECT * FROM customers c WHERE UPPER(c.name) LIKE %:name% AND c.cpf LIKE %:cpf%", nativeQuery = true)
    Page<CustomerEntity> findByNameAndCpfCustom(@Param("name") String name, @Param("cpf") String cpf, Pageable pageable);

    Page<CustomerEntity> findByNameIgnoreCaseContainingOrderByNameAsc(String name, Pageable pageable);
    Page<CustomerEntity> findByCpfContaining(String cpf, Pageable pageable);


}
