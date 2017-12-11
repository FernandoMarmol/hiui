package es.fmm.hiui.sensors;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

/**
 * Created by ferna on 09/12/2017.
 */

public class HiuiSensorEventListener implements SensorEventListener {
	@Override
	public void onSensorChanged(SensorEvent event) {

		// when pressure value is changed, this method will be called.
		float pressure_value = 0.0f;
		float height = 0.0f;

		// if you use this listener as listener of only one sensor (ex, Pressure), then you don't need to check sensor type.
		if( Sensor.TYPE_PRESSURE == event.sensor.getType() ) {
			pressure_value = event.values[0];
			Log.d(HiuiSensorEventListener.class.getSimpleName(), "Presión: " + pressure_value);
			height = SensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, pressure_value);
			Log.d(HiuiSensorEventListener.class.getSimpleName(), "Altura: " + height);
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		Log.d(HiuiSensorEventListener.class.getSimpleName(), "Ha cambiado la exactitud del sensor " + sensor.getName() + " al valor " + accuracy);
	}
}
