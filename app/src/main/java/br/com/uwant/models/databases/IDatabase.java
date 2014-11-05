package br.com.uwant.models.databases;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.List;

/**
 * Interface de todos os bancos de dados do sistema.
 * @param <K> - Classe de modelagem.
 */
public interface IDatabase<K> {

    long create(K data);
    long[] createAll(List<K> data);
    long createOrUpdate(K data);
    long[] createOrUpdate(List<K> data);
    void remove(K data);
    void removeAll();
    void update(K data);
    void updateAll(List<K> data);
    K select(String[] columns, String[] columnArgs);
    List<K> selectAll(String[] columns, String[] columnArgs);
    List<K> selectAll();
    boolean exist(K data);
    boolean exist(String[] columns, String[] columnArgs);
    boolean existAnything();
    ContentValues getValues(K data);
    K getFromCursor(Cursor cursor);

}
