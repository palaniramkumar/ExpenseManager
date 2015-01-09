package com.reader.ramkumar.expensemanager.adapter;

import android.content.Context;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.reader.ramkumar.expensemanager.R;

import java.util.ArrayList;
import java.util.List;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.base.BaseCard;
import it.gmariotti.cardslib.library.prototypes.CardWithList;
import it.gmariotti.cardslib.library.prototypes.LinearListView;

/**
 * Created by Ramkumar on 27/12/14.
 */
public class ExpenseCard extends CardWithList {
    public ExpenseCard(Context context) {
        super(context);
    }

    @Override
    protected CardHeader initCardHeader() {

        //Add Header
        CardHeader header = new CardHeader(getContext(), R.layout.carddemo_googlenowweather_inner_header);

        //Add a popup menu. This method set OverFlow button to visible
        header.setPopupMenu(R.menu.popup_item, new CardHeader.OnClickCardHeaderPopupMenuListener() {
            @Override
            public void onMenuItemClick(BaseCard card, MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.action_add:
                        //Example: add an item
                        CostObject w1 = new CostObject(ExpenseCard.this);
                        w1.type = "Food";
                        w1.amount = 8400;
                        w1.trendIcon = R.drawable.ic_action_sun;
                        w1.setObjectId(w1.type);
                        mLinearListAdapter.add(w1);
                        break;
                    case R.id.action_remove:
                        //Example: remove an item
                        mLinearListAdapter.remove(mLinearListAdapter.getItem(0));
                        break;
                }

            }
        });
        header.setTitle("You have 8,432 Left for this month"); //should use R.string.
        return header;
    }

    @Override
    protected void initCard() {

        //Set the whole card as swipeable
        setSwipeable(true);
        setOnSwipeListener(new OnSwipeListener() {
            @Override
            public void onSwipe(Card card) {
                Toast.makeText(getContext(), "Swipe on " + card.getCardHeader().getTitle(), Toast.LENGTH_SHORT).show();
            }
        });

    }


    @Override
    protected List<ListObject> initChildren() {

        //Init the list
        List<ListObject> mObjects = new ArrayList<ListObject>();

        //Add an object to the list
        CostObject w1 = new CostObject(this);
        w1.type = "Car";
        w1.amount = 2400;
        w1.trendIcon = R.drawable.ic_action_expand;
        w1.setObjectId(w1.type); //It can be important to set ad id
        mObjects.add(w1);

        CostObject w2 = new CostObject(this);
        w2.type = "Home";
        w2.amount = 21000;
        w2.trendIcon = R.drawable.ic_action_collapse;
        w2.setObjectId(w2.type);
        w2.setSwipeable(true);

        //Example onSwipe
        /*w2.setOnItemSwipeListener(new OnItemSwipeListener() {
            @Override
            public void onItemSwipe(ListObject object,boolean dismissRight) {
                Toast.makeText(getContext(), "Swipe on " + object.getObjectId(), Toast.LENGTH_SHORT).show();
            }
        });*/
        mObjects.add(w2);

        CostObject w3 = new CostObject(this);
        w3.type = "Food";
        w3.amount = 1200;
        w3.trendIcon = R.drawable.ic_action_expand;
        w3.setObjectId(w3.type);
        mObjects.add(w3);

        CostObject w4 = new CostObject(this);
        w4.type = "Misc";
        w4.amount = 8000;
        w4.trendIcon = R.drawable.ic_action_collapse;
        w4.setObjectId(w4.type);
        mObjects.add(w4);

        CostObject w5 = new CostObject(this);
        w5.type = "Medical";
        w5.amount = 800;
        w5.trendIcon = R.drawable.ic_action_expand;
        w5.setObjectId(w5.type);
        mObjects.add(w5);

        return mObjects;
    }

    @Override
    public View setupChildView(int childPosition, ListObject object, View convertView, ViewGroup parent) {

        //Setup the ui elements inside the item
        TextView city = (TextView) convertView.findViewById(R.id.carddemo_weather_city);
        ImageView icon = (ImageView) convertView.findViewById(R.id.carddemo_weather_icon);
        TextView temperature = (TextView) convertView.findViewById(R.id.carddemo_weather_temperature);

        //Retrieve the values from the object
        CostObject costObject = (CostObject) object;
        icon.setImageResource(costObject.trendIcon);
        city.setText(costObject.type);
        temperature.setText(costObject.currencyUnit + costObject.amount);

        return convertView;
    }

    @Override
    public int getChildLayoutId() {
        return R.layout.card_table_summary;
    }


    // -------------------------------------------------------------
    // Weather Object
    // -------------------------------------------------------------

    public class CostObject extends DefaultListObject {

        public String type;
        public int trendIcon;
        public int amount;
        public String currencyUnit = "₹";

        public CostObject(Card parentCard) {
            super(parentCard);
            init();
        }

        private void init() {
            //OnClick Listener
            setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(LinearListView parent, View view, int position, ListObject object) {
                    Toast.makeText(getContext(), "Click on " + getObjectId(), Toast.LENGTH_SHORT).show();
                }
            });

            //OnItemSwipeListener
            setOnItemSwipeListener(new OnItemSwipeListener() {
                @Override
                public void onItemSwipe(ListObject object, boolean dismissRight) {
                    Toast.makeText(getContext(), "Swipe on " + object.getObjectId(), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }


}
