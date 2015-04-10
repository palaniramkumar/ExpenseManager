package com.reader.freshmanapp.mywallet.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.reader.freshmanapp.mywallet.BuildConfig;
import com.reader.freshmanapp.mywallet.util.TYPES;

/**
 * Created by Ramkumar on 10/03/15.
 */
public class DBCategoryMap extends SQLiteOpenHelper {
    public final String[] KNOWN_BILLS = {"Airtel", "Vodafone", "Aircel", "acttv"};
    public final String[] KNOWN_TRAVEL = {"IRCTC"};
    public final String[] KNOWN_SHOP = {"Amazon", "Flipkart", "Snapdeal", "Landmark", "textile", "Silks"};
    public final String[] NEUTRAL = {"Credit"};

    public DBCategoryMap(Context context) {
        super(context, DBHelper.DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS KNOWN_CATEGORY_LIST");
        onCreate(db);
    }

    public boolean insertCategoryMap(String place, String type, String trans_type) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("place", place);
        contentValues.put("category", type);
        contentValues.put("trans_type", trans_type);

        try {
            db.insert("KNOWN_CATEGORY_LIST", null, contentValues);
            if (BuildConfig.DEBUG) {
                Log.e(Constants.TAG, "Inserting " + place);
            }
        } catch (SQLException e) {
            if (BuildConfig.DEBUG) {
                Log.e(Constants.TAG, e.getMessage());
            }
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public Cursor getCategoryInfo(String str) {
        SQLiteDatabase db = this.getReadableDatabase();
        //String sql = "select * from KNOWN_CATEGORY_LIST where instr(LOWER('" + str + "'), LOWER(place))"; //only lollipop is supporting
        String sql = "select * from KNOWN_CATEGORY_LIST where replace(upper('"+ str +"'), upper(place), '') != upper('"+ str +"')";
        if (BuildConfig.DEBUG) {
            Log.e(Constants.TAG, sql);
        }
        Cursor res = db.rawQuery(sql, null);
        return res;
    }

    public Cursor getCategoryInfo() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "select * from KNOWN_CATEGORY_LIST ";
        Cursor res = db.rawQuery(sql, null);
        while (res.moveToNext()) {
            Log.e(Constants.TAG, res.getString(1));
        }
        return res;
    }

    public void firstTime() {
        if (BuildConfig.DEBUG) {
            Log.e(Constants.TAG, "First time user");
        }
        for (int i = 0; i < KNOWN_BILLS.length; i++)
            insertCategoryMap(KNOWN_BILLS[i], DBHelper.BILL_PAYMENT, TYPES.TRANSACTION_TYPE.EXPENSE.toString());
        for (int i = 0; i < KNOWN_TRAVEL.length; i++)
            insertCategoryMap(KNOWN_TRAVEL[i], DBHelper.TRAVEL, TYPES.TRANSACTION_TYPE.EXPENSE.toString());
        for (int i = 0; i < KNOWN_SHOP.length; i++)
            insertCategoryMap(KNOWN_SHOP[i], DBHelper.SHOPPING, TYPES.TRANSACTION_TYPE.EXPENSE.toString());
        for (int i = 0; i < NEUTRAL.length; i++)
            insertCategoryMap(NEUTRAL[i], DBHelper.BILL_PAYMENT, TYPES.TRANSACTION_TYPE.NEUTRAL.toString());

    }

    public interface Constants {
        String TAG = "app:DBCategoryMap";
    }
}
