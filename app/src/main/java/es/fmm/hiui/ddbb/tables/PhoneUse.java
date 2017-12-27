package es.fmm.hiui.ddbb.tables;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import es.fmm.hiui.ddbb.SQLiteManager;
import es.fmm.hiui.ddbb.tables.beans.PhoneUseBean;

/**
 * Created by fmm on 8/2/13.
 */
public class PhoneUse {

	public static final String TABLE_NAME = "phone_use";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_DATE = "date"; //Dia
	public static final String COLUMN_TIMES = "times"; //usos
	public static final String COLUMN_TIME_AMOUNT = "time_amount"; //tiempo de uso
	public static final String COLUMN_WEEK_NUMBER = "week_number"; //numero de semana en formato yyyy y después el numero de semana


	private static String[] allColumns = { COLUMN_ID, COLUMN_DATE, COLUMN_TIMES, COLUMN_TIME_AMOUNT, COLUMN_WEEK_NUMBER};

	// Table creation sql statement
	public static final String SQL_TABLE_CREATE = "create table " + TABLE_NAME + "(" + COLUMN_ID + " integer primary key autoincrement, " + COLUMN_DATE + " text not null, " + COLUMN_TIMES + " integer default(0) not null, " + COLUMN_TIME_AMOUNT + " integer default(0) not null, " + COLUMN_WEEK_NUMBER + " integer default(0) not null);";

	/**
	 * @param id - Id del registro a consultar (-1 si se desconoce, pero entonces es obligatorio el parametro date)
	 * @param date - Fecha del registro a consultar en formato YYYYMMDD
	 * @return PhoneUseBean or null if not found
	 */
	public static PhoneUseBean getPhoneUse(long id, String date) {
		PhoneUseBean pub = null;
		Cursor mCursor = null;
		try{
			String where;
			if(id > -1)
				where = COLUMN_ID + " = " + id;
			else
				where = COLUMN_DATE + " = '" + date + "'";

			mCursor = SQLiteManager.getInstance().getDB().query(true, TABLE_NAME, new String[] { COLUMN_ID, COLUMN_DATE, COLUMN_TIMES, COLUMN_TIME_AMOUNT }, where, null, null, null, null, null);
			if (mCursor != null && mCursor.moveToFirst())
				pub = cursorToPhoneUse(mCursor);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			if(mCursor != null)
				mCursor.close();
		}

		return pub;
	}

	public static PhoneUseBean createPhoneUse(String date, long times, long timeAmount) {
		PhoneUseBean pub = getPhoneUse(-1, date);
		try{
			if(pub == null){
				ContentValues values = new ContentValues();
				values.put(COLUMN_DATE, date);
				values.put(COLUMN_TIMES, times);
				values.put(COLUMN_TIME_AMOUNT, timeAmount);

				SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
				Calendar cal = Calendar.getInstance();
				cal.setTime(df.parse(date));

				values.put(COLUMN_WEEK_NUMBER, cal.get(Calendar.YEAR) + "" + cal.get(Calendar.WEEK_OF_YEAR));

				long insertId = SQLiteManager.getInstance().getDB().insert(TABLE_NAME, null, values);
				pub = getPhoneUse(insertId, null);
			}
			else{
				boolean updated = updatePhoneUse(-1, date, times, timeAmount);
				if(updated)
					pub = getPhoneUse(-1, date);
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}

		return pub;
	}

	/**
	 * Actualiza el registro en base de datos
	 * @param id - Id del registro a actualizar (-1 si se desconoce, pero entonces es obligatorio el parametro date)
	 * @param date - Fecha del registro a actualizar en formato YYYYMMDD
	 * @param times - Valor a actualizar
	 * @param timeAmount - Valor a actualizar
	 * @return true if was successfully updated, false otherwise
	 */
	public static boolean updatePhoneUse(long id, String date, long times, long timeAmount){

		boolean salida = false;
		try{
			ContentValues values = new ContentValues();
			values.put(COLUMN_TIMES, times);
			values.put(COLUMN_TIME_AMOUNT, timeAmount);

			String where;
			if(id > -1)
				where = COLUMN_ID + " = " + id;
			else
				where = COLUMN_DATE + " = '" + date + "'";

			salida = SQLiteManager.getInstance().getDB().update(TABLE_NAME, values, where, null) > 0;
		}
		catch(Exception e){
			e.printStackTrace();
		}

		return salida;
	}

	public static void deletePhoneUse(PhoneUseBean phoneUseBean) {
		long id = phoneUseBean.getId();
		System.out.println("PhoneUse deleted with id: " + id);
		SQLiteManager.getInstance().getDB().delete(TABLE_NAME, COLUMN_ID + " = " + id, null);
	}

	public static void deletePhoneUse(String date) {
		System.out.println("PhoneUse deleted for date: " + date);
		SQLiteManager.getInstance().getDB().delete(TABLE_NAME, COLUMN_DATE + " = " + date, null);
	}

	/**
	 * Obtiene todos los usos del teléfono para el año y mes indicado indicado
	 * @return
	 */
	public static List<PhoneUseBean> getPhoneUses(String year, String month) {
		List<PhoneUseBean> phoneUseBeans = new ArrayList<>();

		String where = COLUMN_DATE + " LIKE '" + year + "" + month + "%'";
		String order = COLUMN_DATE + " DESC, " + COLUMN_TIME_AMOUNT + " DESC";

		Cursor cursor = SQLiteManager.getInstance().getDB().query(TABLE_NAME, allColumns, where, null, null, null, order);
		if(cursor.moveToFirst()){
			PhoneUseBean phoneUseBean;
			while (!cursor.isAfterLast()) {
				phoneUseBean = cursorToPhoneUse(cursor);
				phoneUseBeans.add(phoneUseBean);
				cursor.moveToNext();
			}
		}

		// Make sure to close the cursor
		cursor.close();

		return phoneUseBeans;
	}

	private static PhoneUseBean cursorToPhoneUse(Cursor cursor) {
		PhoneUseBean phoneUseBean = new PhoneUseBean();
		phoneUseBean.setId(cursor.getLong(0));
		phoneUseBean.setDate(cursor.getString(1));
		phoneUseBean.setTimes(cursor.getLong(2));
		phoneUseBean.setTimeAmount(cursor.getLong(3));

		return phoneUseBean;
	}

	/**
	 * Devuelve los totales de uso del teléfono
	 * @return
	 * String[0] - Total time (milliseconds)<br>
	 * String[1] - Total times<br>
	 * String[2] - Total days<br>
	 */
	public static String[] getAllTimeUse(){
		String[] fields = new String[3];
		fields[0] = "SUM(" + COLUMN_TIME_AMOUNT + ")";
		fields[1] = "SUM(" + COLUMN_TIMES+ ")";
		fields[2] = "COUNT(*)";

		Cursor cursor = SQLiteManager.getInstance().getDB().query(TABLE_NAME, fields, null, null, null, null, null);
		if(cursor.moveToFirst()){
			fields[0] = (cursor.getLong(0)) + "";
			fields[1] = (cursor.getInt(1) + "");
			fields[2] = cursor.getInt(2) + "";
		}

		// Make sure to close the cursor
		cursor.close();

		return fields;
	}

	/**
	 * Nos dice cual ha sido el mayor número de veces que hemos utilizado el móvil en un día
	 * @return
	 */
	public static int mostUsesInADay(){
		int uses = -1;

		String[] fields = new String[1];
		fields[0] = "MAX(" + COLUMN_TIMES+ ")";

		Cursor cursor = SQLiteManager.getInstance().getDB().query(TABLE_NAME, fields, null, null, null, null, null);
		if(cursor.moveToFirst())
			uses = cursor.getInt(0);

		// Make sure to close the cursor
		cursor.close();

		return uses;
	}

	/**
	 * Nos dice cual ha sido el mayor número de veces que hemos utilizado el móvil en un día
	 * @param year Si le pasamos el año, nos saca la media del año solicitado. Si no se le pasa (se pasa un -1), hace la media global
	 * @return
	 */
	public static int averageUsesPerDay(int year){
		int uses = -1;

		String[] fields = new String[1];
		fields[0] = "AVG(" + COLUMN_TIMES + ")";

		String where = "";
		if(year != -1)
			where += "substr(" + COLUMN_DATE + ", 1, 4) = '" + year + "'";

		Cursor cursor = SQLiteManager.getInstance().getDB().query(TABLE_NAME, fields, where, null, null, null, null);
		if(cursor.moveToFirst())
			uses = cursor.getInt(0);

		// Make sure to close the cursor
		cursor.close();

		return uses;
	}

	/**
	 * Nos dice cual ha sido la media de usos por semana durante un año concreto, o globalmente, dependiento del parámetro year
	 * @param year Si le pasamos el año, nos saca la media del año solicitado. Si no se le pasa (se pasa un -1), hace la media global
	 * @return
	 */
	public static int averageUsesPerWeek(int year){
		int uses = -1;

		String[] fields = new String[1];
		fields[0] = "AVG(SUM(" + COLUMN_TIMES+ "))";

		String where = "";
		if(year != -1)
			where += "substr(" + COLUMN_DATE + ", 1, 4) = '" + year + "'";

		String groupBy = COLUMN_WEEK_NUMBER;

		Cursor cursor = SQLiteManager.getInstance().getDB().query(TABLE_NAME, fields, where, null, groupBy, null, null);
		if(cursor.moveToFirst())
			uses = cursor.getInt(0);

		// Make sure to close the cursor
		cursor.close();

		return uses;
	}

	/**
	 * Nos dice la suma más alta de usos en una semana
	 * @param year Si le pasamos el año, nos saca la media del año solicitado. Si no se le pasa (se pasa un -1), hace la media global
	 * @return
	 */
	public static int mostUsesInAWeek(int year){
		int uses = -1;

		String[] fields = new String[1];
		fields[0] = "MAX(SUM(" + COLUMN_TIMES+ "))";

		String where = "";
		if(year != -1)
			where += "substr(" + COLUMN_DATE + ", 1, 4) = '" + year + "'";

		String groupBy = COLUMN_WEEK_NUMBER;

		Cursor cursor = SQLiteManager.getInstance().getDB().query(TABLE_NAME, fields, where, null, groupBy, null, null);
		if(cursor.moveToFirst())
			uses = cursor.getInt(0);

		// Make sure to close the cursor
		cursor.close();

		return uses;
	}

	/**
	 * Nos dice el tiempo de uso del móvil del día que más lo hemos usado
	 * @param year Si le pasamos el año, nos saca la media del año solicitado. Si no se le pasa (se pasa un -1), hace la media global
	 * @return
	 */
	public static long mostTimeInADay(int year){
		long time = -1;

		String[] fields = new String[1];
		fields[0] = "MAX(" + COLUMN_TIME_AMOUNT + ")";

		String where = "";
		if(year != -1)
			where += "substr(" + COLUMN_DATE + ", 1, 4) = '" + year + "'";

		Cursor cursor = SQLiteManager.getInstance().getDB().query(TABLE_NAME, fields, where, null, null, null, null);
		if(cursor.moveToFirst())
			time = cursor.getLong(0);

		// Make sure to close the cursor
		cursor.close();

		return time;
	}

	/**
	 * Nos dice la media de tiempo de uso del movil por días
	 * @param year Si le pasamos el año, nos saca la media del año solicitado. Si no se le pasa (se pasa un -1), hace la media global
	 * @return
	 */
	public static long averageTimePerDay(int year){
		long averageTime = -1;

		String[] fields = new String[1];
		fields[0] = "AVG(" + COLUMN_TIME_AMOUNT + ")";

		String where = "";
		if(year != -1)
			where += "substr(" + COLUMN_DATE + ", 1, 4) = '" + year + "'";

		long totalTime = 0;
		Cursor cursor = SQLiteManager.getInstance().getDB().query(TABLE_NAME, fields, where, null, null, null, null);
		if(cursor.getCount() > 0) {
			while(cursor.moveToNext()){
				totalTime += cursor.getLong(0);
			}

			averageTime = totalTime / cursor.getCount();
		}

		// Make sure to close the cursor
		cursor.close();

		return averageTime;
	}

	/**
	 * Nos dice la media de uso del móvil por semanas
	 * @param year Si le pasamos el año, nos saca la media del año solicitado. Si no se le pasa (se pasa un -1), hace la media global
	 * @return
	 */
	public static long averageTimePerWeek(int year){
		long averageTime = -1;

		String[] fields = new String[1];
		fields[0] = "AVG(SUM(" + COLUMN_TIME_AMOUNT + "))";

		String where = "";
		if(year != -1)
			where += "substr(" + COLUMN_DATE + ", 1, 4) = '" + year + "'";

		String groupBy = COLUMN_WEEK_NUMBER;

		long totalTime = 0;
		Cursor cursor = SQLiteManager.getInstance().getDB().query(TABLE_NAME, fields, where, null, groupBy, null, null);
		if(cursor.getCount() > 0) {
			while(cursor.moveToNext()){
				totalTime += cursor.getLong(0);
			}

			averageTime = totalTime / cursor.getCount();
		}

		// Make sure to close the cursor
		cursor.close();

		return averageTime;
	}

	/**
	 * Nos dice el tiempo de uso del móvil de la semana que más lo hemos usado
	 * @param year Si le pasamos el año, nos saca la media del año solicitado. Si no se le pasa (se pasa un -1), hace la media global
	 * @return
	 */
	public static long mostTimeInAWeek(int year){
		long mostTime = -1;

		String[] fields = new String[1];
		fields[0] = "MAX(SUM(" + COLUMN_TIME_AMOUNT + "))";

		String where = "";
		if(year != -1)
			where += "substr(" + COLUMN_DATE + ", 1, 4) = '" + year + "'";

		String groupBy = COLUMN_WEEK_NUMBER;

		Cursor cursor = SQLiteManager.getInstance().getDB().query(TABLE_NAME, fields, where, null, groupBy, null, null);
		if(cursor.moveToFirst())
			mostTime = cursor.getLong(0);

		// Make sure to close the cursor
		cursor.close();

		return mostTime;
	}

}
