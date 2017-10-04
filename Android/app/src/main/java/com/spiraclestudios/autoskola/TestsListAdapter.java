// Copyright (c) 2015-2017. Spiracle Software. All Rights Reserved.

package com.spiraclestudios.autoskola;

/**
 * Added by benji on 15/10/2015.
 */

// TODO: Delete this class
public class TestsListAdapter {
/*public class TestsListAdapter extends RecyclerView.Adapter<TestsListAdapter.ViewHolder> {

  private Context mContext;
  private ArrayList<ListEntry> mDataSet;

  public TestsListAdapter(ArrayList<ListEntry> dataSet) {
    mDataSet = dataSet;
  }

  @Override
  public TestsListAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
    mContext = parent.getContext();

    View view = LayoutInflater.from(mContext).inflate(R.layout.test__list_entry, parent, false);

    return new ViewHolder(view, new ViewHolder.IViewOnClickListener() {
      public void onItemClick(View view) {
        int index = mDataSet.get(
            ((RecyclerView) parent.findViewById(R.id.recycler_view)).getChildAdapterPosition(view))
            .getIndex();

        TestOptionsDialog dialog = TestOptionsDialog.newInstance(index);
        dialog.show(((HomeActivity) view.getContext()).getSupportFragmentManager(),
            "MoznostiTestu");
      }

      // public void onExpandButtonClick(View view) { }
    });
  }

  @Override public void onBindViewHolder(final ViewHolder holder, final int position) {
    Resources res = mContext.getResources();
    final TestListEntry entry = getItem(position);
    holder.test_id.setText(String.format(Locale.ENGLISH, "#%d", entry.getIndex()));
    holder.times_completed.setText(
        String.format(res.getString(R.string.tests_list__text__completed), entry.getTimesCompleted()));

    holder.overflow_button.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(final View view) {
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
          @Override public boolean onMenuItemClick(MenuItem item) {
            Intent intent;

            switch (item.getItemId()) {
              case R.id.item__correct_answers:
                intent = new Intent(mContext, TestActivity.class);
                intent.putExtra(TestActivity.EXTRA_TEST_TYPE,
                    TestActivity.TestTypes.CORRECT_ANSWERS);
                intent.putExtra(TestActivity.EXTRA_TEST_ID, entry.getIndex());
                view.getContext().startActivity(intent);
                return true;

              case R.id.item__history:
                if (entry.getTimesCompleted() > 0) {
                  intent = new Intent(mContext, HistoryActivity.class);
                  intent.putExtra(HistoryActivity.EXTRA_TEST_ID, entry.getIndex());
                  mContext.startActivity(intent);
                } else {
                  Toast.makeText(mContext, R.string.history__toast__history_is_empty, Toast.LENGTH_SHORT)
                      .show();
                }
                return true;
            }
            return true;
          }
        });
        popupMenu.inflate(R.menu.list__tests);
        popupMenu.show();
      }
    });
  }

  public void addItem(TestListEntry dataObj, int index) {
    mDataSet.add(dataObj);
    notifyItemInserted(index);
  }

  public void deleteItem(int index) {
    mDataSet.remove(index);
    notifyItemRemoved(index);
  }

  public TestListEntry getItem(int position) {
    return mDataSet.get(position);
  }

  @Override public int getItemCount() {
    return mDataSet.size();
  }

  public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public IViewOnClickListener mListener;

    public TextView test_id;
    public TextView times_completed;
    public ImageButton overflow_button;

    public ViewHolder(View view, IViewOnClickListener listener) {
      super(view);
      mListener = listener;
      test_id = (TextView) view.findViewById(R.id.test_index);
      times_completed = (TextView) view.findViewById(R.id.times_completed);
      overflow_button = (ImageButton) view.findViewById(R.id.overflow_button);

      view.setOnClickListener(this);
    }

    @Override public void onClick(View view) {
      //if (view instanceof ImageButton) { mListener.onExpandButtonClick; }
      mListener.onItemClick(view);
    }

    public interface IViewOnClickListener {

      void onItemClick(View view);
      //void onExpandButtonClick(View view);
    }
  }*/
}