package es.fmm.hiui.ddbb;

import java.sql.SQLException;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.util.Log;

import es.fmm.hiui.R;
import es.fmm.hiui.application.Main;
import es.fmm.hiui.statistics.TodayStats;

/**
 * Created by fmm on 8/2/13.
 */
public class SQLiteManager {

	private static SQLiteManager instance = null;

	private static Context context;
	private static SQLiteHelper helper;
	private static SQLiteDatabase db;

	public static boolean isInUse = false;

	/**
	 * Constructor
	 * @param ctx
	 */
	private SQLiteManager(Context ctx) {
		context = ctx;
		helper = new SQLiteHelper(context);
	}

	/**
	 * Opens DB
	 * @return this
	 * @throws java.sql.SQLException
	 */
	public void write() throws SQLException {
		db = helper.getWritableDatabase();
	}

	/**
	 * Opens DB
	 * @return this
	 * @throws SQLException
	 */
	public void read() throws SQLException {
		db = helper.getReadableDatabase();
	}

	public synchronized SQLiteDatabase openDB(boolean writable, Context context) throws SQLException {
		Log.d(SQLiteManager.class.getSimpleName(), "SQLiteManager - openDB");
		if(doIHaveStorageUsePermission(context)){
			try{
				while(isInUse)
					wait();

				isInUse = true;

				if(db == null || !db.isOpen()){
					if(writable)
						write();
					else
						read();
				}
				else if(db.isOpen() && (writable && db.isReadOnly())){
					helper.close();
					write();
				}
			}
			catch(InterruptedException ie){
				ie.printStackTrace();
			}
		}

		return db;
	}

	/**
	 * Closes DB
	 */
	public synchronized void closeDB() {
		Log.d(SQLiteManager.class.getSimpleName(), "SQLiteManager - closeDB");
		try{
			helper.close();
			isInUse = false;
		}
		finally{
			notify();
		}
	}

	public SQLiteDatabase getDB(){
		return db;
	}

	/**
	 * This returns the instance of MainDBManager
	 * @return
	 */
	public static SQLiteManager getInstance(){
		if(instance == null && context != null)
			instance = new SQLiteManager(context);

		return instance;
	}

	/**
	 * This must be called once when App starts
	 * @param ctx
	 * @return
	 */
	public static SQLiteManager getInstance(Context ctx){
		instance = new SQLiteManager(ctx);
		return instance;
	}

	private static boolean doIHaveStorageUsePermission(Context context){
		// Assume thisActivity is the current activity
		int permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
		if(permissionCheck == PackageManager.PERMISSION_GRANTED){
			return true;
		}
		else{
			PendingIntent appIntent = PendingIntent.getActivity(context, 0, new Intent(context, Main.class), 0);
			Notification notification = new Notification.Builder(context)
					.setContentTitle("Permisos")
					.setContentText("Dame permisos de almacenamiento")
					.setSmallIcon(R.drawable.ic_launcher)
					.setDefaults(Notification.DEFAULT_ALL)
					.setContentIntent(appIntent)
					.build();

			NotificationManager notificationManager = (NotificationManager) context.getSystemService(Service.NOTIFICATION_SERVICE);
			notificationManager.notify(3, notification);
			return false;
		}

	}
}
