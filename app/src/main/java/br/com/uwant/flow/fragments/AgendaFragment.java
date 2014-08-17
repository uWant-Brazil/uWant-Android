package br.com.uwant.flow.fragments;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.BaseAdapter;

import br.com.uwant.models.adapters.ContactsAdapter;
import br.com.uwant.models.classes.Multimedia;
import br.com.uwant.models.classes.Person;

public class AgendaFragment extends ContactsFragment {

    private static final String[] PROJECTION = {
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.Contacts.LOOKUP_KEY,
            ContactsContract.Contacts.PHOTO_URI,
            ContactsContract.Contacts.PHOTO_THUMBNAIL_URI

    };

    private ContactsAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mAdapter = new ContactsAdapter(getActivity(), this.mPersons);
    }

    @Override
    protected void loadPersons() {
        ContentResolver cr = getActivity().getContentResolver();
        Cursor cursor = cr.query(ContactsContract.RawContacts.CONTENT_URI, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast() && !isCancelled()) {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                Cursor cursorCommons = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, String.format("%s = ?", ContactsContract.CommonDataKinds.Email.CONTACT_ID), new String[]{ id }, null);
                if (cursorCommons != null) {
                    cursorCommons.moveToFirst();
                    while (!cursorCommons.isAfterLast() && !isCancelled()) {
                        String displayName = cursorCommons.getString(cursorCommons.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                        String email = cursorCommons.getString(cursorCommons.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                        if (email != null && !email.isEmpty()) {
                            Person person = new Person();
                            person.setName(displayName);
                            person.setMail(email);

                            Uri uri = getPictureUri(id);
                            if (uri != null) {
                                Multimedia multimedia = new Multimedia();
                                multimedia.setUri(uri);
                                person.setPicture(multimedia);
                            }

                            mPersons.add(person);
                        }
                        cursorCommons.moveToNext();
                    }
                    cursorCommons.close();
                }
                cursor.moveToNext();
            }
        }
        cursor.close();
    }

    public Uri getPictureUri(String id) {
        try {
            Cursor cur = getActivity().getContentResolver().query(
                    ContactsContract.Data.CONTENT_URI,
                    null,
                    String.format("%s=%s AND %s='%s'", ContactsContract.Data.CONTACT_ID, id, ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE), null,
                    null);
            if (cur != null) {
                if (!cur.moveToFirst()) {
                    return null; // no photo
                }

                cur.close();
            } else {
                return null; // error in cursor process
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        Uri person = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long
                .parseLong(id));
        return Uri.withAppendedPath(person, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
    }

    @Override
    protected BaseAdapter getAdapter() {
        return this.mAdapter;
    }

}
