package br.com.uwant.models.databases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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

    protected Context mContext;

    public BaseDatabase(Context context) {
        super(context, DB_NAME, null, VERSION);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = getCreateSQL();
        if (sql != null && !sql.isEmpty()) {
            db.execSQL(sql);
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

    protected abstract String getCreateSQL();
    protected abstract String getUpgradeSQL(int oldVersion, int newVersion);

}
