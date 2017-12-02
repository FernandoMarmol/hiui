package es.fmm.hiui.ddbb.tables;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import es.fmm.hiui.ddbb.SQLiteManager;
import es.fmm.hiui.ddbb.tables.beans.AppUseBean;

/**
 * Created by fmm on 7/31/13.
 * This class also works as a DAO
 */
public class AppsUse {

	public static final String TABLE_NAME = "apps_use";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_DATE = "date"; //Dia
	public static final String COLUMN_APP = "app"; //Aplicacion
	public static final String COLUMN_RATE = "rate"; //Rating de uso de la aplicacion

	private static String[] allColumns = { COLUMN_ID, COLUMN_DATE, COLUMN_APP, COLUMN_RATE };

	// Table creation sql statement
	public static final String SQL_TABLE_CREATE = "create table " + TABLE_NAME + "(" + COLUMN_ID + " integer primary key autoincrement, " + COLUMN_DATE + " text not null, " + COLUMN_APP + " text not null, " + COLUMN_RATE + " integer not null);";

	/**
	 * @param id - Id del registro a actualizar (-1 si se desconoce, pero entonces es obligatorio el parametro date)
	 * @param date - Fecha del registro a actualizar en formato YYYYMMDD
	 * @param app - Nombre de la aplicacion
	 * @return AppUseBean or null if not found
	 */
	public static AppUseBean getAppUse(long id, String date, String app) {
		AppUseBean aub = null;
		Cursor mCursor = null;
		try{
			String where;
			if(id > -1)
				where = COLUMN_ID + " = " + id;
			else
				where = COLUMN_DATE + " = '" + date + "' AND " + COLUMN_APP + " = '" + app + "'";

			mCursor = SQLiteManager.getInstance().getDB().query(true, TABLE_NAME, new String[] { COLUMN_ID, COLUMN_DATE, COLUMN_APP, COLUMN_RATE }, where, null, null, null, null, null);
			if (mCursor != null && mCursor.moveToFirst())
				aub = cursorToAppUse(mCursor);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			if(mCursor != null)
				mCursor.close();
		}

		return aub;
	}

	public static AppUseBean createAppUse(String date, String app, long rate) {
		AppUseBean aub = getAppUse(-1, date, app);
		try{
			if(aub == null){
				ContentValues values = new ContentValues();
				values.put(COLUMN_DATE, date);
				values.put(COLUMN_APP, app);
				values.put(COLUMN_RATE, rate);

				long insertId = SQLiteManager.getInstance().getDB().insert(TABLE_NAME, null, values);
				aub = getAppUse(insertId, null, null);
			}
			else{
				boolean updated = updateAppUse(-1, date, app, rate);
				if(updated)
					aub = getAppUse(-1, date, app);
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}

		return aub;
	}

	/**
	 * Actualiza el registro en base de datos
	 * @param id - Id del registro a actualizar (-1 si se desconoce, pero entonces es obligatorio el parametro date)
	 * @param date - Fecha del registro a actualizar en formato YYYYMMDD
	 * @param app - Nombre de la aplicacion
	 * @param rate - Valor a actualizar
	 * @return true if was successfully updated, false otherwise
	 */
	public static boolean updateAppUse(long id, String date, String app, long rate){

		boolean salida = false;
		try{
			ContentValues values = new ContentValues();
			values.put(COLUMN_RATE, rate);

			String where;
			if(id > -1)
				where = COLUMN_ID + " = " + id;
			else
				where = COLUMN_DATE + " = '" + date + "' AND " + COLUMN_APP + " = '" + app + "'";

			salida = SQLiteManager.getInstance().getDB().update(TABLE_NAME, values, where, null) > 0;
		}
		catch(Exception e){
			e.printStackTrace();
		}

		return salida;
	}

	public static void deleteAppUse(AppUseBean appUseBean) {
		long id = appUseBean.getId();
		System.out.println("AppUse deleted with id: " + id);
		SQLiteManager.getInstance().getDB().delete(TABLE_NAME, COLUMN_ID + " = " + id, null);
	}

	public static List<AppUseBean> getAllAppUses(String date) {
		List<AppUseBean> appUseBeans = new ArrayList<AppUseBean>();

		String order = COLUMN_DATE + " DESC, " + COLUMN_RATE + " DESC";
		String where = "";
		if(date != null && !date.equalsIgnoreCase(""))
			where = COLUMN_DATE + " = '" + date + "'";

		Cursor cursor = SQLiteManager.getInstance().getDB().query(TABLE_NAME, allColumns, where, null, null, null, order);
		if(cursor.moveToFirst()){
			AppUseBean appUseBean;
			while (!cursor.isAfterLast()) {
				appUseBean = cursorToAppUse(cursor);
				appUseBeans.add(appUseBean);
				cursor.moveToNext();
			}
		}

		// Make sure to close the cursor
		cursor.close();
		return appUseBeans;
	}

	public static LinkedHashMap<String, Integer> getAllAppUsesToHashMap(String date) {
		LinkedHashMap<String, Integer> appUseBeans = new LinkedHashMap<String, Integer>();

		String order = COLUMN_RATE + " DESC";
		String where = COLUMN_DATE + " = '" + date + "'";
		Cursor cursor = SQLiteManager.getInstance().getDB().query(TABLE_NAME, allColumns, where, null, null, null, order);
		if(cursor.moveToFirst()){
			while (!cursor.isAfterLast()) {
				appUseBeans.put(cursor.getString(2), cursor.getInt(3));
				cursor.moveToNext();
			}
		}

		// Make sure to close the cursor
		cursor.close();
		return appUseBeans;
	}

	private static AppUseBean cursorToAppUse(Cursor cursor) {
		AppUseBean appUseBean = new AppUseBean();
		appUseBean.setId(cursor.getLong(0));
		appUseBean.setDate(cursor.getString(1));
		appUseBean.setApp(cursor.getString(2));
		appUseBean.setRate(cursor.getLong(3));

		return appUseBean;
	}
	
	/**
	 * Devuelve los totales de uso de las aplicaciones
	 * @return LinkedHashMap<String, Long> En el string está el paquete de la aplicacion y en el Long, la suma total de la puntuación de la app
	 */
	public static LinkedHashMap<String, Long> getTotalAppsUse(int numOfApps){
		LinkedHashMap<String, Long> hmSalida = new LinkedHashMap<String, Long>();
		
		String[] fields = new String[2];
		fields[0] = COLUMN_APP;
		fields[1] = "SUM(" + COLUMN_RATE + ") AS TOTAL_RATE";
		
		String groupBy = COLUMN_APP;
		String orderBy = "TOTAL_RATE DESC";
		
		int counter = 0;
		Cursor cursor = SQLiteManager.getInstance().getDB().query(TABLE_NAME, fields, null, null, groupBy, null, orderBy);
		if(cursor.moveToFirst()){
			while (!cursor.isAfterLast() && counter < numOfApps) {
				hmSalida.put(cursor.getString(0), cursor.getLong(1));
				cursor.moveToNext();
				counter++;
			}
		}

		// Make sure to close the cursor
		cursor.close();
		
		return hmSalida;
	}
	
	/**
	 * Devuelve la suma de todos los rates de todas las apps de todos los dias
	 * @return
	 */
	public static long getTotalAppsScore(){
		long totalRates = -1;
		
		String[] fields = new String[1];
		fields[0] = "SUM(" + COLUMN_RATE + ")";
		
		Cursor cursor = SQLiteManager.getInstance().getDB().query(TABLE_NAME, fields, null, null, null, null, null);
		if(cursor.moveToFirst())
			totalRates = cursor.getLong(0);

		// Make sure to close the cursor
		cursor.close();
		
		return totalRates;
	}

}
