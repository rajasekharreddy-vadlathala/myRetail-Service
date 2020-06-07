package com.myretail.model;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;


@Document(collection = "data")
@Data
public class Product {

    @Id
    private int productId;
    //private String name;
    private CurrentPrice currentPrice;

    public Product(int productId, CurrentPrice currentPrice) {
        this.productId = productId;
        this.currentPrice = currentPrice;
    }

    public Product() {

    }
   }
