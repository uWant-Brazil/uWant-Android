package br.com.uwant.models.classes;

import java.io.Serializable;
import java.util.List;

/**
 * Classe de modelagem para todas as listas de desejos cadastrados por usuários no sistema.
 */
public class WishList implements Serializable {

    /**
     * Constante para análise durante a serialização dessa classe.
     */
    public static final String EXTRA = "extra_wishList";

    /**
     * Constante responsável por identificar se a lista de desejos esta vazia.
     */
    public static final long EMPTY_ID = 0x456271;

    /**
     * Constante responsável por identificar uma lista de desejos vazia a partir do ID.
     */
    public static final long EMPTY_DEFAULT_ID = 0x456123;

    /**
     * Identificador único.
     */
    private long id;

    /**
     * Título da lista de desejos.
     */
    private String title;

    /**
     * Descrição da lista de desejos.
     */
    private String description;

    /**
     * Produtos da lista de desejos.
     */
    private List<Product> products;

    public WishList() {
    }

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

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
