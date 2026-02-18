package com.restaurante.bot.business.service;

import com.restaurante.bot.business.interfaces.RestaurantTableInterface;
import com.restaurante.bot.dto.NumberDTO;
import com.restaurante.bot.exception.GenericException;
import com.restaurante.bot.model.*;
import com.restaurante.bot.repository.CompanyRepository;
import com.restaurante.bot.repository.RestaurantTableRepository;
import com.restaurante.bot.repository.SubscriptionRepository;
import com.restaurante.bot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RestaurantTableService implements RestaurantTableInterface {

    private final RestaurantTableRepository restaurantTableRepository;
    private final CompanyRepository companyRepository;
    private final NotificationService notificationService;
    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;

    @Override
    public List<RestaurantTable> ListarMesas(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long tokenCompanyId = (Long) authentication.getPrincipal();

        if (!companyRepository.existsByExternalCompanyId(tokenCompanyId)) {
            throw new GenericException("Compañia no recnocida en la base de datos", HttpStatus.BAD_REQUEST);
        }

        Company company = companyRepository.findByExternalCompanyId(tokenCompanyId);

        return restaurantTableRepository.findAllTablesAsc(company.getId());
    }

    @Override
    public RestaurantTable addTable(Long tableNumber) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long tokenCompanyId = (Long) authentication.getPrincipal();

        if (!companyRepository.existsByExternalCompanyId(tokenCompanyId)) {
            throw new GenericException("Compañia no recnocida en la base de datos", HttpStatus.BAD_REQUEST);
        }

        Company company = companyRepository.findByExternalCompanyId(tokenCompanyId);

        if (restaurantTableRepository.existsByTableNumberAndCompanyId(tableNumber, company.getId())) {

            throw new GenericException("La mesa ya existe", HttpStatus.BAD_REQUEST);

        }else {

            RestaurantTable newTable = new RestaurantTable();
            newTable.setTableNumber(tableNumber);
            newTable.setStatus(1L);
            newTable.setCompanyId(company.getId());
            return restaurantTableRepository.save(newTable);

        }

    }

    @Override
    public GenericResponse deleteTable(Long tableId) {

        if (!restaurantTableRepository.existsById(tableId)) {
            throw new GenericException("Mesa no resgistrada en la base de datos", HttpStatus.BAD_REQUEST);
        }
        restaurantTableRepository.deleteById(tableId);
        return new GenericResponse("Mesa eliminada con exito", 200L);
    }

    @Override
    public RestaurantTable changeStatusOcuped(NumberDTO tableNumber) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long tokenCompanyId = (Long) authentication.getPrincipal();

        if (!companyRepository.existsByExternalCompanyId(tokenCompanyId)) {
            throw new GenericException("Compañia no recnocida en la base de datos", HttpStatus.BAD_REQUEST);
        }

        Company company = companyRepository.findByExternalCompanyId(tokenCompanyId);

        User user = userRepository.findUserByCompany(company.getId());

        Subscription subscription = subscriptionRepository.findByUserId(user.getUserId());


        RestaurantTable table = restaurantTableRepository.findByTableNumberAndCompanyId(tableNumber.getTableNumber(), company.getId());
        table.setStatus(2L);

        notificationService.sendNotificationToClient(subscription.getToken(), "se actualizo la mesa " + tableNumber, "actualizar las mesas para ver el cambio");

        return restaurantTableRepository.save(table);
    }

    @Override
    public RestaurantTable changeStatusFree(Long tableNumber) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long tokenCompanyId = (Long) authentication.getPrincipal();

        if (!companyRepository.existsByExternalCompanyId(tokenCompanyId)) {
            throw new GenericException("Compañia no recnocida en la base de datos", HttpStatus.BAD_REQUEST);
        }

        Company company = companyRepository.findByExternalCompanyId(tokenCompanyId);

        if (!restaurantTableRepository.existsByTableNumberAndCompanyId(tableNumber, company.getId())) {
            throw new GenericException("Mesa no resgistrada en la base de datos", HttpStatus.BAD_REQUEST);
        }

        RestaurantTable table = restaurantTableRepository.findByTableNumberAndCompanyId(tableNumber, company.getId());
        table.setStatus(1L);

        return restaurantTableRepository.save(table);
    }

    @Override
    public RestaurantTable changeStatusRequestingService(NumberDTO tableNumber) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long tokenCompanyId = (Long) authentication.getPrincipal();

        if (!companyRepository.existsByExternalCompanyId(tokenCompanyId)) {
            throw new GenericException("Compañia no recnocida en la base de datos", HttpStatus.BAD_REQUEST);
        }

        Company company = companyRepository.findByExternalCompanyId(tokenCompanyId);

        User user = userRepository.findUserByCompany(company.getId());

        Subscription subscription = subscriptionRepository.findByUserId(user.getUserId());

        if (!restaurantTableRepository.existsByTableNumberAndCompanyId(tableNumber.getTableNumber(), company.getId())) {
            throw new GenericException("Mesa no resgistrada en la base de datos", HttpStatus.BAD_REQUEST);
        }

        RestaurantTable table = restaurantTableRepository.findByTableNumberAndCompanyId(tableNumber.getTableNumber(), company.getId());
        table.setStatus(3L);

        notificationService.sendNotificationToClient(subscription.getToken(), "se actualizo la mesa " + tableNumber, "actualizar las mesas para ver el cambio");

        return restaurantTableRepository.save(table);
    }

    public RestaurantTable changeStatusReserved(Long tableNumber) {
        /*
        if (!restaurantTableRepository.existsByTableNumber(tableNumber)) {
            throw new GenericException("Mesa no resgistrada en la base de datos", HttpStatus.BAD_REQUEST);
        }
        RestaurantTable table = restaurantTableRepository.findByTableNumber(tableNumber);
        table.setStatus(4L);

         */

        return null;
    }

    @Override
    public RestaurantTable changeStatusPay(NumberDTO tableNumber) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long tokenCompanyId = (Long) authentication.getPrincipal();

        if (!companyRepository.existsByExternalCompanyId(tokenCompanyId)) {
            throw new GenericException("Compañia no recnocida en la base de datos", HttpStatus.BAD_REQUEST);
        }

        Company company = companyRepository.findByExternalCompanyId(tokenCompanyId);

        User user = userRepository.findUserByCompany(company.getId());

        Subscription subscription = subscriptionRepository.findByUserId(user.getUserId());


        RestaurantTable table = restaurantTableRepository.findByTableNumberAndCompanyId(tableNumber.getTableNumber(), company.getId());
        table.setStatus(5L);

        notificationService.sendNotificationToClient(subscription.getToken(), "se actualizo la mesa " + tableNumber, "actualizar las mesas para ver el cambio");

        return restaurantTableRepository.save(table);
    }
}
