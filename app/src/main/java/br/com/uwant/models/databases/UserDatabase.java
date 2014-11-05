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
import br.com.uwant.utils.DateUtil;

/**
 * Classe de banco de dados para entidades de usuários.
 */
public class UserDatabase extends BaseDatabase<User> {

    /**
     * Nome da tabela do banco de dados.
     */
    private static final String TABLE = "users";

    public UserDatabase(Context context) {
        super(context);
    }

    @Override
    public ContentValues getValues(User data) {
        ContentValues cv = new ContentValues();
        cv.put(Key.TOKEN, data.getToken());
        cv.put(Key.NAME, data.getName());
        cv.put(Key.LOGIN, data.getLogin());
        cv.put(Key.BIRTHDAY, DateUtil.format(data.getBirthday(), DateUtil.DATE_PATTERN));
        cv.put(Key.GENDER, (data.getGender() != null) ? data.getGender().ordinal() : null);

        Multimedia picture = data.getPicture();
        if (picture != null) {
            String url = picture.getUrl();
            if (url != null && !url.isEmpty()) {
                cv.put(Key.PICTURE_URL, picture.getUrl());
            }
        }

        if (data.getFacebookToken() != null) {
            cv.put(Key.FACEBOOK_TOKEN, data.getFacebookToken());
        }

        return cv;
    }

    @Override
    public User getFromCursor(Cursor cursor) {
        String token = cursor.getString(cursor.getColumnIndex(Key.TOKEN));
        String name = cursor.getString(cursor.getColumnIndex(Key.NAME));
        String login = cursor.getString(cursor.getColumnIndex(Key.LOGIN));
        String birthdayStr = cursor.getString(cursor.getColumnIndex(Key.BIRTHDAY));
        Integer genderOrdinal = cursor.getInt(cursor.getColumnIndex(Key.GENDER));

        Person.Gender gender = null;
        if (genderOrdinal != null) {
            gender = Person.Gender.values()[genderOrdinal];
        }

        Date birthday = null;
        try {
            birthday = DateUtil.parse(birthdayStr, DateUtil.DATE_PATTERN);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Multimedia picture = null;
        String url = cursor.getString(cursor.getColumnIndex(Key.PICTURE_URL));
        if (url != null && !url.isEmpty()) {
            picture = new Multimedia();
            picture.setUrl(url);
        }

        String facebookToken = cursor.getString(cursor.getColumnIndex(Key.FACEBOOK_TOKEN));

        User user = User.getInstance();
        user.setToken(token);
        user.setName(name);
        user.setLogin(login);
        user.setBirthday(birthday);
        user.setGender(gender);
        user.setPicture(picture);
        user.setFacebookToken(facebookToken);

        return user;
    }

    @Override
    public long create(User data) {
        SQLiteDatabase db = getWritableDatabase();
        long id = db.insert(TABLE, null, getValues(data));
        
        return id;
    }

    @Override
    public long[] createAll(List<User> data) {
        long[] ids = null;
        if (data != null && data.size() > 0) {
            int i = 0;
            ids = new long[data.size()];
            for (User user : data) {
                ids[i++] = create(user);
            }
        }
        return ids;
    }

    @Override
    public long createOrUpdate(User data) {
        if (exist(data)) {
            update(data);
        } else {
            create(data);
        }
        return data.getId();
    }

    @Override
    public long[] createOrUpdate(List<User> data) {
        long[] ids = null;
        if (data != null && data.size() > 0) {
            int i = 0;
            ids = new long[data.size()];
            for (User user : data) {
                ids[i++] = createOrUpdate(user);
            }
        }
        return ids;
    }

    @Override
    public void remove(User data) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE, String.format("%s%s", Key.TOKEN, QUERY), new String[] { data.getToken() });
        
    }

    @Override
    public void update(User data) {
        SQLiteDatabase db = getWritableDatabase();
        db.update(TABLE, getValues(data), String.format("%s=?", Key.TOKEN), new String[] { data.getToken() });
        
    }

    @Override
    public void updateAll(List<User> data) {
        if (data != null && data.size() > 0) {
            for (User user : data) {
                update(user);
            }
        }
    }

    @Override
    public void removeAll() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE, null, null);
    }

    @Override
    public User select(String[] columns, String[] columnArgs) {
        User user = null;
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query(TABLE, null, joinColumns(columns), columnArgs, null, null, null);

        if (cursor != null && cursor.getCount() == 1) {
            cursor.moveToFirst();
            user = getFromCursor(cursor);
            cursor.close();
        }
        

        return user;
    }

    @Override
    public List<User> selectAll(String[] columns, String[] columnArgs) {
        List<User> companies = null;
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query(TABLE, null, joinColumns(columns), columnArgs, null, null, null);

        if (cursor != null && cursor.getCount() > 0) {
            companies = new ArrayList<User>(cursor.getCount() + 5);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                companies.add(getFromCursor(cursor));
                cursor.moveToNext();
            }
            cursor.close();
        }
        

        return companies;
    }

    @Override
    public List<User> selectAll() {
        return selectAll(null, null);
    }

    @Override
    public boolean exist(User data) {
        return exist(new String[] { Key.TOKEN }, new String[] { data.getToken() });
    }

    @Override
    public boolean exist(String[] columns, String[] columnArgs) {
        return select(columns, columnArgs) != null;
    }

    @Override
    public boolean existAnything() {
        List<User> all = selectAll(null, null);
        return (all != null && all.size() > 0);
    }
}
