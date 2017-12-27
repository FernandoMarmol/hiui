package es.fmm.hiui.widget;

import java.util.Set;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Html;
import android.util.Log;
import android.widget.RemoteViews;
import es.fmm.hiui.R;
import es.fmm.hiui.application.Util;
import es.fmm.hiui.ddbb.SQLiteManager;
import es.fmm.hiui.ddbb.tables.PhoneUse;
import es.fmm.hiui.services.WidgetPersistentService;
import es.fmm.hiui.settings.Settings;
import es.fmm.hiui.statistics.Statistics;
import es.fmm.hiui.statistics.TodayStats;

/**
 * Created by fmm on 7/21/13.
 */
public class WidgetReceiver extends AppWidgetProvider{

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(WidgetReceiver.class.getSimpleName(), "onReceive Action = " + intent.getAction());

		int[] mAppWidgetIds = null;
		if (intent.getAction().equalsIgnoreCase(Intent.ACTION_USER_PRESENT)) {
			Bundle extras = intent.getExtras();
			if (extras != null)
				mAppWidgetIds = extras.getIntArray(AppWidgetManager.EXTRA_APPWIDGET_IDS);

			if (mAppWidgetIds != null){
				onUpdate(context, AppWidgetManager.getInstance(context), mAppWidgetIds);
			}
			else{
				ComponentName thisWidget = new ComponentName(context, WidgetReceiver.class);
				AppWidgetManager widgetManager = AppWidgetManager.getInstance(context);
				int[] allWidgetIds = widgetManager.getAppWidgetIds(thisWidget);

				onUpdate(context, AppWidgetManager.getInstance(context), allWidgetIds);
			}
		}

		super.onReceive(context, intent);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		Log.d(WidgetReceiver.class.getSimpleName(), "onUpdate");
		PackageManager pm = context.getPackageManager();

		RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.hiui_widget);

		//ACTUALIZAMOS EL VALOR DE LOS TEXTOS DEL WIDGET (EMPEZAMOS POR LAS APPS)
		/*int contador = 0;
		StringBuilder apps = new StringBuilder();
		Set<String> set = TodayStats.applicationsStats.keySet();
		for (String key : set) {
			if(contador < context.getResources().getInteger(R.integer.widget_apps_showing)){
				try {
					apps.append(pm.getApplicationLabel(pm.getApplicationInfo(key, PackageManager.GET_META_DATA)) + " - " + Html.fromHtml("<b>"+Util.getAppPercentageOfUse(key, TodayStats.applicationsStats, 0) + " %</b>") + " \r\n");
				}
				catch(Exception e){
					apps.append(key + " - " + Util.getAppPercentageOfUse(key, TodayStats.applicationsStats, 0) + " % \r\n");
				}
				contador++;
			}
		}

		if(apps.toString().trim().length() > 0)
			views.setTextViewText(R.id.tasksStatus, apps.toString());*/

		views.setTextViewText(R.id.timesON, TodayStats.onCounter + "");
		views.setTextViewText(R.id.timeON,  Util.millisecondsToTimeFormat(TodayStats.timeOn, context.getResources(), false, false, true));
		try{
			SQLiteManager.getInstance().openDB(false, context);
			views.setTextViewText(R.id.averageUsesPerDayThisYear, String.valueOf(PhoneUse.averageUsesPerDay(TodayStats.getYear())));
			views.setTextViewText(R.id.averageTimePerDayThisYear, Util.millisecondsToTimeFormat(PhoneUse.averageTimePerDay(TodayStats.getYear()), context.getResources(), false, true, false));
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			SQLiteManager.getInstance().closeDB();
		}



		// Register an onClickListener para actualizar el widget al tocar su layout
		Intent intent;
		try {
		    intent = pm.getLaunchIntentForPackage(context.getPackageName());
		    if(intent != null){
		    	intent.addCategory(Intent.CATEGORY_LAUNCHER);
		    	PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
		    		views.setOnClickPendingIntent(R.id.touchSensor, pendingIntent);
		    }
		}
		catch(Exception e){ }

		// Register an onClickListener para el boton de ver las estadísticas anteriores
		Intent intentStats = new Intent (context, Statistics.class);
		intentStats.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent pendingIntentStats = PendingIntent.getActivity(context, 0, intentStats, PendingIntent.FLAG_UPDATE_CURRENT);
		views.setOnClickPendingIntent(R.id.buttonStats, pendingIntentStats);

		// Register an onClickListener para el boton de ver los Settings
		Intent intentSettings = new Intent (context, Settings.class);
		intentSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent pendingIntentSettings = PendingIntent.getActivity(context, 0, intentSettings, PendingIntent.FLAG_UPDATE_CURRENT);
		views.setOnClickPendingIntent(R.id.buttonSettings, pendingIntentSettings);

		//Lanzamos la actualizacion del Widget
		appWidgetManager.updateAppWidget(appWidgetIds, views);

		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@Override
	public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
		super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
		Log.d(WidgetReceiver.class.getSimpleName(), "onAppWidgetOptionsChanged");
	}

	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
		Log.d(WidgetReceiver.class.getSimpleName(), "onEnabled");

		//Arrancamos el servicio principal
		Intent serviceIntent = new Intent(context.getApplicationContext(), WidgetPersistentService.class);
		context.getApplicationContext().startService(serviceIntent);
	}

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		super.onDeleted(context, appWidgetIds);
		Log.d(WidgetReceiver.class.getSimpleName(), "onDeleted");
	}

	@Override
	public void onDisabled(Context context) {
		Log.d(WidgetReceiver.class.getSimpleName(), "onDisabled");

		//Finalizamos el servicio
		Intent serviceIntent = new Intent(context.getApplicationContext(), WidgetPersistentService.class);
		context.getApplicationContext().stopService(serviceIntent);

		super.onDisabled(context);
	}

	@Override
	public IBinder peekService(Context myContext, Intent service) {
		Log.d(WidgetReceiver.class.getSimpleName(), "peekService");
		return super.peekService(myContext, service);
	}
}
