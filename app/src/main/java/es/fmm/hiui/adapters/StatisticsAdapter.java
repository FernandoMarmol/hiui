package es.fmm.hiui.adapters;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import es.fmm.hiui.R;
import es.fmm.hiui.application.Util;
import es.fmm.hiui.ddbb.SQLiteManager;
import es.fmm.hiui.ddbb.tables.AppsUse;
import es.fmm.hiui.ddbb.tables.beans.AppUseBean;
import es.fmm.hiui.ddbb.tables.beans.PhoneUseBean;

public class StatisticsAdapter extends ArrayAdapter<PhoneUseBean> {

	public static StatisticsAdapter instance;

	private final Context context;
	private final List<PhoneUseBean> values;

	private static int SELECTED_DAY_POSITION = -1;

	public StatisticsAdapter(Context context, List<PhoneUseBean> values) {
		super(context, android.R.layout.simple_list_item_1, values);
		this.context = context;
		this.values = values;

		instance = this;
	}
	
	@Override
	public int getCount() {
		return values.size();
	}

	@Override
	public PhoneUseBean getItem(int position) {
		return values.get(position);
	}

	@Override
	public long getItemId(int position) {
		return values.get(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		PackageManager pm = context.getPackageManager();
		PhoneUseBean pub = getItem(position);

		View rowView = null;
		if(position == SELECTED_DAY_POSITION)
			rowView = LayoutInflater.from(context).inflate(R.layout.statistics_adapter_l2, parent, false);
		else
			rowView = LayoutInflater.from(context).inflate(R.layout.statistics_adapter_l1, parent, false);

		TextView dayTextView = (TextView) rowView.findViewById(R.id.statsDayText);
		TextView timeTextView = (TextView) rowView.findViewById(R.id.statsTimeText);
		TextView timesTextView = (TextView) rowView.findViewById(R.id.statsTimesText);

		SimpleDateFormat sdfFrom = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
		SimpleDateFormat sdfTo = new SimpleDateFormat("d MMM yy", Locale.getDefault());

		//fecha
		try{
			Date date = sdfFrom.parse(pub.getDate());
			dayTextView.setText(sdfTo.format(date));
		}
		catch(Exception e){
			dayTextView.setText("Err.");
		}

		long milisTime = pub.getTimeAmount();
		timeTextView.setText(context.getString(R.string.statistics_row_time) + Util.millisecondsToTimeFormat(milisTime, context.getResources(), false, true));
		timesTextView.setText(context.getString(R.string.statistics_row_times) + pub.getTimes());

		/*RelativeLayout mainLayout = (RelativeLayout) rowView.findViewById(R.id.statsAdapterLayout);
		RelativeLayout.LayoutParams lp;

		if(position == SELECTED_DAY_POSITION){
			LinearLayout ll = new LinearLayout(context);
			ll.setOrientation(LinearLayout.VERTICAL);
			ll.setVisibility(LinearLayout.INVISIBLE);

			lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
			lp.topMargin = Util.convertDIPtoPX(context, 8);
			lp.addRule(RelativeLayout.BELOW, R.id.mainInfo);
			lp.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);

			//Transaccion BBDD
			List<AppUseBean> aubs;
			try{
				SQLiteManager.getInstance().openDB(false);
				aubs = AppsUse.getAllAppUses(pub.getDate());

				TextView tv;
				LinearLayout.LayoutParams lpTV = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
				lpTV.setMargins(0, 0, 0, 0);
				for(AppUseBean aub : aubs){
					tv = new TextView(context);
					tv.setSingleLine();
					tv.setEllipsize(TextUtils.TruncateAt.MARQUEE);
					tv.setTextColor(context.getResources().getColor(R.color.statsDayAppsTextColor));
					
					try {
						tv.setText(pm.getApplicationLabel(pm.getApplicationInfo(aub.getApp(), PackageManager.GET_META_DATA)) + " - " + Util.getAppPercentageOfUse(aub.getApp(), AppsUse.getAllAppUsesToHashMap(aub.getDate()), 1) + " % \r\n");
					}
					catch(Exception e){
						tv.setText(aub.getApp() + " - " + Util.getAppPercentageOfUse(aub.getApp(), AppsUse.getAllAppUsesToHashMap(aub.getDate()), 1) + " % \r\n");
					}
					
					tv.setPadding(0, 0, 0, 0);
					ll.addView(tv);
				}

				mainLayout.addView(ll, lp);

				ll.startAnimation(AnimationUtils.makeInAnimation(context, false));
				ll.setVisibility(View.VISIBLE);
			}
			catch(Exception e){
				e.printStackTrace();
			}
			finally{
				SQLiteManager.getInstance().closeDB();
			}
		}
		else{
			mainLayout.setOnClickListener(new StatisticsListener(position));
		}*/

		return rowView;
	}

	class StatisticsListener implements View.OnClickListener {

		int position = -1;

		StatisticsListener(int position){
			this.position = position;
		}

		public void onClick(View view){
			StatisticsAdapter.SELECTED_DAY_POSITION = position;
			if(instance != null)
				instance.notifyDataSetChanged();
		}

	}
}
