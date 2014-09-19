package br.com.uwant.models.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.uwant.models.classes.Multimedia;
import br.com.uwant.models.classes.Person;
import br.com.uwant.models.classes.User;
import br.com.uwant.models.classes.WishList;
import br.com.uwant.utils.DateUtil;

public class WishListDatabase extends BaseDatabase<WishList> {

    private static final String TABLE = "wishlists";

    public WishListDatabase(Context context) {
        super(context);
    }

    @Override
    protected String getUpgradeSQL(int oldVersion, int newVersion) {
        String sql = null;
        // Não colocar break, apenas no default...
        switch (newVersion) {
            default:
                break;
        }
        return sql;
    }

    @Override
    public ContentValues getValues(WishList data) {
        ContentValues cv = new ContentValues();
        cv.put(ID, data.getId());
        cv.put(TITLE, data.getTitle());
        cv.put(DESCRIPTION, data.getDescription());
        cv.put(LAST_UPDATE, DateUtil.format(new Date(), DateUtil.DATE_HOUR_PATTERN));

        return cv;
    }

    @Override
    public WishList getFromCursor(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndex(ID));
        String title = cursor.getString(cursor.getColumnIndex(TITLE));
        String description = cursor.getString(cursor.getColumnIndex(DESCRIPTION));

        WishList wishList = new WishList();
        wishList.setId(id);
        wishList.setTitle(title);
        wishList.setDescription(description);

        return wishList;
    }

    @Override
    public long create(WishList data) {
        SQLiteDatabase db = getWritableDatabase();
        long id = db.insert(TABLE, null, getValues(data));
        db.close();

        WishListProductsDatabase wlpdb = new WishListProductsDatabase(this.mContext);
        wlpdb.createAll(data.getProducts());

        return id;
    }

    @Override
    public long[] createAll(List<WishList> data) {
        long[] ids = null;
        if (data != null && data.size() > 0) {
            int i = 0;
            ids = new long[data.size()];
            for (WishList wishList : data) {
                ids[i++] = create(wishList);
            }
        }
        return ids;
    }

    @Override
    public long createOrUpdate(WishList data) {
        if (exist(data)) {
            update(data);
        } else {
            create(data);
        }
        return data.getId();
    }

    @Override
    public long[] createOrUpdate(List<WishList> data) {
        long[] ids = null;
        if (data != null && data.size() > 0) {
            int i = 0;
            ids = new long[data.size()];
            for (WishList wishList : data) {
                ids[i++] = createOrUpdate(wishList);
            }
        }
        return ids;
    }

    @Override
    public void remove(WishList data) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE, String.format("%s%s", ID, QUERY), new String[] { String.valueOf(data.getId()) });
        db.close();
    }

    @Override
    public void update(WishList data) {
        SQLiteDatabase db = getWritableDatabase();
        db.update(TABLE, getValues(data), String.format("%s%s", ID, QUERY), new String[] { String.valueOf(data.getId()) });
        db.close();
    }

    @Override
    public void updateAll(List<WishList> data) {
        if (data != null && data.size() > 0) {
            for (WishList wishList : data) {
                update(wishList);
            }
        }
    }

    @Override
    public void removeAll() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE, null, null);
        db.close();
    }

    @Override
    public WishList select(String[] columns, String[] columnArgs) {
        WishList wishList = null;
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query(TABLE, null, joinColumns(columns), columnArgs, null, null, null);

        if (cursor != null && cursor.getCount() == 1) {
            cursor.moveToFirst();
            wishList = getFromCursor(cursor);
            cursor.close();
        }
        db.close();

        return wishList;
    }

    @Override
    public List<WishList> selectAll(String[] columns, String[] columnArgs) {
        List<WishList> wishLists = null;
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query(TABLE, null, joinColumns(columns), columnArgs, null, null, null);

        if (cursor != null && cursor.getCount() > 0) {
            wishLists = new ArrayList<WishList>(cursor.getCount() + 5);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                wishLists.add(getFromCursor(cursor));
                cursor.moveToNext();
            }
            cursor.close();
        }
        db.close();

        return wishLists;
    }

    @Override
    public List<WishList> selectAll() {
        return selectAll(null, null);
    }

    @Override
    public boolean exist(WishList data) {
        return exist(new String[] { ID }, new String[] { String.valueOf(data.getId()) });
    }

    @Override
    public boolean exist(String[] columns, String[] columnArgs) {
        return select(columns, columnArgs) != null;
    }

    @Override
    public boolean existAnything() {
        List<WishList> all = selectAll(null, null);
        return (all != null && all.size() > 0);
    }
}
