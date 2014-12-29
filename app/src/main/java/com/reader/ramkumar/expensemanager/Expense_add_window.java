package com.reader.ramkumar.expensemanager;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import com.reader.ramkumar.expensemanager.adapter.ListAdapterForRadioButton;
import com.reader.ramkumar.expensemanager.adapter.ListAdapterRadioModel;

import java.util.ArrayList;
import java.util.List;


public class Expense_add_window extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_add_window);
        ArrayAdapter<ListAdapterRadioModel> adapter = new ListAdapterForRadioButton(this, getModel());
        setListAdapter(adapter);
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
        List<ListAdapterRadioModel> list = new ArrayList<ListAdapterRadioModel>();
        list.add(get("Linux"));
        list.add(get("Windows7"));
        list.add(get("Suse"));
        list.add(get("Eclipse"));
        list.add(get("Ubuntu"));
        list.add(get("Solaris"));
        list.add(get("Android"));
        list.add(get("iPhone"));
        // Initially select one of the items
        list.get(1).setSelected(true);
        return list;
    }

    private ListAdapterRadioModel get(String s) {
        return new ListAdapterRadioModel(s);
    }
}
