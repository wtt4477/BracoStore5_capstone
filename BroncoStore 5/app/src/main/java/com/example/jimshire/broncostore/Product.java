package com.example.jimshire.broncostore;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by Jimshire on 10/4/17.
 *
 * This is the product class, including the getter and setter for names, descriptions, price,
 * and calculate the total price.
 */

public class Product implements Saleable, Serializable {

    private String pId;
    private String pName;
    private BigDecimal pPrice;
    private String pDescription;
    private String pImageName;


    public Product(String pId, String pName, BigDecimal pPrice, String pDescription, String pImageName) {
        setId(pId);
        setName(pName);
        setPrice(pPrice);
        setDescription(pDescription);
        setImageName(pImageName);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof Product)) return false;

        return (this.pId == ((Product) o).getId());
    }

    public int hashCode() {
        final int prime = 31;
        int hash = 1;
        hash = hash * prime + (pName == null ? 0 : pName.hashCode());
        hash = hash * prime + (pPrice == null ? 0 : pPrice.hashCode());
        hash = hash * prime + (pDescription == null ? 0 : pDescription.hashCode());

        return hash;
    }


    public String getId() {
        return pId;
    }

    public void setId(String id) {
        this.pId = id;
    }

    @Override
    public BigDecimal getPrice() {
        return pPrice;
    }

    @Override
    public String getName() {
        return pName;
    }

    public void setPrice(BigDecimal price) {
        this.pPrice = price;
    }

    public void setName(String name) {
        this.pName = name;
    }

    public String getDescription() {
        return pDescription;
    }

    public void setDescription(String pDescription) {
        this.pDescription = pDescription;
    }

    public String getImageName() {
        return pImageName;
    }

    public void setImageName(String imageName) {
        this.pImageName = imageName;
    }
}
