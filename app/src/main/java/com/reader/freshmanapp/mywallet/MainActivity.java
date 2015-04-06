package com.reader.freshmanapp.mywallet;

import android.app.AlarmManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.reader.freshmanapp.SMSparser.SMS;
import com.reader.freshmanapp.mywallet.db.DBCategoryMap;
import com.reader.freshmanapp.mywallet.db.DBHelper;
import com.reader.freshmanapp.mywallet.service.SMSListener;
import com.reader.freshmanapp.mywallet.service.SummaryReceiver;
import com.reader.freshmanapp.mywallet.util.AboutBox;
import com.reader.freshmanapp.mywallet.util.Common;
import com.reader.freshmanapp.mywallet.util.DbCSVBackup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.UUID;


public class MainActivity extends ActionBarActivity implements NavigationDrawerCallbacks,
        FragmentHistory.OnFragmentInteractionListener, main.OnFragmentInteractionListener,
        PendingApproval.OnFragmentInteractionListener, Categories.OnFragmentInteractionListener,
        ExpenseTrend.OnFragmentInteractionListener {

    private static String DB_NAME = "MyExpense.db";
    Fragment newFragment = null;
    ViewGroup mContainer;
    FragmentManager fragmentManager;
    private Toolbar mToolbar;
    private NavigationDrawerFragment mNavigationDrawerFragment;
    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";

        SMSListener smsReceiver = new SMSListener();
        try {
            registerReceiver(smsReceiver, new IntentFilter(SMS_RECEIVED));
        } catch (Exception e) {
            e.printStackTrace();
        }


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_topdrawer);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mToolbar.setLayoutParams(params);
        mToolbar.setPadding(0, 5, 0, 0); //modify the text and other object position
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //getSupportActionBar().setDisplayShowTitleEnabled(false);


        mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.fragment_drawer);
        mNavigationDrawerFragment.setup(R.id.fragment_drawer, (DrawerLayout) findViewById(R.id.drawer), mToolbar);

        //Set the alarm to 10 seconds from now
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 30);
        calendar.set(Calendar.AM_PM, Calendar.PM);

        Intent myIntent = new Intent(getBaseContext(), SummaryReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, myIntent, 0);
        ;
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 24 * 60 * 60 * 1000, pendingIntent);
        //currently 24 hours


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        //Toast.makeText(this, "Menu item selected -> " + position, Toast.LENGTH_SHORT).show();
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

    public void aboutApp(MenuItem item) {
        //Toast.makeText(this, "Hello World", Toast.LENGTH_LONG).show();
        AboutBox.Show(MainActivity.this);

    }
    public void onclickHelp(MenuItem item) {

        Intent intent = new Intent(MainActivity.this,
                Help.class);
        startActivity(intent);

    }
    public void hideUIObject(){
        //invalidate all UI object
        TextView header = (TextView) findViewById(R.id.banner_header);
        if(header!=null)
            header.setText("");
        TextView progress_txt = (TextView) findViewById(R.id.txt_progress);
        if(progress_txt!=null)
            progress_txt.setText("");
        ProgressBar master_progressBar = (ProgressBar) findViewById(R.id.loading_spinner);
        if(master_progressBar!=null)
            master_progressBar.setVisibility(View.INVISIBLE);
    }
    public void refreshSMS(final MenuItem item) {

        db = new DBHelper(getApplicationContext());

        class MyAsyncTask extends AsyncTask<Void, Void, Integer> {

            @Override
            protected void onPreExecute() {

                Fragment frag = getFragmentManager().findFragmentById(R.id.frame_container);
                if (frag != null && (frag.getView() != null)) {
                    ProgressBar progressBar = ((ProgressBar) frag.getView().findViewById(R.id.loading_spinner));
                    if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
                }
            }

            @Override
            protected void onPostExecute(Integer result) {

               hideUIObject();
                Fragment frag = getFragmentManager().findFragmentById(R.id.frame_container);
                if (frag != null && (frag.getView() != null)) {
                    ProgressBar progressBar = ((ProgressBar) frag.getView().findViewById(R.id.loading_spinner));
                    if (progressBar != null) progressBar.setVisibility(View.INVISIBLE);
                }

                newFragment = new main();
                // Insert the fragment by replacing any existing fragment
                fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.frame_container, newFragment)
                        .commit();
                setTitle("Summary");
            }

            @Override
            protected Integer doInBackground(Void... params) {
                /** New User -  display demo page **/
                /* first user check */
                SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                String endpoint = sharedPref.getString("Endpoint", "0");
                if (endpoint.equalsIgnoreCase("0")) {
                    DBCategoryMap dbCatMap = new DBCategoryMap(MainActivity.this);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("Endpoint", UUID.randomUUID().toString());
                    editor.commit();
                    if (BuildConfig.DEBUG) {
                        Log.e(Constants.TAG, "New User");
                    }
                    db.firstUser();
                    dbCatMap.firstTime();
                    db.close();
                    dbCatMap.close();
                } else {
                    if (BuildConfig.DEBUG) {
                        Log.e(Constants.TAG, "Existing user");
                    }

                    //else code
                }

                if (item != null || db.getLastSMSID() == 0) {
                    SMS.syncSMS(getApplicationContext(), true, null);
                }
                db.close();



                return 0;
            }

        }
        new MyAsyncTask().execute();

    }

    private void selectItem(int position) {


        switch (position) {
            case 0:
                refreshSMS(null);
                break;
            case 1:

                newFragment = new ExpenseTrend();
                // Insert the fragment by replacing any existing fragment
                fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.frame_container, newFragment)
                        .addToBackStack(null)
                        .commit();
                setTitle("Trend");
                break;
            case 2:
                newFragment = new PendingApproval();
                fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.frame_container, newFragment)
                        .addToBackStack(null)
                        .commit();
                setTitle("Know Your Bill");
                break;
            case 3:
                newFragment = new FragmentHistory();
                fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.frame_container, newFragment)
                        .addToBackStack(null)
                        .commit();
                setTitle("History");
                break;
            case 4:
                newFragment = new Categories();
                fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.frame_container, newFragment)
                        .addToBackStack(null)
                        .commit();
                setTitle("Category List");
                break;
            case 5:
                Intent intent = new Intent(MainActivity.this,
                        PrefsActivity.class);

                startActivity(intent);
                break;
            case 6:
                DbCSVBackup backup = new DbCSVBackup(getApplicationContext());
                if(backup.export())
                    Toast.makeText(this, "Export Completed" , Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, "Export failed", Toast.LENGTH_SHORT).show();
                break;
            case 7:
                try {

                    Toast.makeText(this, "Backup Completed" + copyfile(), Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Backup failed", Toast.LENGTH_SHORT).show();
                }
                break;
            case 8:
                try {

                    new DBHelper(getApplicationContext()).deleteDB(getApplicationContext());
                    Toast.makeText(this, "Delete Completed", Toast.LENGTH_SHORT).show();


                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Backup failed", Toast.LENGTH_SHORT).show();
                }
                break;

        }
        if(position!=1)
            hideUIObject();

        if (BuildConfig.DEBUG) {
            Log.e(Constants.TAG, "Clicked " + position);
        }

        //DrawerList.setItemChecked(position, true);

        //DrawerLayout.closeDrawer(DrawerList);
    }

    public boolean checkDataBase() {
        File dbFile = new File(getApplicationContext().getDatabasePath("MyExpense.db").getPath());
        return dbFile.exists();
    }

    public String copyfile() throws IOException {

        if (checkDataBase()) {
            InputStream myInput = new FileInputStream(getApplicationContext().getDatabasePath("MyExpense.db").getPath());

            // Path to the just created empty db
            String outFileName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + DB_NAME;

            //Open the empty db as the output stream
            OutputStream myOutput;
            try {
                myOutput = new FileOutputStream(outFileName);
                //transfer bytes from the inputfile to the outputfile
                byte[] buffer = new byte[1024];
                int length;
                while ((length = myInput.read(buffer)) > 0) {
                    myOutput.write(buffer, 0, length);
                }
                //Close the streams
                myOutput.flush();
                myOutput.close();
                myInput.close();
                return outFileName;
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } else {
            System.out.print("Failed to identify db");
        }
        return "No files Created";
    }

    public interface Constants {
        String TAG = "app:MainActivity";
    }
}
