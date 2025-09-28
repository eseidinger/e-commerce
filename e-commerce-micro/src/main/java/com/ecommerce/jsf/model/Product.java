package com.ecommerce.jsf.model;

import com.ecommerce.jsf.dto.ProductDto;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
public class Product implements Serializable {

  private static final long serialVersionUID = 1L;

  public static Product fromDto(ProductDto dto) {
    Product product = new Product();
    product.setProductId(dto.productId());
    product.setName(dto.name());
    product.setDescription(dto.description());
    product.setPrice(dto.price());
    return product;
  }

  public static ProductDto toDto(Product product) {
    return new ProductDto(
        product.getProductId(), product.getName(), product.getDescription(), product.getPrice());
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "product_id")
  private Long productId;

  @Column(name = "name")
  private String name;

  @Column(name = "description")
  private String description;

  @Column(name = "price")
  private Double price;

  @OneToMany(mappedBy = "product")
  private List<OrderItem> orderItems;

  @OneToMany(mappedBy = "product")
  private List<Review> reviews;

  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Double getPrice() {
    return price;
  }

  public void setPrice(Double price) {
    this.price = price;
  }

  public List<OrderItem> getOrderItems() {
    return orderItems;
  }

  public void setOrderItems(List<OrderItem> orderItems) {
    this.orderItems = orderItems;
  }

  public List<Review> getReviews() {
    return reviews;
  }

  public void setReviews(List<Review> reviews) {
    this.reviews = reviews;
  }
}
