package br.com.uwant.models.classes;

import java.io.Serializable;
import java.util.List;

/**
 * Created by felipebenezi on 01/07/14.
 */
public class WishList implements Serializable {

    public static final long EMPTY_ID = 0x456271;
    public static final long EMPTY_DEFAULT_ID = 0x456123;

    private long id;
    private String title;
    private String description;
    private List<Product> products;

    public WishList(long id, String title) {
        this.id = id;
        this.title = title;
    }

    public WishList(long id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
