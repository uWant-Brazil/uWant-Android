package br.com.uwant.models.classes;

import java.io.Serializable;

/**
 * Classe de modelagem para as marcas/fabricantes de produtos cadastrados em lista de desejos.
 * Essa informação será MUITO relevante para levantarmos um relatório analítico dos produtos.
 */
public class Manufacturer implements Serializable {

    /**
     * Identificador único.
     */
    private long id;

    /**
     * Nome da marca/fabricante.
     */
    private String name;

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

    @Override
    public String toString() {
        return getName();
    }
}
