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
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class ZoznamTestouView extends CardView implements  ZoznamTestouEntryRecyclerViewAdapter.TestEntryClickListener{
    public TextView title_text;
    public ImageView title_icon;
    public LinearLayout content;
    public ImageButton expand_icon;
    public ImageButton zacat_nahodny_test;

    public RecyclerView recycler_view;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private Helper.Groups mAttrSkupina;

    public ZoznamTestouView(Context context) {
        this(context, null);
    }

    public ZoznamTestouView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZoznamTestouView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ZoznamTestouView,
                0, 0);

        try {
            mAttrSkupina = Helper.Groups.values()[a.getInteger(R.styleable.ZoznamTestouView_skupina, 1)];
        } finally {
            a.recycle();
        }

        inflate(getContext(), R.layout.zoznam_testou_view, this);
        recycler_view = (RecyclerView) findViewById(R.id.recycler_view);
        title_text = (TextView) findViewById(R.id.title_text);
        title_icon = (ImageView) findViewById(R.id.title_icon);
        content = (LinearLayout) findViewById(R.id.content);
        expand_icon = (ImageButton) findViewById(R.id.expand_icon);
        zacat_nahodny_test = (ImageButton) findViewById(R.id.zacat_nahodny_test);

        // Collapse the view's content by default
        content.setVisibility(GONE);

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

        OnClickListener expandOnClickListener = new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (content.isShown()) {
                    Effects.slide_up(getContext(), content);
                    content.setVisibility(GONE);
                }
                else {
                    content.setVisibility(VISIBLE);
                    Effects.slide_down(getContext(), content);
                }
            }
        };

        expand_icon.setOnClickListener(expandOnClickListener);
        title_text.setOnClickListener(expandOnClickListener);

        recycler_view.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(context);
        recycler_view.setLayoutManager(layoutManager);
        adapter = new ZoznamTestouEntryRecyclerViewAdapter(getDataSet());
        recycler_view.setAdapter(adapter);
        //RecyclerView.ItemDecoration itemDecoration =
        //        new DividerItemDecoration(this, LinearLayoutManager.VERTICAL);
        //recycler_view.addItemDecoration(itemDecoration);

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

                    MoznostiTestuFragment newFragment = MoznostiTestuFragment.newInstance(skupina, index);

                    ((MainActivity)getContext()).getSupportFragmentManager().beginTransaction()
                            .replace(R.id.content_main, newFragment)
                            .addToBackStack(null)
                            .commit();
                }
            });

        zacat_nahodny_test.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                int skupina = mAttrSkupina.ordinal();
                int index = -1;

                MoznostiTestuFragment newFragment = MoznostiTestuFragment.newInstance(skupina, index);

                ((MainActivity) getContext()).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.content_main, newFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    // TODO: Implement? I tried jamming the code into some other function a few lines above
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