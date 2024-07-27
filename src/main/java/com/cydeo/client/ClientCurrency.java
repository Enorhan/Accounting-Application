package com.cydeo.client;

import com.cydeo.dto.Response.CurrencyResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name="currency", url = "https://cdn.jsdelivr.net")
public interface ClientCurrency {

@GetMapping("/npm/@fawazahmed0/currency-api@latest/v1/currencies/usd.json")
     CurrencyResponse getCurrencies();

}
