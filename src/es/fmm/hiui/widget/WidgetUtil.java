package es.fmm.hiui.widget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import es.fmm.hiui.R;

public class WidgetUtil {
	
	/**
	 * Nos dice si la instancia del widget es de tipo normal o lockscreen<br>
	 * El código está comentado para que no entre en conflicto de version del SO. Si es necesario en un futuro, se descomenta y se cambia la version mínima para la app
	 * @param context
	 * @param widgetInstanceID
	 * @return
	 */
	public static boolean isLockScreenWidgetInstance(Context context, int widgetInstanceID) throws Exception {
		/*try{
			AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
	
			Bundle myOptions = appWidgetManager.getAppWidgetOptions(widgetInstanceID);
			// Get the value of OPTION_APPWIDGET_HOST_CATEGORY
			int category = myOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_HOST_CATEGORY, -1);
			// If the value is WIDGET_CATEGORY_KEYGUARD, it's a lockscreen widget
			boolean isKeyguard = (category == AppWidgetProviderInfo.WIDGET_CATEGORY_KEYGUARD);
			
			return isKeyguard;
		}
		catch(Exception e){
			throw new Exception();
		}*/
		
		return false;
	}
	
	/**
	 * A partir del contexto de la aplicación, devuelve los identificadores de las diferentes instancias del widget que pudiese haber en el móvil
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public static int[] getWidgetIDs(Context context) throws Exception{
		try{
			AppWidgetManager widgetManager = AppWidgetManager.getInstance(context);
			ComponentName widgetComponent = new ComponentName(context, WidgetReceiver.class);

			//obtenemos los ids de las instancias del widget que queremos actualizar
			return widgetManager.getAppWidgetIds(widgetComponent);
		}
		catch(Exception e){
			throw new Exception();
		}
	}
	
	/**
	 * Performs an independent and full update over the widget
	 */
	public static void updateWidget(Context context){
		try{
			Intent intent = new Intent(context, WidgetReceiver.class);
			intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
			intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, getWidgetIDs(context.getApplicationContext()));
		
			context.startActivity(intent);
		}
		catch(Exception e) { }
	}
	
	/**
	 * Este método activa una alarma que actualizará periódicamente el widget
	 * @param context
	 */
	public static void setAlarmToUpdateWidget(Context context){
		AppWidgetManager widgetManager = AppWidgetManager.getInstance(context);
		ComponentName widgetComponent = new ComponentName(context, WidgetReceiver.class);

		//obtenemos los ids de las instancias del widget que queremos actualizar
		int[] mAppWidgetId = widgetManager.getAppWidgetIds(widgetComponent);

		//Intent a partir del cual se creará el PendingIntent
		Intent intent = new Intent(context, WidgetReceiver.class);
		intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, mAppWidgetId);
		intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

		//Creamos el Intent que se ejecutará repetidamente con la alarma
		PendingIntent pIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

		//Creamos la alarma que repite la actualización del widget
		AlarmManager am = (AlarmManager)(context.getSystemService(Context.ALARM_SERVICE));
		am.setRepeating(AlarmManager.RTC, System.currentTimeMillis() + context.getResources().getInteger(R.integer.widget_update_interval), context.getResources().getInteger(R.integer.widget_update_interval), pIntent);
	}
	
	/**
	 * Este método cancela la alarma que actualiza periódicamente el widget
	 * @param context
	 */
	public static void cancelAlarmToUpdateWidget(Context context){
		//Cancelamos la alarma
		Intent intent = new Intent(context, WidgetReceiver.class);
		intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(pendingIntent);
		//Fin - Cancelamos la alarma
	}

}
