package es.fmm.hiui.application;

import java.util.HashMap;
import java.util.Set;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import es.fmm.hiui.R;
import es.fmm.hiui.custom.ChronoTextView;
import es.fmm.hiui.ddbb.SQLiteManager;
import es.fmm.hiui.ddbb.tables.AppsUse;
import es.fmm.hiui.ddbb.tables.PhoneUse;

public class Main extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hiui_main);
		
		//Transaccion BBDD
		try{
			PackageManager pm = getPackageManager(); 
			
			SQLiteManager.getInstance().openDB(false);
			String[] data = PhoneUse.getAllTimeUse();
			HashMap<String, Long> hmData = AppsUse.getTotalAppsUse(20);
			long totalRates = AppsUse.getTotalAppsScore();
			
			TextView tv = (TextView) findViewById(R.id.timeTitle);
			tv.setText(getString(R.string.main_time));
			
			tv = (TextView) findViewById(R.id.time);
			tv.setText(Util.millisecondsToTimeFormat(Long.parseLong(data[0]), getResources(), true, true));
			
			tv = (TextView) findViewById(R.id.daysTitle);
			tv.setText(getString(R.string.main_days));
			
			tv = (TextView) findViewById(R.id.days);
			tv.setText(data[2]);
			
			tv = (TextView) findViewById(R.id.timesTitle);
			tv.setText(getString(R.string.main_times));
			
			tv = (TextView) findViewById(R.id.times);
			tv.setText(data[1]);
			
			//tv.setText("Tiempo de uso - " + Util.millisecondsToTimeFormat(Long.parseLong(data[0])) + "\n\rVeces que has utilizado el telÃ©fono - " + data[1] + "\n\rDÃ­as que lo has usado - " + data[2]);
			
			TableLayout tl = (TableLayout) findViewById(R.id.appsLayout);
			TableLayout.LayoutParams tlp = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT);
			tlp.setMargins(0, 0, 0, 20);
			
			TableRow.LayoutParams tlLP2 = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 1f);
			
			LinearLayout ll = null;
			LinearLayout.LayoutParams llLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			
			TextView tvAux = null;
			ImageView ivAux = null;
			
			int columnsLeftCounter = 0;
			TableRow tr = new TableRow(this);
			Set<String> set = hmData.keySet();
			for (final String key : set) {
				ll = new LinearLayout(this);
				ll.setOrientation(LinearLayout.VERTICAL);
				ll.setBackgroundResource(R.drawable.border_apps);
				
				try{
					if(!key.equalsIgnoreCase("com.hemispheregames.osmos")){
						Log.d(Main.class.getSimpleName(), key);
						
						ivAux = new ImageView(this);
						ivAux.setImageDrawable(pm.getApplicationIcon(pm.getApplicationInfo(key, PackageManager.GET_META_DATA)));
						ivAux.setOnClickListener(new View.OnClickListener() {
							
							@Override
							public void onClick(View v) {
								Intent i;
								PackageManager manager = getPackageManager();
								try {
								    i = manager.getLaunchIntentForPackage(key);
								    if(i != null){
								    	i.addCategory(Intent.CATEGORY_LAUNCHER);
								    	startActivity(i);
								    }
								}
								catch(Exception e){ }
							}
						});
					
						ll.addView(ivAux, llLP);
					}
				}
				catch(Exception e){ //Si la app está desinstalada da excepcion al hacer el pm.getApplicationInfo, en ese caso pintamos el paquete y listo
					tvAux = new TextView(this);
					tvAux.setText(getString(R.string.main_uninstalled));
					tvAux.setGravity(Gravity.CENTER_HORIZONTAL);
					tvAux.setMaxLines(3);

					ll.addView(tvAux, llLP);
				}
				
				tvAux = new ChronoTextView(this);
				tvAux.setText(Util.parsePercentage((double)((hmData.get(key)*100.0)/totalRates),1)+" %");
				tvAux.setTextSize(24);
				tvAux.setGravity(Gravity.CENTER_HORIZONTAL);
				ll.addView(tvAux, llLP);
				
				columnsLeftCounter++;
				
				tr.addView(ll, tlLP2);
				
				if(columnsLeftCounter == 5){
					tl.addView(tr, tlp);
					tr = new TableRow(this);
					columnsLeftCounter = 0;
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			SQLiteManager.getInstance().closeDB();
		}
	}

}
