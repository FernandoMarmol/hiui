package es.fmm.hiui.em;

import java.util.LinkedList;

import es.fmm.hiui.constants.Launchers;

/**
 * Created by fmm on 8/10/13.
 */
public class AppsEM {

	private static LinkedList<String> NON_SHOWING_APPS;

	public static final String APP_SYSTEMUI = "com.android.systemui";

	static{
		NON_SHOWING_APPS = new LinkedList<String>();
		NON_SHOWING_APPS.add(APP_SYSTEMUI); //System UI
	}

	/**
	 * Nos dice si la aplicación está entre las que no queremos que aparezcan en la lista que mostramos al usuario
	 * @param appPackageName
	 * @return
	 */
	public static boolean isAppNotIncludedInStats(String appPackageName){
		if(NON_SHOWING_APPS.contains(appPackageName) || Launchers.LAUNCHERS_LIST.contains(appPackageName) || appPackageName.indexOf("launcher") != -1)
			return true;
		else
			return false;
	}
	
	/**
	 * Nos dice si la aplicación es un launcher
	 * @param appPackageName
	 * @return
	 */
	public static boolean isAppLauncher(String appPackageName){
		if(Launchers.LAUNCHERS_LIST.contains(appPackageName) || appPackageName.indexOf("launcher") != -1)
			return true;
		else
			return false;
	}

}
