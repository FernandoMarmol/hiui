package es.fmm.hiui.em;

import android.content.Context;

import es.fmm.hiui.ddbb.SQLiteManager;
import es.fmm.hiui.ddbb.tables.PhoneUse;
import es.fmm.hiui.statistics.TodayStats;

/**
 * @author fmm
 * Esta clase sirve para consultar si se han batido records de uso<br><br>
 * Los atributos <b>mostUses</b> y <b>mostTime</b> pueden tener los valores:<br>
 * -> -1 si no se han cargado los valores más altos previamente existentes en BBDD<br>
 * -> -2 si se ha batido el record en el día de hoy<br>
 * -> Cualquier otro valor es el record a superar<br>
 */
public class GlobalRecordsEM {

	private static int mostUses = -1; //-1 indica que no se ha cargado el dato y -2, que el record ya se ha batido hoy
	private static long mostTime = -1; //-1 indica que no se ha cargado el dato y -2, que el record ya se ha batido hoy
	private static long averageTimeDaily = -1; //-1 indica que no se ha cargado el dato y -2, que el record ya se ha batido hoy
	private static long averageUsesDaily = -1; //-1 indica que no se ha cargado el dato y -2, que el record ya se ha batido hoy

	/**
	 * Nos dice si se ha batido hoy el número de usos en un día
	 * @param usesToCompare
	 * @return
	 */
	public static boolean isNewUsesRecord(int usesToCompare, Context context){
		if(mostUses == -1){
			try{
				SQLiteManager.getInstance().openDB(false, context);
				mostUses = PhoneUse.mostUsesInADay();
			}
			catch(Exception e){
				e.printStackTrace();
			}
			finally{
				SQLiteManager.getInstance().closeDB();
			}
		}

		boolean isRecord = false;
		if(mostUses >= 0 && usesToCompare > mostUses){
			isRecord = true;
			mostUses = -2;
		}

		return isRecord;
	}

	/**
	 * Nos dice si se ha batido el record de tiempo en un día
	 * @param timeToCompare
	 * @param year Si le pasamos el año, nos lo dice para el año solicitado. Si no se le pasa (se pasa un -1), lo dice globalmente
	 * @return
	 */
	public static boolean isNewTimeRecord(long timeToCompare, int year, Context context){
		if(mostTime == -1){
			try{
				SQLiteManager.getInstance().openDB(false, context);
				mostTime = PhoneUse.mostTimeInADay(year);
			}
			catch(Exception e){
				e.printStackTrace();
			}
			finally{
				SQLiteManager.getInstance().closeDB();
			}
		}

		boolean isRecord = false;
		if(mostTime >= 0 && timeToCompare > mostTime){
			isRecord = true;
			mostTime = -2;
		}

		return isRecord;
	}

	/**
	 * Nos dice si se ha superado la media de usos del teléfono en un día<br>
	 * Condiciones para que salga la notificación:
	 * - Sale si se supera la media diaria de un año concreto en un 15%
	 * - Sale si el numero de usos supera los 10 usos
	 * @param usesToCompare - Numero de usos que hay que comparar
	 * @param year - Año con el que comparar la media diaria
	 * @return
	 */
	public static boolean isAverageDailyUsesBeaten(int usesToCompare, int year, Context context){
		if(averageUsesDaily == -1){
			try{
				SQLiteManager.getInstance().openDB(false, context);
				averageUsesDaily = PhoneUse.averageUsesPerDay(year);
			}
			catch(Exception e){
				e.printStackTrace();
			}
			finally{
				SQLiteManager.getInstance().closeDB();
			}
		}

		boolean isRecord = false;
		if(averageUsesDaily >= 0 && usesToCompare >= (averageUsesDaily+(averageUsesDaily/(100/15)))){
			isRecord = true;
			averageUsesDaily = -2;
		}

		return isRecord;
	}

	/**
	 * Nos dice si se ha superado la media de tiempo de uso del teléfono en un día<br>
	 * Condiciones para que salga la notificación:
	 *  - Sale si se supera la media diaria de un año concreto en un 10%
	 *  - Sale si ese tiempo supera los 10 minutos de uso en un día
	 * @param timeToCompare - Tiempo de uso que hay que comparar
	 * @param year - Año con el que comparar la media diaria
	 * @return
	 */
	public static boolean isAverageDailyTimeBeaten(long timeToCompare, int year, Context context){
		if(averageTimeDaily == -1){
			try{
				SQLiteManager.getInstance().openDB(false, context);
				averageTimeDaily = PhoneUse.averageTimePerDay(year);
			}
			catch(Exception e){
				e.printStackTrace();
			}
			finally{
				SQLiteManager.getInstance().closeDB();
			}
		}

		boolean isRecord = false;
		if(averageTimeDaily >= 0 && timeToCompare >= (averageTimeDaily+(averageTimeDaily/10)) && timeToCompare >= 600000){
			isRecord = true;
			averageTimeDaily = -2;
		}

		return isRecord;
	}

	/**
	 * Este método debe llamarse al cambiar de día
	 */
	public static void reset(){
		mostUses = -1;
		mostTime = -1;
		averageUsesDaily = -1;
		averageTimeDaily = -1;
	}

}
