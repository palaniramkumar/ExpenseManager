package com.reader.freshmanapp.mywallet;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.reader.freshmanapp.mywallet.adapter.StickyHistoryAdapter;
import com.reader.freshmanapp.mywallet.db.DBCategoryMap;
import com.reader.freshmanapp.mywallet.db.DBHelper;
import com.reader.freshmanapp.mywallet.util.MonthOperations;
import com.reader.freshmanapp.mywallet.util.TYPES;

import java.util.Date;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentHistory.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentHistory#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentHistory extends Fragment implements AdapterView.OnItemClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    ViewGroup mContainer;
    Button btn_month;
    Button btn_year;
    DBHelper db;
    View view;
    //ExpandableListView listView;
    StickyListHeadersListView listView;
    StickyHistoryAdapter adapter;
    private String category_filter="%";

    private OnFragmentInteractionListener mListener;
    public interface Constants {
        String TAG = "app:FragmentHistory";
    }

    public FragmentHistory() {
        // Required empty public constructor
    }
    public FragmentHistory(String filter) {
        category_filter = filter;
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
            
            //added newly
            category_filter=getArguments().getString("CATEGORY");
            
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mContainer = container;
        LayoutInflater mInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = mInflater.inflate(R.layout.fragment_fragment_history, mContainer, false);
        db=new DBHelper(getActivity());
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                init();
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
                init();
            }
        });


        return view;
    }


    void init(){

        btn_month = (Button) view.findViewById(R.id.btn_month);
        btn_month.setText(MonthOperations.getMonthAsString(Integer.parseInt(db.month)-1));
        btn_year = (Button) view.findViewById(R.id.btn_year);
        btn_year.setText(db.year);
        adapter = new StickyHistoryAdapter(getActivity(),db,category_filter);
        listView = (StickyListHeadersListView) view.findViewById(R.id.sticky_list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        if(adapter.getCount()==0){
            if (BuildConfig.DEBUG) {
                view.findViewById(R.id.info_layout).setVisibility(View.VISIBLE);
                Log.e(Constants.TAG, "No record found");
            }
        }
        else
        if (BuildConfig.DEBUG) {
            view.findViewById(R.id.info_layout).setVisibility(View.GONE);
            Log.e(Constants.TAG, adapter.getCount()+" record found");
        }

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String click_id = ((TextView) view.findViewById(R.id.txt_id)).getText().toString();
        final Cursor cursor = db.getFromMasterByID(Integer.parseInt(click_id));
        String trans_type="",sms_id="";
        if(cursor.moveToNext()) {
            trans_type=cursor.getString(cursor.getColumnIndex(DBHelper.MASTER_COLUMN_TRANS_TYPE));
            sms_id = cursor.getString(cursor.getColumnIndex(DBHelper.MASTER_COLUMN_SMS_ID));


        }

        if(trans_type.equalsIgnoreCase(TYPES.TRANSACTION_TYPE.CASH_VAULT.toString())){
            showDialogConfirm(getActivity(),Integer.parseInt(click_id));
        }
        else if(sms_id!=null)
            showDialogCatogories(getActivity(),Integer.parseInt(click_id));

        else {
            Intent i = new Intent(view.getContext(), Expense_add_window.class);
            i.putExtra("RECID", click_id);
            startActivityForResult(i, 200); //200 -OK: assume HTTP 200 for create request :). It can be any value
        }
        if (BuildConfig.DEBUG) {
            Log.e(Constants.TAG, "Item " + click_id + " clicked!");
        }        

    }

    // this code has no impact
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (BuildConfig.DEBUG) {
            Log.e(Constants.TAG, "Results Invoked");
        }
        init();
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
    
    void showDialogCatogories(final Context context, final int RECID){
        final AlertDialog.Builder dialog = new AlertDialog.Builder(context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);

        //final CharSequence myList[] = db.getDefaultCategory();
        Cursor myList =db.getMyBudgetByCategory();
        dialog.setSingleChoiceItems(myList, -1,"category",  new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {

            }
        });
        dialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                ListView lw = ((AlertDialog) dialog).getListView();
                if(lw.getCheckedItemPosition()>=0) {
                    Cursor checkedItem =(Cursor) lw.getAdapter().getItem(lw.getCheckedItemPosition());
                    String category = checkedItem.getString(checkedItem.getColumnIndex("category"));
                    db.updateMaster(RECID, db.MASTER_COLUMN_CATEGORY, category);

                    Cursor cur = db.getFromMasterByID(RECID);
                    if(cur.moveToNext()) {
                        DBCategoryMap dbCatMap = new DBCategoryMap(getActivity());
                        dbCatMap.insertCategoryMap(cur.getString(cur.getColumnIndex(db.DESC)), category, TYPES.TRANSACTION_TYPE.EXPENSE.toString());
                        dbCatMap.close();
                    }
                    init();
                }
                // TODO Auto-generated method stub

            }

        });
        dialog.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub

            }
        });


        // Set dialog title
        dialog.setTitle("Choose the Category");
        dialog.show();
    }
    //we may need to remove this code. No options for cash vault for now
    void showDialogConfirm(final Context context, final int RECID){
        new AlertDialog.Builder(context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                .setTitle("Delete entry")
                .setMessage("Remove from Cash Vault ?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        db.updateMasterStatus(RECID, TYPES.TRANSACTION_STATUS.PENDING.toString());
                        init();

                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
