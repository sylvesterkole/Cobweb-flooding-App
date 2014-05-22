package com.example.cobwebfloodreportapplication;

import java.util.Locale;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class COBWEBMainActivity extends Activity implements OnClickListener {
	private Button takePhotoButton;
	private Button openMapButton;
	private Button oofflineButton;
	private Button profileButton;
	private Button enLangButton;
	private Button cyLangButton;
	private Button helpButton;
	private String lang;
	private TextView notice;

	Context context;
	private boolean currentlyuploading = false;

	boolean flag = true;

	Handler handler;

	ProgressDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		dialog = new ProgressDialog(this);
		dialog.setMessage("Uploading Data");

		context = this;
		setContentView(R.layout.activity_cobwebmain);

		handler = new Handler();

		SharedPreferences prefs = getPreferences(MODE_PRIVATE);
		lang = prefs.getString("lang", null);
		if (lang != null)
			setLocaleView(lang);

		setClickableButtons();

	}

	@Override
	protected void onResume() {
		super.onResume();
		final Context context = this;
		new Thread() {
			public void run() {
				DatabaseHelper db = new DatabaseHelper(context);
				Cursor cursor = db.noPolyObs();
				if (cursor == null) {
					cursor = db.polyObs();
					if (cursor == null) {

						noData();
						db.close();
						return;
					}
				}
				if (db.imageAvailable()) {
					db.close();
					flag = true;

					handler.post(new Runnable() {
						public void run() {
							oofflineButton.setText(R.string.offline_button);
							Toast.makeText(context, R.string.availText,
									Toast.LENGTH_LONG).show();
						}
					});
				} else {
					db.deleteTables();
					noData();
					db.close();
				}

			}
		}.start();
	}

	private void noData() {
		handler.post(new Runnable() {
			public void run() {
				buttonChange(oofflineButton);
			}
		});
	}

	private void buttonChange(Button b) {

		flag = false;
		b.setText(R.string.noObs);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.cobwebmain, menu);
		return true;
	}

	private void setClickableButtons() {

		notice = (TextView) findViewById(R.id.warningTextview);
		SharedPreferences prefs = getSharedPreferences(Constant.SFOLDER,
				MODE_PRIVATE);

		int visibile = prefs.getInt("warning", View.GONE);

		notice.setVisibility(visibile);
		takePhotoButton = (Button) findViewById(R.id.take_photo_button);
		takePhotoButton.setOnClickListener(this);

		openMapButton = (Button) findViewById(R.id.flood_map_button);
		openMapButton.setOnClickListener(this);

		oofflineButton = (Button) findViewById(R.id.offline_button);
		oofflineButton.setOnClickListener(this);

		profileButton = (Button) findViewById(R.id.profile_update);
		profileButton.setOnClickListener(this);

		helpButton = (Button) findViewById(R.id.help_button);
		helpButton.setOnClickListener(this);

		enLangButton = (Button) findViewById(R.id.en_button);
		enLangButton.setOnClickListener(this);

		cyLangButton = (Button) findViewById(R.id.cy_button);
		cyLangButton.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {

		SharedPreferences prefs = getSharedPreferences(Constant.SFOLDER,
				MODE_PRIVATE);
		boolean isSignIn = prefs.getBoolean("isSignIn", false);

		switch (v.getId()) {

		case R.id.take_photo_button:
			Intent photo_intent;
			if (isSignIn) {
				photo_intent = new Intent(this, PHOTOActivity.class);

			} else {
				photo_intent = new Intent(this, UserLogInActivity.class);
				photo_intent.putExtra("CALLER", "PHOTOVIEW");
			}
			// startActivityForResult(photo_intent,PHOTOR);
			startActivity(photo_intent);
			break;

		case R.id.flood_map_button:

			// if(isSignIn){
			Intent map_intent = new Intent(this, Map.class);
			startActivity(map_intent);

			break;
		case R.id.offline_button:

			// int i= prefs.getInt("offlineData",0);
			/*
			 * int i = 0; if (i < 1) { Toast.makeText(this, R.string.serverMsg,
			 * Toast.LENGTH_LONG) .show(); break; }
			 * 
			 * sendOfflineData();
			 */

			if (flag)
				uploadDB();
			else
				Toast.makeText(this, R.string.requestUpload, Toast.LENGTH_LONG)
						.show();
			break;
		case R.id.help_button:
			Toast.makeText(this, R.string.helpMsg, Toast.LENGTH_LONG).show();
			break;

		case R.id.profile_update:
			Toast.makeText(this, R.string.profileMsg, Toast.LENGTH_LONG).show();
			break;

		case R.id.en_button:
			setLocaleView("en");
			setClickableButtons();
			break;
		case R.id.cy_button:
			setLocaleView("cy");
			setClickableButtons();
			break;
		}

	}

	/*
	 * private void sendOfflineData() {
	 * 
	 * // TODO
	 * 
	 * }
	 */

	@Override
	protected void onDestroy() {

		SharedPreferences.Editor editor = getSharedPreferences(
				Constant.SFOLDER, MODE_PRIVATE).edit();
		editor.putInt("warning", View.GONE);

		editor.commit();

		super.onDestroy();

		// option 2: callback after super.onDestroy();
	}

	void uploadDB() {
		final Context context = this;
		new Thread() {
			public void run() {

				ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo info = conMgr.getActiveNetworkInfo();

				if (info == null
						|| !info.isConnected()
						|| !info.isAvailable()
						|| Settings.System.getInt(context.getContentResolver(),
								Settings.System.AIRPLANE_MODE_ON, 0) != 0) {
					noConn();
					return;
				}

				Log.i("Upload connection", "Have a connection to upload");
				if (currentlyuploading) {
					return;
				}

				currentlyuploading = true;
				handler.post(new Runnable() {
					public void run() {

						dialog.show();
						
					}
				});

				GeoJSONHelper.loadImages(context);
				DatabaseHelper db = new DatabaseHelper(context);

				Cursor cursor = db.noPolyObs();

				if (cursor != null)
					while (!cursor.isAfterLast()) {

						GeoJSONHelper.nPolyObs(cursor, context);

						cursor.moveToNext();
					}
				cursor = db.polyObs();
				if (cursor != null)
					while (!cursor.isAfterLast()) {

						
						GeoJSONHelper.processObsPoly(cursor, context);
						cursor.moveToNext();
					}
				

				// Log.d("imagedata",""+db.imageAvailable());
				// Log.d("imagedata",""+GeoJSONHelper.imageSize());
				db.deleteTables();

				db.close();
				handler.post(new Runnable() {

					public void run() {
						Toast.makeText(context, R.string.uploadText,
								Toast.LENGTH_LONG).show();
						buttonChange(oofflineButton);
						currentlyuploading = false;
						dialog.dismiss();
						
					}
				});

			}
		}.start();
	}

	private void noConn() {
		handler.post(new Runnable() {

			public void run() {
				Toast.makeText(context, R.string.noConnection,
						Toast.LENGTH_LONG).show();

			}
		});
	}

	private void setLocaleView(String lan) {
		Locale mLocale = new Locale(lan);
		Locale.setDefault(mLocale);
		Configuration config = getBaseContext().getResources()
				.getConfiguration();
		if (!config.locale.equals(mLocale)) {
			config.locale = mLocale;
			getBaseContext().getResources().updateConfiguration(config, null);

		}
		setContentView(R.layout.activity_cobwebmain);
		SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
		editor.putString("lang", lan);
		editor.commit();
	}

}
