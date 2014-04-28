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

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME;

	static{
		//TODO: Crear una preferencia que nos indique dónde está creada la BBDD una vez que lo está
		//para que cuando se desmonte la SD no vuelva a crearla en otro sitio, o avise al usuario
		if(Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
			DATABASE_NAME = Environment.getExternalStorageDirectory()+"/hiui/hiui.db";
		else
			DATABASE_NAME = "/hiui/hiui.db";
	}

	public SQLiteHelper(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase sqLiteDatabase) {
		sqLiteDatabase.execSQL(AppsUse.SQL_TABLE_CREATE);
		sqLiteDatabase.execSQL(PhoneUse.SQL_TABLE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
		//Future
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
