/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.davidykay.sighted;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Config;
import android.util.Log;
import android.view.View;

public class Compass extends Activity implements CompassDataSource {

  private static final String TAG = "Compass";

  private SensorManager mSensorManager;
  private Sensor mSensor;
  private CompassView mView;
  private float[] mValues;

  private final SensorEventListener mListener = new SensorEventListener() {
    public void onSensorChanged(SensorEvent event) {
      if (Config.LOGD) {
        Log.d(TAG, "sensorChanged (" + event.values[0] + ", " + event.values[1]
            + ", " + event.values[2] + ")");
      }
      mValues = event.values;
      if (mView != null) {
        mView.invalidate();
      }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
  };

  @Override
  protected void onCreate(Bundle icicle) {
    super.onCreate(icicle);
    mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
    mView = new CompassView(this, this);
    setContentView(mView);
  }

  @Override
  protected void onResume() {
    if (Config.LOGD) {
      Log.d(TAG, "onResume");
    }
    super.onResume();

    mSensorManager.registerListener(mListener, mSensor,
        SensorManager.SENSOR_DELAY_GAME);
  }

  @Override
  protected void onStop() {
    if (Config.LOGD) {
      Log.d(TAG, "onStop");
    }
    mSensorManager.unregisterListener(mListener);
    super.onStop();
  }
  
  @Override
  public float getRotation() {
    return -mValues[0];
  }

  private class CompassView extends View {
    private CompassDataSource mDataSource;

    private Paint mPaint = new Paint();
    private Path mPath = createArrowPath();
    private boolean mAnimate;
    private long mNextTime;

    public CompassView(CompassDataSource dataSource, Context context) {
      super(context);

      mDataSource = dataSource;
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
      if (mValues != null) {
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
  }
}
