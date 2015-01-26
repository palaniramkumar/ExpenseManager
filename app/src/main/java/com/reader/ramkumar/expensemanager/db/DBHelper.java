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
    public static final String MASTER_TABLE_NAME = "master";
    public static final String MASTER_COLUMN_ID = "id";
    public static final String MASTER_COLUMN_AMOUNT = "amount";
    public static final String MASTER_COLUMN_BANK_NAME = "bank_name";
    public static final String MASTER_COLUMN_TRANS_SOURCE = "trans_source";//credit,debit,cash or netbanking
    public static final String MASTER_COLUMN_TRANS_TYPE = "trans_type";//credit,debit,cash
    public static final String MASTER_COLUMN_CATEGORY = "category";//home,car,fuel,misc
    public static final String MASTER_COLUMN_NOTES = "notes";//credit,debit
    public static final String MASTER_COLUMN_SMS_ID = "sms_id";
    public static final String DESC = "desc";
    public static final String MASTER_COLUMN_BANK_TRANSACTION_TIME = "bank_trans_time";
    public static final String MASTER_COLUMN_PLACE = "place";
    public static final String MASTER_COLUMN_SMS_TIMESTAMP = "sms_timestamp";
    public static final String MASTER_COLUMN_TIMESTAMP = "timestamp";
    public static final String MASTER_COLUMN_GEO_TAG = "geo_tag";
    public static final String MASTER_COLUMN_SHAREDEXPENSE = "SharedExpense";
    public static final String MASTER_COLUMN_SHAREDMEMBERS = "SharedMembers";
    public static final String MASTER_COLUMN_STATUS = "status"; //pending,deleted,accepted

    private HashMap hp;

    public DBHelper(Context context)
    {

        super(context, DATABASE_NAME , null, 1);
        //context.deleteDatabase(DATABASE_NAME);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub

        db.execSQL(
                "create table MASTER " +
                        "(id integer primary key, amount text,bank_name text,trans_source text, trans_type text,category text," +
                        " notes text,sms_id text,desc text, place text,bank_trans_time text,sms_timestamp text,timestamp text," +
                        "geo_tag text,SharedExpense text, SharedMembers text,status text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS MASTER");
        onCreate(db);
    }

    public boolean insertMaster  (String amount, String bank_name, String trans_source,
                                  String trans_type,String category,String notes,
                                  String sms_id,String desc,String bank_trans_time,
                                  String sms_timestamp,String timestamp,String place,
                                  String geo_tag,String SharedExpense,
                                  String SharedMembers,String status)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("amount", amount);
        contentValues.put("bank_name", bank_name);
        contentValues.put("trans_source", trans_source);
        contentValues.put("trans_type", trans_type);
        contentValues.put("category", category);
        contentValues.put("notes", notes);
        contentValues.put("sms_id", sms_id);
        contentValues.put("desc", desc);
        contentValues.put("bank_trans_time", bank_trans_time);
        contentValues.put("sms_timestamp", sms_timestamp);
        contentValues.put("timestamp", timestamp);
        contentValues.put("geo_tag", geo_tag);
        contentValues.put("SharedExpense", SharedExpense);
        contentValues.put("SharedMembers", SharedMembers);
        contentValues.put("status", status);
        contentValues.put("place", place);

        db.insert("MASTER", null, contentValues);
        return true;
    }
    public Cursor getData(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from MASTER where id="+id+"", null );
        return res;
    }
    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, MASTER_TABLE_NAME);
        return numRows;
    }
    public boolean updateMaster(Integer id, String amount, String bank_name, String trans_source,
                                String trans_type,String category,String notes,
                                String sms_id,String desc,String bank_trans_time,
                                String sms_timestamp,String timestamp,String place,
                                String geo_tag,String SharedExpense,
                                String SharedMembers,String status)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("amount", amount);
        contentValues.put("bank_name", bank_name);
        contentValues.put("trans_source", trans_source);
        contentValues.put("trans_type", trans_type);
        contentValues.put("category", category);
        contentValues.put("notes", notes);
        contentValues.put("sms_id", sms_id);
        contentValues.put("desc", desc);
        contentValues.put("bank_trans_time", bank_trans_time);
        contentValues.put("sms_timestamp", sms_timestamp);
        contentValues.put("timestamp", timestamp);
        contentValues.put("geo_tag", geo_tag);
        contentValues.put("SharedExpense", SharedExpense);
        contentValues.put("SharedMembers", SharedMembers);
        contentValues.put("status", status);
        contentValues.put("place", place);
        db.update("MASTER", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    public boolean updateMasterStatus(Integer id, String status)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("status", status);
        db.update("MASTER", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    public Integer deleteMaster (Integer id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("MASTER",
                "id = ? ",
                new String[] { Integer.toString(id) });
    }

    public Cursor getAllFromMaster()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from MASTER", null );
        return res;
    }
    public Cursor getFromMasterByID(int id)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from MASTER where id="+id+"", null );
        return res;
    }
}