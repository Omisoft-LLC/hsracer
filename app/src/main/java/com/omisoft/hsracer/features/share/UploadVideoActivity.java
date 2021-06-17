package com.omisoft.hsracer.features.share;


import static com.omisoft.hsracer.constants.Constants.KEY_RECYCLER_VIDEOS;
import static com.omisoft.hsracer.constants.Constants.VIDEOS_FOR_UPLOADING_BUNDLE;
import static com.omisoft.hsracer.constants.Constants.VIDEO_CURRENT_INDEX;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.net.ConnectivityManager;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.omisoft.hsracer.R;
import com.omisoft.hsracer.adapters.VideoUploadAdapter;
import com.omisoft.hsracer.adapters.callbacks.VideoViewInterface;
import com.omisoft.hsracer.adapters.classes.UploadItemState;
import com.omisoft.hsracer.common.BaseActivity;
import com.omisoft.hsracer.constants.Constants;
import com.omisoft.hsracer.features.home.HomeActivity;
import com.omisoft.hsracer.services.UploadVideoService;
import java.io.File;
import java.util.ArrayList;

/**
 * Activity for video upload functionality
 * Created by developer on 17.07.17.
 */
public class UploadVideoActivity extends BaseActivity implements VideoViewInterface {

  private final String TAG = UploadVideoActivity.class.getName();


  private Button mUploadVideoButton;
  private Button mHomeButton;
  private VideoView mVideoView;
  private RecyclerView mRecyclerView;
  private VideoUploadAdapter mVideoAdapter;
  private ArrayList<UploadItemState> mVideosToUpload;
  private String mVideoFilePath;
  private int currentPlayingVideo = 0;
  private AlertDialog alertDialog;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    checkOrientation(savedInstanceState);
    setupVideoView();
    if (savedInstanceState != null) {
      mVideoView.seekTo(savedInstanceState.getInt(Constants.VIDEO_POSITION_FROM_APP));
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
    mVideoView.pause();
  }

  @Override
  public void onStop() {
    super.onStop();
    mVideoView.stopPlayback();
  }

  @Override
  protected void onResume() {
    super.onResume();
    mVideoView.resume();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    mVideoView.stopPlayback();
  }

  @Override
  public void onStart() {
    super.onStart();
    if (isPortrait()) {
      loadRecyclerView();
    }
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    outState.putInt(Constants.VIDEO_POSITION, mVideoView.getCurrentPosition());
    outState.putString(Constants.LAST_KNOWN_VIDEO_PATH, mVideoFilePath);
    outState.putParcelableArrayList(KEY_RECYCLER_VIDEOS, mVideosToUpload);
    outState.putInt(VIDEO_CURRENT_INDEX, currentPlayingVideo);
    super.onSaveInstanceState(outState);
  }


  /**
   * Check the orientation so it can set the right Theme in landscape or portrait orientation
   */
  private void checkOrientation(Bundle savedInstanceState) {
    if (isLandscape()) {
      setTheme(R.style.AppThemeNoBar);
      setContentView(R.layout.activity_upload_video);
      mVideoView = findViewById(R.id.show_video);

    } else if (isPortrait()) {
      setTheme(R.style.AppTheme);
      setContentView(R.layout.activity_upload_video);
      mUploadVideoButton = findViewById(R.id.button_share_video);
      mHomeButton = findViewById(R.id.button_upload_to_home);
      mVideoView = findViewById(R.id.show_video);
      mRecyclerView = findViewById(R.id.recycler_view_videos);
    }
    if (savedInstanceState != null) {
      mVideosToUpload = savedInstanceState.getParcelableArrayList(KEY_RECYCLER_VIDEOS);
      mVideoFilePath = savedInstanceState.getString(Constants.LAST_KNOWN_VIDEO_PATH);
      currentPlayingVideo = savedInstanceState.getInt(VIDEO_CURRENT_INDEX);
    } else {
      mVideoFilePath = getVideoPath();
    }
  }

  /**
   * Checks if the screen orientation is in portrait
   * @return
   */
  private boolean isPortrait() {
    return this.getResources().getConfiguration().orientation
        == Configuration.ORIENTATION_PORTRAIT;
  }

  /**
   * Checks if the screen orientation is in landscape
   * @return
   */
  private boolean isLandscape() {
    return this.getResources().getConfiguration().orientation
        == Configuration.ORIENTATION_LANDSCAPE;
  }

  /**
   * Takes all the files and there names including there paths, puts them in a list and then loads
   * them in recyclerView.
   */
  private void loadRecyclerView() {
    if (mVideosToUpload == null) {
      File[] videos = new File(takeVideosFolder()).listFiles();
      mVideosToUpload = takeOnlyFileNames(videos);
    }
    mRecyclerView.setHasFixedSize(true);
    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
    mRecyclerView.setLayoutManager(mLayoutManager);
    mVideoAdapter = new VideoUploadAdapter(this, mVideosToUpload);
    mRecyclerView.setAdapter(mVideoAdapter);
  }

  /**
   * Take all the file paths, put them in ArrayList and then plays the first video from the
   * directory.
   */
  private ArrayList<UploadItemState> takeOnlyFileNames(File[] videos) {
    ArrayList<UploadItemState> arrayList = new ArrayList<>();
    for (File video : videos) {
      UploadItemState uploadVideoRow = new UploadItemState();
      uploadVideoRow.setFullPathToVideo(video.getPath());
      uploadVideoRow.setVideoName(video.getName());
      arrayList.add(uploadVideoRow);
    }
    arrayList.get(0).setPlaying(true);
    return arrayList;
  }

  /**
   * Get the race ID from shared preferences
   */
  private Long getRaceID() {
    return getSharedPreferences()
        .getLong(Constants.RACE_ID, 0);
  }

  private String takeVideosFolder() {

    return
        Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
            + getString(R.string.app_name) + File.separator
            + getRaceID() + File.separator;
  }

  /**
   * Starts to upload the video depending on the connection
   */
  public void buttonShareVideo(View view) throws InterruptedException {
    if (getApp().isConnected()) {
      ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(
          CONNECTIVITY_SERVICE);
      boolean isWiFi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
          .isConnectedOrConnecting();
      boolean isMobileData = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
          .isConnectedOrConnecting();
      if (isWiFi) {
        sendFile();
      } else if (isMobileData) {

        showWarningDialog();

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
      alertBuilder.setView(R.layout.fragment_upload_video_warning);
    } else {
      alertBuilder = new AlertDialog.Builder(this);
      View linearLayout = this.getLayoutInflater()
          .inflate(R.layout.fragment_navigate_back_warning, null);
      alertBuilder.setView(linearLayout);
    }
    alertBuilder
        .setTitle(R.string.currently_you_are_using_mobile_data);
    alertBuilder.setCancelable(true);
    alertDialog = alertBuilder.create();
    alertDialog.show();
  }

  /**
   * Navigates to home
   */
  public void buttonNavigateToHome(View view) {
    Intent i = new Intent(this, HomeActivity.class);
    startActivity(i);
    finish();
  }

  @Override
  public void onBackPressed() {
    Intent intent = new Intent(UploadVideoActivity.this, HomeActivity.class);
    startActivity(intent);
    finish();
  }


  /**
   * locks up the buttons and check boxes while the user waits the file to be uploaded
   */
  private void sendFile() throws InterruptedException {
    if (checkForUploadingVideos() && checkForSelectedVideos()) {
      ArrayList<String> arrayForVideos = new ArrayList<>();
      ObjectMapper objectMapper = new ObjectMapper();
      for (UploadItemState videoInfo : mVideosToUpload) {
        if (!videoInfo.isUploading() && videoInfo.isChecked()) {
          if (new File(videoInfo.getFullPathToVideo()).exists()) {
            videoInfo.setUploading(true);
            UploadItemState uploadItemState = new UploadItemState();
            uploadItemState.setVideoName(videoInfo.getVideoName());
            uploadItemState.setUploading(true);
            uploadItemState.setFullPathToVideo(videoInfo.getFullPathToVideo());
            uploadItemState.setRaceID(getRaceID());
            try {
              arrayForVideos.add(objectMapper.writeValueAsString(uploadItemState));
            } catch (JsonProcessingException e) {
              e.printStackTrace();
            }
          } else {
            showWhiteToastMessage(
                getString(R.string.such_file_does_not_exist, videoInfo.getVideoName()));
            return;
          }
        }
      }
      sendVideosToService(arrayForVideos);
      mVideoAdapter.setVideos(mVideosToUpload);
      mVideoAdapter.notifyDataSetChanged();
    }
  }

  /**
   * Checks if some videos are already uploaded or are currently uploading
   */
  private boolean checkForUploadingVideos() {
    for (UploadItemState videoInfo : mVideosToUpload) {
      if (!videoInfo.isUploading()) {
        return true;
      }
    }
    showWhiteToastMessage(getString(R.string.all_videos_uploaded));
    return false;
  }

  /**
   * Checks if some videos from the list are selected
   */
  private boolean checkForSelectedVideos() {
    for (UploadItemState videoInfo : mVideosToUpload) {
      if (videoInfo.isChecked() && !videoInfo.isUploading()) {
        return true;
      }
    }
    showWhiteToastMessage(getString(R.string.please_select_videos));
    return false;
  }

  /**
   * Takes the path to the video
   */
  private String getVideoPath() {
    File[] files = new File(takeVideosFolder()).listFiles();
    if (files[0].exists()) {
      return files[0].getPath();
    } else {
      return "No such path";
    }
  }


  /**
   * Setups the video by a given file
   */
  private void setupVideoView() {
    setupListenerForErrors(mVideoView);
    mVideoView.setVideoPath(mVideoFilePath);
    MediaController mediaController = new
        MediaController(this);
    mediaController.setAnchorView(mVideoView);
    mVideoView.setMediaController(mediaController);
    mVideoView.start();
  }

  /**
   * Sets Error listener for the video player
   */
  private void setupListenerForErrors(VideoView mVideoView) {
    mVideoView.setOnErrorListener(new OnErrorListener() {
      @Override
      public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        mUploadVideoButton.setEnabled(false);
        mHomeButton.setEnabled(true);
        return false;
      }
    });
  }

  /**
   * Turns on the video which is connected  with the element in the recycler view
   */
  @Override
  public void onItemClicked(View view, int position) {
    mVideoView.stopPlayback();
    mVideoView.setVideoPath(mVideosToUpload.get(position).getFullPathToVideo());
    mVideoView.start();
    mVideosToUpload.get(currentPlayingVideo).setPlaying(false);
    mVideosToUpload.get(position).setPlaying(true);
    currentPlayingVideo = position;
    mVideoAdapter.setVideos(mVideosToUpload);
    mVideoAdapter.notifyDataSetChanged();
  }

  @Override
  public void selectedItem(int position, boolean checked) {
    mVideosToUpload.get(position).setChecked(checked);
  }

  public void uploadCancel(View view) {
    alertDialog.dismiss();
  }

  public void uploadNow(View view) throws InterruptedException {
    sendFile();
    alertDialog.dismiss();
  }

  /**
   * Sends to the service the videos checked by the user
   */
  private void sendVideosToService(ArrayList<String> listWithVideos) {
    Intent service = new Intent(UploadVideoActivity.this, UploadVideoService.class);
    Bundle bundle = new Bundle();
    bundle.putStringArrayList(Constants.VIDEOS_FOR_UPLOADING,
        listWithVideos);
    service.putExtra(VIDEOS_FOR_UPLOADING_BUNDLE, bundle);
    startService(service);
  }
}
