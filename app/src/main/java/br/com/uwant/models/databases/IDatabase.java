package br.com.uwant.models.databases;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.List;

public interface IDatabase<K> {

    long create(K data);
    void remove(K data);
    void update(K data);
    void removeAll();
    K select(String[] columns, String[] columnArgs);
    List<K> selectAll(String[] columns, String[] columnArgs);
    List<K> selectAll();
    boolean exist(String[] columns, String[] columnArgs);
    boolean existAnything();
    ContentValues getValues(K data);
    K getFromCursor(Cursor cursor);

}
