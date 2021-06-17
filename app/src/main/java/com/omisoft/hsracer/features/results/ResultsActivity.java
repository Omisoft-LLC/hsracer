package com.omisoft.hsracer.features.results;

import static com.omisoft.hsracer.constants.Constants.FINISH_POSITION;
import static com.omisoft.hsracer.constants.Constants.ITEMS_PER_PAGE;
import static com.omisoft.hsracer.constants.Constants.LIST_WITH_RESULTS;
import static com.omisoft.hsracer.constants.Constants.NAME_OF_RACE;
import static com.omisoft.hsracer.constants.Constants.NUMBER_OF_ALL_PAGES;
import static com.omisoft.hsracer.constants.Constants.NUMBER_OF_PAGES;
import static com.omisoft.hsracer.constants.Constants.RACE_DATE;
import static com.omisoft.hsracer.constants.Constants.RACE_DESCRIPTION;
import static com.omisoft.hsracer.constants.Constants.RACE_DISTANCE;
import static com.omisoft.hsracer.constants.Constants.RACE_MAX_SPEED;
import static com.omisoft.hsracer.constants.Constants.RACE_SHARE_URL;
import static com.omisoft.hsracer.constants.Constants.RACE_TIME;
import static com.omisoft.hsracer.constants.Constants.RACE_TYPE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import com.omisoft.hsracer.R;
import com.omisoft.hsracer.adapters.ResultsAdapter;
import com.omisoft.hsracer.adapters.callbacks.ResultViewInterface;
import com.omisoft.hsracer.common.BaseActivity;
import com.omisoft.hsracer.dto.RaceResultDTO;
import com.omisoft.hsracer.features.results.actions.GetResultsServerAction;
import com.omisoft.hsracer.features.results.comparator.ResultsDateComparator;
import com.omisoft.hsracer.features.results.events.SuccessResultsEvent;
import java.util.ArrayList;
import java.util.Collections;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Results activity
 * Created by Omisoft LLC. on 4/24/17.
 */

public class ResultsActivity extends BaseActivity implements ResultViewInterface {

  @BindView(R.id.results_list_view)
  RecyclerView mResultsRecyclerView;
  @BindView(R.id.empty_list_for_results)
  TextView mEmptyListTextView;
  private ResultsAdapter mResultsAdapter;
  private ArrayList<RaceResultDTO> mListWithRaceResults;
  private int mCurrentPage;
  private int mNumberOfPages;
  private LinearLayoutManager mLinearLayoutManager;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_results);
    Toolbar toolbar = findViewById(R.id.toolbar_results);
    setSupportActionBar(toolbar);
    @SuppressLint("PrivateResource") final Drawable upArrow = getResources()
        .getDrawable(R.drawable.ic_arrow_left);
    upArrow.setColorFilter(getResources().getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setHomeAsUpIndicator(upArrow);
    if (savedInstanceState == null) {
      getExecutor()
          .submit(new GetResultsServerAction(getApp(), String.valueOf(1),
              String.valueOf(ITEMS_PER_PAGE)));
      initAdapter();
    } else {
      mCurrentPage = savedInstanceState.getInt(NUMBER_OF_PAGES);
      mNumberOfPages = savedInstanceState.getInt(NUMBER_OF_ALL_PAGES);
      mListWithRaceResults = savedInstanceState.getParcelableArrayList(LIST_WITH_RESULTS);
      initAdapter();
    }
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    outState.putParcelableArrayList(LIST_WITH_RESULTS, mListWithRaceResults);
    outState.putInt(NUMBER_OF_PAGES, mCurrentPage);
    outState.putInt(NUMBER_OF_ALL_PAGES, mNumberOfPages);
    super.onSaveInstanceState(outState);
  }

  /**
   * Init the adapter with lazy loading on each 20 elements.
   */
  private void initAdapter() {
    mLinearLayoutManager = new LinearLayoutManager(this);
    mResultsRecyclerView.setHasFixedSize(true);
    mResultsRecyclerView.setLayoutManager(mLinearLayoutManager);
    mResultsAdapter = new ResultsAdapter(mListWithRaceResults, this);
    mResultsRecyclerView.setAdapter(mResultsAdapter);
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
    mResultsRecyclerView.addOnScrollListener(onScrollListener);
  }

  /**
   * Take the list with the results from the server and displays it in the recycler view
   */
  @Subscribe(threadMode = ThreadMode.MAIN)
  public void initListView(SuccessResultsEvent event) {
    mListWithRaceResults = new ArrayList<>(event.getResults());
    mCurrentPage = event.getCurrentPage();
    mNumberOfPages = event.getLastPage();
    if (mCurrentPage == 1 && mListWithRaceResults.size() == 0) {
      hideRecyclerView();
    } else {
      showRecyclerView();
    }
    Collections.sort(mListWithRaceResults, new ResultsDateComparator());

    if (mResultsAdapter.getMRacesResults() == null) {
      mResultsAdapter.setMRacesResults(mListWithRaceResults);
    } else {
      mResultsAdapter.getMRacesResults().addAll(mListWithRaceResults);
    }
    mResultsAdapter.notifyDataSetChanged();
  }


  private void showRecyclerView() {
    mResultsRecyclerView.setVisibility(View.VISIBLE);
    mEmptyListTextView.setVisibility(View.INVISIBLE);
  }

  private void hideRecyclerView() {
    mResultsRecyclerView.setVisibility(View.INVISIBLE);
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
    RaceResultDTO clickedResult = mResultsAdapter.getMRacesResults().get(position);
    Intent intent = new Intent(ResultsActivity.this, ResultDetailsActivity.class);
    Bundle bundle = new Bundle();
    intent.putExtra(NAME_OF_RACE, clickedResult.getName());
    intent.putExtra(FINISH_POSITION,
        ((TextView) view.findViewById(R.id.position_result_text_view)).getText().toString());
    intent.putExtra(RACE_MAX_SPEED, clickedResult.getMaxSpeed());
    intent.putExtra(RACE_SHARE_URL, clickedResult.getShareURL());
    intent.putExtra(RACE_DATE,
        ((TextView) view.findViewById(R.id.date_result_text_view)).getText().toString());
    intent.putExtra(RACE_TYPE, clickedResult.getRaceType().name());
    intent.putExtra(RACE_TIME, clickedResult.getRaceTimeInMills());
    intent.putExtra(RACE_DISTANCE, clickedResult.getRaceDistance());
    intent.putExtra(RACE_DESCRIPTION, clickedResult.getDescription());
    if (VERSION.SDK_INT > VERSION_CODES.LOLLIPOP) {
      Pair<View, String> p1 = Pair
          .create(view.findViewById(R.id.race_name_text_view), getString(R.string.transition_name));
      Pair<View, String> p2 = Pair
          .create(view.findViewById(R.id.position_result_text_view),
              getString(R.string.transition_race_place));
      ActivityOptionsCompat options = ActivityOptionsCompat.
          makeSceneTransitionAnimation(this, p1, p2);
      startActivity(intent, options.toBundle());
    } else {
      startActivity(intent);
    }
  }
}
