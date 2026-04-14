package com.restaurante.bot.business.service;

import com.restaurante.bot.application.ports.incoming.TransactionUseCase;
import com.restaurante.bot.business.interfaces.TransactionInterface;
import com.restaurante.bot.domain.exception.DomainException;
import com.restaurante.bot.domain.exception.DomainErrorCode;
import com.restaurante.bot.model.Company;
import com.restaurante.bot.model.GenericResponse;
import com.restaurante.bot.model.RestaurantTable;
import com.restaurante.bot.model.Transaction;
import com.restaurante.bot.repository.*;
import com.restaurante.bot.util.TransactionStatusConstants;
import jakarta.persistence.Table;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionService implements TransactionInterface, TransactionUseCase {

    private final RestaurantTableRepository restaurantTableRepository;
    private final TransactionRepository transactionRepository;
    private final CustomerRepository customerRepository;
    private final OrderProductRepository orderProductRepository;
    private final OrderTransactionRepository orderTransactionRepository;
    private final HistoryRepository historyRepository;
    private final CompanyRepository companyRepository;


    @Override
    public GenericResponse finalizeTransaction(Integer tableNumber) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long tokenCompanyId = (Long) authentication.getPrincipal();

        if (!companyRepository.existsByExternalCompanyId(tokenCompanyId)) {
            throw new DomainException(DomainErrorCode.INVALID_REQUEST, "Compañia no recnocida en la base de datos");
        }

        Company company = companyRepository.findByExternalCompanyId(tokenCompanyId);

        Transaction transaction = transactionRepository.getTransactionByTableAndStatus(tableNumber, company.getId());
        if (transaction != null) {
            throw new DomainException(DomainErrorCode.INVALID_REQUEST, "hay ordenes por enviar en esta mesa");
        }
        RestaurantTable table = restaurantTableRepository.findByTableNumberAndCompanyId(
                tableNumber.longValue(), company.getId());
        table.setStatus(1L);
        restaurantTableRepository.save(table);

        Transaction transaction2 = transactionRepository.getTransactionByTableAndStatusSend(tableNumber, company.getId());

        if (transaction2 == null) {
            throw new DomainException(DomainErrorCode.INVALID_REQUEST, "No hay transacciones abiertas en esa mesa");
        }

        transaction2.setStatus(TransactionStatusConstants.CLOSED);
        transactionRepository.save(transaction2);
        return new GenericResponse("Transaccion finalizada con exito", 200L);

    }
}
