package com.cydeo.dto.Response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "country_name",
        "country_short_name",
        "country_phone_code"
})
public class CountryResponse {
    @JsonProperty("country_name")
    public String countryName;
    @JsonProperty("country_short_name")
    public String countryShortName;
    @JsonProperty("country_phone_code")
    public Integer countryPhoneCode;
}
