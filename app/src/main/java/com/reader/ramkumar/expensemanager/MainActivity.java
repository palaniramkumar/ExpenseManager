package com.reader.ramkumar.expensemanager;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.interfaces.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.melnykov.fab.FloatingActionButton;
import com.reader.ramkumar.expensemanager.adapter.ExpenseCard;
import com.reader.ramkumar.expensemanager.service.SMSListener;

import java.util.ArrayList;

import it.gmariotti.cardslib.library.internal.ViewToClickToExpand;
import it.gmariotti.cardslib.library.view.CardViewNative;


public class MainActivity extends ActionBarActivity implements NavigationDrawerCallbacks,
        FragmentHistory.OnFragmentInteractionListener, main.OnFragmentInteractionListener,
        PendingApproval.OnFragmentInteractionListener{

    private Toolbar mToolbar;
    private NavigationDrawerFragment mNavigationDrawerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";

        SMSListener smsReceiver = new SMSListener();

        registerReceiver(smsReceiver, new IntentFilter(SMS_RECEIVED));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_topdrawer);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mToolbar.setLayoutParams(params);
        mToolbar.setPadding(0,25,0,0);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.fragment_drawer);
        mNavigationDrawerFragment.setup(R.id.fragment_drawer, (DrawerLayout) findViewById(R.id.drawer), mToolbar);

    }

      @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        Toast.makeText(this, "Menu item selected -> " + position, Toast.LENGTH_SHORT).show();
        selectItem(position);
    }

    @Override
    public void onBackPressed() {
        if (mNavigationDrawerFragment.isDrawerOpen())
            mNavigationDrawerFragment.closeDrawer();
        else
            super.onBackPressed();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private void selectItem(int position) {

        Fragment newFragment = null;
        FragmentManager fragmentManager;
        switch (position) {
            case 0:
                newFragment = new main();
                // Insert the fragment by replacing any existing fragment
                fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.frame_container, newFragment)
                        .commit();
                setTitle("Expense Summary");
                break;
            case 1:
                newFragment = new PendingApproval();
                fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.frame_container, newFragment)
                        .commit();
                setTitle("Pending Approval");
                break;
            case 2:
                newFragment = new FragmentHistory();
                fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.frame_container, newFragment)
                        .commit();
                setTitle("History");
                break;


        }


        //DrawerList.setItemChecked(position, true);

        //DrawerLayout.closeDrawer(DrawerList);
    }


}
