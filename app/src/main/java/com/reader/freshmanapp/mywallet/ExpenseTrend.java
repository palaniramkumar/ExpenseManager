package com.reader.freshmanapp.mywallet;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.reader.freshmanapp.mywallet.adapter.BudgetCard;
import com.reader.freshmanapp.mywallet.db.DBHelper;
import com.reader.freshmanapp.mywallet.util.CurrencyFormatter;
import com.reader.freshmanapp.mywallet.util.MonthOperations;

import java.util.ArrayList;

import it.gmariotti.cardslib.library.internal.ViewToClickToExpand;
import it.gmariotti.cardslib.library.view.CardViewNative;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ExpenseTrend.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ExpenseTrend#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExpenseTrend extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    ViewGroup mContainer;
    DBHelper db;
    View view;
    BarChart mChart, dChart;
    CombinedChart cChart;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;

    public ExpenseTrend() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ExpenseTrend.
     */
    // TODO: Rename and change types and number of parameters
    public static ExpenseTrend newInstance(String param1, String param2) {
        ExpenseTrend fragment = new ExpenseTrend();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mContainer = container;
        LayoutInflater mInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = mInflater.inflate(R.layout.fragment_expense_trend, mContainer, false);
        db = new DBHelper(getActivity());
        getActivity().runOnUiThread(new Runnable() { //throws array index out of bound exception if the transaction is null
            @Override
            public void run() {
                init();
            }
        });

        return view;
    }

    void init() {
        try {
            SharedPreferences prefs = PreferenceManager
                    .getDefaultSharedPreferences(getActivity());
            final boolean isBudget = prefs.getBoolean("budget", false);
            if (isBudget) {
                BudgetCard card = new BudgetCard(getActivity(), db);
                ViewToClickToExpand viewToClickToExpand = ViewToClickToExpand.builder().enableForExpandAction();
                card.setViewToClickToExpand(viewToClickToExpand);
                card.init();
                CardViewNative cardView = (CardViewNative) view.findViewById(R.id.cardbudget);
        /*code to update the card value without overlaping the previous text Defect #11*/
                if (cardView.getCard() == null)
                    cardView.setCard(card);
                else
                    cardView.replaceCard(card);
            } else {
                view.findViewById(R.id.cardbudget).setVisibility(View.GONE);
            }
            if (db.getMyTotalExpense(db.month) == 0 || db.getBudget() == 0) {
                view.findViewById(R.id.cardbudget).setVisibility(View.GONE);
            } else
                view.findViewById(R.id.cardbudget).setVisibility(View.VISIBLE);
            //month chart creation
            mChart = (BarChart) view.findViewById(R.id.chart_trend);
            // no description text
            mChart.setDescription("");

            // if disabled, scaling can be done on x- and y-axis separately
            mChart.setPinchZoom(false);

            mChart.setDrawGridBackground(false);
            mChart.setBackgroundColor(Color.WHITE);

            mChart.setDrawBarShadow(false);
            mChart.setDrawValueAboveBar(false);

            // mChart.setDrawValueAboveBar(true);

            XAxis x = mChart.getXAxis();
            x.setDrawGridLines(false);
            x.setDrawLabels(true);
            x.setDrawAxisLine(false);
            x.setPosition(XAxis.XAxisPosition.BOTTOM);


            //x.setTypeface(tf);

            YAxis leftAxis = mChart.getAxisLeft();
            leftAxis.setLabelCount(6);
            leftAxis.setDrawLabels(false);
            leftAxis.setDrawGridLines(false);
            leftAxis.setDrawAxisLine(false);

            YAxis rightAxis = mChart.getAxisRight();
            rightAxis.setDrawGridLines(false);
            rightAxis.setDrawAxisLine(false);
            rightAxis.setDrawLabels(false);

            // add data
            monthTrendData();

            //mChart.getLegend().setEnabled(true);

            mChart.animateXY(2000, 2000);

            // dont forget to refresh the drawing
            mChart.invalidate();


            //day trend graph started here
            dChart = (BarChart) view.findViewById(R.id.chart_monthTrend);
            // no description text
            dChart.setDescription("");

            dChart.setPinchZoom(false);

            dChart.setDrawGridBackground(false);
            dChart.setBackgroundColor(Color.WHITE);

            dChart.setDrawBarShadow(false);
            dChart.setDrawValueAboveBar(true);

            x = dChart.getXAxis();
            x.setDrawGridLines(false);
            x.setDrawLabels(true);
            x.setDrawAxisLine(false);
            x.setPosition(XAxis.XAxisPosition.BOTTOM);

            leftAxis = dChart.getAxisLeft();
            leftAxis.setLabelCount(6);
            leftAxis.setDrawLabels(false);
            leftAxis.setDrawGridLines(false);
            leftAxis.setDrawAxisLine(false);

            rightAxis = dChart.getAxisRight();
            rightAxis.setDrawGridLines(false);
            rightAxis.setDrawAxisLine(false);
            rightAxis.setDrawLabels(false);


            // add data
            dayTrendData();

            //dChart.getLegend().setEnabled(true);

            dChart.animateXY(2000, 2000);

            // dont forget to refresh the drawing
            dChart.invalidate();

            //compined chart for expense vs income

            cChart = (CombinedChart) view.findViewById(R.id.chart_incomeTrend);
            cChart.setDescription("");
            cChart.setDrawGridBackground(false);
            cChart.setDrawBarShadow(false);

            YAxis rightAxis1= cChart.getAxisRight();
            rightAxis1.setDrawGridLines(false);
            rightAxis1.setDrawLabels(false);
            rightAxis1.setDrawAxisLine(false);

            YAxis leftAxis1 = cChart.getAxisLeft();
            leftAxis1.setDrawGridLines(false);
            leftAxis1.setDrawLabels(false);
            leftAxis1.setDrawAxisLine(false);

            XAxis xAxis = cChart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setDrawAxisLine(false);
            xAxis.setDrawGridLines(false);


            // draw bars behind lines
            cChart.setDrawOrder(new CombinedChart.DrawOrder[]{
                    CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.LINE
            });


            String[] mMonths = new String[] {
                    "Jan", "Feb", "Mar", "Apr" //need to update this code dynamically
            };
            CombinedData data1 = new CombinedData(mMonths);

            data1.setData(generateLineData());
            data1.setData(generateBarData());


            cChart.setData(data1);
            Log.e("Debug","chart Cpount" + cChart.getData().getBarData().getXValCount());
            cChart.invalidate();

            if (BuildConfig.DEBUG) {
                Log.e(Constants.TAG, "init() completed");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void monthTrendData() {

        Cursor cur = db.getMyTotalExpense();

        ArrayList<String> xVals = new ArrayList<String>();
        ArrayList<BarEntry> vals1 = new ArrayList<BarEntry>();
        int i = 0;
        while (cur.moveToNext()) {
            xVals.add(MonthOperations.getMonthAsString(Integer.parseInt(cur.getString(0)) - 1));
            vals1.add(new BarEntry(cur.getFloat(1), i));
            i++;
        }


        BarDataSet set1 = new BarDataSet(vals1, "Months");
        set1.setBarSpacePercent(12f);

        set1.setColor(getResources().getColor(R.color.myAccentColor));
        ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
        dataSets.add(set1);

        BarData data = new BarData(xVals, dataSets);
        data.setValueTextSize(10f);
        data.setValueFormatter(new CurrencyFormatter());
        if (i != 0)
            mChart.setData(data);
    }



    private void dayTrendData() {

        Cursor cur = db.getMyExpenseByDay();

        ArrayList<String> xVals = new ArrayList<String>();
        ArrayList<BarEntry> vals1 = new ArrayList<BarEntry>();
        int i = 0;
        while (cur.moveToNext()) {
            xVals.add(cur.getString(0));
            vals1.add(new BarEntry(cur.getFloat(1), i));
            i++;
        }


        BarDataSet set1 = new BarDataSet(vals1, "Day");
        set1.setBarSpacePercent(35f);
        set1.setColor(getResources().getColor(R.color.myAccentColor));
        ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
        dataSets.add(set1);

        BarData data = new BarData(xVals, dataSets);
        data.setValueTextSize(10f);
        data.setValueFormatter(new CurrencyFormatter());
        if (i != 0)
            dChart.setData(data);
    }


    private LineData generateLineData() {

        LineData d = new LineData();

        ArrayList<Entry> entries = new ArrayList<Entry>();

        Cursor cur = db.getMyTotalExpense();
        int i=0;
        while(cur.moveToNext()){
            entries.add(new Entry(cur.getFloat(1), i));
            i++;
        }

        LineDataSet set = new LineDataSet(entries, "Expense");
        set.setColor(Color.rgb(240, 238, 70));
        set.setLineWidth(2.5f);
        set.setCircleColor(Color.rgb(240, 238, 70));
        set.setCircleSize(5f);
        set.setFillColor(Color.rgb(240, 238, 70));
        set.setDrawCubic(true);
        set.setDrawValues(true);
        set.setValueTextSize(10f);
        set.setValueTextColor(Color.rgb(240, 238, 70));

        set.setAxisDependency(YAxis.AxisDependency.LEFT);

        d.addDataSet(set);

        return d;
    }



    private BarData generateBarData() {

        BarData d = new BarData();

        ArrayList<BarEntry> entries = new ArrayList<BarEntry>();
        Cursor cur = db.getMyTotalIncome();
        int i=0;
        while(cur.moveToNext()){
            entries.add(new BarEntry(cur.getFloat(1), i));
            i++;
        }

        BarDataSet set = new BarDataSet(entries, "Income");
        set.setColor(getResources().getColor(R.color.myAccentColor));
        set.setValueTextColor(getResources().getColor(R.color.myAccentColor));
        set.setValueTextSize(10f);
        d.addDataSet(set);

        set.setAxisDependency(YAxis.AxisDependency.LEFT);

        return d;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
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

    @Override
    public void onResume() {
        super.onResume();
        init();
    }

    public interface Constants {
        String TAG = "app:ExpenseTrend";
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
