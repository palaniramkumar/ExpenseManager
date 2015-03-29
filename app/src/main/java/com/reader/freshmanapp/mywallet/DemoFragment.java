package com.reader.freshmanapp.mywallet;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.reader.freshmanapp.mywallet.adapter.DemoPagerAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DemoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DemoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DemoFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    //private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment DemoFragment.
     */
    int[] mResources = {
            R.drawable.image1,
            R.drawable.image2,
            R.drawable.image3,
            R.drawable.image4,
            R.drawable.image5,
            R.drawable.image6,
    };

    String[] mResourceContents = {
            "Piechart -All your expense in one place\n\nCashvault – All your Atm Withdrawn taken as cash vault. Further cash expense will be deducted from your vault",
            "Bill Payment – Keep tracking your bill Payments through credit/debit card and compare your expense with previous month" ,
            "Expense Trend – Compare expense against various month\n\nCurrent Budget – View current budget against actual expense",
            "Unknown banking transactions will be added to know your bill.\n\n Can approve by click choose the expense category\n\nSwipe to delete it",
            "Expense History – View your expense history.\n\nClick to edit the expense",
            "Flexible List of Expense category\n\nYou can add your own custom category\n\nSwipe to delete the entry",
    };
    public static DemoFragment newInstance(int param1) {
        DemoFragment fragment = new DemoFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_INDEX, param1);
        fragment.setArguments(args);
        fragment.setArguments(args);
        return fragment;
    }


    private static final String ARG_INDEX = "color";

    private int position;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            position = getArguments().getInt(ARG_INDEX);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View itemView = inflater.inflate(R.layout.fragment_demo, container, false);
        ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);
        TextView txtView = (TextView) itemView.findViewById(R.id.caption);
        imageView.setImageResource(mResources[position]);
        txtView.setText(mResourceContents[position]);

        return itemView;

    }

    public DemoFragment() {
        // Required empty public constructor
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
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
