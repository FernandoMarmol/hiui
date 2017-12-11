package es.fmm.hiui.widget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import es.fmm.hiui.statistics.TodayStats;

/**
 * Created by fmm on 7/20/13.
 * Esta clase se la llama CADA VEZ que creamos una instancia nueva del widget!!
 */
public class WidgetConfigure extends Activity {

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setResult(RESULT_CANCELED);

		int mAppWidgetId = -1;

		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		if (extras != null)
			mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
		
		TodayStats.newDay();
		TodayStats.loadStats(TodayStats.today, this.getApplicationContext());
		
		WidgetUtil.updateWidget(getApplicationContext());
		
		Intent resultValue = new Intent();
		resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
		setResult(RESULT_OK, resultValue);
		
		finish();
	}
}
