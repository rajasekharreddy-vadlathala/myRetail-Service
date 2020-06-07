package com.myretail.vo;


import org.slf4j.Logger;

import lombok.Data;

@Data
public class ProductResponse {

    private int statusCode;
    private StackTraceElement[] errorMessage;

    public ProductResponse(int statusCode, StackTraceElement[] errorMessage) {
        this.statusCode = statusCode;
        this.errorMessage = errorMessage;
    }


    public ProductResponse() {
        this.statusCode = 500;
        this.errorMessage = errorMessage;
    }
    public void copy(Logger log,Exception exception){
        this.statusCode = 500;
        this.errorMessage = exception.getStackTrace();
    }

   
}
