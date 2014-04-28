package es.fmm.hiui.ddbb;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

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

	public synchronized SQLiteDatabase openDB(boolean writable) throws SQLException {
		Log.d(SQLiteManager.class.getSimpleName(), "SQLiteManager - openDB");
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
}
