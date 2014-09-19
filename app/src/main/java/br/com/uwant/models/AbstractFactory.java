package br.com.uwant.models;

/**
 * Classe abstrata para herança em todos os factory's utilizados no projeto.
 */
public abstract class AbstractFactory<K, P, T> {

    /**
     * Retorna a entidade baseada em seu identificador.
     * @param id
     * @return <T> - entidade
     */
    public abstract T get(K id);

    public abstract T get(K id, P parameter);

}
