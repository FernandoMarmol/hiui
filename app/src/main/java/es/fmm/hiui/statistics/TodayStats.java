package es.fmm.hiui.statistics;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import android.app.backup.BackupManager;
import android.content.Context;
import android.util.Log;

import es.fmm.hiui.BuildConfig;
import es.fmm.hiui.application.Util;
import es.fmm.hiui.ddbb.SQLiteManager;
import es.fmm.hiui.ddbb.tables.AppsUse;
import es.fmm.hiui.ddbb.tables.PhoneUse;
import es.fmm.hiui.ddbb.tables.beans.PhoneUseBean;

/**
 * Created by fmm on 7/30/13.
 */
public class TodayStats {

	public static String today = null; //fecha de hoy en formato yyyyMMdd

	public static int onCounter = 0; //veces que se ha encendido la pantalla
	public static int offCounter = 0; //veces que se ha apagado la pantalla

	public static long timeOn = 0; //tiempo con la pantalla encendida
	public static long lastTimeOn = 0; //Ultima vez que se tiene consciencia de que el teléfono estuviese activo (pantalla encendida)

	public static LinkedHashMap<String, Integer> applicationsStats = new LinkedHashMap<>(); //Estadisticas de uso de las apps

	/**
	 * A partir del atributo today, nos dice en qué año estamos
	 * @return
	 */
	public static int getYear(){
		if(today == null){
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());

			return cal.get(Calendar.YEAR);
		}
		return Integer.parseInt(today.substring(0, 4));
	}

	/**
	 * This method is called when the day is off and statistics must be initialized to store a new day statistics
	 */
	public static void newDay(){
		Date time = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());

		if(today == null || !today.equalsIgnoreCase(sdf.format(time))){
			today = sdf.format(time);

			timeOn = 0;
			lastTimeOn = time.getTime();

			onCounter = 0;
			offCounter = 0;

			applicationsStats = new LinkedHashMap<>();

			//Con esta línea indicamos al sistema que tiene que hacer un backup de los datos de la app. De esta manera se hace diariamente
			BackupManager.dataChanged(BuildConfig.APPLICATION_ID);
		}
	}

	public static boolean saveStats(String date, Context context){
		boolean saved = false;
		//Transaccion BBDD
		try{
			SQLiteManager.getInstance().openDB(true, context);

			//EMPEZAMOS CON EL USO DE LAS APLICACIONES
			//Comienza la insercion en BBDD
			Iterator<Map.Entry<String, Integer>> registry = applicationsStats.entrySet().iterator();
			Map.Entry<String, Integer> entry = null;
			while(registry.hasNext()){
				entry = registry.next();
				AppsUse.createAppUse(date, entry.getKey(), entry.getValue());
			}

			//CONTINUAMOS CON EL USO DEL TELÉFONO
			PhoneUse.createPhoneUse(date, onCounter, timeOn);

			saved = true;
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			SQLiteManager.getInstance().closeDB();
			BackupManager.dataChanged(BuildConfig.APPLICATION_ID);
		}

		return saved;
	}

	public static boolean loadStats(String date, Context context){
		boolean loaded = false;

		//Transaccion BBDD
		try{
			SQLiteManager.getInstance().openDB(false, context);

			//Estadisticas apps
			applicationsStats = AppsUse.getAllAppUsesToHashMap(date);

			//Estadisticas uso telefono
			PhoneUseBean pub = PhoneUse.getPhoneUse(-1 ,date);
			if(pub != null){
				onCounter = (int) pub.getTimes();
				timeOn = pub.getTimeAmount();
			}

			loaded = true;
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			SQLiteManager.getInstance().closeDB();
		}

		return loaded;
	}

	/**
	 * Returns the score of an app considering its position in the task process
	 * @param position
	 * @return
	 */
	public static int getAppScore(int position){
		switch (position){
			case 3:
				return 100;
			case 2:
				return 10;
			case 1:
				return 1;
			default:
				return 1;
		}
	}

	/**
	 * Método que se encarga de sumar el tiempo de uso del teléfono
	 * @param milliseconds
	 */
	public static void addTime(long milliseconds){
		Log.d(TodayStats.class.getSimpleName(), "Tiempo ANTES " + Util.millisecondsToTimeFormat(timeOn, null, false, false, true));
		timeOn += milliseconds;
		Log.d(TodayStats.class.getSimpleName(), "Tiempo DESPUES " + Util.millisecondsToTimeFormat(timeOn, null, false, false, true));
	}
}
