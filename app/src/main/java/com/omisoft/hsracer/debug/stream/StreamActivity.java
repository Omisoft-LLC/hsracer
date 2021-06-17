package com.omisoft.hsracer.debug.stream;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import com.omisoft.hsracer.BuildConfig;
import com.omisoft.hsracer.R;
import com.omisoft.hsracer.common.BaseActivity;
import com.omisoft.hsracer.constants.Constants;
import com.omisoft.hsracer.utils.CryptoUtils;
import com.yakivmospan.scytale.Store;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.bytedeco.javacpp.avcodec;
import org.bytedeco.javacpp.avutil;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;

public class StreamActivity extends BaseActivity implements View.OnClickListener {


  private PowerManager.WakeLock mWakeLock;

  private String ffmpeg_link;
//  private String ffmpeg_link = Environment.getExternalStorageDirectory() + "/test.flv";

  private volatile FFmpegFrameRecorder recorder;
  boolean recording = false;
  long startTime = 0;

  private int imageWidth = 320;
  private int imageHeight = 240;
  private int frameRate = 24;

  //  private Thread audioThread;
  volatile boolean runAudioThread = true;

  private CameraView cameraView;
  //private IplImage yuvIplimage = null;
  private Frame yuvImage;

  private Button recordButton;
  private LinearLayout mainLayout;
  private boolean init = false;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);


    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    setContentView(R.layout.activity_stream);
    Store store = new Store(getApplicationContext());
    String raceId = getSharedPreferences().getString(Constants.RACE_ID, "123");
    String storedPass = getApp().getCrypto()
        .decrypt(getSharedPreferences().getString(Constants.PASSWORD, ""),
            store.getAsymmetricKey(BuildConfig.APPLICATION_ID, null));
    String aesKeyEncoded = getSharedPreferences().getString(Constants.AES_KEY, "");
    byte[] key = Base64.decode(aesKeyEncoded, Base64.NO_WRAP);
    SecretKey originalKey = new SecretKeySpec(key, "AES");
    byte[] encrypted = CryptoUtils
        .encrypt(originalKey, storedPass.getBytes(Charset.defaultCharset()));
    Log.e(TAG, "ENCRYPTED");
    try {
      ffmpeg_link =
          BuildConfig.RTMP_URL + getSharedPreferences().getString(Constants.API_KEY, "") + "?r="
              + URLEncoder.encode(raceId, "utf-8") + "&p=" + URLEncoder
              .encode(Base64.encodeToString(encrypted, Base64.NO_WRAP), "utf-8");
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }

    Log.e(TAG, ffmpeg_link);
  }

  @Override
  protected void onResume() {
    super.onResume();

    if (!init) {
      initLayout();
      init = true;
      }

    if (mWakeLock == null) {
      PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
      mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, TAG);
      mWakeLock.acquire(10 * 60 * 1000L /*10 minutes*/);
    }
  }

  @Override
  protected void onPause() {
    super.onPause();

    if (mWakeLock != null) {
      mWakeLock.release();
      mWakeLock = null;
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();

    recording = false;
  }


  private void initLayout() {

    mainLayout = this.findViewById(R.id.record_layout);

    recordButton = findViewById(R.id.recorder_control);
    recordButton.setText("Start");
    recordButton.setOnClickListener(this);

    cameraView = new CameraView(this);

    LinearLayout.LayoutParams layoutParam = new LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
    mainLayout.addView(cameraView, layoutParam);
    Log.v(TAG, "added cameraView to mainLayout");
  }

  // TODO Play with settings to  find optimal
  private void initRecorder() {
    Log.w(TAG, "initRecorder");

    // region
    yuvImage = new Frame(imageWidth, imageHeight, Frame.DEPTH_UBYTE, 2);
    Log.d(TAG, "IplImage.create");
    // endregion

    recorder = new FFmpegFrameRecorder(ffmpeg_link, imageWidth, imageHeight, 1);
    Log.v(TAG,
        "FFmpegFrameRecorder: " + ffmpeg_link + " imageWidth: " + imageWidth + " imageHeight "
            + imageHeight);

    recorder.setFormat("flv");
    recorder.setVideoOption("preset", "ultrafast");
    recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
    recorder.setAudioCodec(0);
    recorder.setFrameRate(15);
    recorder.setPixelFormat(avutil.AV_PIX_FMT_YUV420P);
//    recorder.setVideoBitrate(5000);
//    recorder.setOption("content_type","video/webm");
    Log.v(TAG, "recorder.setFormat(\"mp4\")");

    Log.v(TAG, "recorder.setSampleRate(sampleAudioRateInHz)");

    // re-set in the surface changed method as well
    recorder.setFrameRate(frameRate);
    Log.v(TAG, "recorder.setFrameRate(frameRate)");


  }

  // Start the capture
  public void startRecord() {
    initRecorder();

    try {
      recorder.start();
      startTime = System.currentTimeMillis();
      recording = true;
//      audioThread.start();
    } catch (FFmpegFrameRecorder.Exception e) {
      e.printStackTrace();
    }
  }

  public void stopRecord() {
    // This should stop the audio thread from running
    runAudioThread = false;

    if (recorder != null && recording) {
      recording = false;
      Log.v(TAG, "Finishing recording, calling stop and release on recorder");
      try {
        recorder.stop();
        recorder.release();
      } catch (FFmpegFrameRecorder.Exception e) {
        e.printStackTrace();
      }
      recorder = null;
    }
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    // Quit when back button is pushed
    if (keyCode == KeyEvent.KEYCODE_BACK) {
      if (recording) {
        stopRecord();
      }
      finish();
      return true;
    }
    return super.onKeyDown(keyCode, event);
  }

  @Override
  public void onClick(View v) {
    if (!recording) {
      startRecord();
      Log.w(TAG, "Start Button Pushed");
      recordButton.setText(getString(R.string.stop_button));
    } else {
      stopRecord();
      Log.w(TAG, "Stop Button Pushed");
      recordButton.setText(getString(R.string.play_button));
    }
  }


  /**
   * Takes the current time so it can be used like a name for each file
   */
  public String getCurrentTime() {
    Date date = Calendar.getInstance().getTime();
    SimpleDateFormat format = new SimpleDateFormat("MM_dd_yyyy_hh_mm_ss");
    return format.format(date);
  }


  class CameraView extends SurfaceView implements SurfaceHolder.Callback, Camera.PreviewCallback {

    private boolean previewRunning = false;

    private SurfaceHolder holder;
    private Camera camera;

    private byte[] previewBuffer;

    long videoTimestamp = 0;

    Bitmap bitmap;
    Canvas canvas;

    public CameraView(Context _context) {
      super(_context);

      holder = this.getHolder();
      holder.addCallback(this);
      holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
      camera = Camera.open(CameraInfo.CAMERA_FACING_BACK);

      try {
        camera.setPreviewDisplay(holder);
        camera.setPreviewCallback(this);

        Camera.Parameters currentParams = camera.getParameters();
        Log.v(TAG, "Preview Framerate: " + currentParams.getPreviewFrameRate());
        Log.v(TAG, "Preview imageWidth: " + currentParams.getPreviewSize().width + " imageHeight: "
            + currentParams.getPreviewSize().height);

        // Use these values
        imageWidth = currentParams.getPreviewSize().width;
        imageHeight = currentParams.getPreviewSize().height;
        frameRate = currentParams.getPreviewFrameRate();

        bitmap = Bitmap.createBitmap(imageWidth, imageHeight, Bitmap.Config.ALPHA_8);


	        	/*
        Log.v(LOG_TAG,"Creating previewBuffer size: " + imageWidth * imageHeight * ImageFormat.getBitsPerPixel(currentParams.getPreviewFormat())/8);
	        	previewBuffer = new byte[imageWidth * imageHeight * ImageFormat.getBitsPerPixel(currentParams.getPreviewFormat())/8];
				camera.addCallbackBuffer(previewBuffer);
	            camera.setPreviewCallbackWithBuffer(this);
	        	*/

        camera.startPreview();
        previewRunning = true;
      } catch (IOException e) {
        Log.v(TAG, e.getMessage());
        e.printStackTrace();
      }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
      Log.v(TAG, "Surface Changed: width " + width + " height: " + height);

      // We would do this if we want to reset the camera parameters
            /*
            if (!recording) {
    			if (previewRunning){
    				camera.stopPreview();
    			}

    			try {
    				//Camera.Parameters cameraParameters = camera.getParameters();
    				//p.setPreviewSize(imageWidth, imageHeight);
    			    //p.setPreviewFrameRate(frameRate);
    				//camera.setParameters(cameraParameters);

    				camera.setPreviewDisplay(holder);
    				camera.startPreview();
    				previewRunning = true;
    			}
    			catch (IOException e) {
    				Log.e(LOG_TAG,e.getMessage());
    				e.printStackTrace();
    			}
    		}
            */

      // Get the current parameters
      Camera.Parameters currentParams = camera.getParameters();
      Log.v(TAG, "Preview Framerate: " + currentParams.getPreviewFrameRate());
      Log.v(TAG, "Preview imageWidth: " + currentParams.getPreviewSize().width + " imageHeight: "
          + currentParams.getPreviewSize().height);

      // Use these values
      imageWidth = currentParams.getPreviewSize().width;
      imageHeight = currentParams.getPreviewSize().height;
      frameRate = currentParams.getPreviewFrameRate();

      // Create the yuvIplimage if needed
      //yuvIplimage = IplImage.create(imageWidth, imageHeight, IPL_DEPTH_8U, 2);
      yuvImage = new Frame(imageWidth, imageHeight, Frame.DEPTH_UBYTE, 2);
      //yuvIplimage = IplImage.create(imageWidth, imageHeight, IPL_DEPTH_32S, 2);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
      try {
        camera.setPreviewCallback(null);

        previewRunning = false;
        camera.release();

      } catch (RuntimeException e) {
        Log.v(TAG, e.getMessage());
        e.printStackTrace();
      }
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {

      if (yuvImage != null && recording) {
        videoTimestamp = 1000 * (System.currentTimeMillis() - startTime);

        // Put the camera preview frame right into the yuvIplimage object
        //yuvIplimage.getByteBuffer().put(data);

        // region
        ((ByteBuffer) yuvImage.image[0].position(0)).put(data);
        // endregion

        // FAQ about IplImage:
        // - For custom raw processing of data, getByteBuffer() returns an NIO direct
        //   buffer wrapped around the memory pointed by imageData, and under Android we can
        //   also use that Buffer with Bitmap.copyPixelsFromBuffer() and copyPixelsToBuffer().
        // - To get a BufferedImage from an IplImage, we may call getBufferedImage().
        // - The createFrom() factory method can construct an IplImage from a BufferedImage.
        // - There are also a few copy*() methods for BufferedImage<->IplImage data transfers.

        // Let's try it..
        // This works but only on transparency
        // Need to find the right Bitmap and IplImage matching types

            	/*
            	bitmap.copyPixelsFromBuffer(yuvIplimage.getByteBuffer());
            	//bitmap.setPixel(10,10,Color.MAGENTA);

            	canvas = new Canvas(bitmap);
            	Paint paint = new Paint();
            	paint.setColor(Color.GREEN);
            	float leftx = 20;
            	float topy = 20;
            	float rightx = 50;
            	float bottomy = 100;
            	RectF rectangle = new RectF(leftx,topy,rightx,bottomy);
            	canvas.drawRect(rectangle, paint);

            	bitmap.copyPixelsToBuffer(yuvIplimage.getByteBuffer());
                */
        //Log.v(LOG_TAG,"Writing Frame");

        try {

          // Get the correct time
          recorder.setTimestamp(videoTimestamp);

          // Record the image into FFmpegFrameRecorder
          //recorder.record(yuvIplimage);

          // region
          recorder.record(yuvImage);
          // endregion

        } catch (FFmpegFrameRecorder.Exception e) {
          Log.v(TAG, e.getMessage());
          e.printStackTrace();
        }
      }
    }
  }
}
