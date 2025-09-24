
package com.ecommerce.jsf.bean;

import jakarta.faces.context.FacesContext;
import jakarta.faces.application.FacesMessage;

import com.ecommerce.jsf.model.Product;
import jakarta.inject.Named;
import jakarta.transaction.Transactional;
import jakarta.faces.view.ViewScoped;
import com.ecommerce.jsf.repository.ProductRepository;
import jakarta.inject.Inject;
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
    @Inject
    private ProductRepository productRepository;

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public List<Product> getProducts() {
        try {
            if (products == null) {
                logger.info("Loading product list from database");
                products = productRepository.findAll();
            }
            return products;
        } catch (Exception e) {
            logger.error("Error loading products: {}", e.getMessage());
            return null;
        }
    }

    @Transactional
    public String save() {
        FacesContext context = FacesContext.getCurrentInstance();
        if (!context.getExternalContext().isUserInRole("admin")) {
            logger.warn("Unauthorized save attempt by user without admin role");
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Security Error",
                    "Access denied: admin role required"));
            return null;
        }
        try {
            productRepository.save(product);
            product = new Product();
            products = null;
        } catch (Exception e) {
            logger.error("Error saving product: {}", e.getMessage());
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                    "An error occurred while saving the product."));
        }
        return null;
    }

    public String edit(Product p) {
        logger.info("Editing product with ID: {}", p.getProductId());
        FacesContext context = FacesContext.getCurrentInstance();
        if (!context.getExternalContext().isUserInRole("admin")) {
            logger.warn("Unauthorized edit attempt by user without admin role");
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Security Error",
                    "Access denied: admin role required"));
            return null;
        }
        this.product = p;
        return null;
    }

    @Transactional
    public String delete(Product p) {
        FacesContext context = FacesContext.getCurrentInstance();
        if (!context.getExternalContext().isUserInRole("admin")) {
            logger.warn("Unauthorized delete attempt by user without admin role");
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Security Error",
                    "Access denied: admin role required"));
            return null;
        }
        try {
            logger.info("Deleting product with ID: {}", p.getProductId());
            productRepository.delete(p.getProductId());
            products = null;
        } catch (Exception e) {
            logger.error("Error deleting product: {}", e.getMessage());
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                    "An error occurred while deleting the product."));
        }
        return null;
    }
}
