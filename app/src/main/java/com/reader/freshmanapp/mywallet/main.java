package com.reader.freshmanapp.mywallet;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Highlight;
import com.github.mikephil.charting.utils.LargeValueFormatter;
import com.github.mikephil.charting.utils.PercentFormatter;

import com.melnykov.fab.FloatingActionButton;
import com.reader.freshmanapp.SMSparser.SMS;
import com.reader.freshmanapp.mywallet.adapter.ExpenseCard;
import com.reader.freshmanapp.mywallet.db.DBCategoryMap;
import com.reader.freshmanapp.mywallet.db.DBHelper;
import com.reader.freshmanapp.mywallet.util.CashVault;
import com.reader.freshmanapp.mywallet.util.Common;
import com.reader.freshmanapp.mywallet.util.MonthOperations;
import com.reader.freshmanapp.mywallet.util.UndoBar;


import java.util.ArrayList;
import java.util.Calendar;

import java.util.UUID;

import it.gmariotti.cardslib.library.internal.ViewToClickToExpand;
import it.gmariotti.cardslib.library.view.CardViewNative;


/*fragment main*/

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link main.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link main#newInstance} factory method to
 * create an instance of this fragment.
 */
public class main extends Fragment implements OnChartValueSelectedListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public ProgressBar mProgress;
    ViewGroup mContainer;
    CardViewNative cardView;
    Button btn_month;
    Button btn_year;
    ArrayList<String> xVals;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private PieChart mChart;
    private HorizontalBarChart hChart,incChart;
    private OnFragmentInteractionListener mListener;
    private Handler mHandler = new Handler();
    private DBHelper db;
    private View view;
    private ExpenseCard card;

    public main() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment main.
     */
    // TODO: Rename and change types and number of parameters
    public static main newInstance(String param1, String param2) {
        main fragment = new main();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        db = new DBHelper(getActivity());

        SMS.syncSMS(getActivity());

        /** New User -  display demo page **/
                /* first user check */
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        String endpoint = sharedPref.getString("demo", "0");
        if (endpoint.equalsIgnoreCase("0")) {
            DBCategoryMap dbCatMap = new DBCategoryMap(getActivity());
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("demo", UUID.randomUUID().toString());
            editor.commit();
            Intent i = new Intent(getActivity(),Help.class);
            startActivity(i);
        } else {
            if (BuildConfig.DEBUG) {
                Log.e(Constants.TAG, "Existing user");
            }

            //else code
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 201) {
            if (resultCode == Activity.RESULT_OK) {
                //String result=data.getStringExtra("result");
                init();
                UndoBar undobar = new UndoBar(getActivity());
                undobar.show(data.getStringExtra("result"));
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }//onActivityResult

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mContainer = container;
        LayoutInflater mInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = mInflater.inflate(R.layout.fragment_main, mContainer, false);

        mProgress = (ProgressBar) view.findViewById(R.id.loading_spinner);
        mProgress.setVisibility(View.INVISIBLE);
        //Set card in the cardView
        cardView = (CardViewNative) view.findViewById(R.id.carddemo); //if you want list, pls change the xml to "CardListView"

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                init();
            }
        });

        //fab
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);


        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Toast.makeText(mContainer.getContext(), "New Clicked", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(mContainer.getContext(), Expense_add_window.class);
                startActivityForResult(i, 201); //201 -Create: assume HTTP 201 for create request :). It can be any value
            }
        });

        //month navigation
        Button btn_prev = (Button) view.findViewById(R.id.btn_prev);
        btn_prev.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Calendar date = Calendar.getInstance();
                String current_month = btn_month.getText().toString();
                date.setTime(MonthOperations.previous(current_month, db.year));
                int prev_month = date.get(Calendar.MONTH);
                db.month = MonthOperations.getMonthin2Digit(prev_month+1);
                db.year =  date.get(Calendar.YEAR) + "";

                init();
            }
        });
        //month navigation
        Button btn_next = (Button) view.findViewById(R.id.btn_next);
        btn_next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Calendar date = Calendar.getInstance();
                String current_month = btn_month.getText().toString();
                date.setTime(MonthOperations.next(current_month, db.year));
                int next_month = date.get(Calendar.MONTH);
                db.month = MonthOperations.getMonthin2Digit(next_month+1);
                db.year =  date.get(Calendar.YEAR) + "";
                init();
            }
        });


        TextView t = (TextView) view.findViewById(R.id.txt_progress);

        t.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View viewIn) {
                view_atm_trans(viewIn);
            }
        });

        return view;
    }

    public void view_atm_trans(View v) {
        FragmentHistory secFrag = new FragmentHistory(DBHelper.ATM);
        FragmentTransaction fragTransaction = (getActivity()).getFragmentManager().beginTransaction();
        fragTransaction.replace(R.id.frame_container, secFrag);
        fragTransaction.addToBackStack(null);
        fragTransaction.commit();


        if (BuildConfig.DEBUG) {
            Log.e(Constants.TAG, "Clicked on " + DBHelper.ATM);
        }
    }

    void init() {



        card = new ExpenseCard(getActivity(), db);

        TextView amountHdr = (TextView) view.findViewById(R.id.txt_tot_amount);
        amountHdr.setText(Common.CURRENCY + " " + db.getMyTotalExpense(db.month));

        TextView amt_today = (TextView) view.findViewById(R.id.txt_today_amount);
        amt_today.setText("( Today's Expense " + Common.CURRENCY + " " + db.getExpensebyDay(Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + "", "%") + " )");

        btn_month = (Button) view.findViewById(R.id.btn_month);
        btn_year = (Button) view.findViewById(R.id.btn_year);
        btn_month.setText(MonthOperations.getMonthAsString(Integer.parseInt(db.month) - 1));
        btn_year.setText(db.year);

        ViewToClickToExpand viewToClickToExpand = ViewToClickToExpand.builder().enableForExpandAction();
        card.setViewToClickToExpand(viewToClickToExpand);
        card.init();

        /*code to update the card value without overlaping the previous text Defect #11*/
        if (cardView.getCard() == null)
            cardView.setCard(card);
        else
            cardView.replaceCard(card);

        if (db.getMyTotalExpense(db.month) == 0) { //check whether the child is exist or not. if no child inside the card pls hide it.
            cardView.setVisibility(View.GONE);
            view.findViewById(R.id.info_layout).setVisibility(View.VISIBLE);
            if (BuildConfig.DEBUG) {
                Log.e(Constants.TAG, "Child is empty");
            }
        } else {
            cardView.setVisibility(View.VISIBLE);
            view.findViewById(R.id.info_layout).setVisibility(View.GONE);
        }


        /*generate the expense distribution pie chart*/

        mChart = (PieChart) view.findViewById(R.id.chart);

        // change the color of the center-hole
        mChart.setHoleColor(Color.rgb(235, 235, 235));
        mChart.setBackgroundColor(Color.WHITE);

        mChart.setHoleRadius(60f);

        mChart.setDescription("");

        mChart.setDrawCenterText(true);

        mChart.setDrawHoleEnabled(true);

        //mChart.setRotationAngle(0);

        // enable rotation of the chart by touch
        mChart.setRotationEnabled(false);

        // display percentage values
        mChart.setUsePercentValues(true);

        mChart.setDrawSliceText(false);


        // add a selection listener
        mChart.setOnChartValueSelectedListener(this);
        mChart.setTouchEnabled(true);
        mChart.setCenterTextColor(Color.DKGRAY);


        setData();

        mChart.animateXY(1500, 1500);

        mChart.getLegend().setTextColor(Color.DKGRAY);

        if (mChart.getChildAt(0) != null)
            mChart.getChildAt(0).callOnClick();

        // mChart.spin(2000, 0, 360);

        //setting up remaining amount in cash vault
        //https://github.com/akexorcist/Android-RoundCornerProgressBar

        final float[] roundedCorners = new float[]{5, 5, 5, 5, 5, 5, 5, 5};
        ShapeDrawable pgDrawable = new ShapeDrawable(new RoundRectShape(roundedCorners, null, null));

        // Sets the progressBar color
        pgDrawable.getPaint().setColor(getActivity().getResources()
                .getColor(R.color.myProgressColor));
        ProgressBar progress_bar = (ProgressBar) view.findViewById(R.id.progress_1);
        ClipDrawable progress = new ClipDrawable(pgDrawable, Gravity.LEFT, ClipDrawable.HORIZONTAL);
        progress_bar.setProgressDrawable(progress);
        progress_bar.setBackgroundColor(getActivity().getResources()
                .getColor(R.color.myLightPrimaryColor));

        CashVault vault = new CashVault(db);
        if (vault.vault_amount== 0) {
            view.findViewById(R.id.cash_holder).setVisibility(View.GONE);
            //cash_holder
        } else
            view.findViewById(R.id.cash_holder).setVisibility(View.VISIBLE);

        progress_bar.setMax(vault.vault_amount);
        progress_bar.setProgress(vault.amount_left);
        TextView progress_caption = (TextView) view.findViewById(R.id.txt_progress);
        progress_caption.setText(vault.msg);


        hChart = (HorizontalBarChart) view.findViewById(R.id.chart_horz);

        hChart.setDrawBarShadow(false);

        hChart.setDrawValueAboveBar(true);

        hChart.setDescription("");

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        hChart.setMaxVisibleValueCount(60);

        // scaling can now only be done on x- and y-axis separately
        hChart.setPinchZoom(false);

        // draw shadows for each bar that show the maximum value
        // mChart.setDrawBarShadow(true);

        // mChart.setDrawXLabels(false);

        hChart.setDrawGridBackground(false);

        XAxis xl = hChart.getXAxis();
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);
        xl.setDrawAxisLine(false);
        xl.setDrawGridLines(false);
        //xl.setGridLineWidth(0.3f);
        // xl.setEnabled(false);

        //xl.setAdjustXLabels(true);
        xl.setTextColor(Color.DKGRAY);
        //hChart.setScaleXEnabled(true);

        YAxis yl = hChart.getAxisLeft();
        yl.setDrawAxisLine(false);
        yl.setDrawGridLines(false);
        //yl.setGridLineWidth(0.3f);
        yl.setDrawLabels(false);
        yl.setLabelCount(0);

        YAxis yr = hChart.getAxisRight();
        yr.setDrawAxisLine(false);
        yr.setDrawGridLines(false);
        yr.setDrawLabels(false);
        yr.setLabelCount(0);

        bill_expense_data();
        hChart.animateY(2500);
        hChart.getLegend().setTextColor(Color.DKGRAY);



        incChart = (HorizontalBarChart) view.findViewById(R.id.chart_incomevsexpense);

        incChart.setDrawBarShadow(false);

        incChart.setDrawValueAboveBar(false);

        incChart.setDescription("");

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        incChart.setMaxVisibleValueCount(60);

        // scaling can now only be done on x- and y-axis separately
        incChart.setPinchZoom(false);

        // draw shadows for each bar that show the maximum value
        // mChart.setDrawBarShadow(true);

        // mChart.setDrawXLabels(false);

        incChart.setDrawGridBackground(false);

        xl = incChart.getXAxis();
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);
        xl.setDrawAxisLine(false);
        xl.setDrawGridLines(false);
        //xl.setGridLineWidth(0.3f);
        // xl.setEnabled(false);

        xl.setAdjustXLabels(true);
        xl.setTextColor(Color.DKGRAY);
        incChart.setScaleXEnabled(true);

        yl = incChart.getAxisLeft();
        yl.setDrawAxisLine(false);
        yl.setDrawGridLines(false);
        //yl.setGridLineWidth(0.3f);
        yl.setDrawLabels(false);
        yl.setLabelCount(0);

        yr = incChart.getAxisRight();
        yr.setDrawAxisLine(false);
        yr.setDrawGridLines(false);
        yr.setDrawLabels(false);
        yr.setLabelCount(0);

        incomevsexpense_data();
        incChart.animateY(2500);
        incChart.getLegend().setTextColor(Color.DKGRAY);




    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;


            //Toast.makeText(this, "Home Page", Toast.LENGTH_SHORT).show();

        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    //graph related methods
    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
        if (BuildConfig.DEBUG) {
            Log.e(Constants.TAG, "Value: " + e.getVal() + ", xIndex: " + e.getXIndex()
                    + ", DataSet index: " + dataSetIndex);
            Log.e(Constants.TAG, "data: " + xVals.get(e.getXIndex()));

        }
        mChart.setCenterText(xVals.get(e.getXIndex()) + "\n" + Common.CURRENCY + " " + e.getVal());
    }

    @Override
    public void onNothingSelected() {
    }

    private void bill_expense_data() {

        Cursor cur = db.getFromMaster(db.MASTER_COLUMN_CATEGORY, db.BILL_PAYMENT, false);

        ArrayList<String> xVals = new ArrayList<String>();
        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
        int i = 0;
        while (cur.moveToNext()) {
            String bill_name = cur.getString(cur.getColumnIndex(db.MASTER_COLUMN_NOTES));

            if (bill_name == null || bill_name.trim().equals(""))
                bill_name = "<Not Specified>";
            else
                bill_name = bill_name.toUpperCase().replace("PAYMENT", "");

            if (BuildConfig.DEBUG) {
                Log.e(Constants.TAG, "Retrived Val: " + bill_name + " ," + cur.getInt(cur.getColumnIndex(db.MASTER_COLUMN_AMOUNT)));
            }

            xVals.add(bill_name);
            yVals1.add(new BarEntry(cur.getInt(cur.getColumnIndex(db.MASTER_COLUMN_AMOUNT)), i));
            i++;
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        BarDataSet set1 = new BarDataSet(yVals1, "Paid Bill Amount");
        set1.setBarSpacePercent(35f);

        ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
        ArrayList<Integer> colors = new ArrayList<Integer>();
        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);
        set1.setColors(colors);
        dataSets.add(set1);

        BarData data = new BarData(xVals, dataSets);
        data.setValueTextSize(10f);

        data.setValueFormatter(new LargeValueFormatter());

        hChart.setData(data);
        //hChart.invalidate();

        // condition for hiding the graph: if data is null pls hide
        if (yVals1.size() == 0)
            view.findViewById(R.id.arrayBills).setVisibility(View.GONE);
        else
            view.findViewById(R.id.arrayBills).setVisibility(View.VISIBLE);


    }

    private void incomevsexpense_data() {

        ArrayList<String> xVals = new ArrayList<String>();
        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
        float income = db.getMyTotalIncome(db.month);
        float expense = db.getMyTotalExpense(db.month);


        xVals.add("INCOME");
        yVals1.add(new BarEntry(income, 0));
        xVals.add("EXPENSE");
        yVals1.add(new BarEntry(expense, 1));

        BarDataSet set1 = new BarDataSet(yVals1, "Amount");
       // set1.setBarSpacePercent(40f);


        ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
        ArrayList<Integer> colors = new ArrayList<Integer>();
        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);
        set1.setColors(colors);
        dataSets.add(set1);

        BarData data = new BarData(xVals, dataSets);
        data.setValueTextSize(10f);

        data.setValueFormatter(new LargeValueFormatter());

        incChart.setData(data);
        //incChart.invalidate();

        // condition for hiding the graph: if data is null pls hide
        if (income + expense == 0)
            view.findViewById(R.id.incomevsexpense_holder).setVisibility(View.GONE);
        else
            view.findViewById(R.id.incomevsexpense_holder).setVisibility(View.VISIBLE);


    }


    private void setData() {
        Cursor cursor = db.getMyExpenseByCategory();
        ArrayList<Entry> yVals1 = new ArrayList<Entry>();
        xVals = new ArrayList<String>();

        // IMPORTANT: In a PieChart, no values (Entry) should have the same
        // xIndex (even if from different DataSets), since no values can be
        // drawn above each other.
        int i = 0;
        while (cursor.moveToNext()) {
            yVals1.add(new Entry(cursor.getFloat(1), i++));
            xVals.add(cursor.getString(0).toUpperCase());
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        PieDataSet set1 = new PieDataSet(yVals1, "");
        set1.setSliceSpace(3f);

        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<Integer>();
        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

       /* for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);
*/

        colors.add(ColorTemplate.getHoloBlue());

        set1.setColors(colors);

        PieData data = new PieData(xVals, set1);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextColor(Color.TRANSPARENT);

        mChart.setData(data);

        // undo all highlights
        mChart.highlightValue(0, 0);

        mChart.invalidate();
        if (yVals1.size() != 0 && xVals.size() != 0) {
            mChart.setCenterText(xVals.get(0) + "\n" + Common.CURRENCY + " " + ((Entry) yVals1.get(0)).getVal());
            view.findViewById(R.id.chart_holder).setVisibility(View.VISIBLE);
        } else {
            view.findViewById(R.id.chart_holder).setVisibility(View.GONE);
        }

    }

    public interface Constants {
        String TAG = "app:main";
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }


}
