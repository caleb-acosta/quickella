package com.caleb.acosta.quickella;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {
    //Database constants
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "contactsManager";
    //Tables
    private static final String TABLE_CONTACTS = "contacts";
    //Columns
    private static final String COLUMN_CONTACTS_ID = "id";
    private static final String COLUMN_CONTACTS_NAME = "name";
    private static final String COLUMN_CONTACTS_AREA_CODE = "areaCode";
    private static final String COLUMN_CONTACTS_PH_NO = "phone_number";

    public DatabaseHandler(@Nullable Context context, @Nullable SQLiteDatabase.CursorFactory factory) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE =
                "CREATE TABLE " + TABLE_CONTACTS + "(" +
                        COLUMN_CONTACTS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        COLUMN_CONTACTS_NAME + " TEXT,"  +
                        COLUMN_CONTACTS_AREA_CODE + " TEXT," +
                        COLUMN_CONTACTS_PH_NO + " TEXT" +
                        ")";
        db.execSQL(CREATE_CONTACTS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);

        // Create tables again
        onCreate(db);
    }

    // code to add the new contact
    void addContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        //values.put(COLUMN_CONTACTS_NAME, contact.getName()); // Contact Name
        values.put(COLUMN_CONTACTS_AREA_CODE, contact.getAreaCode()); // Contact Name
        values.put(COLUMN_CONTACTS_PH_NO, contact.getPhone()); // Contact Phone

        // Inserting Row
        db.insert(TABLE_CONTACTS, null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }

    // Code to get the single contact by id
    Contact getContact(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_CONTACTS, new String[] {
                    COLUMN_CONTACTS_ID, COLUMN_CONTACTS_NAME, COLUMN_CONTACTS_AREA_CODE, COLUMN_CONTACTS_PH_NO }, COLUMN_CONTACTS_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        Contact contact = new Contact(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2), cursor.getString(3));
        db.close();
        // return contact
        return contact;
    }

    Contact getContactByPhone(String areaCode, String phone) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_CONTACTS, new String[] {
                        COLUMN_CONTACTS_ID, COLUMN_CONTACTS_NAME, COLUMN_CONTACTS_AREA_CODE, COLUMN_CONTACTS_PH_NO }, COLUMN_CONTACTS_AREA_CODE + " = ? AND " + COLUMN_CONTACTS_PH_NO + "= ?" ,
                new String[] { areaCode, phone }, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        Contact contact = new Contact(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2), cursor.getString(3));
        db.close();
        // return contact
        return contact;
    }

    //Get a single contact by Phone number
    boolean contactExists(String areaCode, String phone) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_CONTACTS, new String[] {
                        COLUMN_CONTACTS_ID, COLUMN_CONTACTS_NAME, COLUMN_CONTACTS_AREA_CODE, COLUMN_CONTACTS_PH_NO }, COLUMN_CONTACTS_AREA_CODE + "= ? AND " + COLUMN_CONTACTS_PH_NO + "= ?",
                new String[] { areaCode, phone }, null, null, null, null);

        boolean exists;
        if (cursor.getCount()>0)
              exists = true;
        else exists = false;
        db.close();
        return exists;
    }


    // Code to get all contacts
    public List<Contact> getAllContacts() {
        List<Contact> contactList = new ArrayList<Contact>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                Contact contact = new Contact();
                contact.setId(Integer.parseInt(cursor.getString(0)));
                contact.setName(cursor.getString(1));
                contact.setAreaCode(cursor.getString(2));
                contact.setPhone(cursor.getString(3));

                // Adding contact to list
                contactList.add(contact);
            } while (cursor.moveToNext());
        }

        // return contact list
        return contactList;
    }

    // code to update the single contact
    public int updateContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_CONTACTS_NAME, contact.getName());
        values.put(COLUMN_CONTACTS_AREA_CODE, contact.getAreaCode());
        values.put(COLUMN_CONTACTS_PH_NO, contact.getPhone());

        // updating row
        return db.update(TABLE_CONTACTS, values, COLUMN_CONTACTS_ID + " = ?",
                new String[] { String.valueOf(contact.getId()) });
    }

    // Deleting single contact
    public void deleteContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTACTS, COLUMN_CONTACTS_ID + " = ?",
                new String[] { String.valueOf(contact.getId()) });
        db.close();
    }

    // Getting contacts Count
    public int getContactsCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        String countQuery = "SELECT  * FROM " + TABLE_CONTACTS;
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();

        // return count
        return count;
    }
}
