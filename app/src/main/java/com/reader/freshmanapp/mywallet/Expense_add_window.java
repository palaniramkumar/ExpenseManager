package com.reader.freshmanapp.mywallet;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.reader.freshmanapp.mywallet.adapter.ListAdapterForRadioButton;
import com.reader.freshmanapp.mywallet.adapter.ListAdapterRadioModel;
import com.reader.freshmanapp.mywallet.db.DBHelper;
import com.reader.freshmanapp.mywallet.util.Common;
import com.reader.freshmanapp.mywallet.util.NumbPad;
import com.reader.freshmanapp.mywallet.util.TYPES;
import com.reader.freshmanapp.mywallet.util.UndoBar;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class Expense_add_window extends ListActivity {

    Button btn_accept;
    List<ListAdapterRadioModel> list;
    CalendarView calendar;
    int listIndex = -1;
    Button btn_amount;
    RadioGroup trans_src;
    RadioGroup trans_type;
    EditText edit_notes;
    Button btn_date;
    DBHelper db;
    String bank_name = null;
    String sms_id = null;
    String ENTRY_TYPE = "ADD";
    String recid;
    String place;
    String geo_tag;
    ArrayAdapter<ListAdapterRadioModel> adapter;
    private Animation inAnimation;
    private Animation outAnimation;
    private RadioButton listRadioButton = null;
    private String entryDesc = "OTHERS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the message from the intent
        Intent intent = getIntent();
        recid = intent.getStringExtra("RECID");
        db = new DBHelper(this);

        setContentView(R.layout.activity_expense_add_window);
        adapter = new ListAdapterForRadioButton(this, getModel());

        //finding all fields
        btn_amount = ((Button) findViewById(R.id.btn_amount));
        trans_src = ((RadioGroup) findViewById(R.id.rdo_trans_src));
        trans_type = ((RadioGroup) findViewById(R.id.rdo_trans_type));
        edit_notes = ((EditText) findViewById(R.id.edit_notes));
        btn_date = ((Button) findViewById(R.id.btn_date));
        Button btn_delete = ((Button) findViewById(R.id.btn_delete));
        Button btnDecline = (Button) findViewById(R.id.btn_decline);
        final Button edit_amount = (Button) findViewById(R.id.btn_amount);
        btn_accept = (Button) findViewById(R.id.btn_accept);
        //business logic
        onAcceptButtonClick();
        onChooseDateClick(getApplicationContext());

        trans_type.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {


            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (BuildConfig.DEBUG) {
                    Log.e(Constants.TAG, "Clicked ATM Radio" + ((RadioButton) findViewById(checkedId)).getText().toString());
                }
                String selected_String = ((RadioButton) findViewById(checkedId)).getText().toString();
                if (selected_String.equals("ATM")) {
                    hide(findViewById(R.id.rdo_cash));
                    hide(findViewById(R.id.list_holder));
                    hide(findViewById(R.id.list_holder_seperator));
                    show(findViewById(R.id.trans_src_holder));
                    /*findViewById(R.id.rdo_cash).setVisibility(View.GONE);
                    findViewById(R.id.list_holder).setVisibility(View.GONE);
                    findViewById(R.id.list_holder_seperator).setVisibility(View.GONE);*/

                    ((RadioButton) findViewById(R.id.rdo_debit_card)).setChecked(true);
                }
                else if (selected_String.equals("INCOME")){
                    hide(findViewById(R.id.rdo_cash));
                    hide(findViewById(R.id.list_holder));
                    hide(findViewById(R.id.list_holder_seperator));
                    hide(findViewById(R.id.trans_src_holder));
                }
                else {
                    show(findViewById(R.id.rdo_cash));
                    show(findViewById(R.id.list_holder));
                    show(findViewById(R.id.list_holder_seperator));
                    show(findViewById(R.id.trans_src_holder));
                   /* findViewById(R.id.rdo_cash).setVisibility(View.VISIBLE);
                    findViewById(R.id.list_holder).setVisibility(View.VISIBLE);
                    findViewById(R.id.list_holder_seperator).setVisibility(View.VISIBLE);*/
                    ((RadioButton) findViewById(R.id.rdo_cash)).setChecked(true);
                }
            }
        });

        if (recid != null) {
            edit_amount.setText(recid);//code has bug
            Cursor sms = db.getFromMasterByID(Integer.parseInt(recid));
            if (sms != null) {
                sms.moveToFirst();
                btn_delete.setVisibility(Button.VISIBLE);
                ENTRY_TYPE = "UPDATE";
                System.out.println("Rec id: " + recid);
                btn_amount.setText(sms.getString(1));
                edit_notes.setText(sms.getString(sms.getColumnIndex(DBHelper.MASTER_COLUMN_NOTES)));


                btn_date.setText(sms.getString(sms.getColumnIndex("local_time")));
                /* selection for TRANSACTION SOURCE */
                if (sms.getString(sms.getColumnIndex(DBHelper.MASTER_COLUMN_TRANS_SOURCE)).equalsIgnoreCase(TYPES.TRANSACTION_SOURCE.CREDIT_CARD.toString()))
                    trans_src.check(R.id.rdo_credit_card);
                else if (sms.getString(sms.getColumnIndex(DBHelper.MASTER_COLUMN_TRANS_SOURCE)).equalsIgnoreCase(TYPES.TRANSACTION_SOURCE.DEBIT_CARD.toString()))
                    trans_src.check(R.id.rdo_debit_card);
                else if (sms.getString(sms.getColumnIndex(DBHelper.MASTER_COLUMN_TRANS_SOURCE)).equalsIgnoreCase(TYPES.TRANSACTION_SOURCE.CASH.toString()))
                    trans_src.check(R.id.rdo_cash);

                /* selection for TRANSACTION TRPE */
                if (sms.getString(sms.getColumnIndex(DBHelper.MASTER_COLUMN_TRANS_TYPE)).equalsIgnoreCase(TYPES.TRANSACTION_TYPE.CASH_VAULT.toString()))
                    trans_type.check(R.id.rdo_ATM_WRL);
                else if (sms.getString(sms.getColumnIndex(DBHelper.MASTER_COLUMN_TRANS_TYPE)).equalsIgnoreCase(TYPES.TRANSACTION_TYPE.EXPENSE.toString()))
                    trans_type.check(R.id.rdo_expense);
                else if (sms.getString(sms.getColumnIndex(DBHelper.MASTER_COLUMN_TRANS_TYPE)).equalsIgnoreCase(TYPES.TRANSACTION_TYPE.INCOME.toString()))
                    trans_type.check(R.id.rdo_income);
                String category = sms.getString(sms.getColumnIndex(DBHelper.MASTER_COLUMN_CATEGORY));
                System.out.println(category);
                int selectedIndex = getListIndex(category);
                if (selectedIndex != -1) {
                    list.get(selectedIndex).setSelected(true); //bug: getListIndex(category) is retuning -1 which led to fc. need to check theList adapter.
                    listIndex = selectedIndex;
                }

                /*storing it in global variable*/
                bank_name = sms.getString(sms.getColumnIndex(DBHelper.MASTER_COLUMN_BANK_NAME));
                sms_id = sms.getString(sms.getColumnIndex(DBHelper.MASTER_COLUMN_SMS_ID));
                place = sms.getString(sms.getColumnIndex(DBHelper.MASTER_COLUMN_PLACE));
                geo_tag = sms.getString(sms.getColumnIndex(DBHelper.MASTER_COLUMN_GEO_TAG));
                //btn_delete
                btn_accept.setText("Update");

            }
        } else {
            edit_amount.setText("0.00");
            ENTRY_TYPE = "ADD";
        }


        btnDecline.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }

        });
        btn_delete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                db.updateMaster(Integer.parseInt(recid), db.MASTER_COLUMN_STATUS, TYPES.TRANSACTION_STATUS.DELETED + "");
                 /*returning results*/

                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", "Success");
                setResult(RESULT_OK, returnIntent);
                finish();
            }

        });

        edit_amount.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onSelectAmount(edit_amount.getText().toString());
            }

        });
        if (adapter.getViewTypeCount() > 0) //safe condition to avoid fc - Issue #30
            setListAdapter(adapter);

        onSelectAmount(edit_amount.getText().toString());


    }

    public void onAcceptButtonClick() {


        btn_accept.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                String amount = btn_amount.getText().toString();
                int selectedId = trans_src.getCheckedRadioButtonId();
                String trans_src = ((RadioButton) findViewById(selectedId)).getText().toString();
                selectedId = trans_type.getCheckedRadioButtonId();
                String trans_type = ((RadioButton) findViewById(selectedId)).getText().toString();
                String category;
                String notes = edit_notes.getText().toString();
                String date = btn_date.getText().toString();
                if (date.equalsIgnoreCase("Today"))
                    date = DBHelper.getDateTime(new Date());
                else
                    date = DBHelper.getDateTime(date);

                if (trans_src.contains("Credit"))
                    trans_src = TYPES.TRANSACTION_SOURCE.CREDIT_CARD.toString();
                if (trans_src.contains("Debit"))
                    trans_src = TYPES.TRANSACTION_SOURCE.DEBIT_CARD.toString();
                if (trans_src.contains("Cash"))
                    trans_src = TYPES.TRANSACTION_SOURCE.CASH.toString();

                if (trans_type.equalsIgnoreCase("expense"))
                    trans_type = TYPES.TRANSACTION_TYPE.EXPENSE.toString();
                if (trans_type.equalsIgnoreCase("income"))
                    trans_type = TYPES.TRANSACTION_TYPE.INCOME.toString();
                if (trans_type.equalsIgnoreCase("atm"))
                    trans_type = TYPES.TRANSACTION_TYPE.CASH_VAULT.toString(); //ATM transactions considered as cash vault

                DBHelper db = new DBHelper(getApplicationContext());


                String result = "Failed to Add";

                if (amount == null || amount.trim().equals("") || Float.parseFloat(amount) == 0) {
                    UndoBar undobar = new UndoBar(Expense_add_window.this);
                    undobar.show("Amount should not be empty or zero");
                    return;
                }

                if (listIndex == -1 && trans_type.equals(TYPES.TRANSACTION_TYPE.EXPENSE.toString())) {
                    UndoBar undobar = new UndoBar(Expense_add_window.this);
                    undobar.show("Select at least one Category");
                    return;
                }
                if(!trans_type.equals(TYPES.TRANSACTION_TYPE.INCOME.toString())){
                    category = trans_type.equals(TYPES.TRANSACTION_TYPE.CASH_VAULT.toString()) ? db.ATM : list.get(listIndex).getName();
                    result = trans_type.equals(TYPES.TRANSACTION_TYPE.CASH_VAULT.toString()) ? Common.CURRENCY + " " + amount + " to ATM" : Common.CURRENCY + " " + amount + " to " + category + " " + trans_type;
                }
                else{
                    category = TYPES.TRANSACTION_TYPE.INCOME.toString();
                    result = Common.CURRENCY + " " + amount + " added to your "+category;
                }


                if (ENTRY_TYPE.equalsIgnoreCase("ADD")) {

                    System.out.println("Expense Source: " + trans_src);
                    System.out.println("Category : " + category + " , Index: " + listIndex);

                    db.insertMaster(amount, null, trans_src, trans_type, category, notes, null, entryDesc, date, db.getNow(), null, null, null, null,null, TYPES.TRANSACTION_STATUS.APPROVED.toString()); //date:datetime() is returning just a text not a value
                    result = "Added " + result;
                }
                if (ENTRY_TYPE.equalsIgnoreCase("UPDATE")) {//this code is for future enhancement
                    db.updateMaster(Integer.parseInt(recid), amount, bank_name, trans_src, trans_type, category, notes, sms_id, entryDesc, date, db.getNow(), place, geo_tag, null, null, TYPES.TRANSACTION_STATUS.APPROVED.toString());
                    result = "Updated " + result;
                }


                /*returning results*/

                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", result);
                setResult(RESULT_OK, returnIntent);
                finish();


            }

        });

    }

    public void onSelectAmount(String amount) {

        // create an instance of NumbPad
        NumbPad np = new NumbPad(amount);
        // optionally set additional title
        //np.setAdditionalText("Enter the Amount");
        // show the NumbPad to capture input.
        np.show(this, NumbPad.HIDE_INPUT,
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
                        if (BuildConfig.DEBUG) {
                            Log.e(Constants.TAG, "Clicked Cancel Button");
                        }
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

                final AlertDialog.Builder dialog = new AlertDialog.Builder(Expense_add_window.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
                LayoutInflater inflater = getLayoutInflater();
                View iView = inflater.inflate(R.layout.calender, null, false);
                dialog.setView(iView);
                calendar = (CalendarView) iView.findViewById(R.id.calendarView1);
                //dialog.setContentView(R.layout.calender);
                dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dlg, int sumthin) {
                        Button edit_amount = (Button) findViewById(R.id.btn_date);
                        edit_amount.setText(db.getLocalDate(calendar.getDate() / 1000));
                        dlg.dismiss();

                    }
                });
                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dlg, int sumthin) {
                        dlg.dismiss();
                        //postrun.numPadCanceled();
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
            if (BuildConfig.DEBUG) {
                Log.e(Constants.TAG, "Clicked Menu");
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private List<ListAdapterRadioModel> getModel() {
        list = new ArrayList<ListAdapterRadioModel>();
        Cursor cur = db.getMyBudgetByCategory();
        while (cur.moveToNext())
            list.add(get(cur.getString(1)));

        // Initially select one of the items
        //list.get(1).setSelected(true);
        return list;
    }

    private ListAdapterRadioModel get(String s) {
        return new ListAdapterRadioModel(s);
    }

    public void onClickListView(View v) {
        TextView txt_view = (TextView) v.findViewById(R.id.label);
        for (int i = 0; i < list.size(); i++)
            list.get(i).setSelected(false);
        listIndex = getListIndex(txt_view.getText());
        list.get(listIndex).setSelected(true);
        adapter.notifyDataSetChanged();
    }

    public int getListIndex(Object object) {

        System.out.println("Object value:" + object);

        if (object == null) return -1;

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getName().equalsIgnoreCase(object.toString())) {
                return i;
            }
        }
        return -1;
    }

    void hide(final View view){

        if (view.getVisibility() == View.VISIBLE) {
            view.animate()
                    .translationY(0)
                    .alpha(0.0f)
                    .setDuration(500)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            view.setVisibility(View.GONE);
                        }
                    });
        }
    }
    void show(final View view){

        if(view.getVisibility() == View.GONE) {
            view.animate()
                    .translationY(0)
                    .alpha(1.0f)
                    .setDuration(500)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            view.setVisibility(View.VISIBLE);
                        }
                    });

        }
    }

    public interface Constants {
        String TAG = "app:ExpenseAddWindow";
    }

}
