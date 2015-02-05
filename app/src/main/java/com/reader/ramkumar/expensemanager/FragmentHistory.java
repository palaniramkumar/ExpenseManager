package com.reader.ramkumar.expensemanager;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.Toast;

//import com.melnykov.fab.FloatingActionButton;
import com.reader.ramkumar.expensemanager.adapter.Group;
import com.reader.ramkumar.expensemanager.adapter.MyExpandableListAdapter;
import com.reader.ramkumar.expensemanager.db.DBHelper;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentHistory.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentHistory#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentHistory extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    ViewGroup mContainer;
    DBHelper db;
    SparseArray<Group> groups = new SparseArray<Group>();

    private OnFragmentInteractionListener mListener;

    public FragmentHistory() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentHistory.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentHistory newInstance(String param1, String param2) {
        FragmentHistory fragment = new FragmentHistory();
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
        View view = mInflater.inflate(R.layout.fragment_fragment_history, mContainer, false);
        db=new DBHelper(getActivity());
        createData();
        ExpandableListView listView = (ExpandableListView) view.findViewById(R.id.listView);
        MyExpandableListAdapter adapter = new MyExpandableListAdapter(getActivity(),groups);
        listView.setAdapter(adapter);

        /*FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(mContainer.getContext(), "New Clicked", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(mContainer.getContext(), Expense_add_window.class);
                startActivity(i);
            }
        });*/

        return view;
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

    public void createData() {
        Cursor cur = db.getTransactionHistory();
        int j=0;
        while (cur.moveToNext()) {

            Group group = new Group(cur.getString(0)+", "+cur.getString(1));
            System.out.println(cur.getString(0)+":"+cur.getString(1)+":"+cur.getString(2));

            String [] category = cur.getString(2).split(","); //Bug: code glitch in spltting files. fc split values are mis mattching between others - array o o bounfd
            String [] amount = cur.getString(3).split(",");
            String [] id=cur.getString(4).split(",");
            for (int i = 0; i < category.length; i++) {
                String [] param =new String[3];
                param[0]=category[i];
                param[1]=amount[i];
                param[2]=id[i];
                group.children.add(param);
            }
           // group.children.add(cur.getString(1) +":"+ cur.getString(2));
            groups.append(j, group);
            j++;
        }

    }
}
