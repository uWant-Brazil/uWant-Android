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
import br.com.uwant.models.classes.Product;
import br.com.uwant.models.classes.User;
import br.com.uwant.models.classes.WishList;
import br.com.uwant.utils.DateUtil;

public class WishListProductsDatabase extends BaseDatabase<Product> {

    private static final String TABLE = "wishlist_products";

    public WishListProductsDatabase(Context context) {
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
    public ContentValues getValues(Product data) {
        ContentValues cv = new ContentValues();
        cv.put(ID, data.getId());
        cv.put(NAME, data.getName());
        cv.put(NICKNAME, data.getNickName());
        cv.put(LAST_UPDATE, DateUtil.format(new Date(), DateUtil.DATE_HOUR_PATTERN));
        cv.put(ID_WISHLIST, data.getWishListId());

        Multimedia picture = data.getPicture();
        if (picture != null) {
            String url = picture.getUrl();
            if (url != null && !url.isEmpty()) {
                cv.put(PICTURE_URL, picture.getUrl());
            }
        }

        return cv;
    }

    @Override
    public Product getFromCursor(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndex(ID));
        String name = cursor.getString(cursor.getColumnIndex(NAME));
        String nickname = cursor.getString(cursor.getColumnIndex(NICKNAME));
        String pictureUrl = cursor.getString(cursor.getColumnIndex(PICTURE_URL));

        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setNickName(nickname);

        if (pictureUrl != null) {
            Multimedia multimedia = new Multimedia();
            multimedia.setUrl(pictureUrl);
            product.setPicture(multimedia);
        }

        return product;
    }

    @Override
    public long create(Product data) {
        SQLiteDatabase db = getWritableDatabase();
        long id = db.insert(TABLE, null, getValues(data));
        

        ProductManufacturerDatabase pmdb = new ProductManufacturerDatabase(this.mContext);
        pmdb.create(data.getManufacturer());

        return id;
    }

    @Override
    public long[] createAll(List<Product> data) {
        long[] ids = null;
        if (data != null && data.size() > 0) {
            int i = 0;
            ids = new long[data.size()];
            for (Product product : data) {
                ids[i++] = create(product);
            }
        }
        return ids;
    }

    @Override
    public long createOrUpdate(Product data) {
        if (exist(data)) {
            update(data);
        } else {
            create(data);
        }
        return data.getId();
    }

    @Override
    public long[] createOrUpdate(List<Product> data) {
        long[] ids = null;
        if (data != null && data.size() > 0) {
            int i = 0;
            ids = new long[data.size()];
            for (Product product : data) {
                ids[i++] = createOrUpdate(product);
            }
        }
        return ids;
    }

    @Override
    public void remove(Product data) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE, String.format("%s%s", ID, QUERY), new String[] { String.valueOf(data.getId()) });
        
    }

    @Override
    public void update(Product data) {
        SQLiteDatabase db = getWritableDatabase();
        db.update(TABLE, getValues(data), String.format("%s%s", ID, QUERY), new String[] { String.valueOf(data.getId()) });
        
    }

    @Override
    public void updateAll(List<Product> data) {
        if (data != null && data.size() > 0) {
            for (Product product : data) {
                update(product);
            }
        }
    }

    @Override
    public void removeAll() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE, null, null);
        
    }

    @Override
    public Product select(String[] columns, String[] columnArgs) {
        Product product = null;
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query(TABLE, null, joinColumns(columns), columnArgs, null, null, null);

        if (cursor != null && cursor.getCount() == 1) {
            cursor.moveToFirst();
            product = getFromCursor(cursor);
            cursor.close();
        }
        

        return product;
    }

    @Override
    public List<Product> selectAll(String[] columns, String[] columnArgs) {
        List<Product> products = null;
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query(TABLE, null, joinColumns(columns), columnArgs, null, null, null);

        if (cursor != null && cursor.getCount() > 0) {
            products = new ArrayList<Product>(cursor.getCount() + 5);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                products.add(getFromCursor(cursor));
                cursor.moveToNext();
            }
            cursor.close();
        }
        

        return products;
    }

    @Override
    public List<Product> selectAll() {
        return selectAll(null, null);
    }

    @Override
    public boolean exist(Product data) {
        return exist(new String[] { ID }, new String[] { String.valueOf(data.getId()) });
    }

    @Override
    public boolean exist(String[] columns, String[] columnArgs) {
        return select(columns, columnArgs) != null;
    }

    @Override
    public boolean existAnything() {
        List<Product> all = selectAll(null, null);
        return (all != null && all.size() > 0);
    }
}
