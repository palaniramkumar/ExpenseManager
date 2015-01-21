package com.reader.ramkumar.expensemanager;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.melnykov.fab.FloatingActionButton;
import com.reader.ramkumar.expensemanager.adapter.ExpenseCard;
import com.reader.ramkumar.expensemanager.db.DBHelper;

import java.util.ArrayList;

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
public class main extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    ViewGroup mContainer;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private PieChart mChart;
    private OnFragmentInteractionListener mListener;
    private Handler mHandler = new Handler();
    private ProgressBar mProgress;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mContainer = container;
        LayoutInflater mInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = mInflater.inflate(R.layout.fragment_main, mContainer, false);


        //check for new SMS
        mProgress= (ProgressBar)view.findViewById(R.id.loading_spinner) ;
        // Start lengthy operation in a background thread

        /*new Thread(new Runnable() {
            public void run() {
                int iProgress=0;
                //mProgress.setVisibility(View.VISIBLE);
                while (iProgress < 10) {
                    iProgress ++;
                    try {
                        // Perform long-running task here
                        // (like audio buffering).
                        // you may want to update some progress
                        // bar every second, so use handler:
                        // Sleep for 200 milliseconds.
                        //Just to display the progress slowly
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    // Update the progress bar
                    mHandler.post(new Runnable() {
                        // make operation on UI - on example
                        // on progress bar.
                        public void run() {


                        }

                    });

                }


            }
        }).start();*/

        class MyAsyncTask extends AsyncTask<Void, Void, Integer> {


            @Override
            protected void onPreExecute() {
                mProgress.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(Integer result) {
                mProgress.setVisibility(View.GONE);
            }

            @Override
            protected Integer doInBackground(Void... params) {
                int iProgress=0;
                DBHelper db=new DBHelper(getActivity().getApplicationContext());
                db.deleteMaster(1);
                while (iProgress < 2) {
                    iProgress++;
                    try {
                        // Perform long-running task here
                        // (like audio buffering).
                        // you may want to update some progress
                        // bar every second, so use handler:
                        // Sleep for 200 milliseconds.
                        //Just to display the progress slowly
                        db.insertMaster("1","1","1","1","1","1","1","1","1","1","1","1","1","1","1","1");
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return 0;
            }

        }
        new MyAsyncTask().execute();

        ExpenseCard card = new ExpenseCard(getActivity());

        ViewToClickToExpand viewToClickToExpand = ViewToClickToExpand.builder().enableForExpandAction();
        card.setViewToClickToExpand(viewToClickToExpand);
        card.init();
        //Set card in the cardView
        CardViewNative cardView = (CardViewNative) view.findViewById(R.id.carddemo); //if you want list, pls change the xml to "CardListView"
        cardView.setCard(card);

        mChart = (PieChart) view.findViewById(R.id.chart);

        // change the color of the center-hole
        mChart.setHoleColor(Color.rgb(235, 235, 235));
        mChart.setBackgroundColor(Color.WHITE);

        mChart.setHoleRadius(60f);

        mChart.setDescription("");

        mChart.setDrawYValues(true);
        mChart.setDrawCenterText(true);

        mChart.setDrawHoleEnabled(true);

        mChart.setRotationAngle(0);

        // draws the corresponding description value into the slice
        mChart.setDrawXValues(true);

        // enable rotation of the chart by touch
        mChart.setRotationEnabled(true);

        // display percentage values
        mChart.setUsePercentValues(true);
        // mChart.setUnit(" €");
        // mChart.setDrawUnitsInChart(true);

        // add a selection listener
        //mChart.setOnChartValueSelectedListener(this);//need to uncomment
        // mChart.setTouchEnabled(false);

        mChart.setCenterText("December\n2014");

        setData(3, 100);

        mChart.animateXY(1500, 1500);
        // mChart.spin(2000, 0, 360);

        /*Legend l = mChart.getLegend();
        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(5f);
*/

        //fab
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(mContainer.getContext(), "New Clicked", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(mContainer.getContext(), Expense_add_window.class);
                startActivity(i);
            }
        });
        Toast.makeText(mContainer.getContext(), "onActivityCreated", Toast.LENGTH_SHORT).show();

        return view;
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

    private void setData(int count, float range) {

        float mult = range;

        ArrayList<Entry> yVals1 = new ArrayList<Entry>();

        // IMPORTANT: In a PieChart, no values (Entry) should have the same
        // xIndex (even if from different DataSets), since no values can be
        // drawn above each other.
        for (int i = 0; i < count + 1; i++) {
            yVals1.add(new Entry((float) (Math.random() * mult) + mult / 5, i));
        }

        ArrayList<String> xVals = new ArrayList<String>();

        String[] mType = {"Food", "Vehicles", "House", "Travel", "Gifts"};
        for (int i = 0; i < count + 1; i++)
            xVals.add(mType[i % mType.length]);

        PieDataSet set1 = new PieDataSet(yVals1, "Expense Distribution");
        set1.setSliceSpace(3f);

        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<Integer>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());

        set1.setColors(colors);

        PieData data = new PieData(xVals, set1);
        mChart.setData(data);

        // undo all highlights
        mChart.highlightValues(null);

        mChart.invalidate();


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
