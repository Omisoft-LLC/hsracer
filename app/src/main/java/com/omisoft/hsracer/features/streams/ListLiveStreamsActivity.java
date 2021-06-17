package com.omisoft.hsracer.features.streams;

import static com.omisoft.hsracer.constants.Constants.ITEMS_PER_PAGE;
import static com.omisoft.hsracer.constants.Constants.LIST_WITH_RESULTS;
import static com.omisoft.hsracer.constants.Constants.NUMBER_OF_ALL_PAGES;
import static com.omisoft.hsracer.constants.Constants.NUMBER_OF_PAGES;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import com.omisoft.hsracer.R;
import com.omisoft.hsracer.adapters.StreamsListAdapter;
import com.omisoft.hsracer.adapters.callbacks.ResultViewInterface;
import com.omisoft.hsracer.common.BaseActivity;
import com.omisoft.hsracer.dto.StreamListDTO;
import com.omisoft.hsracer.features.results.ResultDetailsActivity;
import com.omisoft.hsracer.features.results.actions.GetResultsServerAction;
import com.omisoft.hsracer.features.streams.actions.GetLiveStreamsListAction;
import com.omisoft.hsracer.features.streams.comparator.StreamsDateComparator;
import com.omisoft.hsracer.features.streams.events.SuccessStreamsListEvent;
import java.util.ArrayList;
import java.util.Collections;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Results activity
 * Created by Omisoft LLC. on 4/24/17.
 */

public class ListLiveStreamsActivity extends BaseActivity implements ResultViewInterface {


  @BindView(R.id.streams_list_view)
  RecyclerView mStreamsResultsListView;
  @BindView(R.id.empty_streams_list)
  TextView mEmptyListTextView;
  private StreamsListAdapter mStreamsListAdapter;
  private ArrayList<StreamListDTO> streamListDTOArrayList;
  private int mCurrentPage;
  private int mNumberOfPages;
  private LinearLayoutManager mLinearLayoutManager;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_list_live_streams);
    Toolbar toolbar = findViewById(R.id.toolbar_streams_list);
    setSupportActionBar(toolbar);
    @SuppressLint("PrivateResource") final Drawable upArrow = getResources()
        .getDrawable(R.drawable.ic_arrow_left);
    upArrow.setColorFilter(getResources().getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setHomeAsUpIndicator(upArrow);
    if (savedInstanceState == null) {
      getExecutor()
          .submit(new GetLiveStreamsListAction(getApp(), String.valueOf(1),
              String.valueOf(ITEMS_PER_PAGE)));
      initAdapter();
    } else {
      mCurrentPage = savedInstanceState.getInt(NUMBER_OF_PAGES);
      mNumberOfPages = savedInstanceState.getInt(NUMBER_OF_ALL_PAGES);
      streamListDTOArrayList = savedInstanceState.getParcelableArrayList(LIST_WITH_RESULTS);
      initAdapter();
    }
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    outState.putParcelableArrayList(LIST_WITH_RESULTS, streamListDTOArrayList);
    outState.putInt(NUMBER_OF_PAGES, mCurrentPage);
    outState.putInt(NUMBER_OF_ALL_PAGES, mNumberOfPages);
    super.onSaveInstanceState(outState);
  }

  /**
   * Init the adapter with lazy loading on each 20 elements.
   */
  private void initAdapter() {
    mLinearLayoutManager = new LinearLayoutManager(this);
    mStreamsResultsListView.setHasFixedSize(true);
    mStreamsResultsListView.setLayoutManager(mLinearLayoutManager);
    mStreamsListAdapter = new StreamsListAdapter(streamListDTOArrayList, this);
    mStreamsResultsListView.setAdapter(mStreamsListAdapter);
    OnScrollListener onScrollListener = new OnScrollListener() {
      @Override
      public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        if (mLinearLayoutManager.getChildCount() +
            mLinearLayoutManager.findFirstVisibleItemPosition()
            >= mLinearLayoutManager.getItemCount() && mNumberOfPages != mCurrentPage) {
          mCurrentPage++;
          getExecutor()
              .submit(new GetResultsServerAction(getApp(), String.valueOf(mCurrentPage),
                  String.valueOf(ITEMS_PER_PAGE)));
        }
      }

      @Override
      public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
      }
    };
    mStreamsResultsListView.addOnScrollListener(onScrollListener);
  }

  /**
   * Take the list with the results from the server and displays it in the recycler view
   */
  @Subscribe(threadMode = ThreadMode.MAIN)
  public void initListView(SuccessStreamsListEvent event) {
    streamListDTOArrayList = new ArrayList<>(event.getResults());
    mCurrentPage = event.getCurrentPage();
    mNumberOfPages = event.getLastPage();
    if (mCurrentPage == 1 && streamListDTOArrayList.size() == 0) {
      hideRecyclerView();
    } else {
      showRecyclerView();
    }
    Collections.sort(streamListDTOArrayList, new StreamsDateComparator());

    if (mStreamsListAdapter.getMStreamsList() == null) {
      mStreamsListAdapter.setMStreamsList(streamListDTOArrayList);
    } else {
      mStreamsListAdapter.getMStreamsList().addAll(streamListDTOArrayList);
    }
    mStreamsListAdapter.notifyDataSetChanged();
  }


  private void showRecyclerView() {
    mStreamsResultsListView.setVisibility(View.VISIBLE);
    mEmptyListTextView.setVisibility(View.INVISIBLE);
  }

  private void hideRecyclerView() {
    mStreamsResultsListView.setVisibility(View.INVISIBLE);
    mEmptyListTextView.setVisibility(View.VISIBLE);
  }

  /**
   * When a view from the list is clicked the user is navigated to {@link ResultDetailsActivity}
   * where you can see the details for the clicked race. The transfer of the names (in particular
   * the race name, place in the race and date of the race) is achieved with transition;
   */
  @SuppressWarnings("unchecked")
  @Override
  public void clickedView(View view, int position) {
    StreamListDTO streamListDTO = mStreamsListAdapter.getMStreamsList().get(position);
    Intent intent = new Intent(ListLiveStreamsActivity.this, StreamDetailsActivity.class);
    intent.putExtra(StreamListDTO.class.getSimpleName(), streamListDTO);
    startActivity(intent);
  }
}
