package com.reader.freshmanapp.mywallet.db;

/**
 * Created by Ramkumar on 19/01/15.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.reader.freshmanapp.mywallet.BuildConfig;
import com.reader.freshmanapp.mywallet.util.TYPES;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
//http://www.tutorialspoint.com/android/android_sqlite_database.htm

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "MyExpense.db";
    public static final String MASTER_TABLE_NAME = "master";
    public static final String MASTER_COLUMN_ID = "id";
    public static final String MASTER_COLUMN_AMOUNT = "amount";
    public static final String MASTER_COLUMN_BANK_NAME = "bank_name";
    public static final String MASTER_COLUMN_TRANS_SOURCE = "trans_source";//credit,debit card,Cash or netbanking
    public static final String MASTER_COLUMN_TRANS_TYPE = "trans_type";//credit,debit,ATM
    public static final String MASTER_COLUMN_CATEGORY = "category";//home,car,fuel,misc
    public static final String MASTER_COLUMN_NOTES = "notes";//credit,debit
    public static final String MASTER_COLUMN_SMS_ID = "sms_id";
    public static final String DESC = "desc";
    public static final String MASTER_COLUMN_TRANSACTION_TIME = "trans_time";
    public static final String MASTER_COLUMN_PLACE = "place";
    public static final String MASTER_COLUMN_TIMESTAMP = "timestamp";
    public static final String MASTER_COLUMN_GEO_TAG = "geo_tag";
    public static final String MASTER_COLUMN_SHAREDEXPENSE = "SharedExpense";
    public static final String MASTER_COLUMN_SHAREDMEMBERS = "SharedMembers";
    public static final String MASTER_COLUMN_ACCOUNT = "account";
    public static final String MASTER_COLUMN_STATUS = "status"; //pending,deleted,accepted
    public static final String CATEGORY_TABLE_NAME = "category";

    //{ "Food & Drinks", "Home", "Fuel" ,"Groceries","Travel","Health","Entertainment","Shopping","BILL","UNCATEGORIZED"};
    public static final String UNCATEGORIZED = "Uncategorized";
    public static final String BILL_PAYMENT = "Bill";
    public static final String SHOPPING = "Shopping";
    public static final String TRAVEL = "Travel";
    public static final String ATM = "ATM Withdrawal";


    public String month = "";
    public String year = "";

    public DBHelper(Context context) {

        super(context, DATABASE_NAME, null, 1);
        month = new SimpleDateFormat("MM").format(Calendar.getInstance().getTime());
        year = new SimpleDateFormat("yyyy").format(Calendar.getInstance().getTime());

        //context.deleteDatabase(DATABASE_NAME); //force close if the db is not created
    }

    public DBHelper(Context context, String month) {

        super(context, DATABASE_NAME, null, 1);
        this.month = month;

        //context.deleteDatabase(DATABASE_NAME); //force close if the db is not created
    }

    public void deleteDB(Context context) {
        context.deleteDatabase(DATABASE_NAME);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub

        db.execSQL(
                "create table MASTER " +
                        "(id integer primary key, amount text,bank_name text,trans_source text, trans_type text,category text DEFAULT '" + UNCATEGORIZED + "'," +
                        " notes text,sms_id integer UNIQUE,desc text, place text,trans_time DATETIME ," +
                        "timestamp DATETIME ,geo_tag text,SharedExpense text, SharedMembers text,account text, status text)"
        );
        db.execSQL(
                "create table CATEGORY " +
                        "(id integer primary key, category text,amount text, " +
                        "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,status text)"
        );

        db.execSQL(
                "create table KNOWN_CATEGORY_LIST " +
                        "(id integer primary key, place text UNIQUE,category text,trans_type text,trans_time DATETIME DEFAULT CURRENT_TIMESTAMP)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS MASTER");
        onCreate(db);
    }

    public void firstUser() {
        CharSequence[] categoryName = getDefaultCategory();
        for (int i = 0; i < categoryName.length; i++)
            insertCategory(categoryName[i].toString(), "0", TYPES.TRANSACTION_STATUS.APPROVED.toString());
    }

    public boolean insertMaster(String amount, String bank_name, String trans_source,
                                String trans_type, String category, String notes,
                                String sms_id, String desc, String trans_time,
                                String timestamp, String place,
                                String geo_tag, String SharedExpense,
                                String SharedMembers, String account,String status) {
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
        contentValues.put("trans_time", trans_time);
        contentValues.put("timestamp", timestamp);
        contentValues.put("geo_tag", geo_tag);
        contentValues.put("SharedExpense", SharedExpense);
        contentValues.put("SharedMembers", SharedMembers);
        contentValues.put("status", status);
        contentValues.put("place", place);
        contentValues.put("account", account);

        try {
            db.insert("MASTER", null, contentValues);
        } catch (SQLException e) {
            if (BuildConfig.DEBUG) {
                Log.e(Constants.TAG, e.getMessage());
            }
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean updateMaster(Integer id, String amount, String bank_name, String trans_source,
                                String trans_type, String category, String notes,
                                String sms_id, String desc, String trans_time,
                                String timestamp, String place,
                                String geo_tag, String SharedExpense,
                                String SharedMembers, String status) {
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
        contentValues.put("trans_time", trans_time);
        contentValues.put("timestamp", timestamp);
        contentValues.put("geo_tag", geo_tag);
        contentValues.put("SharedExpense", SharedExpense);
        contentValues.put("SharedMembers", SharedMembers);
        contentValues.put("status", status);
        contentValues.put("place", place);
        db.update("MASTER", contentValues, "id = ? ", new String[]{Integer.toString(id)});
        return true;
    }

    public boolean insertCategory(String name, String amount, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("amount", amount);
        contentValues.put("category", name);
        contentValues.put("status", status);

        db.insert("category", null, contentValues);
        return true;
    }

    public Integer deleteCategory(int id) {

        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("CATEGORY",
                "id = ? ",
                new String[]{Integer.toString(id)});
    }

    public int updateCategory(Integer id, String field, String value) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(field, value);
        int count = db.update("Category", contentValues, "id = ? ", new String[]{Integer.toString(id)});
        return count;

    }

    public Cursor getCategory(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from CATEGORY where id = " + id, null);
        return res;

    }



    public Cursor getTransactionHistory(String category_filter) {
        String sql = "select case strftime('%m', trans_time) when '01' then 'January' when '02' then 'Febuary' when '03' then 'March' when '04' then 'April' when '05' then 'May' when '06' then 'June' when '07' then 'July' when '08' then 'August' when '09' then 'September' when '10' then 'October' when '11' then 'November' when '12' then 'December' else '' end\n" +
                "as month,strftime('%d',trans_time) day, category, amount,id,UPPER(notes),geo_tag,trans_type from MASTER " +
                "where status ='" + TYPES.TRANSACTION_STATUS.APPROVED + "' and  strftime('%m', `trans_time`) = '" + month + "' " +
                "and  strftime('%Y', `trans_time`) = '" + year + "' and category like '" + category_filter + "' order by day desc";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery(sql, null);
        return res;
    }



    public int getCashVault() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select sum(amount) from MASTER where trans_type = '" + TYPES.TRANSACTION_TYPE.CASH_VAULT + "' and status = '" + TYPES.TRANSACTION_STATUS.APPROVED + "'" +
                "  and strftime('%m', `trans_time`) = '" + month + "' and  strftime('%Y', `trans_time`) = '" + year + "'", null);
        if (res.moveToNext()) {
            if (res.getString(0) == null) return 0;
            return Integer.parseInt(res.getString(0));
        } else {
            return 0;
        }
    }

    public int numberOfRows() {
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, MASTER_TABLE_NAME);
        return numRows;
    }

    public int updateMaster(Integer id, String field, String value) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(field, value);
        int count = db.update("MASTER", contentValues, "id = ? ", new String[]{Integer.toString(id)});
        return count;

    }

    public int updateMasterStatus(Integer id, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("status", status);
        int count = db.update("MASTER", contentValues, "id = ? ", new String[]{Integer.toString(id)});
        return count;
    }

    public Integer deleteMaster(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("MASTER",
                "id = ? ",
                new String[]{Integer.toString(id)});
    }

    public int getCountMaster(String field, String value, Boolean all) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "select count(*) from MASTER where " + field + " = '" + value + "' and status='" + TYPES.TRANSACTION_STATUS.APPROVED + "'  order by trans_time desc";
        if (!all)
            sql = "select count(*) from MASTER where " + field + " = '" + value + "' and status='" + TYPES.TRANSACTION_STATUS.APPROVED + "' and  strftime('%m', `trans_time`) = '" + month + "' and  strftime('%Y', `trans_time`) = '" + year + "' order by trans_time desc"; //
        Cursor res = db.rawQuery(sql, null);
        if (res.moveToNext()) {
            return res.getInt(0);
        }
        return 0;
    }

    public Cursor getFromMaster(String field, String value, Boolean all) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "select * from MASTER where " + field + " = '" + value + "' and status='" + TYPES.TRANSACTION_STATUS.APPROVED + "'  order by trans_time desc";
        if (!all)
            sql = "select * from MASTER where " + field + " = '" + value + "' and status='" + TYPES.TRANSACTION_STATUS.APPROVED + "' and  strftime('%m', `trans_time`) = '" + month + "' and  strftime('%Y', `trans_time`) = '" + year + "' order by trans_time desc"; //
        Cursor res = db.rawQuery(sql, null);
        return res;
    }
    public Cursor getFromMaster() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "select * from MASTER  order by trans_time desc";
        Cursor res = db.rawQuery(sql, null);
        return res;
    }

    public Cursor getMasterByStatus(String status) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from MASTER where status ='" + status + "'", null);
        return res;
    }

    /* Category Related Methods*/

    public Cursor getMasterByStatus(String status, int days) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from MASTER where status ='" + status + "' and trans_time >= date('now', '-" + days + " days')" +
                "  AND trans_time <  date('now')", null);
        return res;
    }

    public int getLastSMSID() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select max(sms_id) from MASTER ", null);
        if (res.moveToNext()) {
            if (res.getString(0) == null) return 0;
            return Integer.parseInt(res.getString(0));
        } else {
            return 0;
        }
    }

    public Cursor getFromMasterByID(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select *,strftime('%m/%d/%Y',trans_time) local_time from MASTER where id=" + id + "", null);
        return res;
    }

    public CharSequence[] getDefaultCategory() {
        final CharSequence myList[] = {"Food & Drinks", "Home", "Fuel", "Groceries", "Travel", "Health", "Entertainment", "Shopping", "Bill", "Uncategorized" +
                ""};
        return myList;
    }




    /*Date Conversion Methods */

    public static String getDateTime(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        //Date date = new Date();
        return dateFormat.format(date);
    }

    public static String getLocalDate(long timestamp) {
        try {
            Calendar calendar = Calendar.getInstance();
            TimeZone tz = TimeZone.getDefault();
            calendar.setTimeInMillis(timestamp * 1000);
            calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
            Date date = (Date) calendar.getTime();
            return sdf.format(date);
        } catch (Exception e) {
        }
        return "";
    }

    public static String getDroidDate(long timestamp) {
        try {
            Calendar calendar = Calendar.getInstance();
            TimeZone tz = TimeZone.getDefault();
            calendar.setTimeInMillis(timestamp * 1000);
            calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date = (Date) calendar.getTime();
            return sdf.format(date);
        } catch (Exception e) {
        }
        return "";
    }

    public static String getDateTime(String dateinactivity) {
        int month = Integer.parseInt(dateinactivity.split("/")[0]) - 1;
        int day = Integer.parseInt(dateinactivity.split("/")[1]);
        int year = Integer.parseInt(dateinactivity.split("/")[2]);
        Calendar calendar = new GregorianCalendar(year, month, day);
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        dateFormat.setCalendar(calendar);
        return dateFormat.format(calendar.getTime());
    }


    public Cursor getMyBudgetByCategory() { //need to have history in order to maintain the prev month budget
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select id _id,category,amount from CATEGORY where status='" + TYPES.TRANSACTION_STATUS.APPROVED + "'", null);
        return res;
    }

    public int getBudget() { //need to have history in order to maintain the prev month budget

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select sum(amount) from CATEGORY where status='" + TYPES.TRANSACTION_STATUS.APPROVED + "'", null);
        if (res.moveToNext()) return res.getInt(0);
        return 0;
    }

    //select  c.category,c.amount,m.amount from category c left join master m on c.category=m.category where c.amount <> "0"
    public Cursor getBudgetSummary() {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select  c.category,c.amount,sum(m.amount) from category c CROSS join master m " +
                "on c.category=m.category where  c.status='" + TYPES.TRANSACTION_STATUS.APPROVED + "' and trans_type='" + TYPES.TRANSACTION_TYPE.EXPENSE + "' and strftime('%m', `trans_time`) = '" + month + "'" + //bug: Use case: user removed category this month, but the trans entry was there for previous month
                " and  strftime('%Y', `trans_time`) = '" + year + "' group by m.category", null);
        return res;
    }

    public String getNow() {
        // set the format to sql date time
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public String getJavaDate(String sqltime) throws ParseException {
        SimpleDateFormat DFTS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formatteddate = new SimpleDateFormat("MMM, dd").format(DFTS.parse(sqltime));
        return formatteddate;
    }

    /********************* Income and Expense Details *************/


    public float getMyTotalIncome(String reqMonth){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select sum(amount) from MASTER where status = '" + TYPES.TRANSACTION_STATUS.APPROVED + "' and trans_type='" + TYPES.TRANSACTION_TYPE.INCOME + "'" +
                " and  strftime('%m', `trans_time`) = '" + reqMonth + "' and  strftime('%Y', `trans_time`) = '" + year + "'", null);
        if (res.moveToNext()) return res.getFloat(0);
        else return 0;
    }
    public Cursor getMyTotalIncome(){
        SQLiteDatabase db = this.getReadableDatabase();
        //Bug: should not use the year
        Cursor res = db.rawQuery("select strftime('%m', `trans_time`), sum(amount) from MASTER where status = '" + TYPES.TRANSACTION_STATUS.APPROVED + "' and trans_type='" + TYPES.TRANSACTION_TYPE.INCOME + "'" +
                " and   strftime('%Y', `trans_time`) = '" + year + "' group by strftime('%m', `trans_time`)", null);
        return res;
    }

    public Cursor getMyTotalExpense() {
        SQLiteDatabase db = this.getReadableDatabase();
        //Bug: should not use the year
        Cursor res = db.rawQuery("select strftime('%m', `trans_time`), sum(amount) from MASTER where status = '" + TYPES.TRANSACTION_STATUS.APPROVED + "' and trans_type='" + TYPES.TRANSACTION_TYPE.EXPENSE + "'" +
                " and   strftime('%Y', `trans_time`) = '" + year + "' group by strftime('%m', `trans_time`)", null);
        return res;
    }
    public float getMyTotalExpense(String reqMonth) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select sum(amount) from MASTER where status = '" + TYPES.TRANSACTION_STATUS.APPROVED + "' and trans_type='" + TYPES.TRANSACTION_TYPE.EXPENSE + "'" +
                " and  strftime('%m', `trans_time`) = '" + reqMonth + "' and  strftime('%Y', `trans_time`) = '" + year + "'", null);
        if (res.moveToNext()) return res.getFloat(0);
        else return 0;
    }
    public int getCashExpense() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select sum(amount) from MASTER where trans_source='" + TYPES.TRANSACTION_SOURCE.CASH + "' and trans_type = '" + TYPES.TRANSACTION_TYPE.EXPENSE + "' and status = '" + TYPES.TRANSACTION_STATUS.APPROVED + "' " +
                " and strftime('%m', `trans_time`) = '" + month + "' and strftime('%Y', `trans_time`) = '" + year + "'", null);
        if (res.moveToNext()) {
            if (res.getString(0) == null) return 0;

            return Integer.parseInt(res.getString(0));
        } else {
            return 0;
        }
    }

    public String getExpensebyDay(String day, String category_filter) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "select sum(amount) from master where status = '" + TYPES.TRANSACTION_STATUS.APPROVED + "' and trans_type ='"+TYPES.TRANSACTION_TYPE.EXPENSE   +"'" +
                "and strftime('%m', `trans_time`) = '" + month + "' and strftime('%d', `trans_time`) = '" + day + "' and  " +
                "strftime('%Y', `trans_time`) = '" + year + "' and  category like '" + category_filter + "' group by strftime('%d', `trans_time`)";
        //Log.e("Sql",sql);
        Cursor res = db.rawQuery(sql, null);
        if (res.moveToNext()) {
            if (res.getString(0) == null) return "0";
            return res.getString(0);
        } else {
            return "0";
        }
    }

    public int getMyExpenseByCategory(String category, String from, String to) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "select sum(amount) from MASTER  where status = '" + TYPES.TRANSACTION_STATUS.APPROVED + "' and trans_type='" + TYPES.TRANSACTION_TYPE.EXPENSE + "' " +
                " and trans_time between  '" + from + "' and '" + to + "'  and category = '" + category + "'";
        if (BuildConfig.DEBUG) {
            Log.e(Constants.TAG, "SQL: " + sql);
        }
        Cursor res = db.rawQuery(sql, null);
        if (res.moveToNext()) {
            return res.getInt(0);
        }
        return 0;
    }



    public Cursor getMyTotalTransction() {
        SQLiteDatabase db = this.getReadableDatabase();
        //Bug: should not use the year
        Cursor res = db.rawQuery("select strftime('%m', `trans_time`), sum(amount) from MASTER where status = '" + TYPES.TRANSACTION_STATUS.APPROVED + "'" +
                " and   strftime('%Y', `trans_time`) = '" + year + "' group by strftime('%m', `trans_time`)", null);
        return res;
    }

    public Cursor getMyExpenseByDay() {
        SQLiteDatabase db = this.getReadableDatabase();
        //Bug: should not use the year
        Cursor res = db.rawQuery("select strftime('%d', `trans_time`), sum(amount) from MASTER where status = '" + TYPES.TRANSACTION_STATUS.APPROVED + "' and trans_type='" + TYPES.TRANSACTION_TYPE.EXPENSE + "'" +
                " and   strftime('%Y', `trans_time`) = '" + year + "' and strftime('%m', `trans_time`) = '" + month + "' group by strftime('%d', `trans_time`)", null);
        return res;
    }

    /*Budget Related Methods */

    public Cursor getMyExpenseByDay(int day) {
        SQLiteDatabase db = this.getReadableDatabase();
        //Bug: should not use the year
        Cursor res = db.rawQuery("select  category , amount from MASTER where status = '" + TYPES.TRANSACTION_STATUS.APPROVED + "' and trans_type='" + TYPES.TRANSACTION_TYPE.EXPENSE + "'" +
                " and   strftime('%Y', `trans_time`) = '" + year + "' and strftime('%m', `trans_time`) = '" + month + "' and strftime('%d', `trans_time`) = '" + day + "'", null);
        return res;
    }

    public Cursor getMyExpenseByCategory() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select category,sum(amount) from MASTER  where status = '" + TYPES.TRANSACTION_STATUS.APPROVED + "' and trans_type='" + TYPES.TRANSACTION_TYPE.EXPENSE + "' " +
                " and strftime('%m', `trans_time`) = '" + month + "' and  strftime('%Y', `trans_time`) = '" + year + "' group by category", null);
        return res;
    }


    /****** Application Logger ****/





    public interface Constants {
        String TAG = "app:DBHelper";
    }

}