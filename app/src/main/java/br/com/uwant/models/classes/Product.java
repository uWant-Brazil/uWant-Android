package br.com.uwant.models.classes;

import java.io.Serializable;
import java.util.Comparator;


/**
 * Classe de modelagem para os produtos de lista de desejos de usuários.
 */
public class Product implements Serializable{

    public static final String EXTRA = "extra_product";

    /**
     * Identificador único.
     */
    private long id;

    /**
     * Nome do produto.
     */
    private String name;

    /**
     * Apelido para o produto -> Segundo nome.
     */
    private String nickName;

    /**
     * Marca/Fabricante do produto.
     */
    private Manufacturer manufacturer;

    /**
     * Foto do produto.
     */
    private Multimedia picture;

    public Product() {
        this.id = 0; // Valor padrão.
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Multimedia getPicture() {
        return picture;
    }

    public void setPicture(Multimedia picture) {
        this.picture = picture;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Manufacturer getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(Manufacturer manufacturer) {
        this.manufacturer = manufacturer;
    }

}
