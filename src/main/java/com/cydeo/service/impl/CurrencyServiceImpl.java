package com.cydeo.service.impl;

import com.cydeo.client.ClientCurrency;
import com.cydeo.dto.Response.CurrencyResponse;
import com.cydeo.dto.Response.Usd;
import org.springframework.stereotype.Service;

@Service
public class CurrencyServiceImpl {

    private final ClientCurrency clientCurrency;

    public CurrencyServiceImpl(ClientCurrency clientCurrency) {
        this.clientCurrency = clientCurrency;
    }


    public Usd getDataFromApi(){
        CurrencyResponse allCurrencies=clientCurrency.getCurrencies();


        return  allCurrencies.getUsd();
    }
}
