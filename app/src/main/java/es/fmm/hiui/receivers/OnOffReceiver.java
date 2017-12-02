package es.fmm.hiui.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import es.fmm.hiui.services.WidgetPersistentService;

/**
 * Created by fmm on 7/28/13.
 */
public class OnOffReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent newIntent = new Intent(context, WidgetPersistentService.class);
		newIntent.setAction(intent.getAction());
		if(intent.getExtras() != null)
			newIntent.putExtras(intent.getExtras());

		context.startService(newIntent);
	}
}
