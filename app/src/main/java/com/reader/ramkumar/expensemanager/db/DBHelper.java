package com.reader.ramkumar.expensemanager.db;

/**
 * Created by Ramkumar on 19/01/15.
 */

import java.security.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Locale;
import java.util.TimeZone;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

import com.reader.ramkumar.expensemanager.util.TYPES;
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
    public static final String MASTER_COLUMN_STATUS = "status"; //pending,deleted,accepted
    public static final String CATEGORY_TABLE_NAME = "category";

    //{ "Food & Drinks", "Home", "Fuel" ,"Groceries","Travel","Health","Entertainment","Shopping","BILL","UNCATEGORIZED"};
    public static final String UNCATEGORIZED = "UNCATEGORIZED";
    public static final String BILL_PAYMENT = "BILL";
    public static final String SHOPPING = "Shopping";
    public static final String TRAVEL = "Travel";

    public String month = "";
    public String year = "";
    public DBHelper(Context context)
    {

        super(context, DATABASE_NAME , null, 1);
        month= new SimpleDateFormat("MM").format(Calendar.getInstance().getTime());
        year  = new SimpleDateFormat("yyyy").format(Calendar.getInstance().getTime());

        //context.deleteDatabase(DATABASE_NAME); //force close if the db is not created
    }
    public DBHelper(Context context,String month)
    {

        super(context, DATABASE_NAME , null, 1);
        this.month=month;

        //context.deleteDatabase(DATABASE_NAME); //force close if the db is not created
    }
    public void deleteDB(Context context){
        context.deleteDatabase(DATABASE_NAME);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub

        db.execSQL(
                "create table MASTER " +
                        "(id integer primary key, amount text,bank_name text,trans_source text, trans_type text,category text DEFAULT '"+UNCATEGORIZED+"'," +
                        " notes text,sms_id integer UNIQUE,desc text, place text,trans_time DATETIME DEFAULT CURRENT_TIMESTAMP," +
                        "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP," +
                        "geo_tag text,SharedExpense text, SharedMembers text,status text)"
        );
        db.execSQL(
                "create table CATEGORY " +
                        "(id integer primary key, category text,amount text, "+
                        "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,status text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS MASTER");
        onCreate(db);
    }

    public void firstUser(){
        CharSequence[] categoryName = getDefaultCategory();
        for(int i=0;i<categoryName.length;i++)
        insertCategory(categoryName[i].toString(),"0",TYPES.TRANSACTION_STATUS.APPROVED.toString());
    }
    public boolean insertMaster  (String amount, String bank_name, String trans_source,
                                  String trans_type,String category,String notes,
                                  String sms_id,String desc,String trans_time,
                                  String timestamp,String place,
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
        contentValues.put("trans_time", trans_time);
        contentValues.put("timestamp", timestamp);
        contentValues.put("geo_tag", geo_tag);
        contentValues.put("SharedExpense", SharedExpense);
        contentValues.put("SharedMembers", SharedMembers);
        contentValues.put("status", status);
        contentValues.put("place", place);

        try {
            db.insert("MASTER", null, contentValues);
        }
        catch(SQLException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public boolean updateMaster(Integer id, String amount, String bank_name, String trans_source,
                                String trans_type,String category,String notes,
                                String sms_id,String desc,String trans_time,
                                String timestamp,String place,
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
        contentValues.put("trans_time", trans_time);
        contentValues.put("timestamp", timestamp);
        contentValues.put("geo_tag", geo_tag);
        contentValues.put("SharedExpense", SharedExpense);
        contentValues.put("SharedMembers", SharedMembers);
        contentValues.put("status", status);
        contentValues.put("place", place);
        db.update("MASTER", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    public boolean insertCategory (String name, String amount,String status)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("amount", amount);
        contentValues.put("category", name);
        contentValues.put("status", status);

        db.insert("category", null, contentValues);
        return true;
    }
    public Integer deleteCategory (int id)
    {

        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("CATEGORY",
                "id = ? ",
                new String[] { Integer.toString(id) });
    }
    public  int updateCategory(Integer id,String field,String value){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(field, value);
        int count = db.update("Category", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return count;

    }
    public  Cursor getCategory(Integer id){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery( "select * from CATEGORY where id = "+id, null );
        return res;

    }

    public int getCashExpense(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select sum(amount) from MASTER where trans_source='"+TYPES.TRANSACTION_SOURCE.CASH+"' and trans_type = '"+TYPES.TRANSACTION_TYPE.EXPENSE+"'" +
                " and strftime('%m', `trans_time`) = '"+month+"'", null );
        if(res.moveToNext()){
            if(res.getString(0) == null) return 0;
            System.out.println("Inside Cash Expense: "+res.getString(0));
            return Integer.parseInt(res.getString(0));
        }
        else{
            return 0;
        }
    }
    public String getExpensebyDay(String day){
        SQLiteDatabase db = this.getReadableDatabase();
        String sql="select sum(amount) from master where strftime('%m', `trans_time`) = '"+month+"' and strftime('%d', `trans_time`) = '"+day+"' group by strftime('%d', `trans_time`)";
        Cursor res =  db.rawQuery( sql, null );
        if(res.moveToNext()){
            if(res.getString(0) == null) return "0";
            return res.getString(0);
        }
        else{
            return "0";
        }
    }
    public int getCashVault(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select sum(amount) from MASTER where trans_type = '"+TYPES.TRANSACTION_TYPE.CASH_VAULT+"' and status = '"+TYPES.TRANSACTION_STATUS.APPROVED+"'" +
                "  and strftime('%m', `trans_time`) = '"+month+"'", null );
        if(res.moveToNext()){
            if(res.getString(0) == null) return 0;
            return Integer.parseInt(res.getString(0));
        }
        else{
            return 0;
        }
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, MASTER_TABLE_NAME);
        return numRows;
    }


    public  int updateMaster(Integer id,String field,String value){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(field, value);
        int count = db.update("MASTER", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return count;

    }
    public int updateMasterStatus(Integer id, String status)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("status", status);
        int count = db.update("MASTER", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return count;
    }

    public Integer deleteMaster (Integer id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("MASTER",
                "id = ? ",
                new String[] { Integer.toString(id) });
    }

    public Cursor getFromMaster(String field,String value)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from MASTER where "+field+" = '"+value+"' and status='"+TYPES.TRANSACTION_STATUS.APPROVED+"' and trans_type='"+TYPES.TRANSACTION_TYPE.EXPENSE+"' order by trans_time desc", null );
        return res;
    }
    public Cursor getMasterByStatus(String status)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from MASTER where status ='"+status+"'", null );
        return res;
    }
    public Cursor getMasterByStatus(String status,int days)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from MASTER where status ='"+status+"' and trans_time >= date('now', '-"+days+" days')" +
                "  AND trans_time <  date('now')", null );
        return res;
    }
    public int getLastSMSID(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select max(sms_id) from MASTER ", null );
        if(res.moveToNext()){
            if(res.getString(0) == null) return 0;
            return Integer.parseInt(res.getString(0));
        }
        else{
            return 0;
        }
    }
    public Cursor getFromMasterByID(int id)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select *,strftime('%d/%m/%Y',trans_time) local_time from MASTER where id="+id+"", null );
        return res;
    }

    /* Category Related Methods*/



    public CharSequence [] getDefaultCategory(){
        final CharSequence myList[] = { "Food & Drinks", "Home", "Fuel" ,"Groceries","Travel","Health","Entertainment","Shopping","BILL","UNCATEGORIZED"};
        return  myList;
    }

    public float getMyTotalExpense(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select sum(amount) from MASTER where status = '"+ TYPES.TRANSACTION_STATUS.APPROVED+"' and trans_type='"+TYPES.TRANSACTION_TYPE.EXPENSE+"'" +
                " and  strftime('%m', `trans_time`) = '"+month+"'", null );
        if(res.moveToNext())return res.getFloat(0);
        else return 0;
    }
    public Cursor getMyExpenseByCategory(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select category,sum(amount) from MASTER  where status = '"+ TYPES.TRANSACTION_STATUS.APPROVED+"' and trans_type='"+TYPES.TRANSACTION_TYPE.EXPENSE+"' " +
                " and strftime('%m', `trans_time`) = '"+month+"' group by category", null );
        return res;
    }

    /*Budget Related Methods */

    public Cursor getMyBudgetByCategory(){ //need to have history in order to maintain the prev month budget
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select id _id,category,amount from CATEGORY where status='"+TYPES.TRANSACTION_STATUS.APPROVED+"'", null );
        return res;
    }

    public int getBudget(){ //need to have history in order to maintain the prev month budget

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select sum(amount) from CATEGORY where status='"+TYPES.TRANSACTION_STATUS.APPROVED+"'", null );
        if(res.moveToNext())return res.getInt(0);
        return 0;
    }
    //select  c.category,c.amount,m.amount from category c left join master m on c.category=m.category where c.amount <> "0"
    public Cursor getBudgetSummary(){

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select  c.category,c.amount,sum(m.amount) from category c CROSS join master m " +
                "on c.category=m.category where  c.status='"+TYPES.TRANSACTION_STATUS.APPROVED+"' and trans_type='"+TYPES.TRANSACTION_TYPE.EXPENSE+"' and strftime('%m', `trans_time`) = '"+month+"'" + //bug: Use case: user removed category this month, but the trans entry was there for previous month
                " group by m.category", null );
        return res;
    }


    /*Date Conversion Methods */

    public Cursor getTransactionHistory(){
        String sql = "select case strftime('%m', trans_time) when '01' then 'January' when '02' then 'Febuary' when '03' then 'March' when '04' then 'April' when '05' then 'May' when '06' then 'June' when '07' then 'July' when '08' then 'August' when '09' then 'September' when '10' then 'October' when '11' then 'November' when '12' then 'December' else '' end\n" +
                "as month,strftime('%d',trans_time) day, category, amount,id,UPPER(notes) from MASTER where status ='"+TYPES.TRANSACTION_STATUS.APPROVED+"' and  strftime('%m', `trans_time`) = '"+month+"' order by day desc";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( sql, null );
        return res;
    }

    public static String getDateTime(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        //Date date = new Date();
        return dateFormat.format(date);
    }

    public  static String getDroidDate(long timestamp) {
        try{
            Calendar calendar = Calendar.getInstance();
            TimeZone tz = TimeZone.getDefault();
            calendar.setTimeInMillis(timestamp * 1000);
            calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date = (Date) calendar.getTime();
            return sdf.format(date);
        }catch (Exception e) {
        }
        return "";
    }
    public static String getDateTime(String dateinactivity) {
        int day=Integer.parseInt(dateinactivity.split("/")[0]);
        int month=Integer.parseInt(dateinactivity.split("/")[1])-1;
        int year=Integer.parseInt(dateinactivity.split("/")[2]);
        Calendar calendar = new GregorianCalendar(year,month,day);
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        dateFormat.setCalendar(calendar);
        return dateFormat.format(calendar.getTime());
    }
    public String getNow(){
        // set the format to sql date time
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }
    public String getJavaDate(String sqltime) throws ParseException{
        SimpleDateFormat DFTS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formatteddate= new SimpleDateFormat("MMM, dd").format(DFTS.parse(sqltime));
        return formatteddate;
    }

}