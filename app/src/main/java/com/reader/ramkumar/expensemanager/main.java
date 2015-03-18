package com.reader.ramkumar.expensemanager;

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
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.listener.OnDrawListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Highlight;
import com.github.mikephil.charting.utils.PercentFormatter;
import com.melnykov.fab.FloatingActionButton;
import com.reader.ramkumar.SMSparser.SMS;
import com.reader.ramkumar.expensemanager.adapter.ExpenseCard;
import com.reader.ramkumar.expensemanager.db.DBHelper;
import com.reader.ramkumar.expensemanager.util.CashVault;
import com.reader.ramkumar.expensemanager.util.Common;
import com.reader.ramkumar.expensemanager.util.CurrencyFormatter;
import com.reader.ramkumar.expensemanager.util.MonthOperations;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
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
public class main extends Fragment implements OnChartValueSelectedListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    ViewGroup mContainer;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private PieChart mChart;
    private HorizontalBarChart hChart;
    private OnFragmentInteractionListener mListener;
    private Handler mHandler = new Handler();
    public ProgressBar mProgress;
    CardViewNative cardView;
    private DBHelper db;
    private View view;
    private ExpenseCard card;
    Button btn_month;
    Button btn_year;
    ArrayList<String> xVals;
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
    public interface Constants {
        String TAG = "app:main";
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        db=new DBHelper(getActivity());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 201) {
            if(resultCode == Activity.RESULT_OK){
                //String result=data.getStringExtra("result");
                init();
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

        mProgress= (ProgressBar)view.findViewById(R.id.loading_spinner) ;
        mProgress.setVisibility(View.INVISIBLE);
        //Set card in the cardView
        cardView = (CardViewNative) view.findViewById(R.id.carddemo); //if you want list, pls change the xml to "CardListView"

        init();

        //fab
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Toast.makeText(mContainer.getContext(), "New Clicked", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(mContainer.getContext(), Expense_add_window.class);
                startActivityForResult(i,201); //201 -Create: assume HTTP 201 for create request :). It can be any value
            }
        });

        //month navigation
        Button btn_prev = (Button) view.findViewById(R.id.btn_prev);
        btn_prev.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String current_month = btn_month.getText().toString();
                Date date = MonthOperations.previous(current_month,db.year);
                int prev_month = date.getMonth();
                db.month = MonthOperations.getMonthin2Digit(prev_month+1);
                db.year = date.getYear()+"";
                //db=new DBHelper(getActivity(),MonthOperations.getMonthin2Digit(prev_month+1));
                init();
            }
        });
        //month navigation
        Button btn_next = (Button) view.findViewById(R.id.btn_next);
        btn_next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String current_month = btn_month.getText().toString();
                Date date = MonthOperations.next(current_month,db.year);
                int next_month = date.getMonth();
                db.month = MonthOperations.getMonthin2Digit(next_month+1);
                db.year = date.getYear()+"";
                //db=new DBHelper(getActivity(),MonthOperations.getMonthin2Digit(next_month+1));
                init();
            }
        });

        TextView t = (TextView)view.findViewById(R.id.txt_progress);

        t.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View viewIn) {
                view_atm_trans(viewIn);
            }
        });

        return view;
    }

    public void view_atm_trans(View v){
        FragmentHistory secFrag = new FragmentHistory(DBHelper.ATM);
        FragmentTransaction fragTransaction = (getActivity()).getFragmentManager().beginTransaction();
        fragTransaction.replace(R.id.frame_container,secFrag );
        fragTransaction.addToBackStack(null);
        fragTransaction.commit();

        if (BuildConfig.DEBUG) {
            Log.e(Constants.TAG, "Clicked on " + DBHelper.ATM);
        }
    }

    void init(){


        card = new ExpenseCard(getActivity(),db);

        TextView amountHdr = (TextView) view.findViewById(R.id.txt_tot_amount);
        amountHdr.setText(Common.CURRENCY+" "+db.getMyTotalExpense());

        TextView amt_today = (TextView) view.findViewById(R.id.txt_today_amount);
        amt_today.setText("( Today's Expense "+Common.CURRENCY+" "+db.getExpensebyDay(Calendar.getInstance().get(Calendar.DAY_OF_MONTH)+"","%")+" )");

        btn_month = (Button) view.findViewById(R.id.btn_month);
        btn_year = (Button) view.findViewById(R.id.btn_year);
        btn_month.setText(MonthOperations.getMonthAsString(Integer.parseInt(db.month)-1));
        btn_year.setText(db.year);

        ViewToClickToExpand viewToClickToExpand = ViewToClickToExpand.builder().enableForExpandAction();
        card.setViewToClickToExpand(viewToClickToExpand);
        card.init();

        /*code to update the card value without overlaping the previous text Defect #11*/
        if(cardView.getCard()==null)
            cardView.setCard(card);
        else
            cardView.replaceCard(card);

        /*generate the expense distribution pie chart*/

        mChart = (PieChart) view.findViewById(R.id.chart);

        // change the color of the center-hole
        mChart.setHoleColor(Color.rgb(235, 235, 235));
        mChart.setBackgroundColor(Color.WHITE);

        mChart.setHoleRadius(60f);

        mChart.setDescription("");

        mChart.setDrawCenterText(true);

        mChart.setDrawHoleEnabled(true);

        mChart.setRotationAngle(0);

        // enable rotation of the chart by touch
        mChart.setRotationEnabled(true);

        // display percentage values
        mChart.setUsePercentValues(true);

        mChart.setDrawSliceText(false);


        // add a selection listener
        mChart.setOnChartValueSelectedListener(this);
         mChart.setTouchEnabled(true);



        setData();

        mChart.animateXY(1500, 1500);

        if(mChart.getChildAt(0)!=null)
            mChart.getChildAt(0).callOnClick();

        // mChart.spin(2000, 0, 360);

        //setting up remaining amount in cash vault
        //https://github.com/akexorcist/Android-RoundCornerProgressBar

       final float[] roundedCorners = new float[] { 5, 5, 5, 5, 5, 5, 5, 5 };
        ShapeDrawable pgDrawable = new ShapeDrawable(new RoundRectShape(roundedCorners,     null, null));

        // Sets the progressBar color
        pgDrawable.getPaint().setColor(getActivity().getResources()
                .getColor(R.color.myProgressColor));
        ProgressBar progress_bar = (ProgressBar) view.findViewById(R.id.progress_1);
        ClipDrawable progress = new ClipDrawable(pgDrawable, Gravity.LEFT, ClipDrawable.HORIZONTAL);
        progress_bar.setProgressDrawable(progress);
        progress_bar.setBackgroundColor(getActivity().getResources()
                .getColor(R.color.myLightPrimaryColor));

        CashVault vault = new CashVault(db);
        
        progress_bar.setMax(vault.vault_amount);
        progress_bar.setProgress(vault.amount_left);
        TextView progress_caption =  (TextView)view.findViewById(R.id.txt_progress);
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
        xl.setPosition(XAxis.XAxisPosition.TOP);
        xl.setDrawAxisLine(false);
        xl.setDrawGridLines(false);
        //xl.setGridLineWidth(0.3f);
        // xl.setEnabled(false);

        xl.setAdjustXLabels(true);
        hChart.setScaleXEnabled(true);

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
        mChart.setCenterText(xVals.get(e.getXIndex())+"\n"+Common.CURRENCY+" "+e.getVal());
    }

    @Override
    public void onNothingSelected() {
    }


    private void bill_expense_data() {
        
        Cursor cur = db.getFromMaster(db.MASTER_COLUMN_CATEGORY,db.BILL_PAYMENT,false);

        ArrayList<String> xVals = new ArrayList<String>();
        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
        int i=0;
        while(cur.moveToNext()){
            String bill_name = cur.getString(cur.getColumnIndex(db.MASTER_COLUMN_NOTES));
           
            if(bill_name == null || bill_name.trim().equals(""))
                bill_name = "<Not Specified>";
            else
                bill_name = bill_name.toUpperCase().replace("PAYMENT","");
            
            if (BuildConfig.DEBUG) {
                Log.e(Constants.TAG, "Retrived Val: "+bill_name+" ,"+cur.getFloat(cur.getColumnIndex(db.MASTER_COLUMN_AMOUNT)));
            }
         
            xVals.add(bill_name);
            yVals1.add(new BarEntry(cur.getFloat(cur.getColumnIndex(db.MASTER_COLUMN_AMOUNT)), i));
            i++;
        }
        
         BarDataSet set1 = new BarDataSet(yVals1, "Paid Bill Amount");
        set1.setBarSpacePercent(25f);

        ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
        ArrayList<Integer> colors = new ArrayList<Integer>();
        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);
        set1.setColors(colors);
        dataSets.add(set1);

        BarData data = new BarData(xVals, dataSets);
        data.setValueTextSize(10f);
        data.setValueFormatter(new CurrencyFormatter());

        hChart.setData(data);
        hChart.invalidate();

    }

    private void setData() {
        Cursor cursor = db.getMyExpenseByCategory();
        ArrayList<Entry> yVals1 = new ArrayList<Entry>();
        xVals = new ArrayList<String>();

        // IMPORTANT: In a PieChart, no values (Entry) should have the same
        // xIndex (even if from different DataSets), since no values can be
        // drawn above each other.
        int i=0;
        while(cursor.moveToNext()){
            yVals1.add(new Entry(cursor.getInt(1), i++));
            xVals.add(cursor.getString(0).toUpperCase());
        }


        PieDataSet set1 = new PieDataSet(yVals1, "");
        set1.setSliceSpace(3f);

        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<Integer>();
        for (int c : ColorTemplate.COLORFUL_COLORS)
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

        mChart.setCenterText(xVals.get(0) + "\n" + Common.CURRENCY+" " + ((Entry) yVals1.get(0)).getVal());

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
