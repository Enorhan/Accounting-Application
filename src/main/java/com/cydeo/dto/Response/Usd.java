package com.cydeo.dto.Response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Usd {

    @JsonProperty("eur")
    private BigDecimal euro;
    @JsonProperty("gbp")
    private BigDecimal britishPound;
    @JsonProperty("inr")
    private BigDecimal indianRupee;
    @JsonProperty("cad")
    private BigDecimal canadianDollar;
    @JsonProperty("jpy")
    private BigDecimal japaneseYen;
    public void setEuro(BigDecimal euro) {
        this.euro = euro.setScale(4, RoundingMode.HALF_UP);
    }

    public void setBritishPound(BigDecimal britishPound) {
        this.britishPound = britishPound.setScale(4, RoundingMode.HALF_UP);
    }

    public void setIndianRupee(BigDecimal indianRupee) {
        this.indianRupee = indianRupee.setScale(4, RoundingMode.HALF_UP);
    }

    public void setCanadianDollar(BigDecimal canadianDollar) {
        this.canadianDollar = canadianDollar.setScale(4, RoundingMode.HALF_UP);
    }

    public void setJapaneseYen(BigDecimal japaneseYen) {
        this.japaneseYen = japaneseYen.setScale(4, RoundingMode.HALF_UP);
    }

}