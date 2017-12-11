package es.fmm.hiui.statistics;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import es.fmm.hiui.R;
import es.fmm.hiui.adapters.StatisticsAdapter;
import es.fmm.hiui.ddbb.SQLiteManager;
import es.fmm.hiui.ddbb.tables.PhoneUse;
import es.fmm.hiui.ddbb.tables.beans.PhoneUseBean;
import es.fmm.hiui.settings.Settings;

/**
 * Actividad que muestra el histórico de las estadísticas almacenadas en la BBDD del uso del movil
 */
public class Statistics extends Activity {
	
	private static Statistics instance;
	
	private static String monthNum;
	private static String monthName;
	private static String year;
	
	private Spinner spinnerMonth;
	private Spinner spinnerYear;
	
	private static final SimpleDateFormat sdfMNum = new SimpleDateFormat("MM", Locale.getDefault());
	private static final SimpleDateFormat sdfMName = new SimpleDateFormat("MMMM", Locale.getDefault());
	private static final SimpleDateFormat sdfY = new SimpleDateFormat("yyyy", Locale.getDefault());
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        instance = this;
        
        setContentView(R.layout.statistics_main);
        
        if(year == null){
	        Date date = new Date();
			
			monthNum = sdfMNum.format(date);
			monthName = sdfMName.format(date);
			year = sdfY.format(date);
    	}
        
        if(spinnerYear == null){
        	spinnerMonth = (Spinner) findViewById(R.id.spinnerMonths);
    		spinnerYear = (Spinner) findViewById(R.id.spinnerYears);
        }
        
        spinnerMonth.setSelection(getPositionInArray(getResources().getStringArray(R.array.monthsOfYear), monthName));
		spinnerYear.setSelection(getPositionInArray(getResources().getStringArray(R.array.years), year));

		spinnerYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
		    @Override
		    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
		    	Statistics.year = spinnerYear.getSelectedItem().toString();
		    	//Transaccion BBDD
				List<PhoneUseBean> pubs;
				try{
					SQLiteManager.getInstance().openDB(false, instance.getApplicationContext());
					pubs = PhoneUse.getPhoneUses(year, monthNum);
					
					StatisticsAdapter adapter = new StatisticsAdapter(Statistics.instance, pubs);
					ListView lv = (ListView) findViewById(R.id.statisticsLV);
					lv.setAdapter(adapter);
					
					lv.invalidate();
				}
				catch(Exception e){
					e.printStackTrace();
				}
				finally{
					SQLiteManager.getInstance().closeDB();
				}
		    }

		    @Override
		    public void onNothingSelected(AdapterView<?> parentView) {
		        // your code here
		    }

		});
		
		spinnerMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
		    @Override
		    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
		    	Statistics.monthName = spinnerMonth.getSelectedItem().toString();
		    	Statistics.monthNum = (((position+1)+"").length()==1)?("0"+(position+1)):((position+1)+"");
		    	//Transaccion BBDD
				List<PhoneUseBean> pubs;
				try{
					SQLiteManager.getInstance().openDB(false, instance.getApplicationContext());
					pubs = PhoneUse.getPhoneUses(year, monthNum);
					
					StatisticsAdapter adapter = new StatisticsAdapter(Statistics.instance, pubs);
					ListView lv = (ListView) findViewById(R.id.statisticsLV);
					lv.setAdapter(adapter);
					
					lv.invalidate();
				}
				catch(Exception e){
					e.printStackTrace();
				}
				finally{
					SQLiteManager.getInstance().closeDB();
				}
		    }

		    @Override
		    public void onNothingSelected(AdapterView<?> parentView) {
		        // your code here
		    }

		});
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.statistics, menu);
        return true;
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem menu) {
		switch (menu.getItemId()) {
			case R.id.menuAbout:
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage(getString(R.string.about_text))
						.setCancelable(false)
						.setNeutralButton(getString(R.string.about_market_button), new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {
								String appPackageName= getPackageName();
								Intent marketIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName));
								marketIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
								startActivity(marketIntent);
							}
						})
						.setPositiveButton(getString(R.string.about_ok_button), new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
				AlertDialog alert = builder.create();
				alert.show();
				return true;

			case R.id.menuSettings:
				Intent settingsIntent = new Intent(this, Settings.class);
				settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
				startActivity(settingsIntent);
				
				return true;
			default:
				return true;
		}
	}
	
	private int getPositionInArray(String[] array, String value){
		for(int i=0; i<array.length; i++){
			if(array[i].equalsIgnoreCase(value))
				return i;
		}
		
		return -1;
	}
}
