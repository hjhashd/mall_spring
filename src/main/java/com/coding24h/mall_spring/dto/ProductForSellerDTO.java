package com.coding24h.mall_spring.dto;

public class ProductForSellerDTO {
    private Long id;
    private String image;
    private String name;
    private Double price;
    private Integer stock;
    private Integer status;  // 数字状态 (1-上架, 2-下架, 3-售罄, 4-审核中)
    private String statusText; // 状态文本

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getImage() {
        return image != null ? image : "http://localhost:8080/uploads/products/example.png";
    }
    public void setImage(String image) { this.image = image; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public String getStatusText() { return statusText; }
    public void setStatusText(String statusText) { this.statusText = statusText; }
}
