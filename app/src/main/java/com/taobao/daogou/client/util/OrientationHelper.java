package com.taobao.daogou.client.util;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OrientationHelper {
    private static OrientationHelper instance = null;

    public static OrientationHelper getInstance(Context context) {
        if (instance == null)
            instance = new OrientationHelper(context);
        return instance;
    }

    private final Context mContext;
    private final SensorManager mSensorManager;

    float[] accelerometerValues = new float[3];
    float[] magneticFieldValues = new float[3];

    private List<onOrientationListener> mListenerList = new ArrayList<onOrientationListener>();

    private OrientationHelper(Context context) {
        this.mContext = context;
        mSensorManager = (SensorManager) mContext
                .getSystemService(Context.SENSOR_SERVICE);

    }

    public void addListener(onOrientationListener listener) {
        mListenerList.remove(listener);
        mListenerList.add(listener);
        if (mListenerList.size() == 1) {
            mSensorManager.registerListener(sensorEventListener,
                    mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                    SensorManager.SENSOR_DELAY_UI);
            mSensorManager.registerListener(sensorEventListener,
                    mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                    SensorManager.SENSOR_DELAY_UI);
        }
    }

    public void removeListener(onOrientationListener listener) {
        mListenerList.remove(listener);
        if (mListenerList.size() == 0) {
            mSensorManager.unregisterListener(sensorEventListener);
        }
    }

    private final SensorEventListener sensorEventListener = new SensorEventListener() {

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
                magneticFieldValues = sensorEvent.values;
            if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
                accelerometerValues = sensorEvent.values;
            calculateOrientation();
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    private void calculateOrientation() {
        float[] values = new float[3];
        float[] R = new float[9];
        SensorManager.getRotationMatrix(R, null, accelerometerValues,
                magneticFieldValues);
        SensorManager.getOrientation(R, values);

        // 要经过一次数据格式的转换，转换为度
        values[0] = (float) Math.toDegrees(values[0]);
        if (!Float.isNaN(values[0]))
            callListeners(values[0]);
    }

    private void callListeners(float value) {
        for (onOrientationListener listener : mListenerList)
            listener.onOrientation(value);
    }
}
