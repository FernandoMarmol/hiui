package es.fmm.hiui.settings;

import android.app.Activity;
import android.app.backup.BackupManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import es.fmm.hiui.BuildConfig;

public class Settings extends Activity {

	@Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Display the fragment as the main content.
		getFragmentManager().beginTransaction()
				.replace(android.R.id.content, new SettingsFragment())
				.commit();

		PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(
				new SharedPreferences.OnSharedPreferenceChangeListener() {
					public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
						BackupManager.dataChanged(BuildConfig.APPLICATION_ID);
					}
				});
	}
}
