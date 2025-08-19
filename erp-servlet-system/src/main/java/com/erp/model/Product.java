package com.erp.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Product entity for ERP system
 */
public class Product {
    private Long productId;
    private String productCode;
    private String productName;
    private String description;
    private String category;
    private String brand;
    private BigDecimal unitPrice;
    private BigDecimal costPrice;
    private Integer stockQuantity;
    private Integer minStockLevel;
    private Integer maxStockLevel;
    private String unit; // PCS, KG, LITER, etc.
    private String status; // ACTIVE, INACTIVE, DISCONTINUED
    private String barcode;
    private String hsn; // Harmonized System of Nomenclature
    private BigDecimal taxRate;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private String createdBy;
    private String modifiedBy;
    
    // Constructors
    public Product() {}
    
    public Product(String productCode, String productName, String description, 
                  String category, BigDecimal unitPrice, BigDecimal costPrice, 
                  Integer stockQuantity, String unit) {
        this.productCode = productCode;
        this.productName = productName;
        this.description = description;
        this.category = category;
        this.unitPrice = unitPrice;
        this.costPrice = costPrice;
        this.stockQuantity = stockQuantity;
        this.unit = unit;
        this.status = "ACTIVE";
        this.createdDate = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    
    public String getProductCode() { return productCode; }
    public void setProductCode(String productCode) { this.productCode = productCode; }
    
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    
    public BigDecimal getCostPrice() { return costPrice; }
    public void setCostPrice(BigDecimal costPrice) { this.costPrice = costPrice; }
    
    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }
    
    public Integer getMinStockLevel() { return minStockLevel; }
    public void setMinStockLevel(Integer minStockLevel) { this.minStockLevel = minStockLevel; }
    
    public Integer getMaxStockLevel() { return maxStockLevel; }
    public void setMaxStockLevel(Integer maxStockLevel) { this.maxStockLevel = maxStockLevel; }
    
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getBarcode() { return barcode; }
    public void setBarcode(String barcode) { this.barcode = barcode; }
    
    public String getHsn() { return hsn; }
    public void setHsn(String hsn) { this.hsn = hsn; }
    
    public BigDecimal getTaxRate() { return taxRate; }
    public void setTaxRate(BigDecimal taxRate) { this.taxRate = taxRate; }
    
    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }
    
    public LocalDateTime getModifiedDate() { return modifiedDate; }
    public void setModifiedDate(LocalDateTime modifiedDate) { this.modifiedDate = modifiedDate; }
    
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    
    public String getModifiedBy() { return modifiedBy; }
    public void setModifiedBy(String modifiedBy) { this.modifiedBy = modifiedBy; }
    
    public boolean isLowStock() {
        return minStockLevel != null && stockQuantity != null && stockQuantity <= minStockLevel;
    }
    
    public BigDecimal getProfit() {
        if (unitPrice != null && costPrice != null) {
            return unitPrice.subtract(costPrice);
        }
        return BigDecimal.ZERO;
    }
    
    @Override
    public String toString() {
        return "Product{" +
                "productId=" + productId +
                ", productCode='" + productCode + '\'' +
                ", productName='" + productName + '\'' +
                ", category='" + category + '\'' +
                ", unitPrice=" + unitPrice +
                ", stockQuantity=" + stockQuantity +
                ", status='" + status + '\'' +
                '}';
    }
}