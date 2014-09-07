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

public class UserDatabase extends BaseDatabase<User> {

    private static final String TABLE = "users";
    private static final String SQL_CREATE = String.format("CREATE TABLE %s (" +
            "%s varchar(255) primary key" +
            ",%s varchar(255) not null" +
            ",%s varchar(255) not null" +
            ",%s integer not null" +
            ",%s varchar(255) not null" +
            ",%s varchar(255));"
            , TABLE, TOKEN, NAME, LOGIN, GENDER, BIRTHDAY, PICTURE_URL);

    public UserDatabase(Context context) {
        super(context);
    }

    @Override
    protected String getCreateSQL() {
        return SQL_CREATE;
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
    public ContentValues getValues(User data) {
        ContentValues cv = new ContentValues();
        cv.put(TOKEN, data.getToken());
        cv.put(NAME, data.getName());
        cv.put(LOGIN, data.getLogin());
        cv.put(BIRTHDAY, DateUtil.format(data.getBirthday(), DateUtil.DATE_PATTERN));
        cv.put(GENDER, data.getGender().ordinal());

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
    public User getFromCursor(Cursor cursor) {
        String token = cursor.getString(cursor.getColumnIndex(TOKEN));
        String name = cursor.getString(cursor.getColumnIndex(NAME));
        String login = cursor.getString(cursor.getColumnIndex(LOGIN));
        String birthdayStr = cursor.getString(cursor.getColumnIndex(BIRTHDAY));
        int genderOrdinal = cursor.getInt(cursor.getColumnIndex(GENDER));

        Person.Gender gender = Person.Gender.values()[genderOrdinal];
        Date birthday = null;
        try {
            birthday = DateUtil.parse(birthdayStr, DateUtil.DATE_PATTERN);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        User user = User.getInstance();
        user.setToken(token);
        user.setName(name);
        user.setLogin(login);
        user.setBirthday(birthday);
        user.setGender(gender);

        return user;
    }

    @Override
    public long create(User data) {
        SQLiteDatabase db = getWritableDatabase();
        long id = db.insert(TABLE, null, getValues(data));
        db.close();
        return id;
    }

    @Override
    public void remove(User data) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE, String.format("%s%s", TOKEN, QUERY), new String[] { data.getToken() });
        db.close();
    }

    @Override
    public void update(User data) {
        SQLiteDatabase db = getWritableDatabase();
        db.update(TABLE, getValues(data), String.format("%s=?", TOKEN), new String[] { data.getToken() });
        db.close();
    }

    @Override
    public void removeAll() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE, null, null);
        db.close();
    }

    @Override
    public User select(String[] columns, String[] columnArgs) {
        User user = null;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE, null, joinColumns(columns), columnArgs, null, null, null);

        if (cursor != null && cursor.getCount() == 1) {
            cursor.moveToFirst();
            user = getFromCursor(cursor);
            cursor.close();
        }
        db.close();

        return user;
    }

    @Override
    public List<User> selectAll(String[] columns, String[] columnArgs) {
        List<User> companies = null;
        SQLiteDatabase db = getReadableDatabase();
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
        db.close();

        return companies;
    }

    @Override
    public List<User> selectAll() {
        return selectAll(null, null);
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
