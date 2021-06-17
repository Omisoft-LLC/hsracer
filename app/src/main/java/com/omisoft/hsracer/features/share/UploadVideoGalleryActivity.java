package com.omisoft.hsracer.features.share;

import static com.omisoft.hsracer.constants.Constants.VIDEOS_FOR_UPLOADING_BUNDLE;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;
import butterknife.BindView;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.omisoft.hsracer.R;
import com.omisoft.hsracer.adapters.classes.UploadItemState;
import com.omisoft.hsracer.common.BaseActivity;
import com.omisoft.hsracer.constants.Constants;
import com.omisoft.hsracer.services.UploadVideoService;
import java.io.File;
import java.util.ArrayList;

public class UploadVideoGalleryActivity extends BaseActivity {

  private static final String TAG = UploadVideoGalleryActivity.class.getName();

  @BindView(R.id.share_show_video)
  VideoView mVideoPreview;

  private AlertDialog alertDialog;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    checkOrientation();
    Intent shareIntent = getIntent();
    String action = shareIntent.getAction();
    if (action.equalsIgnoreCase(Intent.ACTION_SEND) && shareIntent.hasExtra(Intent.EXTRA_STREAM)) {
      Uri s = (Uri) shareIntent.getExtras().get(Intent.EXTRA_STREAM);
      setupVideoView(s);
      if (savedInstanceState != null) {
        mVideoPreview.seekTo(savedInstanceState.getInt(Constants.VIDEO_POSITION_FROM_GALLERY));
      }
      String realPathToVideo = getRealPathFromURI(s);
      if (!realPathToVideo.equals("")) {
        try {

        } catch (NumberFormatException e) {
          showWhiteToastMessage(R.string.such_file_does_not_exist);
          finish();
        }
      }
    }
  }


  @Override
  protected void attachBaseContext(Context newBase) {
    super.attachBaseContext(AudioServiceContext.getContext(newBase));
  }

  @Override
  protected void onPause() {
    super.onPause();
    if (alertDialog != null && alertDialog.isShowing()) {
      alertDialog.dismiss();
    }
    mVideoPreview.pause();
  }

  @Override
  public void onStop() {
    super.onStop();
    mVideoPreview.stopPlayback();
  }

  @Override
  protected void onResume() {
    super.onResume();
    mVideoPreview.resume();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    mVideoPreview.stopPlayback();
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    outState.putInt(Constants.VIDEO_POSITION_FROM_GALLERY, mVideoPreview.getCurrentPosition());
    super.onSaveInstanceState(outState);
  }


  /**
   * Check the orientation so it can set the right Theme in landscape or portrait orientation
   */
  private void checkOrientation() {
    if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
      setTheme(R.style.AppThemeNoBar);
      setContentView(R.layout.activity_upload_video_gallery);
    } else if (this.getResources().getConfiguration().orientation
        == Configuration.ORIENTATION_PORTRAIT) {
      setTheme(R.style.AppTheme);
      setContentView(R.layout.activity_upload_video_gallery);
      Button mCancelButton = findViewById(R.id.share_button_cancel);
      Button mShareButton = findViewById(R.id.share_button_share_video);
    }

  }

  /**
   * Iterates the files in the Media directory so it can find the video and return its path
   */

  public String getRealPathFromURI(Uri uri) {
    String[] projection = {MediaStore.Video.Media.DATA};
    Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
    if (cursor == null) {
      showWhiteToastMessage(R.string.such_file_does_not_exist);
      return "";
    } else {
      cursor.moveToFirst();
      int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
      String path = cursor.getString(idx);
      cursor.close();
      return path;
    }
  }

  /**
   * Parse the string coming from the gallery so it can be taken the ID for the race.
   * @param filePath
   * @return
   */
  private long getRaceID(String filePath) {
    try {
      StringBuilder stringBuilder = new StringBuilder(filePath);
      return Long.parseLong(stringBuilder
          .substring(stringBuilder.lastIndexOf("HSracer/") + 8,
              stringBuilder.lastIndexOf("/")));
    } catch (NumberFormatException e) {
      showWhiteToastMessage(R.string.such_file_does_not_exist);
      return 0;
    }
  }

  private String getNameOfRace(String filePath) {
    StringBuilder stringBuilder = new StringBuilder(filePath);
    return stringBuilder
        .substring(stringBuilder.lastIndexOf("/") + 1,
            stringBuilder.length());
  }

  /**
   * When clicked shares the video to youtube by checking which connection will be used
   */
  public void buttonShareVideoToYouTube(View view) throws JsonProcessingException {

    if (getApp().isConnected()) {
      ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(
          CONNECTIVITY_SERVICE);
      boolean isWiFi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
          .isConnectedOrConnecting();
      boolean isMobileData = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
          .isConnectedOrConnecting();
      if (isWiFi) {
        uploadFile();
      } else {
        if (isMobileData) {
          showWarningDialog();
        }
      }
    } else {
      showWhiteToastMessage(getString(R.string.no_internet_connection));
    }
  }

  /**
   * Show a dialog for the user whome wants to share his videos with data so he can be worn that the
   * videos can be uploaded later using wi-fi connection
   */
  private void showWarningDialog() {

    AlertDialog.Builder alertBuilder;
    if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
      alertBuilder = new AlertDialog.Builder(this);
      alertBuilder.setView(R.layout.fragment_upload_video_warning_from_gallery);
    } else {
      alertBuilder = new AlertDialog.Builder(this);
      View linearLayout = this.getLayoutInflater()
          .inflate(R.layout.fragment_upload_video_warning_from_gallery, null);
      alertBuilder.setView(linearLayout);
    }
    alertBuilder
        .setTitle(R.string.currently_you_are_using_mobile_data);
    alertBuilder.setCancelable(true);
    alertDialog = alertBuilder.create();
    alertDialog.show();
  }

  /**
   * Gets the video file from a given Uri
   */
  private File getVideoFile() {
    Uri s = (Uri) getIntent().getExtras().get(Intent.EXTRA_STREAM);
    return new File(getRealPathFromURI(s));
  }

  /**
   * The method that shares the file to youtube
   */
  private void uploadFile() {

    File fileToShare = getVideoFile();
    if (fileToShare.exists() && fileToShare.length() > 0) {
      UploadItemState uploadItemState = new UploadItemState();
      ArrayList<String> arrayForVideos = new ArrayList<>();
      uploadItemState.setUploading(true);
      uploadItemState.setVideoName(getNameOfRace(fileToShare.getPath()));
      uploadItemState.setFullPathToVideo(fileToShare.getPath());
      uploadItemState.setRaceID(getRaceID(fileToShare.getPath()));
      try {
        arrayForVideos.add(getObjectMapper().writeValueAsString(uploadItemState));
      } catch (JsonProcessingException e) {
        Log.e("UploadVideoService", "Error in parsing", e);
      }
      sendVideosToService(arrayForVideos);
    } else {
      showWhiteToastMessage(R.string.such_file_does_not_exist);
    }
  }

  /**
   * Turns on the video from a given URL
   */
  private void setupVideoView(Uri videoUri) {
    setupListenerForErrors(mVideoPreview);
    mVideoPreview.setVideoURI(videoUri);

    MediaController mediaController = new
        MediaController(this);
    mediaController.setAnchorView(mVideoPreview);
    mVideoPreview.setMediaController(mediaController);
    mVideoPreview.start();
  }
  /**
   * Sets Error listener for the video player
   * @param mVideoView
   */
  private void setupListenerForErrors(VideoView mVideoView) {
    mVideoView.setOnErrorListener(new OnErrorListener() {
      @Override
      public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {

        return false;
      }
    });
  }

  public void cancelShare(View view) {
    onBackPressed();
  }

  public void uploadCancel(View view) {
    alertDialog.dismiss();
  }

  public void uploadNow(View view) throws JsonProcessingException {
    uploadFile();
    alertDialog.dismiss();
  }

  /**
   * Sends to the service the videos checked by the user
   */
  private void sendVideosToService(ArrayList<String> listWithVideos) {
    Intent service = new Intent(UploadVideoGalleryActivity.this, UploadVideoService.class);
    Bundle bundle = new Bundle();
    bundle.putStringArrayList(Constants.VIDEOS_FOR_UPLOADING,
        listWithVideos);
    service.putExtra(VIDEOS_FOR_UPLOADING_BUNDLE, bundle);
    startService(service);
  }
}
