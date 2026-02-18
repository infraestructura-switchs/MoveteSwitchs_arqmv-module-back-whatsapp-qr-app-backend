package com.restaurante.bot.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.restaurante.bot.api.definition.ServiceCallProductsApi;
import com.restaurante.bot.util.DateDeserializerTimestamp;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.time.LocalDateTime;

@Component
public class ApiConfig {

    @Value("${app.url.services.products-service}")
    private String baseUrlProductServices;

    public ServiceCallProductsApi getAllProduct() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new DateDeserializerTimestamp())
                .create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrlProductServices)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        return retrofit.create(ServiceCallProductsApi.class);
    }
}
