package es.fmm.hiui.services;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import es.fmm.hiui.R;
import es.fmm.hiui.application.Constants;
import es.fmm.hiui.application.Util;
import es.fmm.hiui.em.AppsEM;
import es.fmm.hiui.em.GlobalRecordsEM;
import es.fmm.hiui.receivers.OnOffReceiver;
import es.fmm.hiui.statistics.TodayStats;
import es.fmm.hiui.widget.WidgetUtil;

/**
 * Created by fmm on 7/31/13.
 */
public class WidgetPersistentService extends Service {

	private static OnOffReceiver oor = null;
	
	private static boolean isDeviceActive = false;

	@Override
	public void onCreate() {
		//Cargamos las estadísticas que pudiese haber en BBDD
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		deactivateOOR(getApplicationContext());
		TodayStats.saveStats(TodayStats.today);
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		activateOOR(getApplicationContext());
		
		Date time = new Date();
		
		//Si no está inicializado el día, lo hacemos y cargamos los datos para ese día
		if(TodayStats.today == null){
			TodayStats.newDay();
			TodayStats.loadStats(TodayStats.today);
		}
		
		//Comprobamos si estamos cambiando de dia
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
		if(Integer.parseInt(TodayStats.today) != Integer.parseInt(sdf.format(time))){
			TodayStats.saveStats(TodayStats.today); //Guardamos las estadísticas de hoy
			TodayStats.newDay(); //Empezamos estadísticas para un día nuevo
			TodayStats.loadStats(TodayStats.today); //Por si acaso cargamos estadísticas que pudiese haber para ese dia nuevo (algo que nunca debería pasar)
			
			GlobalRecordsEM.reset();
		}

		String action = "";
		if(intent != null && intent.getAction() != null)
			action = intent.getAction();
		
		if(action.equalsIgnoreCase(Intent.ACTION_SCREEN_ON)){
			Log.d(WidgetPersistentService.class.getSimpleName(), "onHandleIntent - SCREEN ON");
			TodayStats.lastTimeOn = time.getTime();
			isDeviceActive = true;
			
			WidgetUtil.setAlarmToUpdateWidget(getApplicationContext());
			
			TodayStats.onCounter++;
			
			//Intent a partir del cual se creará el PendingIntent
			Intent intentAlarma = new Intent(getApplicationContext(), WidgetPersistentService.class);
			//Creamos el Intent que se ejecutará repetidamente con la alarma para ejecutar el servicio
			PendingIntent pIntent = PendingIntent.getService(getApplicationContext(), 0, intentAlarma, PendingIntent.FLAG_UPDATE_CURRENT);

			//Creamos la alarma que repite la llamada a este servicio para que recoja estadísticas
			AlarmManager am = (AlarmManager)(getApplicationContext().getSystemService(Context.ALARM_SERVICE));
			am.setRepeating(AlarmManager.RTC, System.currentTimeMillis() + 1000, getApplicationContext().getResources().getInteger(R.integer.service_update_interval), pIntent);
		}
		
		if(isDeviceActive){
			checkForHourNotification(getApplicationContext(), TodayStats.timeOn, TodayStats.timeOn + (time.getTime() - TodayStats.lastTimeOn));
			checkForRecords(getApplicationContext());
			//Sumamos el tiempo que lleva activo
			TodayStats.addTime(time.getTime() - TodayStats.lastTimeOn);
			//Fijamos la ultima vez que supimos que el teléfono estaba activo en la hora actual
			TodayStats.lastTimeOn = time.getTime();
			
			//Recuperamos la información de las apps y la guardamos
			gatherAppsInformation(getApplicationContext());
			TodayStats.saveStats(TodayStats.today);
		}
		
		if(action.equalsIgnoreCase(Intent.ACTION_SCREEN_OFF)){
			Log.d(WidgetPersistentService.class.getSimpleName(), "onHandleIntent - SCREEN OFF");
			isDeviceActive = false;

			WidgetUtil.cancelAlarmToUpdateWidget(getApplicationContext());
				
			TodayStats.offCounter++;
			
			//Cancelamos la alarma que ejecuta el servicio
			Intent intentAlarma = new Intent(getApplicationContext(), WidgetPersistentService.class);
			PendingIntent pIntent = PendingIntent.getService(getApplicationContext(), 0, intentAlarma, PendingIntent.FLAG_UPDATE_CURRENT);

			AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
			alarmManager.cancel(pIntent);
			//Fin - Cancelamos la alarma
		}

		return Service.START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	/**
	 * Este método reordena el hashmap. Al usar un linkedhashmap, recuerda el orden de insercion y con el keyset, se van recuperando en ese orden.
	 * @param map
	 * @return
	 */
	private static LinkedHashMap<String, Integer> sortByValueDesc(HashMap<String, Integer> map) {
		LinkedHashMap<String, Integer> lhmSorted = new LinkedHashMap<String, Integer>();

		Integer valueAux = null;
		String keyAux = null;
		Set<String> keys = null;
		while(map.size() > 0){
			keys = map.keySet();
			valueAux = Integer.valueOf(0);
			keyAux = "";
			for(String key: keys){
				if(map.get(key).compareTo(valueAux) > 0){
					keyAux = key;
					valueAux = map.get(key);
				}
			}

			lhmSorted.put(keyAux, valueAux);
			map.remove(keyAux);
		}

		return lhmSorted;
	}

	/**
	 * This method activates the OnOffReceiver
	 * @param context
	 */
	public static void activateOOR(Context context){
		if(oor == null){
			oor = new OnOffReceiver();
			IntentFilter intentFilter = new IntentFilter();
			intentFilter.addAction(Intent.ACTION_SCREEN_ON);
			intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
			context.registerReceiver(oor, intentFilter);
		}
	}

	/**
	 * This method deactivates the OnOffReceiver
	 * @param context
	 */
	public static void deactivateOOR(Context context){
		if(oor != null){
			context.unregisterReceiver(oor);
			oor = null;
		}
	}

	/**
	 * This method checks for running apps and stores the info
	 * @param context
	 */
	private static void gatherAppsInformation(Context context){
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

		boolean isLauncherOnTop = false;
		if(am.getRunningTasks(1) != null && am.getRunningTasks(1).size() > 0){
			isLauncherOnTop = AppsEM.isAppLauncher(am.getRunningTasks(1).get(0).baseActivity.getPackageName());
		}

		KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
		boolean isPhoneScreenLocked = km.inKeyguardRestrictedInputMode();

		//Sólo recogemos estadísticas cuando el teléfono no está bloqueado
		if(!isPhoneScreenLocked && !isLauncherOnTop){

			int counter = 3;
			List<ActivityManager.RunningTaskInfo> lTasks = am.getRunningTasks(6);
			Iterator<ActivityManager.RunningTaskInfo> iTasks = lTasks.iterator();

			while(iTasks.hasNext() && counter > 0) {
				ActivityManager.RunningTaskInfo info = iTasks.next();
				try {
					if(!AppsEM.isAppNotIncludedInStats(info.baseActivity.getPackageName())){
						int score = TodayStats.getAppScore(counter);
						if(TodayStats.applicationsStats.containsKey(info.baseActivity.getPackageName())){
							Integer iAux = TodayStats.applicationsStats.get(info.baseActivity.getPackageName());
							iAux += score;
							TodayStats.applicationsStats.put(info.baseActivity.getPackageName(), iAux);
						}
						else{
							TodayStats.applicationsStats.put(info.baseActivity.getPackageName(), score);
						}
						
						counter--;
					}
				}
				catch(Exception e) { }
			}
		}

		TodayStats.applicationsStats = sortByValueDesc(TodayStats.applicationsStats);
	}
	
	/**
	 * Este método envía una notificación al usuario cada vez que cumple una hora de uso del teléfono móvil
	 * @param context
	 * @param spentTimeBefore
	 * @param spentTimeAfter
	 */
	@SuppressWarnings(value = { "deprecation" })
	private void checkForHourNotification(Context context, long spentTimeBefore, long spentTimeAfter){		
		boolean areNotificationsActive = PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getString(R.string.sett_notifications), true);
		if(areNotificationsActive){
			int hoursBefore = (int) (spentTimeBefore / Constants.MILLISECONDS_IN_AN_HOUR);
			int hoursAfter= (int) (spentTimeAfter / Constants.MILLISECONDS_IN_AN_HOUR);
			
			if(hoursAfter > hoursBefore){
				//For Android 11 and up
				/*Notification notification = new Notification.Builder(context)
					.setContentTitle(context.getString(R.string.notification_title))
					.setContentText(hoursAfter + context.getString(R.string.notification_message_hours))
					.setSmallIcon(R.drawable.ic_launcher)
					.setDefaults(Notification.DEFAULT_ALL)
					.build();*/
				
				//For Android 10
				Notification notification = new Notification(R.drawable.ic_launcher, hoursAfter + context.getString(R.string.notification_message_hours), java.lang.System.currentTimeMillis());
				notification.defaults = Notification.DEFAULT_ALL;
				PendingIntent appIntent = PendingIntent.getActivity(context, 0, new Intent(), 0);
				notification.setLatestEventInfo(context, context.getString(R.string.notification_title), hoursAfter + context.getString(R.string.notification_message_hours), appIntent);
				
				NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
				nm.notify(1, notification);
			}
		}
	}
	
	/**
	 * Envía notificaciones al usuario en funcion de los nuevos récords establecidos
	 * @param context
	 */
	@SuppressWarnings(value = { "deprecation" })
	private static void checkForRecords(Context context){
		boolean areNotificationsActive = PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getString(R.string.sett_notifications), true);
		boolean isTodayInstallationDay = (PreferenceManager.getDefaultSharedPreferences(context).getString(context.getString(R.string.sett_installation_day), "000000").equalsIgnoreCase(TodayStats.today))?true:false;
		if(areNotificationsActive && !isTodayInstallationDay){
			if(GlobalRecordsEM.isNewTimeRecord(TodayStats.timeOn)){
				Notification notification = new Notification(R.drawable.ic_launcher, context.getString(R.string.record_time_title), java.lang.System.currentTimeMillis());
				notification.defaults = Notification.DEFAULT_ALL;
				PendingIntent appIntent = PendingIntent.getActivity(context, 0, new Intent(), 0);
				notification.setLatestEventInfo(context, context.getString(R.string.record_time_title), Html.fromHtml(context.getString(R.string.record_time_text) + Util.millisecondsToTimeFormat(TodayStats.timeOn, context.getResources(), false, false) + context.getString(R.string.record_time_text2)), appIntent);
	
				NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
				nm.notify(1, notification);
			}
			
			if(GlobalRecordsEM.isNewUsesRecord(TodayStats.onCounter)){
				Notification notification = new Notification(R.drawable.ic_launcher, context.getString(R.string.record_uses_title), java.lang.System.currentTimeMillis());
				notification.defaults = Notification.DEFAULT_ALL;
				PendingIntent appIntent = PendingIntent.getActivity(context, 0, new Intent(), 0);
				notification.setLatestEventInfo(context, context.getString(R.string.record_uses_title), Html.fromHtml(context.getString(R.string.record_uses_text) + TodayStats.onCounter + context.getString(R.string.record_uses_text2)) , appIntent);
				
				NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
				nm.notify(3, notification);
			}
		}
	}

}
