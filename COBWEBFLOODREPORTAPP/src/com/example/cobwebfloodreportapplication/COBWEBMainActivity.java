package com.example.cobwebfloodreportapplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Locale;
import java.util.Properties;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
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

	public final static String FOLDER_LOC = Environment
			.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
			.getAbsolutePath()

			+ "/" + Constant.SFOLDER + "/";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		context = this;
		setContentView(R.layout.activity_cobwebmain);

		SharedPreferences prefs = getPreferences(MODE_PRIVATE);
		lang = prefs.getString("lang", null);
		if (lang != null)
			setLocaleView(lang);

		setClickableButtons();
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

			uploadDB();

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

				GeoJSONHelper.loadImages(context);
				DatabaseHelper db = new DatabaseHelper(context);

				Cursor cursor = db.noPolyObs();
				db.close();

				if (cursor != null)
					while (!cursor.isAfterLast()) {

						GeoJSONHelper.nPolyObs(cursor);

						cursor.moveToNext();
					}
				cursor = db.polyObs();
				if (cursor != null)
					while (!cursor.isAfterLast()) {

						GeoJSONHelper.processObsPoly(cursor, db);
						cursor.moveToNext();
					}

				db.close();
				
				
			}
		}.start();
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

	public void postFiles() {

		File storageDir = new File(FOLDER_LOC);
		final String[] fn = storageDir.list();
		// final String[]fn={"fromphone.txt"};
		if (fn == null || fn.length == 0)
			Toast.makeText(this, R.string.noUpload, Toast.LENGTH_LONG).show();
		else {

			Toast.makeText(this, fn[0], Toast.LENGTH_LONG).show();
			new Thread() {
				public void run() {

					SharedPreferences prefs = getSharedPreferences(
							Constant.SFOLDER, MODE_PRIVATE);
					final String s = prefs.getString("polygon", null);

					// Toast.makeText(this, s, Toast.LENGTH_LONG).show();

					String file = FOLDER_LOC
							+ fn[0].substring(0, fn[0].length() - 5);
					// + ".txt";
					// String file=FOLDER_LOC +"fromPhone2.txt";
					// GeoJSONHelper.geoJSON(file);
					post(file, "application/json", false,
							fn[0].substring(0, fn[0].length() - 5), context);

					post(FOLDER_LOC + fn[0], "image/jpeg", true, fn[0], context);

				}
			}.start();

		}

	}

	public static void post(final String fn, final String mime, boolean b,
			String fs, Context context) {

		try {
			HttpClient httpclient = new DefaultHttpClient();
			httpclient.getParams().setParameter(

			CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

			String hn = fs;
			if (b) {
				hn = hn.substring(0, fs.length() - 5) + ".b64";
			}

			Properties properties = new Properties();
			AssetManager assetManager = context.getAssets();
			InputStream inputStream = assetManager
					.open("cobweb_flooding.properties");
			properties.load(inputStream);

			HttpPost httppost = new HttpPost(

			properties.getProperty("pcapiurl") + hn);

			// System.out.println("here");

			File file = new File(fn);
			;
			if (b) {
				FileInputStream imageInFile = new FileInputStream(file);
				byte imageData[] = new byte[(int) file.length()];
				imageInFile.read(imageData);

				System.out.println("len ------------- " + file.length());

				String imageDataString = encode(imageData);

				File f = new File(FOLDER_LOC + "tmpconvertfile.string");
				f.createNewFile();
				FileOutputStream imageOutFile = new FileOutputStream(f);

				PrintWriter pw = new PrintWriter(imageOutFile);
				pw.println(imageDataString);

				pw.flush();
				pw.close();

				imageInFile.close();
				imageOutFile.close();
				file.delete();
				file = f;
			}

			MultipartEntity mpEntity = new MultipartEntity();
			// ContentBody cbFile = new FileBody(file, "plain/text");
			ContentBody cbFile = new FileBody(file, mime);
			mpEntity.addPart("userfile", cbFile);

			httppost.setEntity(mpEntity);

			System.out
					.println("executing request " + httppost.getRequestLine());

			if (b) {
				httppost.setHeader("C2ontent-Transfer-Encoding", "base64");
			}
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity resEntity = response.getEntity();

			System.out.println(response.getStatusLine());
			if (resEntity != null) {
				System.out.println(EntityUtils.toString(resEntity));
			}
			if (resEntity != null) {
				resEntity.consumeContent();
			}

			httpclient.getConnectionManager().shutdown();

			file.delete();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public final static String encode(byte[] d) {
		if (d == null)
			return null;

		byte data[] = new byte[d.length + 2];
		System.arraycopy(d, 0, data, 0, d.length);
		byte dest[] = new byte[(data.length / 3) * 4];

		// 3-byte to 4-byte conversion
		for (int sidx = 0, didx = 0; sidx < d.length; sidx += 3, didx += 4) {
			dest[didx] = (byte) ((data[sidx] >>> 2) & 077);
			dest[didx + 1] = (byte) ((data[sidx + 1] >>> 4) & 017 | (data[sidx] << 4) & 077);
			dest[didx + 2] = (byte) ((data[sidx + 2] >>> 6) & 003 | (data[sidx + 1] << 2) & 077);
			dest[didx + 3] = (byte) (data[sidx + 2] & 077);
		}

		// 0-63 to ascii printable conversion
		for (int idx = 0; idx < dest.length; idx++) {
			if (dest[idx] < 26)
				dest[idx] = (byte) (dest[idx] + 'A');
			else if (dest[idx] < 52)
				dest[idx] = (byte) (dest[idx] + 'a' - 26);
			else if (dest[idx] < 62)
				dest[idx] = (byte) (dest[idx] + '0' - 52);
			else if (dest[idx] < 63)
				dest[idx] = (byte) '+';
			else
				dest[idx] = (byte) '/';
		}

		// add padding
		for (int idx = dest.length - 1; idx > (d.length * 4) / 3; idx--) {
			dest[idx] = (byte) '=';
		}
		return new String(dest);
	}

	/**
	 * Encode a String using Base64 using the default platform encoding
	 **/
	public final static String encode(String s) {
		return encode(s.getBytes());
	}

}
