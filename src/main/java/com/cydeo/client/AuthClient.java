package com.cydeo.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;
@FeignClient(name = "authClient", url = "https://www.universal-tutorial.com/api")
public interface AuthClient {
    @GetMapping("/getaccesstoken")
    Map<String, String> getAccessToken(@RequestHeader("api-token") String apiToken, @RequestHeader("user-email") String userEmail);
}