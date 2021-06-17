package com.omisoft.hsracer.features.share;

import static com.omisoft.hsracer.constants.Constants.SHOW_PREVIEW;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import butterknife.BindView;
import com.omisoft.hsracer.R;
import com.omisoft.hsracer.common.BaseActivity;
import com.omisoft.hsracer.dto.PublishDTO;
import com.omisoft.hsracer.features.home.HomeActivity;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class ShareRaceDataActivity extends BaseActivity {

  @BindView(R.id.share_to_fb)
  CheckBox shareToFB;
  @BindView(R.id.car_for_publish)
  TextView carText;
  @BindView(R.id.car_alias_for_publish)
  TextView carAliasText;
  @BindView(R.id.position_for_publish)
  TextView positionText;
  @BindView(R.id.race_time_for_publish)
  TextView raceTimeText;
  @BindView(R.id.max_speed_for_publish)
  TextView maxSpeedText;
  @BindView(R.id.avg_speed_for_publish)
  TextView avgSpeedText;
  @BindView(R.id.button_home)
  Button homeButton;
  @BindView(R.id.button_share)
  Button shareButton;
  private PublishDTO publishDTO;
  private String shareURL;
  private long racingRestID;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_share_race_data);
    publishDTO = getIntent().getExtras().getParcelable(PublishDTO.class.getName() + "publishDTO");
    shareURL = getIntent().getExtras().getString(PublishDTO.class.getName() + "shareURL");
    racingRestID = getIntent().getExtras().getLong(PublishDTO.class.getName() + "racingRestID");
    publishDataView();
  }

  public void buttonShareClick(View view) {
    facebookCheckEventBus();
  }

  public void goToHome(View view) {
    Intent i = new Intent(this, HomeActivity.class);
    startActivity(i);
  }

  private void publishDataView() {
    String unit;
    if (getSharedPreferences()
        .getString(getString(R.string.pref_metric_key), getString(R.string.metric_system))
        .equals(getString(R.string.metric_system))) {
      unit = getString(R.string.roll_metric_speed);
    } else {
      unit = getString(R.string.roll_imperial_speed);
    }
    formatTime(publishDTO.getRaceTime());
    carText.setText(publishDTO.getCarName());
    carAliasText.setText(publishDTO.getAlias());
    positionText.setText(String.valueOf(publishDTO.getPosition()));
    raceTimeText.setText(String.valueOf(formatTime(publishDTO.getRaceTime())));
    //TODO: fix that later dammit people why do you make like that ?
    maxSpeedText.setText(String.valueOf(publishDTO.getMaxSpeed()) + " " + unit);
    avgSpeedText.setText(String.valueOf(publishDTO.getAvgSpeed()) + " " + unit);
  }

  /**
   * Checks if the check box is checked
   */
  private void facebookCheckEventBus() {
    Intent i = new Intent(this, UploadVideoActivity.class);
    i.putExtra(ShareRaceDataActivity.class.getName(), racingRestID);

    if (shareToFB.isChecked()) {
      if (getSharedPreferences().getBoolean(SHOW_PREVIEW, true)) {
        startActivity(i);
      }
      shareURLToFacebook();
    } else {
      if (getSharedPreferences().getBoolean(SHOW_PREVIEW, true)) {
        startActivity(i);
      } else {
        showWhiteToastMessage(R.string.where_to_share);
      }
    }
  }

  /**
   * Share the information to facebook
   */
  private void shareURLToFacebook() {
    Intent i = new Intent(Intent.ACTION_SEND);
    i.setType("text/plain");

    boolean facebookFound = false;
    List<ResolveInfo> match = getPackageManager().queryIntentActivities(i, 0);
    for (ResolveInfo elem : match) {
      if (elem.activityInfo.packageName.toLowerCase().startsWith("com.facebook.katana")) {
        i.setPackage(elem.activityInfo.packageName);
        if (shareURL != null) {
          i.putExtra(Intent.EXTRA_TEXT, shareURL);
        }
        facebookFound = true;
        break;
      }
    }
    if (!facebookFound) {
      String browserURL = "https://www.facebook.com/sharer/sharer.php?u=" + shareURL;
      i = new Intent(Intent.ACTION_VIEW, Uri.parse(browserURL));
    }
    startActivity(i);
  }

  /**
   * Parse a given time by long
   * @param l
   * @return
   */
  private String formatTime(long l) {
    final long hr = TimeUnit.MILLISECONDS.toHours(l);
    final long min = TimeUnit.MILLISECONDS.toMinutes(l - TimeUnit.HOURS.toMillis(hr));
    final long sec = TimeUnit.MILLISECONDS
        .toSeconds(l - TimeUnit.HOURS.toMillis(hr) - TimeUnit.MINUTES.toMillis(min));
    final long ms = TimeUnit.MILLISECONDS.toMillis(
        l - TimeUnit.HOURS.toMillis(hr) - TimeUnit.MINUTES.toMillis(min) - TimeUnit.SECONDS
            .toMillis(sec));

    if (sec == 0) {
      return String.format(Locale.US, "%03d", ms) + getString(R.string.abbreviation_milliseconds);
    } else if (min == 0) {
      return String.format(Locale.US, "%02d.%03d", sec, ms) + getString(
          R.string.abbreviation_seconds);
    } else if (hr == 0) {
      return String.format(Locale.US, "%02d:%02d.%03d", min, sec, ms) + getString(
          R.string.abbreviation_minutes);
    } else {
      return String.format(Locale.US, "%02d:%02d:%02d.%03d", hr, min, sec, ms) + getString(
          R.string.abbreviation_hours);
    }

  }
}