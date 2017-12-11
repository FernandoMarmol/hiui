package es.fmm.hiui.tests;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import es.fmm.hiui.R;
import es.fmm.hiui.application.Util;
import es.fmm.hiui.ddbb.SQLiteManager;
import es.fmm.hiui.ddbb.tables.PhoneUse;
import es.fmm.hiui.statistics.TodayStats;

@SuppressWarnings(value = { "deprecation" })
public class TestUtil {

	/**
	 * Método de test para poner el cronómetro en 59 minutos y probar la notificación de uso
	 */
	public static void setTodaysTimeTo59Minutes(){
		TodayStats.timeOn = 3540000;
	}
	
	/**
	 * Método de test para hacer que salte la notificacion de recod de tiempo de uso
	 */
	public static void setRecordStats(){
		TodayStats.timeOn = 14400000;
	}
	
	public static void deleteStats(String date, Context context){
		try{
			SQLiteManager.getInstance().openDB(false, context);
			PhoneUse.deletePhoneUse(date);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			SQLiteManager.getInstance().closeDB();
		}
	}
	
	public static void sendCustomNotification(Context context){
		Notification.BigTextStyle bts = new Notification.BigTextStyle();
		bts.setBigContentTitle("Mola que te mola title");
		bts.setSummaryText("Mola que te mola summary");
		bts.bigText("Mola que te mola Mola que te mola Mola que te mola Mola que te mola Mola que te mola Mola que te mola Mola que te mola Mola que te mola Mola que te mola Mola que te mola Mola que te mola Mola que te mola Mola que te mola Mola que te mola Mola que te mola text");
		
		Notification notification = new Notification.Builder(context)
		.setContentTitle("sdljsdjkf wi isdf isuh dfisuhd fisudf ")
		.setContentText("asjkdbnak sdbak dbjaksjdh aklsdjh alsdh asls dh")
		.setSmallIcon(R.drawable.ic_launcher)
		.setDefaults(Notification.DEFAULT_ALL)
		.setStyle(bts)
		.setNumber(1)
		.build();
		
		NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		nm.notify(1, notification);
		
		Notification.InboxStyle is = new Notification.InboxStyle();
		is.setBigContentTitle("Mola que te mola title inbox");
		is.setSummaryText("Mola que te mola summary");
		is.addLine("Linea 1");
		is.addLine("Linea 2");
		is.addLine("Linea 3");
		is.addLine("Linea 4");
		is.addLine("Linea 5");
		is.addLine("Linea 6");
		is.addLine("Linea 7");
		
		Notification notification2 = new Notification.Builder(context)
		.setContentTitle("sdljsdjkf wi isdf isuh dfisuhd fisudf ")
		.setContentText("asjkdbnak sdbak dbjaksjdh aklsdjh alsdh asls dh")
		.setSmallIcon(R.drawable.ic_launcher)
		.setDefaults(Notification.DEFAULT_ALL)
		.setStyle(is)
		.setNumber(20)
		.build();
		
		nm.notify(2, notification2);
		
		Notification.InboxStyle is2 = new Notification.InboxStyle();
		is2.setBigContentTitle("Mola que te mola title inbox");
		is2.setSummaryText("Mola que te mola summary");
		is2.addLine("Linea 1");
		is2.addLine("Linea 2");
		is2.addLine("Linea 3");
		is2.addLine("Linea 4");
		
		Notification notification3 = new Notification.Builder(context)
		.setContentTitle("sdljsdjkf wi isdf isuh dfisuhd fisudf ")
		.setContentText("asjkdbnak sdbak dbjaksjdh aklsdjh alsdh asls dh")
		.setSmallIcon(R.drawable.ic_launcher)
		.setDefaults(Notification.DEFAULT_ALL)
		.setStyle(is2)
		.setNumber(13)
		.build();
		
		nm.notify(3, notification3);
		
		Notification not = new Notification(R.drawable.ic_launcher, context.getString(R.string.record_time_title), java.lang.System.currentTimeMillis());
		not.defaults = Notification.DEFAULT_ALL;
		PendingIntent ai = PendingIntent.getActivity(context, 0, new Intent(), 0);
		//not.setLatestEventInfo(context, context.getString(R.string.record_time_title), Html.fromHtml(context.getString(R.string.record_time_text) + Util.millisecondsToTimeFormat(TodayStats.timeOn, context.getResources(), false, false) + context.getString(R.string.record_time_text2)), ai);

		nm.notify(4, not);
	
		Notification not2 = new Notification(R.drawable.ic_launcher, context.getString(R.string.record_uses_title), java.lang.System.currentTimeMillis());
		not2.defaults = Notification.DEFAULT_ALL;
		PendingIntent ai2 = PendingIntent.getActivity(context, 0, new Intent(), 0);
		//not2.setLatestEventInfo(context, context.getString(R.string.record_uses_title), Html.fromHtml(context.getString(R.string.record_uses_text) + TodayStats.onCounter + context.getString(R.string.record_uses_text2)) , ai2);
		
		nm.notify(5, not2);

	}
	
}
