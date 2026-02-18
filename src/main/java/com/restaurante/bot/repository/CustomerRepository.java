package com.restaurante.bot.repository;

import com.restaurante.bot.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Customer findByPhone(String phone);

}
