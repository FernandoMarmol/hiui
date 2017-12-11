package es.fmm.hiui.application.backup;

import android.app.backup.BackupAgentHelper;
import android.app.backup.BackupDataInput;
import android.app.backup.BackupDataOutput;
import android.app.backup.BackupManager;
import android.app.backup.SharedPreferencesBackupHelper;
import android.os.ParcelFileDescriptor;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.IOException;

/**
 * Created by ferna on 06/12/2017.
 */

public class HiuiBackupAgentHelper extends BackupAgentHelper {

	public static final String MY_GENERAL_SETTINGS_BACKUP_KEY = "mySettingsBackupKey";

	@Override
	public void onQuotaExceeded(long backupDataBytes, long quotaBytes) {
		Log.d(HiuiBackupAgentHelper.class.getSimpleName(), "HiuiBackupAgent:onQuotaExceeded - He excedido el espacio de Backup - backupDataBytes: " + backupDataBytes + ", quotaBytes: " + quotaBytes);
		super.onQuotaExceeded(backupDataBytes, quotaBytes);
	}

	@Override
	public void onBackup(ParcelFileDescriptor oldState, BackupDataOutput data, ParcelFileDescriptor newState) throws IOException {
		Log.d(HiuiBackupAgentHelper.class.getSimpleName(), "HiuiBackupAgent:onBackup");
		super.onBackup(oldState, data, newState);
	}

	@Override
	public void onRestore(BackupDataInput data, int appVersionCode, ParcelFileDescriptor newState) throws IOException {
		Log.d(HiuiBackupAgentHelper.class.getSimpleName(), "HiuiBackupAgent:onRestore");
		super.onRestore(data, appVersionCode, newState);
	}

	@Override
	public void onCreate() {

		SharedPreferencesBackupHelper helper = new SharedPreferencesBackupHelper(this, PreferenceManager.getDefaultSharedPreferencesName(this));
		addHelper(MY_GENERAL_SETTINGS_BACKUP_KEY, helper);

		super.onCreate();
	}
}
