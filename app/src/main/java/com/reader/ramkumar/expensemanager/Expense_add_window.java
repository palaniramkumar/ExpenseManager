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

import java.util.ArrayList;
import java.util.List;


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

    String ENTRY_TYPE="ADD";
    String mSMS;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the message from the intent
        Intent intent = getIntent();
        mSMS = intent.getStringExtra("RECID");

        setContentView(R.layout.activity_expense_add_window);
        ArrayAdapter<ListAdapterRadioModel> adapter = new ListAdapterForRadioButton(this, getModel());

        //finding all fields
        Button btn_amount = ((Button) findViewById(R.id.btn_amount));
        RadioGroup trans_src = ((RadioGroup) findViewById(R.id.rdo_trans_src));
        RadioGroup trans_type = ((RadioGroup) findViewById(R.id.rdo_trans_type));
        EditText edit_notes = ((EditText) findViewById(R.id.edit_notes));
        Button btn_date = ((Button) findViewById(R.id.btn_date));

        //business logic
        onAcceptButtonClick();
        onChooseDateClick(getApplicationContext());

        Button edit_amount = (Button) findViewById(R.id.btn_amount);
        if(mSMS!=null) {
            edit_amount.setText(mSMS);
            DBHelper db=new DBHelper(this);
            Cursor sms=db.getFromMasterByID(Integer.parseInt(mSMS));
            if(sms!=null){
                sms.moveToFirst();
                ENTRY_TYPE="UPDATE";
                btn_amount.setText(sms.getString(1));
                btn_amount.setEnabled(false);
            }
        }
        else
            edit_amount.setText("0.00");

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


    }

    public void onAcceptButtonClick() {

        btn_accept = (Button) findViewById(R.id.btn_accept);

        btn_accept.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (listIndex != -1) {
                    String amount = ((Button) findViewById(R.id.btn_amount)).getText().toString();
                    int selectedId = ((RadioGroup) findViewById(R.id.rdo_trans_src)).getCheckedRadioButtonId();
                    String trans_src = ((RadioButton) findViewById(selectedId)).getText().toString();
                    selectedId = ((RadioGroup) findViewById(R.id.rdo_trans_type)).getCheckedRadioButtonId();
                    String trans_type = ((RadioButton) findViewById(selectedId)).getText().toString();
                    String category = list.get(listIndex).getName();
                    String notes = ((EditText) findViewById(R.id.edit_notes)).getText().toString();
                    String date = ((Button) findViewById(R.id.btn_date)).getText().toString();

                    DBHelper db =new DBHelper(getApplicationContext());
                    if(ENTRY_TYPE.equalsIgnoreCase("ADD"))
                        db.insertMaster(amount,null,trans_src,trans_type,category,notes,null,entryDesc,null,null,null,null,null,null,null,TYPES.TRANSACTION_STATUS.APPROVED.toString());
                    if(ENTRY_TYPE.equalsIgnoreCase("UPDATE"))
                        db.updateMaster(Integer.parseInt(mSMS),amount,null/*bankname*/,trans_src,trans_type,category,notes,null,entryDesc,null,null,null,null,null,null,null, TYPES.TRANSACTION_STATUS.APPROVED.toString());

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
        list.add(get("Food"));
        list.add(get("Home"));
        list.add(get("Fuel"));
        list.add(get("Groceries"));
        list.add(get("Travel"));
        list.add(get("Medicine"));
        list.add(get("Restaurant"));
        list.add(get("Others"));
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
}
