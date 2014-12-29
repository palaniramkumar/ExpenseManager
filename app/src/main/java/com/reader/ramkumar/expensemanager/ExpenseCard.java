package com.reader.ramkumar.expensemanager;

import android.content.Context;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
                        WeatherObject w1 = new WeatherObject(ExpenseCard.this);
                        w1.city = "Madrid";
                        w1.temperature = 24;
                        w1.weatherIcon = R.drawable.ic_action_sun;
                        w1.setObjectId(w1.city);
                        mLinearListAdapter.add(w1);
                        break;
                    case R.id.action_remove:
                        //Example: remove an item
                        mLinearListAdapter.remove(mLinearListAdapter.getItem(0));
                        break;
                }

            }
        });
        header.setTitle("Weather"); //should use R.string.
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
        WeatherObject w1 = new WeatherObject(this);
        w1.city = "London";
        w1.temperature = 16;
        w1.weatherIcon = R.drawable.ic_action_cloud;
        w1.setObjectId(w1.city); //It can be important to set ad id
        mObjects.add(w1);

        WeatherObject w2 = new WeatherObject(this);
        w2.city = "Rome";
        w2.temperature = 25;
        w2.weatherIcon = R.drawable.ic_action_sun;
        w2.setObjectId(w2.city);
        w2.setSwipeable(true);

        //Example onSwipe
        /*w2.setOnItemSwipeListener(new OnItemSwipeListener() {
            @Override
            public void onItemSwipe(ListObject object,boolean dismissRight) {
                Toast.makeText(getContext(), "Swipe on " + object.getObjectId(), Toast.LENGTH_SHORT).show();
            }
        });*/
        mObjects.add(w2);

        WeatherObject w3 = new WeatherObject(this);
        w3.city = "Paris";
        w3.temperature = 19;
        w3.weatherIcon = R.drawable.ic_action_cloudy;
        w3.setObjectId(w3.city);
        mObjects.add(w3);

        WeatherObject w4 = new WeatherObject(this);
        w4.city = "Virudhunagar";
        w4.temperature = 29;
        w4.weatherIcon = R.drawable.ic_action_cloudy;
        w4.setObjectId(w4.city);
        mObjects.add(w4);

        WeatherObject w5 = new WeatherObject(this);
        w5.city = "Chennai";
        w5.temperature = 21;
        w5.weatherIcon = R.drawable.ic_action_cloudy;
        w5.setObjectId(w5.city);
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
        WeatherObject weatherObject = (WeatherObject) object;
        icon.setImageResource(weatherObject.weatherIcon);
        city.setText(weatherObject.city);
        temperature.setText(weatherObject.temperature + weatherObject.temperatureUnit);

        return convertView;
    }

    @Override
    public int getChildLayoutId() {
        return R.layout.card_table_summary;
    }


    // -------------------------------------------------------------
    // Weather Object
    // -------------------------------------------------------------

    public class WeatherObject extends DefaultListObject {

        public String city;
        public int weatherIcon;
        public int temperature;
        public String temperatureUnit = "°C";

        public WeatherObject(Card parentCard) {
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
