package com.reader.ramkumar.expensemanager;

import android.app.Activity;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.reader.ramkumar.expensemanager.adapter.ListAdapterForRadioButton;
import com.reader.ramkumar.expensemanager.adapter.ListAdapterRadioModel;

import java.util.ArrayList;
import java.util.List;


public class Expense_add_window extends ListActivity {

    Button btn_accept,btn_date;
    List<ListAdapterRadioModel> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_add_window);
        ArrayAdapter<ListAdapterRadioModel> adapter = new ListAdapterForRadioButton(this, getModel());

        //business logic


        onAcceptButtonClick();
        onChooseDateClick(getApplicationContext());

        setListAdapter(adapter);


    }

    public void onAcceptButtonClick() {

        btn_accept = (Button) findViewById(R.id.btn_accept);

        btn_accept.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Toast.makeText(getApplicationContext(), list.get(listIndex).getName(),
                        Toast.LENGTH_LONG).show();


            }

        });

    }

    CalendarView calendar;

    public void onChooseDateClick( final Context context){

        btn_date = (Button) findViewById(R.id.btn_date);

        btn_date.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // custom dialog
                final Dialog dialog = new Dialog(Expense_add_window.this);
                dialog.setContentView(R.layout.calender);
                dialog.setTitle("Title...");

                // set the custom dialog components - text, image and button

                LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

    private RadioButton listRadioButton = null;
    int listIndex = -1;
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
