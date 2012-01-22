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
    setContentView(R.layout.main);

    mView = (CompassView) findViewById(R.id.compass);
    mView.setDataSource(this);
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
    if (mValues == null) {
      return 0.0f;
    } 
    return -mValues[0];
  }

}
