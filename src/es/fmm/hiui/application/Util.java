package es.fmm.hiui.application;

import java.text.DecimalFormat;
import java.util.Currency;
import java.util.LinkedHashMap;
import java.util.Set;

import es.fmm.hiui.R;
import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.WindowManager;

/**
 * Created by fmm on 9/15/13.
 */
public class Util {

	/**
	 * Takes a dip value and calculates its pixels correspondant value
	 * @param dip
	 * @return int - calculated pixels
	 */
	public static int convertDIPtoPX(Context context, int dip){
		float pixels = -1;
		pixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, context.getResources().getDisplayMetrics());
		return Math.round(pixels);
	}

	@Deprecated
	public static int getDeviceHightResolution(WindowManager wm) {
		return wm.getDefaultDisplay().getHeight();
	}

	@Deprecated
	public static int getDeviceWitdhResolution(WindowManager wm) {
		return wm.getDefaultDisplay().getWidth();
	}

	public static boolean isDeviceConnected() {
		/*ConnectivityManager cm = (ConnectivityManager) instance.getSystemService(Context.CONNECTIVITY_SERVICE);
		// test for connection
		NetworkInfo nInfo = cm.getActiveNetworkInfo();
		//if(nInfo.getType() == ConnectivityManager.TYPE_MOBILE);
		if (nInfo != null
				&& nInfo.isAvailable()
				&& nInfo.isConnected()) {

			return true;
		}
		else {
			Log.w(TAG, "Internet Connection Not Present");
			return false;
		}*/
		return false;
	}

	public static String getEmail(Context context) {
		/*AccountManager accountManager = AccountManager.get(context);
		Account account = getAccount(accountManager);

		if(account == null)
			return null;

		else
			return account.name;*/

		return null;
	}

	/*private static Account getAccount(AccountManager accountManager) {
		Account[] accounts = accountManager.getAccountsByType("com.google");
		Account account;

		if(accounts.length > 0)
			account = accounts[0];
		else
			account = null;

		return account;
	}*/

	public static String getDeviceId(){
		/*Log.d(IberiAndroid.TAG, "Utils - getDeviceId");

		String uid = null;
		SharedPreferences sp = IberiAndroid.getUserPreferences();
		uid = sp.getString(Constants.USER_PREFERENCES_DEVICE_ID, null);

		if(uid == null || uid.equalsIgnoreCase("")){
			Log.d(IberiAndroid.TAG, "Utils - getDeviceId - Recuperamos del teléfono el id unico");

			//Ask for the celular ID
			uid = Settings.Secure.getString(IberiAndroid.getContentResolverInstance(), Settings.Secure.ANDROID_ID);

			//If still not found an ID, ask for deprecated behave to get ID
			if(uid == null || uid.equalsIgnoreCase(""))
				uid = Settings.System.getString(IberiAndroid.getContentResolverInstance(), Settings.System.ANDROID_ID);

			//If still not found, ask for WiFi Mac ID
			if(uid == null || uid.equalsIgnoreCase("")){
				Object oAux = IberiAndroid.instance.getSystemService(Context.WIFI_SERVICE);
				if(oAux != null){
					WifiManager wm = (WifiManager) oAux;
					if(wm.getWifiState() == WifiManager.WIFI_STATE_ENABLED)
						uid = wm.getConnectionInfo().getMacAddress();
				}
			}

			//As a last resource, we try to get an ID from service provider. TelephonyManager.getDeviceId() is required to
			//return (depending on the network technology) the IMEI, MEID, or ESN of the phone, which is unique to that piece of hardware.
			if(uid == null || uid.equalsIgnoreCase("")){
				Object oAux = IberiAndroid.instance.getSystemService(Context.TELEPHONY_SERVICE);
				if(oAux != null){
					TelephonyManager myTelephonyManager = (TelephonyManager) oAux;
					String imei = myTelephonyManager.getDeviceId();
					uid = imei;
				}
			}
			//End of asking

			//Avoid Android 2.2 device id bug where some devices (Galaxy S) has the same distinct id
			if(uid != null && uid.equalsIgnoreCase("9774d56d682e549c")){
				Log.d(IberiAndroid.TAG, "Utils - getDeviceId - Bug 2.2 DeviceID(9774d56d682e549c) AFFECTED");
				uid = UUID.randomUUID().toString();
			}

			//If still not found an id, get a random one
			if(uid == null || uid.equalsIgnoreCase(""))
				uid = UUID.randomUUID().toString();

			Editor editor = sp.edit();
			editor.putString(Constants.USER_PREFERENCES_DEVICE_ID, uid);
			editor.commit();
		}

		Log.d(IberiAndroid.TAG, "Utils - getDeviceId - " + uid);

		return uid;*/

		return null;
	}

	public static String getCurrencySymbol(String currencyCode){
		return Currency.getInstance(currencyCode).getSymbol();
	}

	/**
	 * Devuelve el porcentaje de uso de una aplicación basado en las estadísticas almacenadas en un día concreto
	 * @param appKey - app sobre la que queremos saber el porcentaje
	 * @param dayAppsStats - estadisticas de uso de apps en un dia concreto
	 * @param numDecimals - Indica cuantos decimales queremos mostrar
	 * @return
	 */
	public static String getAppPercentageOfUse(String appKey, LinkedHashMap<String, Integer> dayAppsStats, int numDecimals){
		Integer appUse = dayAppsStats.get(appKey);

		Integer totalUse = 0;

		Set<String> set = dayAppsStats.keySet();
		for (String key : set) {
			totalUse += dayAppsStats.get(key);
		}

		double percentage = (appUse*100.0)/totalUse;

		return parsePercentage(percentage, numDecimals);
	}
	
	/**
	 * Parses milliseconds to tima in format XXh: XXm: XXs
	 * @param milliseconds
	 * @param resources - recursos de contexto para sacar textos de idioma (puede ser nulo, pero no habrá textos)
	 * @param completeText - Indica si queremos el texto horas minutos y segundos completo o sólo h m s
	 * @param withSpaces - Indica si queremos que haya un espacio entre cada texto
	 * @return
	 */
	public static String millisecondsToTimeFormat(long milliseconds, Resources resources, boolean completeText, boolean withSpaces){
		
		String space = withSpaces?" ":"";
		
		String time = null;
		
		int seconds = (int) (milliseconds / 1000) % 60 ;
		int minutes = (int) ((milliseconds / (1000*60)) % 60);
		int hours   = (int) ((milliseconds / (1000*60*60)));
		
		if(completeText && resources != null)
			time = hours + space + resources.getString(R.string.others_hours) + space + ":" + space + minutes + space + resources.getString(R.string.others_minutes) + space + ":" + space + seconds + space + resources.getString(R.string.others_seconds);
		else
			time = hours + space + "h" + space + ":" + space + minutes + space + "m" + space + ":" + space + seconds + space + "s";
		
		return time;
	}
	
	/**
	 * Método que recoge un double que es el porcentaje de un cálculo y lo devuelve como String formateado
	 * @param value
	 * @param numDecimals - Indica cuantos decimales queremos mostrar
	 * @return
	 */
	public static String parsePercentage(double value, int numDecimals){
		DecimalFormat df;
		switch(numDecimals){
			case 0:
				df = new DecimalFormat("##");
				break;
			case 1:
				df = new DecimalFormat("##.#");
				break;
			default:
				df = new DecimalFormat("##");
				break;
		}

		return df.format(value);
	}

}
