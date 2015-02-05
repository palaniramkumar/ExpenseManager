package com.reader.ramkumar.expensemanager;

import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.reader.ramkumar.expensemanager.adapter.ListAdapterForRadioButton;
import com.reader.ramkumar.expensemanager.adapter.ListAdapterRadioModel;
import com.reader.ramkumar.expensemanager.db.DBHelper;
import com.reader.ramkumar.expensemanager.util.NumbPad;
import com.reader.ramkumar.expensemanager.util.TYPES;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class Expense_add_window extends ListActivity {

    Button btn_accept;
    List<ListAdapterRadioModel> list;
    CalendarView calendar;
    int listIndex = -1;
    private RadioButton listRadioButton = null;
    private String entryDesc = "OTHERS";
    Button btn_amount;
    RadioGroup trans_src;
    RadioGroup trans_type;
    EditText edit_notes;
    Button btn_date;
    DBHelper db;
    String bank_name=null;
    String sms_id=null;
    String ENTRY_TYPE="ADD";
    String recid;
    String place;
    String geo_tag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the message from the intent
        Intent intent = getIntent();
        recid = intent.getStringExtra("RECID");
        db=new DBHelper(this);

        setContentView(R.layout.activity_expense_add_window);
        ArrayAdapter<ListAdapterRadioModel> adapter = new ListAdapterForRadioButton(this, getModel());

        //finding all fields
        btn_amount = ((Button) findViewById(R.id.btn_amount));
        trans_src = ((RadioGroup) findViewById(R.id.rdo_trans_src));
        trans_type = ((RadioGroup) findViewById(R.id.rdo_trans_type));
        edit_notes = ((EditText) findViewById(R.id.edit_notes));
        btn_date = ((Button) findViewById(R.id.btn_date));

        //business logic
        onAcceptButtonClick();
        onChooseDateClick(getApplicationContext());

        Button edit_amount = (Button) findViewById(R.id.btn_amount);
        if(recid!=null) {
            edit_amount.setText(recid);//code has bug
            Cursor sms=db.getFromMasterByID(Integer.parseInt(recid));
            if(sms!=null){
                sms.moveToFirst();
                ENTRY_TYPE="UPDATE";
                btn_amount.setText(sms.getString(1));
                edit_notes.setText(sms.getString( sms.getColumnIndex(DBHelper.MASTER_COLUMN_NOTES) ));


                btn_date.setText(sms.getString( sms.getColumnIndex("local_time") ));
                /* selection for TRANSACTION SOURCE */
                if(sms.getString( sms.getColumnIndex(DBHelper.MASTER_COLUMN_TRANS_SOURCE)).equalsIgnoreCase(TYPES.TRANSACTION_SOURCE.CREDIT_CARD.toString()))
                    trans_src.check(R.id.rdo_credit_card);
                else if(sms.getString( sms.getColumnIndex(DBHelper.MASTER_COLUMN_TRANS_SOURCE)).equalsIgnoreCase(TYPES.TRANSACTION_SOURCE.DEBIT_CARD.toString()))
                    trans_src.check(R.id.rdo_debit_card);
                else if(sms.getString( sms.getColumnIndex(DBHelper.MASTER_COLUMN_TRANS_SOURCE)).equalsIgnoreCase(TYPES.TRANSACTION_SOURCE.CASH.toString()))
                    trans_src.check(R.id.rdo_cash);

                /* selection for TRANSACTION TRPE */
                if(sms.getString( sms.getColumnIndex(DBHelper.MASTER_COLUMN_TRANS_TYPE)).equalsIgnoreCase(TYPES.TRANSACTION_TYPE.CASH_VAULT.toString()))
                    trans_type.check(R.id.rdo_ATM_WRL);
                else if(sms.getString( sms.getColumnIndex(DBHelper.MASTER_COLUMN_TRANS_TYPE)).equalsIgnoreCase(TYPES.TRANSACTION_TYPE.EXPENSE.toString()))
                    trans_type.check(R.id.rdo_expense);
                else if(sms.getString( sms.getColumnIndex(DBHelper.MASTER_COLUMN_TRANS_TYPE)).equalsIgnoreCase(TYPES.TRANSACTION_TYPE.INCOME.toString()))
                    trans_type.check(R.id.rdo_income);
                String category= sms.getString( sms.getColumnIndex(DBHelper.MASTER_COLUMN_CATEGORY));
                System.out.println(category);
                list.get(getListIndex(category)).setSelected(true); //bug: getListIndex(category) is retuning -1 which led to fc. need to check theList adapter.

                /*storing it in global variable*/
                bank_name = sms.getString( sms.getColumnIndex(DBHelper.MASTER_COLUMN_BANK_NAME) );
                sms_id = sms.getString( sms.getColumnIndex(DBHelper.MASTER_COLUMN_SMS_ID) );
                place =sms.getString( sms.getColumnIndex(DBHelper.MASTER_COLUMN_PLACE) );
                geo_tag =sms.getString( sms.getColumnIndex(DBHelper.MASTER_COLUMN_GEO_TAG) );

                btn_amount.setEnabled(false);
            }
        }
        else {
            edit_amount.setText("0.00");
            ENTRY_TYPE="ADD";
        }

        Button btnDecline = (Button) findViewById(R.id.btn_decline);

        btnDecline.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }

        });

        edit_amount.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onSelectAmount();
            }

        });

        setListAdapter(adapter);

      /*  Cursor cur = db.getAllFromMaster();
        while(cur.moveToNext())
            System.out.println(cur.getString(0)+":"+cur.getString(1)+":"+cur.getString(2)+":"+cur.getString(3)+":"+cur.getString(4)+":"+cur.getString(5)+":"+cur.getString(6)+":"+cur.getString(7)+":"+cur.getString(8)+":"+cur.getString(14)+":");
    */

    }

    public void onAcceptButtonClick() {

        btn_accept = (Button) findViewById(R.id.btn_accept);

        btn_accept.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (listIndex != -1) {
                    String amount = btn_amount.getText().toString();
                    int selectedId = trans_src.getCheckedRadioButtonId();
                    String trans_src = ((RadioButton) findViewById(selectedId)).getText().toString();
                    selectedId = trans_type.getCheckedRadioButtonId();
                    String trans_type = ((RadioButton) findViewById(selectedId)).getText().toString();
                    String category = list.get(listIndex).getName();
                    String notes = edit_notes.getText().toString();
                    String date = btn_date.getText().toString();
                    if(date.equalsIgnoreCase("Today"))
                        date = DBHelper.getDateTime(new Date());
                    else
                        date=DBHelper.getDateTime(date);

                    DBHelper db =new DBHelper(getApplicationContext());
                    if(ENTRY_TYPE.equalsIgnoreCase("ADD") ) {
                        if(trans_src.equalsIgnoreCase("credit")) trans_src=TYPES.TRANSACTION_SOURCE.CREDIT_CARD.toString();
                        if(trans_src.equalsIgnoreCase("debit")) trans_src=TYPES.TRANSACTION_SOURCE.DEBIT_CARD.toString();
                        if(trans_src.equalsIgnoreCase("cash")) trans_src=TYPES.TRANSACTION_SOURCE.CASH.toString();

                        if(trans_type.equalsIgnoreCase("expense")) trans_type=TYPES.TRANSACTION_TYPE.EXPENSE.toString();
                        if(trans_type.equalsIgnoreCase("income")) trans_type=TYPES.TRANSACTION_TYPE.INCOME.toString();
                        if(trans_type.equalsIgnoreCase("atm")) trans_type=TYPES.TRANSACTION_TYPE.CASH_VAULT.toString(); //ATM transactions considered as cash vault

                        db.insertMaster(amount, null, trans_src, trans_type, category, notes, null, entryDesc, date, "datetime()", null, null, null, null, TYPES.TRANSACTION_STATUS.APPROVED.toString());
                    }
                    if(ENTRY_TYPE.equalsIgnoreCase("UPDATE"))//this code is for future enhancement
                        db.updateMaster(Integer.parseInt(recid),amount,bank_name,trans_src,trans_type,category,notes,sms_id,entryDesc,date,"datetime()",place,geo_tag,null,null, TYPES.TRANSACTION_STATUS.APPROVED.toString());

                    Toast.makeText(getApplicationContext(), "Successfully Added",
                            Toast.LENGTH_LONG).show();
                    finish();
                }
                else
                    Toast.makeText(getApplicationContext(), "Please Select From the List",
                            Toast.LENGTH_LONG).show();


            }

        });

    }

    public void onSelectAmount() {

        // create an instance of NumbPad
        NumbPad np = new NumbPad();
        // optionally set additional title
        //np.setAdditionalText("Enter the Amount");
        // show the NumbPad to capture input.
        np.show(this, "Enter the Amount", NumbPad.HIDE_INPUT,
                new NumbPad.numbPadInterface() {
                    // This is called when the user click the 'Ok' button on the dialog
                    // value is the captured input from the dialog.
                    public String numPadInputValue(String value) {
                        Button edit_amount = (Button) findViewById(R.id.btn_amount);
                        edit_amount.setText(value);
                        return null;
                    }

                    // This is called when the user clicks the 'Cancel' button on the dialog
                    public String numPadCanceled() {
                        // generate a toast message to inform the user that the pin
                        // capture was canceled
                        Toast.makeText(getApplicationContext(),
                                "Amount Cancelled", Toast.LENGTH_LONG).show();
                        return null;
                    }
                });

        //URL: http://www.tomswebdesign.net/Articles/Android/number-pad-input-class.html
    }

    public void onChooseDateClick(final Context context) {

        btn_date = (Button) findViewById(R.id.btn_date);

        btn_date.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                final Dialog dialog = new Dialog(Expense_add_window.this);

                dialog.setContentView(R.layout.calender);
                calendar = (CalendarView) dialog.findViewById(R.id.calendarView1);


                calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

                    @Override
                    public void onSelectedDayChange(CalendarView view, int year, int month,
                                                    int dayOfMonth) {
                        // TODO Auto-generated method stub
                        Button edit_amount = (Button) findViewById(R.id.btn_date);
                        edit_amount.setText(dayOfMonth+"/"+(month+1)+"/"+year);
                        //Toast.makeText(getBaseContext(), "Selected Date is\n\n" + dayOfMonth + " : " + month + " : " + year,Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                });
                dialog.show();

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.expense_add_window, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private List<ListAdapterRadioModel> getModel() {
        list = new ArrayList<ListAdapterRadioModel>();
        Cursor cur =db.getMyBudgetByCategory();
        while (cur.moveToNext())
        list.add(get(cur.getString(1)));

        // Initially select one of the items
        //list.get(1).setSelected(true);
        return list;
    }

    private ListAdapterRadioModel get(String s) {
        return new ListAdapterRadioModel(s);
    }

    public void onClickRadioButton(View v) {
        View vMain = ((View) v.getParent());
        int newIndex = ((ViewGroup) vMain.getParent()).indexOfChild(vMain);
        if (listIndex == newIndex) return;

        if (listRadioButton != null) {
            listRadioButton.setChecked(false);
        }
        listRadioButton = (RadioButton) v;
        listIndex = newIndex;
    }

    public int getListIndex(Object object){
        for(int i=0;i<list.size();i++){
            if(list.get(0).getName().equalsIgnoreCase(object.toString())){
                return i;
            }
        }
        return -1;
    }

}
