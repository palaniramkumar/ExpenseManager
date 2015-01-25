package com.reader.ramkumar.expensemanager;

import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;

import com.reader.ramkumar.expensemanager.adapter.ListAdapterForRadioButton;
import com.reader.ramkumar.expensemanager.adapter.ListAdapterRadioModel;
import com.reader.ramkumar.expensemanager.util.NumbPad;

import java.util.ArrayList;
import java.util.List;


public class Expense_add_window extends ListActivity {

    Button btn_accept, btn_date;
    List<ListAdapterRadioModel> list;
    CalendarView calendar;
    int listIndex = -1;
    private RadioButton listRadioButton = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the message from the intent
        Intent intent = getIntent();
        String mAmount = intent.getStringExtra("AMOUNT");

        setContentView(R.layout.activity_expense_add_window);
        ArrayAdapter<ListAdapterRadioModel> adapter = new ListAdapterForRadioButton(this, getModel());

        //business logic


        onAcceptButtonClick();
        onChooseDateClick(getApplicationContext());

        Button edit_amount = (Button) findViewById(R.id.btn_amount);
        if(mAmount!=null)
            edit_amount.setText(mAmount);
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
                if (listIndex != -1)
                    Toast.makeText(getApplicationContext(), list.get(listIndex).getName(),
                            Toast.LENGTH_LONG).show();
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

                        Toast.makeText(getBaseContext(), "Selected Date is\n\n"
                                        + dayOfMonth + " : " + month + " : " + year,
                                Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                });
                dialog.show();

                /*LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = mInflater.inflate( R.layout.calender,null); //buggy code need to fix


                calendar = (CalendarView) view.findViewById(R.id.calendar);

                // sets whether to show the week number.
                calendar.setShowWeekNumber(false);

                // sets the first day of week according to Calendar.
                // here we set Monday as the first day of the Calendar
                calendar.setFirstDayOfWeek(2);

                //The background color for the selected week.
                calendar.setSelectedWeekBackgroundColor(getResources().getColor(R.color.secondary_text_default_material_light));

                //sets the color for the dates of an unfocused month.
                calendar.setUnfocusedMonthDateColor(getResources().getColor(R.color.abc_primary_text_material_dark));

                //sets the color for the separator line between weeks.
                calendar.setWeekSeparatorLineColor(getResources().getColor(R.color.abc_primary_text_material_dark));

                //sets the color for the vertical bar shown at the beginning and at the end of the selected date.
                calendar.setSelectedDateVerticalBar(R.color.accent_material_dark);

                //sets the listener to be notified upon selected date change.
                calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                    //show the selected date as a toast
                    @Override
                    public void onSelectedDayChange(CalendarView view, int year, int month, int day) {
                        Toast.makeText(getApplicationContext(), day + "/" + month + "/" + year, Toast.LENGTH_LONG).show();

                    }
                });

                dialog.show();*/
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
        list.add(get("Online"));
        list.add(get("Restorent"));
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
