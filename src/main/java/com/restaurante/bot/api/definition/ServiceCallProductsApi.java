package com.restaurante.bot.api.definition;

import com.restaurante.bot.api.dto.ProductDTO;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface ServiceCallProductsApi {

    @GET("public/api/ss-productos/obtener/all")
    Call<List<ProductDTO>>getAllProduct(@Header ("api-key") String apiKey,
                                       @Query("companyId")Long idIntegration);
}
