package com.omisoft.hsracer.features.results;

import static com.omisoft.hsracer.constants.Constants.FINISH_POSITION;
import static com.omisoft.hsracer.constants.Constants.NAME_OF_RACE;
import static com.omisoft.hsracer.constants.Constants.RACE_DATE;
import static com.omisoft.hsracer.constants.Constants.RACE_DESCRIPTION;
import static com.omisoft.hsracer.constants.Constants.RACE_DISTANCE;
import static com.omisoft.hsracer.constants.Constants.RACE_MAX_SPEED;
import static com.omisoft.hsracer.constants.Constants.RACE_SHARE_URL;
import static com.omisoft.hsracer.constants.Constants.RACE_TIME;
import static com.omisoft.hsracer.constants.Constants.RACE_TYPE;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import butterknife.BindView;
import com.omisoft.hsracer.R;
import com.omisoft.hsracer.common.BaseActivity;
import com.omisoft.hsracer.utils.AndroidUtils;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by developer on 23.11.17.
 */

public class ResultDetailsActivity extends BaseActivity {

  private final SimpleDateFormat SIMPLE_DATE_FORMATTER = new SimpleDateFormat(" MMM d");

  @BindView(R.id.result_details_name)
  TextView mRaceName;
  @BindView(R.id.details_finish_position)
  TextView mFinishPosition;
  @BindView(R.id.result_details_description)
  TextView mDetailedDescription;
  @BindView(R.id.result_details_date)
  TextView mDate;
  @BindView(R.id.result_details_max_speed)
  TextView mMaxSpeed;
  @BindView(R.id.result_details_max_distance)
  TextView mMaxDistance;
  @BindView(R.id.result_details_type)
  TextView mRaceType;
  @BindView(R.id.result_details_time)
  TextView mRacingTime;

  String mURLForSharing;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_result_details);
    Toolbar toolbar = findViewById(R.id.result_details_toolbar);
    toolbar.setTitle("");
    setSupportActionBar(toolbar);
    @SuppressLint("PrivateResource") final Drawable upArrow = getResources()
        .getDrawable(R.drawable.ic_arrow_left);
    upArrow.setColorFilter(getResources().getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setHomeAsUpIndicator(upArrow);
    populateViews();
  }

  /**
   * Populates views
   */
  private void populateViews() {

    Bundle bundleResult = getIntent().getExtras();

    if (bundleResult != null) {
      mRaceName.setText(bundleResult.getString(NAME_OF_RACE));
      mFinishPosition.setText(bundleResult.getString(FINISH_POSITION));
      mMaxSpeed.setText(String.valueOf(bundleResult.getDouble(RACE_MAX_SPEED)));
      mURLForSharing = bundleResult.getString(RACE_SHARE_URL);
      mDate.setText(bundleResult.getString(RACE_DATE));
      mRaceType.setText(bundleResult.getString(RACE_TYPE));
      mRacingTime.setText(AndroidUtils.formatTimeRacing(bundleResult.getLong(RACE_TIME)));
      mMaxDistance.setText(String.valueOf(bundleResult.getLong(RACE_DISTANCE)));
      mDetailedDescription.setText(bundleResult.getString(RACE_DESCRIPTION));
    }
  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_result_details, menu);
    return true;
  }

  /**
   * When the share button is clicked a dialog with all the application which can share that
   * information is displayed on the bottom of the screen
   */
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {

    int itemFromMenu = item.getItemId();

    switch (itemFromMenu) {

      case R.id.menu_item_share_result_details:

        List<Intent> targetShareIntents = new ArrayList<>();
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        List<ResolveInfo> resInfo = getPackageManager().queryIntentActivities(shareIntent, 0);
        if (!resInfo.isEmpty()) {
          for (ResolveInfo resolveInfo : resInfo) {
            String packageName = resolveInfo.activityInfo.packageName;
            if (packageName.contains("com.twitter.android") || packageName
                .contains("com.facebook.katana")
                || packageName.contains("com.whatsapp") || packageName
                .contains("com.google.android.apps.plus")
                || packageName.contains("com.google.android.talk") || packageName
                .contains("com.slack")
                || packageName.contains("com.google.android.gm") || packageName
                .contains("com.facebook.orca")
                || packageName.contains("com.yahoo.mobile") || packageName
                .contains("com.skype.raider")
                || packageName.contains("com.android.mms") || packageName
                .contains("com.linkedin.android")
                || packageName.contains("com.google.android.apps.messaging")) {
              Intent intentToBeAdded = new Intent();

              intentToBeAdded
                  .setComponent(new ComponentName(packageName, resolveInfo.activityInfo.name));
              intentToBeAdded.putExtra("AppName", getString(R.string.app_name));
              intentToBeAdded.setAction(Intent.ACTION_SEND);
              intentToBeAdded.setType("text/plain");
              Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
              intentToBeAdded.putExtra(Intent.EXTRA_SHORTCUT_ICON, bitmap);
              intentToBeAdded.putExtra(Intent.EXTRA_TEXT, mURLForSharing);
              intentToBeAdded
                  .putExtra(Intent.EXTRA_SUBJECT, "My Race Results");
              intentToBeAdded.setPackage(packageName);
              targetShareIntents.add(intentToBeAdded);
            }
          }
          Intent chooserIntent = Intent
              .createChooser(targetShareIntents.remove(0), "Select app to share");
          chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
              targetShareIntents.toArray(new Parcelable[]{}));
          startActivity(chooserIntent);
        } else {
          showWhiteToastMessage("No apps to share");
        }
        return true;

      case android.R.id.home:
        supportFinishAfterTransition();
        return true;
    }

    return super.onOptionsItemSelected(item);
  }
}
