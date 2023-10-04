package com.riyadstudio.ecommerceapplication.model;

import java.util.ArrayList;
import java.util.List;

public class Product {

    private List<String> productImagesUrlList = new ArrayList<>();
    private String productTitle;
    private Integer productPrice;
    private String productDescription;
    private List<Integer> productColorList = new ArrayList<>();
    private List<String> productSizeList = new ArrayList<>();


    public Product(List<String> productImagesUrlList, String productTitle, Integer productPrice, String productDescription, List<Integer> productColorList, List<String> productSizeList) {
        this.productImagesUrlList = productImagesUrlList;
        this.productTitle = productTitle;
        this.productPrice = productPrice;
        this.productDescription = productDescription;
        this.productColorList = productColorList;
        this.productSizeList = productSizeList;
    }

    public List<String> getProductImagesUrlList() {
        return productImagesUrlList;
    }

    public void setProductImagesUrlList(List<String> productImagesUrlList) {
        this.productImagesUrlList = productImagesUrlList;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public Integer getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(Integer productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public List<Integer> getProductColorList() {
        return productColorList;
    }

    public void setProductColorList(List<Integer> productColorList) {
        this.productColorList = productColorList;
    }

    public List<String> getProductSizeList() {
        return productSizeList;
    }

    public void setProductSizeList(List<String> productSizeList) {
        this.productSizeList = productSizeList;
    }
}
