package com.omisoft.hsracer.features.streams;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import butterknife.BindView;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MediaSourceEventListener;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.util.Util;
import com.omisoft.hsracer.BuildConfig;
import com.omisoft.hsracer.R;
import com.omisoft.hsracer.common.BaseActivity;
import com.omisoft.hsracer.common.BaseApp;
import com.omisoft.hsracer.constants.Constants;
import com.omisoft.hsracer.dto.StreamDTO;
import com.omisoft.hsracer.dto.StreamListDTO;
import com.omisoft.hsracer.features.streams.actions.GetStreamsForRaceAction;
import com.omisoft.hsracer.features.streams.events.SuccessStreamDTOEvent;
import com.omisoft.hsracer.utils.SDCardUtils;
import com.omisoft.hsracer.utils.Utils;
import java.io.File;
import java.util.List;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Stream Player Activity
 */
public class StreamDetailsActivity extends BaseActivity {

  @BindView(R.id.dash_player)
  SimpleExoPlayerView dashPlayerView;
  private SimpleExoPlayer player;
  private static final Uri NO_SOURCE_URI = Uri
      .fromFile(new File(
          SDCardUtils.getSDCardPath() + Utils.getBaseApp().getString(R.string.app_name)
              + File.separator
              + Constants.NO_STREAM_FILE));


  private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
  private long playbackPosition;
  private int currentWindow;
  private List<StreamDTO> streamDTOS;
  private DataSource.Factory mediaDataSourceFactory;
  private Handler mainHandler;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_stream_details);
    mediaDataSourceFactory = buildDataSourceFactory(true);
    mainHandler = new Handler();


  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void initMediaSources(SuccessStreamDTOEvent event) {

    streamDTOS = event.getStreamDTOS();
    DefaultTrackSelector trackSelector = new DefaultTrackSelector();
    EventLogger eventListener = new EventLogger(trackSelector);
    player = ExoPlayerFactory.newSimpleInstance(
        new DefaultRenderersFactory(this),
        trackSelector, new DefaultLoadControl());
    player.addListener(eventListener);

    dashPlayerView.setPlayer(player);
    player.setPlayWhenReady(true);
    player.seekTo(currentWindow, playbackPosition);
    MediaSource source = null;
    if (!streamDTOS.isEmpty()) {
      source = buildMediaSource(
          Uri.parse(BuildConfig.WEB_URL + event.getStreamDTOS().get(0).getDash()), mainHandler,
          eventListener, C.TYPE_DASH);
// Prepare the player with the source.
    } else { // No stream

      source = buildMediaSource(NO_SOURCE_URI, mainHandler, eventListener,
          C.TYPE_OTHER);

    }
    player.prepare(source);

    player.setPlayWhenReady(true);
  }

  @Override
  protected void onDestroy() {
    if (player != null) {
      player.release();
    }
    super.onDestroy();
  }
// TODO refactor to not use deprecated code


  private MediaSource buildMediaSource(
      Uri uri,
      @Nullable Handler handler,
      @Nullable MediaSourceEventListener listener, int type) {

    switch (type) {
      case C.TYPE_DASH:
        return new DashMediaSource.Factory(
            new DefaultDashChunkSource.Factory(mediaDataSourceFactory),
            buildDataSourceFactory(false))
            .createMediaSource(uri, handler, listener);
      case C.TYPE_SS:
        Log.e(TAG, "NOT SUPPORTED");
        throw new IllegalArgumentException();
      case C.TYPE_HLS:
        return new HlsMediaSource.Factory(mediaDataSourceFactory)
            .createMediaSource(uri, handler, listener);
      case C.TYPE_OTHER:
        return new ExtractorMediaSource.Factory(mediaDataSourceFactory)
            .createMediaSource(uri, handler, listener);
      default: {
        throw new IllegalStateException("Unsupported type: " + type);
      }
    }
  }


  /**
   * Returns a new DataSource factory.
   *
   * @param useBandwidthMeter Whether to set {@link #BANDWIDTH_METER} as a listener to the new
   * DataSource factory.
   * @return A new DataSource factory.
   */
  private DataSource.Factory buildDataSourceFactory(boolean useBandwidthMeter) {
    return ((BaseApp) getApplication())
        .buildDataSourceFactory(useBandwidthMeter ? BANDWIDTH_METER : null);
  }

  /**
   * Build specific media source based on type
   */
//  @Nullable
//  private MediaSource buildMediaSource(final Uri uri, final int type) {
//    switch (type) {
//      case C.TYPE_DASH:
//        try {
//          DataSource.Factory manifestDataSourceFactory =
//              new DefaultHttpDataSourceFactory(BuildConfig.APPLICATION_ID);
//          DashChunkSource.Factory dashChunkSourceFactory =
//              new DefaultDashChunkSource.Factory(
//                  new DefaultHttpDataSourceFactory(BuildConfig.APPLICATION_ID, BANDWIDTH_METER));
//          return new DashMediaSource.Factory(dashChunkSourceFactory,
//              manifestDataSourceFactory).createMediaSource(uri);
//        } catch (Throwable e) {
//          return buildMediaSource(NO_SOURCE_URI, C.TYPE_OTHER);
//        }
//      case C.TYPE_OTHER:
//        DataSpec dataSpec = new DataSpec(uri);
//        final FileDataSource fileDataSource = new FileDataSource();
//        try {
//          fileDataSource.open(dataSpec);
//        } catch (FileDataSourceException e) {
//          e.printStackTrace();
//        }
//        DataSource.Factory factory = new DataSource.Factory() {
//          @Override
//          public DataSource createDataSource() {
//            return fileDataSource;
//          }
//        };
//        return new ExtractorMediaSource(fileDataSource.getUri(),
//            factory, new DefaultExtractorsFactory(), null, null);
//    }
//    return null;
//  }
  @Override
  public void onStart() {
    super.onStart();
    if (Util.SDK_INT > 23) {
      initializePlayer();
    }
  }

  @Override
  public void onPause() {
    super.onPause();
    if (Util.SDK_INT <= 23) {
      releasePlayer();
    }
  }

  @Override
  public void onStop() {
    super.onStop();
    if (Util.SDK_INT > 23) {
      releasePlayer();
    }
  }

  private void releasePlayer() {
    if (player != null) {
      playbackPosition = player.getCurrentPosition();
      currentWindow = player.getCurrentWindowIndex();
      player.release();
      player = null;
    }
  }


  private void initializePlayer() {
    if (streamDTOS != null) {
      initMediaSources(new SuccessStreamDTOEvent(streamDTOS));
    } else {
      StreamListDTO selectedStreams = getIntent()
          .getParcelableExtra(StreamListDTO.class.getSimpleName());
      getExecutor()
          .submit(new GetStreamsForRaceAction(getApp(), selectedStreams.getRaceId()));
    }
  }


  @Override
  public void onResume() {
    super.onResume();
    hideSystemUi(dashPlayerView);
    if ((Util.SDK_INT <= 23 || player == null)) {
      initializePlayer();
    }
  }

  /**
   * Go to site
   * TODO Implement ui component to click on
   */
  public void playerLogoClkListener(View view) {
    String websiteUrl = BuildConfig.WEB_URL;
    Intent websiteIntent = new Intent(Intent.ACTION_VIEW);
    websiteIntent.setData(Uri.parse(websiteUrl));
    startActivity(websiteIntent);
  }


}