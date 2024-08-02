package com.cydeo.service.impl;

import com.cydeo.client.AuthClient;
import com.cydeo.client.CountryClient;
import com.cydeo.dto.Response.CountryResponse;
import com.cydeo.service.CountryService;
import feign.FeignException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CountryServiceImpl implements CountryService {

    private final CountryClient countryClient;
    private final AuthClient authClient;
    private String token;

    public CountryServiceImpl(CountryClient countryClient, AuthClient authClient) {
        this.countryClient = countryClient;
        this.authClient = authClient;
        this.token = null;
    }

    private String getToken() {
        if (token == null || token.isEmpty()) {
            Map<String, String> tokenResponse = authClient.getAccessToken(
                    "Ey-ZHdDfnXfMp0eIDVfjviiNCEaxbDnraP41Zc2ndRCekvvIS052MxpV-s-UvUy9sTc",
                    "martin.manuilov.asenov@gmail.com"
            );
            token = "Bearer " + tokenResponse.get("auth_token");
        }
        return token;
    }

    @Override
    public List<String> getAllCountries() {
        List<CountryResponse> countries;

        try {
            countries = countryClient.getCountries(getToken());
        } catch (FeignException e) {
            throw new RuntimeException("Failed to fetch countries", e);
        }

        List<String> countryList = countries.stream()
                .map(CountryResponse::getCountryName)
                .collect(Collectors.toList());
        countryList.remove("United States");
        return countryList;
    }
}