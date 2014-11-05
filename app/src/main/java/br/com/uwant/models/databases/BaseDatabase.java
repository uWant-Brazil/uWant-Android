package br.com.uwant.models.databases;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import br.com.uwant.R;

/**
 * Classe-pai de todos os banco de dados do app para sincronização com o WS.
 * @param <K> - Classe de modelagem
 */
public abstract class BaseDatabase<K> extends SQLiteOpenHelper implements IDatabase<K> {

    private static final int VERSION = 1;
    private static final String DB_NAME = "uw.db";
    private static final String ANDROID_METADATA = "android_metadata";
    private static final String SQLITE_SEQUENCE = "sqlite_sequence";

    private static final String SQL_SELECT_FOR_DROP_TABLES = "SELECT * FROM sqlite_master WHERE type='table';";
    private static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS %s;";

    public static final String QUERY = "=?";
    public static final String AND = " AND ";

    /**
     * Classe abstrata responsável por conter todas as chaves de acesso a colunas do banco.
     */
    public abstract class Key implements BaseColumns {
        public static final String TOKEN = "token";
        public static final String NAME = "name";
        public static final String LOGIN = "login";
        public static final String MAIL = "mail";
        public static final String GENDER = "gender";
        public static final String BIRTHDAY = "birthday";
        public static final String PICTURE_URL = "picture_url";
        public static final String TITLE = "title";
        public static final String DESCRIPTION = "description";
        public static final String LAST_UPDATE = "last_update";
        public static final String NICKNAME = "nickname";
        public static final String ID_WISHLIST = "id_wishlist";
        public static final String ID_PRODUCT = "id_product";
        public static final String FACEBOOK_TOKEN = "facebook_token";
    }

    /**
     * Contexto da Activity que inicializou o banco de dados.
     */
    protected Context mContext;

    public BaseDatabase(Context context) {
        super(context, DB_NAME, null, VERSION);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            String sqls = getCreateSQL();
            if (sqls != null && !sqls.isEmpty()) {
                String[] spplited = sqls.split(";");
                for (String sql : spplited) {
                    db.execSQL(sql);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        drop(db);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
        drop(db);
        onCreate(db);
    }

    private void drop(SQLiteDatabase db) {
        List<String> tables = new ArrayList<String>();
        Cursor cursor = db.rawQuery(SQL_SELECT_FOR_DROP_TABLES, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String tableName = cursor.getString(1);
            if (!tableName.equals(ANDROID_METADATA) &&
                    !tableName.equals(SQLITE_SEQUENCE))
                tables.add(tableName);
            cursor.moveToNext();
        }
        cursor.close();

        for(String tableName:tables) {
            db.execSQL(String.format(SQL_DROP_TABLE, tableName));
        }
    }

    protected String joinColumns(String[] columns) {
        if (columns == null)
            return null;

        StringBuilder builder = new StringBuilder();
        for (int i = 0;i < columns.length;i++) {
            String column = columns[i];
            builder.append(column);
            builder.append(QUERY);
            if (i < (columns.length - 1)) {
                builder.append(AND);
            }
        }
        return builder.toString();
    }

    private String getCreateSQL() throws IOException {
        InputStream is = this.mContext.getResources().openRawResource(R.raw.sql_tables);
        BufferedReader in = new BufferedReader(new InputStreamReader(is, "UTF-8"));

        String str;
        StringBuilder buf = new StringBuilder();
        while ((str = in.readLine()) != null) {
            buf.append(str);
        }
        in.close();

        return buf.toString();
    }

}
