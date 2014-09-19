package br.com.uwant.models.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.uwant.models.classes.Manufacturer;
import br.com.uwant.models.classes.Multimedia;
import br.com.uwant.models.classes.Product;
import br.com.uwant.utils.DateUtil;

public class ProductManufacturerDatabase extends BaseDatabase<Manufacturer> {

    private static final String TABLE = "product_manufacturers";

    public ProductManufacturerDatabase(Context context) {
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
    public ContentValues getValues(Manufacturer data) {
        ContentValues cv = new ContentValues();
        cv.put(ID, data.getId());
        cv.put(NAME, data.getName());
        cv.put(LAST_UPDATE, DateUtil.format(new Date(), DateUtil.DATE_HOUR_PATTERN));
        cv.put(ID_PRODUCT, data.getProductId());

        return cv;
    }

    @Override
    public Manufacturer getFromCursor(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndex(ID));
        String name = cursor.getString(cursor.getColumnIndex(NAME));

        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(id);
        manufacturer.setName(name);

        return manufacturer;
    }

    @Override
    public long create(Manufacturer data) {
        if (data == null)
            return -1;

        SQLiteDatabase db = getWritableDatabase();
        long id = db.insert(TABLE, null, getValues(data));
        db.close();
        return id;
    }

    @Override
    public long[] createAll(List<Manufacturer> data) {
        long[] ids = null;
        if (data != null && data.size() > 0) {
            int i = 0;
            ids = new long[data.size()];
            for (Manufacturer manufacturer : data) {
                ids[i++] = create(manufacturer);
            }
        }
        return ids;
    }

    @Override
    public long createOrUpdate(Manufacturer data) {
        if (exist(data)) {
            update(data);
        } else {
            create(data);
        }
        return data.getId();
    }

    @Override
    public long[] createOrUpdate(List<Manufacturer> data) {
        long[] ids = null;
        if (data != null && data.size() > 0) {
            int i = 0;
            ids = new long[data.size()];
            for (Manufacturer manufacturer : data) {
                ids[i++] = createOrUpdate(manufacturer);
            }
        }
        return ids;
    }

    @Override
    public void remove(Manufacturer data) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE, String.format("%s%s", ID, QUERY), new String[] { String.valueOf(data.getId()) });
        db.close();
    }

    @Override
    public void update(Manufacturer data) {
        SQLiteDatabase db = getWritableDatabase();
        db.update(TABLE, getValues(data), String.format("%s%s", ID, QUERY), new String[] { String.valueOf(data.getId()) });
        db.close();
    }

    @Override
    public void updateAll(List<Manufacturer> data) {
        if (data != null && data.size() > 0) {
            for (Manufacturer manufacturer : data) {
                update(manufacturer);
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
    public Manufacturer select(String[] columns, String[] columnArgs) {
        Manufacturer manufacturer = null;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE, null, joinColumns(columns), columnArgs, null, null, null);

        if (cursor != null && cursor.getCount() == 1) {
            cursor.moveToFirst();
            manufacturer = getFromCursor(cursor);
            cursor.close();
        }
        db.close();

        return manufacturer;
    }

    @Override
    public List<Manufacturer> selectAll(String[] columns, String[] columnArgs) {
        List<Manufacturer> manufacturers = null;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE, null, joinColumns(columns), columnArgs, null, null, null);

        if (cursor != null && cursor.getCount() > 0) {
            manufacturers = new ArrayList<Manufacturer>(cursor.getCount() + 5);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                manufacturers.add(getFromCursor(cursor));
                cursor.moveToNext();
            }
            cursor.close();
        }
        db.close();

        return manufacturers;
    }

    @Override
    public List<Manufacturer> selectAll() {
        return selectAll(null, null);
    }

    @Override
    public boolean exist(Manufacturer data) {
        return exist(new String[] { ID }, new String[] { String.valueOf(data.getId()) });
    }

    @Override
    public boolean exist(String[] columns, String[] columnArgs) {
        return select(columns, columnArgs) != null;
    }

    @Override
    public boolean existAnything() {
        List<Manufacturer> all = selectAll(null, null);
        return (all != null && all.size() > 0);
    }
}
