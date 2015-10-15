package com.spiraclestudios.autoskola;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * TODO: document your custom view class.
 */
public class ZoznamTestouView extends CardView implements  ZoznamTestouEntryRecyclerViewAdapter.TestEntryClickListener{
    public TextView title_text;
    public ImageView title_icon;
    public LinearLayout content;
    public ImageButton expand_icon;
    public TextView zacat_nahodny_test;

    public RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    public enum Skupiny {
        AB,
        CDT
    }

    private Skupiny mAttrSkupina;

    public ZoznamTestouView(Context context) {
        super(context);
        //init();
    }

    public ZoznamTestouView(final Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ZoznamTestouView,
                0, 0);

        try {
            mAttrSkupina = Skupiny.values()[a.getInteger(R.styleable.ZoznamTestouView_skupina, 1)];
        } finally {
            a.recycle();
        }


        // [Init]
        inflate(getContext(), R.layout.zoznam_testou_view, this);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        title_text = (TextView) findViewById(R.id.title_text);
        title_icon = (ImageView) findViewById(R.id.title_icon);
        content = (LinearLayout) findViewById(R.id.content);
        expand_icon = (ImageButton) findViewById(R.id.expand_icon);
        zacat_nahodny_test = (TextView) findViewById(R.id.zacat_nahodny_test);

        Resources resources = getResources();
        String title = "Title";
        Drawable icon = null;

        if (mAttrSkupina != null) {
            switch (mAttrSkupina) {
                case AB:
                    title = resources.getString(R.string.skupina_ab);
                    icon = ContextCompat.getDrawable(context, R.drawable.ic_directions_car_black_24dp);
                    break;
                case CDT:
                    title = resources.getString(R.string.skupina_cdt);
                    icon = ContextCompat.getDrawable(context, R.drawable.ic_local_shipping_black_24dp);
                    break;
            }

            title_text.setText(title);
            title_icon.setImageDrawable(icon);
        }

        expand_icon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                content.setVisibility(content.isShown() ? GONE : VISIBLE);
            }
        });

        zacat_nahodny_test.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                int skupina = mAttrSkupina.ordinal();
                int index = -1;

                MoznostiTestuFragment newFragment = MoznostiTestuFragment.newInstance(skupina, index);

                ((MainActivity)getContext()).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.content_main, newFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ZoznamTestouEntryRecyclerViewAdapter(getDataSet());
        recyclerView.setAdapter(adapter);
        //RecyclerView.ItemDecoration itemDecoration =
        //        new DividerItemDecoration(this, LinearLayoutManager.VERTICAL);
        //recyclerView.addItemDecoration(itemDecoration);

        // Code to Add an item with default animation
        //((ZoznamTestouEntryRecyclerViewAdapter) adapter).addItem(obj, index);

        // Code to remove an item with default animation
        //((ZoznamTestouEntryRecyclerViewAdapter) adapter).deleteItem(index);


        // [SetOnClickListener for the adapter entries]
        ((ZoznamTestouEntryRecyclerViewAdapter)adapter).setOnItemClickListener(
            new ZoznamTestouEntryRecyclerViewAdapter.TestEntryClickListener() {
                @Override
                public void onItemClick(int position, View view) {
                    int skupina = mAttrSkupina.ordinal();
                    int index = position;
                    //int index = ((ZoznamTestouEntry)view).index;

                    MoznostiTestuFragment newFragment = MoznostiTestuFragment.newInstance(skupina, index);

                    ((MainActivity)getContext()).getSupportFragmentManager().beginTransaction()
                            .replace(R.id.content_main, newFragment)
                            .addToBackStack(null)
                            .commit();
                    }
                });
    }

    // TODO: Implement based on the android studio View template
    public ZoznamTestouView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        //init();
    }

    //private void init() {
    //}

    // TODO: Implement? I tried using the code a few lines above
    /*@Override
    protected void onResume() {
        super.onResume();
        ((ZoznamTestouEntryRecyclerViewAdapter) adapter).setOnItemClickListener(
                new ZoznamTestouEntryRecyclerViewAdapter.TestEntryClickListener() {
          @Override
          public void onItemClick(int position, View view) {
              Log.i("ZOZNAM_TESTOU_VIEW", " Clicked on Item " + position);
          }
        });
    }*/

    private ArrayList<DataObject> getDataSet() {
        ArrayList results = new ArrayList<>();
        for (int index = 0; index < 35; index++) {
            DataObject obj = new DataObject(index);
            results.add(index, obj);
        }
        return results;
    }

    public void onItemClick(int position, View view) {

    }
}