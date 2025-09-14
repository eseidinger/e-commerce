package com.ecommerce.jsf.bean;

import com.ecommerce.jsf.model.Product;
import jakarta.inject.Named;
import jakarta.faces.view.ViewScoped;
import jakarta.transaction.Transactional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;

@Named("productBean")
@ViewScoped
@Transactional
public class ProductBean implements Serializable {
    private static final Logger logger = Logger.getLogger(ProductBean.class.getName());
    private static final long serialVersionUID = 1L;
    private Product product = new Product();
    private List<Product> products;
    @PersistenceContext(unitName = "ecommercePU")
    private EntityManager em;

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
                TypedQuery<Product> query = em.createQuery("SELECT p FROM Product p", Product.class);
                products = query.getResultList();
            }
            return products;
        } catch (Exception e) {
            logger.severe("Error loading products: " + e.getMessage());
            return null;
        }
    }

    public String save() {
        try {
            if (product.getProductId() == null) {
                logger.info("Persisting new product");
                em.persist(product);
            } else {
                logger.info("Merging existing product with ID: " + product.getProductId());
                em.merge(product);
            }
            product = new Product();
            products = null;
        } catch (Exception e) {
            logger.severe("Error saving product: " + e.getMessage());
        }
        return null;
    }

    public String edit(Product p) {
        logger.info("Editing product with ID: " + p.getProductId());
        this.product = p;
        return null;
    }

    public String delete(Product p) {
        try {
            logger.info("Deleting product with ID: " + p.getProductId());
            Product toRemove = em.find(Product.class, p.getProductId());
            if (toRemove != null) {
                em.remove(toRemove);
            }
            products = null;
        } catch (Exception e) {
            logger.severe("Error deleting product: " + e.getMessage());
        }
        return null;
    }
}
