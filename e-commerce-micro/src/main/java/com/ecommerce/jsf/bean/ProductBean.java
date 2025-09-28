package com.ecommerce.jsf.bean;

import com.ecommerce.jsf.model.Product;
import com.ecommerce.jsf.service.ProductService;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.transaction.Transactional;
import java.io.Serializable;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named("productBean")
@ViewScoped
public class ProductBean implements Serializable {
  private static final Logger logger = LoggerFactory.getLogger(ProductBean.class);
  private static final long serialVersionUID = 1L;
  private Product product = new Product();
  private List<Product> products;
  @Inject private ProductService productService;

  public Product getProduct() {
    return product;
  }

  public void setProduct(Product product) {
    this.product = product;
  }

  public List<Product> getProducts() {
    if (products == null) {
      logger.info("Loading product list from service");
      products = productService.findAll();
    }
    return products;
  }

  @Transactional
  public String save() {
    FacesContext context = FacesContext.getCurrentInstance();
    if (!context.getExternalContext().isUserInRole("admin")) {
      logger.warn("Unauthorized save attempt by user without admin role");
      throw new SecurityException("Access denied: admin role required");
    }
    logger.info("Saving product: {}", product);
    productService.save(product);
    product = new Product();
    products = null;
    return null;
  }

  public String edit(Product p) {
    logger.info("Editing product with ID: {}", p.getProductId());
    FacesContext context = FacesContext.getCurrentInstance();
    if (!context.getExternalContext().isUserInRole("admin")) {
      logger.warn("Unauthorized edit attempt by user without admin role");
      throw new SecurityException("Access denied: admin role required");
    }
    this.product = p;
    return null;
  }

  @Transactional
  public String delete(Product p) {
    FacesContext context = FacesContext.getCurrentInstance();
    if (!context.getExternalContext().isUserInRole("admin")) {
      logger.warn("Unauthorized delete attempt by user without admin role");
      throw new SecurityException("Access denied: admin role required");
    }
    logger.info("Deleting product with ID: {}", p.getProductId());
    productService.delete(p.getProductId());
    products = null;
    return null;
  }
}
