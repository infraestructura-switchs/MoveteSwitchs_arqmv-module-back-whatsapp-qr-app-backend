package com.restaurante.bot.repository;

import com.restaurante.bot.model.TransactionClient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionClientRespository extends JpaRepository<TransactionClient, Long> {

    TransactionClient findByCustomerId(Long customerId);
}
