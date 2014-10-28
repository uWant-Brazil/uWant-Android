package br.com.uwant.models.databases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import br.com.uwant.R;

public abstract class BaseDatabase<K> extends SQLiteOpenHelper implements IDatabase<K> {

    private static final int VERSION = 1;
    private static final String DB_NAME = "uw.db";

    public static final String QUERY = "=?";
    public static final String AND = " AND ";

    public static final String ID = "_id";
    public static final String TOKEN = "token";
    public static final String NAME = "name";
    public static final String LOGIN = "login";
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
    public static final String MAIL = "mail";

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
        String sql = getUpgradeSQL(oldVersion, newVersion);
        if (sql != null && !sql.isEmpty()) {
            db.execSQL(sql);
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
        StringBuilder buf = new StringBuilder();
        InputStream json= this.mContext.getResources().openRawResource(R.raw.sql_tables);
        BufferedReader in = new BufferedReader(new InputStreamReader(json, "UTF-8"));
        String str;

        while ((str = in.readLine()) != null) {
            buf.append(str);
        }
        in.close();

        return buf.toString();
    }

    protected abstract String getUpgradeSQL(int oldVersion, int newVersion);

}
