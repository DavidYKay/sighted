package com.davidykay.sighted;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

public class CompassView extends View {

  private CompassDataSource mDataSource;

  private Paint mPaint = new Paint();
  private Path mPath = createArrowPath();
  private boolean mAnimate;
  private long mNextTime;
  
  

//  public CompassView(Context context, AttributeSet attrs, int defStyle) {
//    super(context, attrs, defStyle);
//    // TODO Auto-generated constructor stub
//  }

  public CompassView(Context context, AttributeSet attrs) {
    super(context, attrs);    
  }

  public CompassView(Context context) {
    super(context);
  }

  private Path createArrowPath() {
    Path path = new Path();
    // Construct a wedge-shaped path
    path.moveTo(0, -50);
    path.lineTo(-20, 60);
    path.lineTo(0, 50);
    path.lineTo(20, 60);
    path.close();

    return path;
  }

  @Override
  protected void onDraw(Canvas canvas) {
    Paint paint = mPaint;

    canvas.drawColor(Color.WHITE);

    paint.setAntiAlias(true);
    paint.setColor(Color.BLACK);
    paint.setStyle(Paint.Style.FILL);

    int w = canvas.getWidth();
    int h = canvas.getHeight();
    int cx = w / 2;
    int cy = h / 2;

    canvas.translate(cx, cy);
    //if (mDataSource.getRotation() != null) {
    if (mDataSource != null) {
      canvas.rotate(mDataSource.getRotation());
    }
    canvas.drawPath(mPath, mPaint);
  }

  @Override
  protected void onAttachedToWindow() {
    mAnimate = true;
    super.onAttachedToWindow();
  }

  @Override
  protected void onDetachedFromWindow() {
    mAnimate = false;
    super.onDetachedFromWindow();
  }

  public void setDataSource(CompassDataSource dataSource) {
    mDataSource = dataSource;    
  }
}
