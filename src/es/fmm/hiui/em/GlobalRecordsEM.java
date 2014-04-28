package es.fmm.hiui.em;

import es.fmm.hiui.ddbb.SQLiteManager;
import es.fmm.hiui.ddbb.tables.PhoneUse;

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
	
	/**
	 * Nos dice si se ha batido hoy el número de usos en un día
	 * @param usesToCompare
	 * @return
	 */
	public static boolean isNewUsesRecord(int usesToCompare){
		if(mostUses == -1){
			try{
				SQLiteManager.getInstance().openDB(false);
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
	 * @return
	 */
	public static boolean isNewTimeRecord(long timeToCompare){
		if(mostTime == -1){
			try{
				SQLiteManager.getInstance().openDB(false);
				mostTime = PhoneUse.mostTimeInADay();
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
	 * Este método debe llamarse al cambiar de día
	 */
	public static void reset(){
		mostUses = -1;
		mostTime = -1;
	}

}
