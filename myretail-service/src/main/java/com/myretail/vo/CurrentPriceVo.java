package com.myretail.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown=true)
public class CurrentPriceVo {
    @JsonProperty(value="value")
    private String value;
    @JsonProperty(value="currency_code")
    private String currencyCode;

    @Override
    public String toString() {
        return "CurrentPriceVO [value=" + value + ", currencyCode=" + currencyCode + "]";
    }

}
