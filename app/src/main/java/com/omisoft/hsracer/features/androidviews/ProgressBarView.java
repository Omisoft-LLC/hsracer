package com.omisoft.hsracer.features.androidviews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;

/**
 * Created by developer on 26.09.17.
 * Progress bar that is showing after each request to the server
 */

public class ProgressBarView extends View {

  //Bounced Dot Radius
  private int mBounceDotRadius = 8;

  //to get identified in which position dot has to bounce
  private int mDotPosition;

  //specify how many dots you need in a progressbar
  private int mDotAmount = 9;
  private Paint mPaint;
  public ProgressBarView(Context context) {
    super(context);

  }

  public ProgressBarView(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    mPaint = new Paint();
  }

  public ProgressBarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  //Method to draw your customized dot on the canvas
  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);


    //set the color for the dot that you want to draw
    mPaint.setColor(Color.parseColor("#d31411"));

    //function to create dot
    createDot(canvas, mPaint);
  }

  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    //Animation called when attaching to the window, i.e to your screen
    startAnimation();
  }

  private void createDot(Canvas canvas, Paint paint) {

    //here i set progress bar with 10 dots , so repeat and when i = mDotPosition  then increase the radius of dot i.e mBounceDotRadius
    for (int i = 0; i < mDotAmount; i++) {
      if (i == mDotPosition) {
        canvas.drawCircle(10 + (i * 20), mBounceDotRadius, mBounceDotRadius, paint);
      } else {
        int mDotRadius = 5;
        canvas.drawCircle(10 + (i * 20), mBounceDotRadius, mDotRadius, paint);
      }
    }


  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    int width;
    int height;

    //calculate the view width

    width = (20 * 9);
    height = (mBounceDotRadius * 2);

    //MUST CALL THIS
    setMeasuredDimension(width, height);
  }

  private void startAnimation() {
    BounceAnimation bounceAnimation = new BounceAnimation();
    bounceAnimation.setDuration(100);
    bounceAnimation.setRepeatCount(Animation.INFINITE);
    bounceAnimation.setInterpolator(new LinearInterpolator());
    bounceAnimation.setAnimationListener(new Animation.AnimationListener() {
      @Override
      public void onAnimationStart(Animation animation) {

      }

      @Override
      public void onAnimationEnd(Animation animation) {

      }

      @Override
      public void onAnimationRepeat(Animation animation) {
        mDotPosition++;
        //when mDotPosition == mDotAmount , then start again applying animation from 0th position , i.e  mDotPosition = 0;
        if (mDotPosition == mDotAmount) {
          mDotPosition = 0;
        }
      }
    });
    startAnimation(bounceAnimation);
  }


  private class BounceAnimation extends Animation {

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
      super.applyTransformation(interpolatedTime, t);
      //call invalidate to redraw your view again.
      invalidate();
    }
  }
}
