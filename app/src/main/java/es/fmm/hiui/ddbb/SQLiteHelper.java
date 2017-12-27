package es.fmm.hiui.ddbb;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import es.fmm.hiui.ddbb.tables.AppsUse;
import es.fmm.hiui.ddbb.tables.PhoneUse;

/**
 * Created by fmm on 7/31/13.
 */
public class SQLiteHelper extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION_FIRST = 1; //PRIMERA VERSION DE LA APLICACION
	private static final int DATABASE_VERSION_UPDATE_1 = 2; //PRIMERA GRAN ACTUALIZACION
	private static final String DATABASE_NAME;

	private static final String PHONE_USE_NEW_COLUMN_WEEK_NUMBER = "ALTER TABLE " + PhoneUse.TABLE_NAME + " ADD COLUMN " + PhoneUse.COLUMN_WEEK_NUMBER + " integer default 0 not null";
	private static final String PHONE_USE_NEW_COLUMN_WEEK_NUMBER_UPDATING_PREVIOUS_DATA = "UPDATE " + PhoneUse.TABLE_NAME + " SET " + PhoneUse.COLUMN_WEEK_NUMBER + " = (substr(" + PhoneUse.COLUMN_DATE + ", 1, 4) || strftime('%W', substr(" + PhoneUse.COLUMN_DATE + ", 1, 4) || '-' || substr(" + PhoneUse.COLUMN_DATE + ", 5, 2) || '-' || substr(" + PhoneUse.COLUMN_DATE + ", 7)))";

	static{
		//TODO: Crear una preferencia que nos indique dónde está creada la BBDD una vez que lo está
		//para que cuando se desmonte la SD no vuelva a crearla en otro sitio, o avise al usuario
		if(Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
			DATABASE_NAME = Environment.getExternalStorageDirectory()+"/hiui/hiui.db";
		else
			DATABASE_NAME = "/hiui/hiui.db";
	}

	public SQLiteHelper(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION_UPDATE_1);
	}

	@Override
	public void onCreate(SQLiteDatabase sqLiteDatabase) {
		sqLiteDatabase.execSQL(AppsUse.SQL_TABLE_CREATE);
		sqLiteDatabase.execSQL(PhoneUse.SQL_TABLE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
		if(oldVersion == DATABASE_VERSION_FIRST && newVersion == DATABASE_VERSION_UPDATE_1){
			sqLiteDatabase.execSQL(PHONE_USE_NEW_COLUMN_WEEK_NUMBER);
			sqLiteDatabase.execSQL(PHONE_USE_NEW_COLUMN_WEEK_NUMBER_UPDATING_PREVIOUS_DATA);
		}
	}

	@Override
	public void onOpen(SQLiteDatabase db){
		super.onOpen(db);
		if (!db.isReadOnly()){ // Enable foreign key constraints
			db.execSQL("PRAGMA foreign_keys=ON;");
		}
	}

	@Override
	public synchronized void close() {
		super.close();
	}

}
