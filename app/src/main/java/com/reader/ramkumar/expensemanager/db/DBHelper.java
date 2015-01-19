package com.reader.ramkumar.expensemanager.db;

/**
 * Created by Ramkumar on 19/01/15.
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
//http://www.tutorialspoint.com/android/android_sqlite_database.htm

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "MyExpense.db";
    public static final String CONTACTS_TABLE_NAME = "expense";
    public static final String CONTACTS_COLUMN_ID = "id";
    public static final String CONTACTS_COLUMN_NAME = "desc";
    public static final String CONTACTS_COLUMN_EMAIL = "bank_name";
    public static final String CONTACTS_COLUMN_STREET = "trans_source";//credit,debit,cash,online or netbanking
    public static final String CONTACTS_COLUMN_CITY = "trans_type";//credit,debit
    public static final String CONTACTS_COLUMN_CITY1 = "expanse_type";//home,car,fuel,misc
    public static final String CONTACTS_COLUMN_CITY2 = "notes";//credit,debit
    public static final String CONTACTS_COLUMN_PHONE = "sms_id";
    public static final String CONTACTS_COLUMN_PHONE1 = "where";
    public static final String CONTACTS_COLUMN_PHONE2 = "when";
    public static final String CONTACTS_COLUMN_PHONE3 = "sms_timestamp";
    public static final String CONTACTS_COLUMN_PHONE4 = "timestamp";
    public static final String CONTACTS_COLUMN_PHONE5 = "geo_tag";
    public static final String CONTACTS_COLUMN_PHONE6 = "SharedExpense";
    public static final String CONTACTS_COLUMN_PHONE8 = "SharedMembers";
    public static final String CONTACTS_COLUMN_PHONE7 = "status"; //pending,deleted,accepted

    private HashMap hp;

    public DBHelper(Context context)
    {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table contacts " +
                        "(id integer primary key, name text,phone text,email text, street text,place text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS contacts");
        onCreate(db);
    }

    public boolean insertContact  (String name, String phone, String email, String street,String place)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("name", name);
        contentValues.put("phone", phone);
        contentValues.put("email", email);
        contentValues.put("street", street);
        contentValues.put("place", place);

        db.insert("contacts", null, contentValues);
        return true;
    }
    public Cursor getData(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from contacts where id="+id+"", null );
        return res;
    }
    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, CONTACTS_TABLE_NAME);
        return numRows;
    }
    public boolean updateContact (Integer id, String name, String phone, String email, String street,String place)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("phone", phone);
        contentValues.put("email", email);
        contentValues.put("street", street);
        contentValues.put("place", place);
        db.update("contacts", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    public Integer deleteContact (Integer id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("contacts",
                "id = ? ",
                new String[] { Integer.toString(id) });
    }
    public ArrayList getAllCotacts()
    {
        ArrayList array_list = new ArrayList();
        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from contacts", null );
        res.moveToFirst();
        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(CONTACTS_COLUMN_NAME)));
            res.moveToNext();
        }
        return array_list;
    }
}