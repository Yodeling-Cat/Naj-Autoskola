// Copyright (c) 2015-2017. Spiracle Software. All Rights Reserved.

package com.spiraclestudios.autoskola.presentation.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.mikepenz.fastadapter.FastAdapter.OnClickListener;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.spiraclestudios.autoskola.R;
import com.spiraclestudios.autoskola.TestListEntry;
import com.spiraclestudios.autoskola.domain.Groups;
import com.spiraclestudios.autoskola.presentation.ui.dialogs.TestOptionsDialog;
import com.spiraclestudios.autoskola.repository.TestsListRepository;

/**
 * Added by benji on 14/10/2015.
 */
public class MainActivityFragment extends Fragment {

  private final static String STATE_RECYCLER_VIEW_LAST_POSITION = "recyclerViewLastPosition";

  public Groups group = Groups.AB;
  private FastItemAdapter<AbstractItem> fastAdapter;

  private int recyclerViewLastPosition = 0;

  @BindView(R.id.recycler_view) public RecyclerView recycler_view;

  public MainActivityFragment() {
  }

  public static MainActivityFragment newInstance(Groups group) {
    MainActivityFragment fragment = new MainActivityFragment();
    Bundle bundle = new Bundle();

    bundle.putSerializable("group", group);

    fragment.setArguments(bundle);
    return fragment;
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    group = (Groups) getArguments().getSerializable("group");

    View view = inflater.inflate(R.layout.fragment_main_tests_list, container, false);
    ButterKnife.bind(this, view);

    recycler_view.setHasFixedSize(true);
    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
    recycler_view.setLayoutManager(layoutManager);

    fastAdapter = new FastItemAdapter<>();
    fastAdapter.setHasStableIds(true);
    fastAdapter.withOnClickListener(new OnClickListener<AbstractItem>() {
      @Override public boolean onClick(View v, IAdapter<AbstractItem> adapter, AbstractItem item,
          int position) {
        if (item instanceof TestListEntry) {
          int index = ((TestListEntry) item).index;

          TestOptionsDialog dialog = TestOptionsDialog.newInstance(index);
          dialog.show(MainActivityFragment.this.getActivity().getSupportFragmentManager(),
              "TestOptions");
          return true;
        }
        return false;
      }
    });
    recycler_view.setAdapter(fastAdapter);
    fastAdapter.add(new TestsListRepository(getActivity()).getList(group));
    fastAdapter.notifyAdapterDataSetChanged();

    if (savedInstanceState != null) {
      // Restore recycler view scrolling position.
      recyclerViewLastPosition = savedInstanceState.getInt(STATE_RECYCLER_VIEW_LAST_POSITION);
      recycler_view.getLayoutManager().scrollToPosition(recyclerViewLastPosition);
    }

    return view;
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);

    // Save recycler view scrolling position.
    outState.putInt(STATE_RECYCLER_VIEW_LAST_POSITION,
        ((LinearLayoutManager) recycler_view.getLayoutManager()).findFirstCompletelyVisibleItemPosition());
  }

  @Override public void onPause() {
    super.onPause();

    // Save recycler view scrolling position.
    recyclerViewLastPosition =
        ((LinearLayoutManager) recycler_view.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
  }

  @Override public void onResume() {
    super.onResume();

    // Update date inside of adapter by recreating it.
    fastAdapter.clear();
    fastAdapter.add(new TestsListRepository(getActivity()).getList(group));
    fastAdapter.notifyAdapterDataSetChanged();

    // Restore recycler view scrolling position.
    recycler_view.getLayoutManager().scrollToPosition(recyclerViewLastPosition);
  }
}