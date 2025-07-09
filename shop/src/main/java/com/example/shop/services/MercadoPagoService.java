package com.example.shop.services;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.*;
import com.mercadopago.resources.preference.Preference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MercadoPagoService {

    @Value("${mercadopago.access.token}")
    
    private String accessToken;

    public Preference crearPreferencia(List<PreferenceItemRequest> items) throws Exception {
        MercadoPagoConfig.setAccessToken(accessToken);

        PreferenceClient client = new PreferenceClient();
        PreferenceRequest request = PreferenceRequest.builder()
                .items(items)
                .build();

        return client.create(request);
    }
}