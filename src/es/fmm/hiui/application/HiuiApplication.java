package es.fmm.hiui.application;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Application;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import es.fmm.hiui.R;
import es.fmm.hiui.ddbb.SQLiteManager;
import es.fmm.hiui.services.WidgetPersistentService;

/**
 * Created by fmm on 8/2/13.
 */
public class HiuiApplication extends Application {

	public static HiuiApplication instance;

	@Override
	public void onTerminate() {
		super.onTerminate();
	}

	/**
	 * Constructor
	 */
	public HiuiApplication(){
		super();
	}

	/**
	 * Initializes all values needed for the correct loading of the app
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		
		instance = this;

		WidgetPersistentService.activateOOR(this);

		/*
		 * Esto solo se ejecuta la primera vez que arranca la aplicación
		 */
		if(PreferenceManager.getDefaultSharedPreferences(instance).getAll().isEmpty()){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
			Date date = new Date();
			Editor editor = PreferenceManager.getDefaultSharedPreferences(instance).edit();
			editor.putString(getString(R.string.sett_installation_day), sdf.format(date));
			editor.commit();
		}

		SQLiteManager.getInstance(instance); //Do instantiate DB Manager
	}

}
