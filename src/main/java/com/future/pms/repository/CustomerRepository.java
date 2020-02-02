package com.future.pms.repository;

import com.future.pms.model.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository public interface CustomerRepository extends MongoRepository<Customer, String> {
    Page<Customer> findCustomerByNameContainingAllIgnoreCase(Pageable pageable, String name);

    Customer findByEmail(String email);

    Customer findByIdCustomer(String idCustomer);
}
