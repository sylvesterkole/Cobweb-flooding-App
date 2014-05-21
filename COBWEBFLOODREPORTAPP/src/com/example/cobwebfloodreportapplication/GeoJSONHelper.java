package com.example.cobwebfloodreportapplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

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

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class GeoJSONHelper {

	// GeoJSON names
	private final static String PROP = "properties";
	private final static String FCOLL = "FeatureCollection";
	private final static String TYPE = "type";
	private final static String FEATURES = "features";
	private final static String FEAT = "feature";
	private final static String GEOMETRY = "geometry";
	private final static String COORD = "coordinates";

	// GeoJSON types
	private final static String POLYGON = "Polygon";
	private final static String POINT = "Point";

	// Properties Point
	private final static String LOC = "Location";
	private final static String LOCVAL = "GPS Coordinates";

	// Properties General
	private final static String TFLOOD = "Flood Type";
	private final static String FVEL = "Flow Velocity";
	private final static String DEST = "Depth Estimate";
	private final static String NOTE = "Note";

	// Image directory
	private final static String FOLDER_LOC = Environment
			.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
			.getAbsolutePath()

			+ "/" + Constant.SFOLDER + "/";

	private static String B64EX = ".b64";
	private static String FSTRING = "File name ";
	private static String GEOJEX = ".geoj";

	private static HashMap<Integer, Set<String>> image = new HashMap<Integer, Set<String>>();
	private static HashMap<Integer, Set<PolyImage>> polImage = new HashMap<Integer, Set<PolyImage>>();

	static public void loadImages(Context context) {

		DatabaseHelper db = new DatabaseHelper(context);
		Cursor cur = db.noLineImage();
		if (cur != null) {
			while (!cur.isAfterLast()) {

				int oid = cur.getInt(0);
				String fn = cur.getString(1);
				Set<String> list = image.get(oid);
				if (list == null) {
					list = new HashSet<String>();
					image.put(oid, list);
				}
				list.add(fn);
				cur.moveToNext();
			}
		}

		cur = db.lineImage();
		if (cur != null) {
			while (!cur.isAfterLast()) {

				int oid = cur.getInt(0);
				String fn = cur.getString(1);
				Set<PolyImage> list = polImage.get(oid);
				if (list == null) {
					list = new HashSet<PolyImage>();
					polImage.put(oid, list);
				}

				String polLine = cur.getString(2);
				list.add(new PolyImage(fn, polLine));
				cur.moveToNext();
			}
		}

		// System.out.println("images loaded");
		db.close();
	}
	
	

	static public boolean nPolyObs(Cursor cursor, Context context) {

		try {

			JSONObject point = pointGeo(cursor);

			JSONArray properties = props(cursor);

			JSONArray features = handleImages(cursor, properties, context);

			JSONObject gpsProp = new JSONObject();
			gpsProp.put(LOC, LOCVAL);

			if (features.length() == 0) {
				properties.put(gpsProp);
				point.put(PROP, properties);
				// Send point
				return sendGeoJSON(point.toString(4), geoJFN(cursor), context);
			} else {
				JSONArray pProp = new JSONArray();
				pProp.put(gpsProp);
				point.put(PROP, pProp);

				features.put(point);
				JSONObject featureCollection = new JSONObject();
				featureCollection.put(TYPE, FCOLL);
				featureCollection.put(FEATURES, features);
				featureCollection.put(PROP, properties);

				return sendGeoJSON(features.toString(4), geoJFN(cursor), context);
			}

		} catch (JSONException e) {
			e.printStackTrace();
			return false;
		}

	}

	private static String geoJFN(Cursor cursor) {
		return FOLDER_LOC + cursor.getString(9) + cursor.getInt(0) + GEOJEX;
	}

	static public boolean processObsPoly(Cursor cursor, DatabaseHelper db,
			Context context) {

		try {

			JSONObject point = pointGeo(cursor);

			String poly = cursor.getString(10);
			JSONObject polygon = polygonPolyline(poly, POLYGON);

			JSONArray properties = props(cursor);

			JSONArray features = handleImages(cursor, properties, context);

			JSONObject gpsProp = new JSONObject();
			gpsProp.put(LOC, LOCVAL);
			JSONArray pProp = new JSONArray();
			pProp.put(gpsProp);
			point.put(PROP, pProp);

			JSONObject featureCollection = new JSONObject();
			featureCollection.put(TYPE, FCOLL);
			featureCollection.put(FEATURES, features);
			featureCollection.put(PROP, properties);

			features.put(point);
			features.put(polygon);

			// Send feature collection
			return sendGeoJSON(features.toString(4), geoJFN(cursor), context);

		} catch (JSONException e) {
			e.printStackTrace();
			return false;
		}

	}

	static private boolean sendGeoJSON(String jsonString, String fn,
			Context context) throws JSONException {

		try {
			File file = new File(fn);

			file.createNewFile();

			PrintWriter pw = new PrintWriter(new FileWriter(file));
			pw.print(jsonString);
			pw.flush();
			pw.close();
			return post(fn, "application/json", false,
					fn.substring(fn.lastIndexOf('/') + 1), context);

		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

	}

	static private JSONArray handleImages(Cursor cursor, JSONArray properties,
			Context context) throws JSONException {
		int oid = cursor.getInt(0);

		Set<String> img = image.get(oid);

		int cnt = 0;
		if (img != null)
			for (String fn : img) {
				addFileName(properties, fn, cnt);

				// send file
				post(fn, "image/jpeg", true,
						fn.substring(fn.lastIndexOf('/') + 1), context);

				cnt++;

			}

		Set<PolyImage> pi = polImage.get(oid);

		JSONArray features = new JSONArray();

		if (pi != null)
			for (PolyImage polI : pi) {
				polI.addName(properties, cnt);

				// send file
				JSONObject pl = polI.polylObj(PROP);

				features.put(pl);

				cnt++;

			}
		return features;

	}

	static private JSONArray props(Cursor cursor) throws JSONException {
		String typ, flowv, nt, dpt;
		typ = cursor.getString(5);
		dpt = cursor.getString(6);
		flowv = cursor.getString(7);
		nt = cursor.getString(8);

		JSONArray properties = new JSONArray();

		JSONObject type = new JSONObject();
		type.put(TFLOOD, typ);
		JSONObject flow = new JSONObject();
		flow.put(FVEL, flowv);
		JSONObject depth = new JSONObject();
		depth.put(DEST, dpt);
		JSONObject note = new JSONObject();
		note.put(NOTE, nt);

		properties.put(type);
		properties.put(flow);
		properties.put(depth);
		properties.put(note);

		return properties;

	}

	public static JSONObject polygonPolyline(String polygon, String type)
			throws JSONException {
		String[] str = polygon.split(" ");
		int n = Integer.parseInt(str[0]);
		StringBuilder sb = new StringBuilder('[');
		for (int i = 1; i < n; i++) {

			sb.append('[');
			String[] crd = str[i].split(":");
			sb.append(crd[0]);
			sb.append(',');
			sb.append(crd[1]);
			sb.append(']');
			if (i != n - 1)
				sb.append(',');
		}
		sb.append(']');

		JSONObject feature = new JSONObject();
		feature.put(TYPE, FEAT);
		JSONObject poly = new JSONObject();
		poly.put(TYPE, type);
		poly.put(COORD, sb.toString());
		feature.put(GEOMETRY, poly);
		return feature;
	}

	private static JSONObject pointGeo(Cursor cursor) throws JSONException {

		double lat = cursor.getDouble(1);
		double lon = cursor.getDouble(2);
		JSONObject feature = new JSONObject();
		feature.put(TYPE, FEAT);
		JSONObject point = new JSONObject();

		point.put(TYPE, POINT);
		
		point.put(COORD, "[" + lat + ',' + lon + ']');
		
		feature.put(GEOMETRY, point);
		return feature;

	}

	public static String b64FileName(String fn) {
		return fn.substring(fn.lastIndexOf('/') + 1, fn.length() - 5) + B64EX;
	}

	public static void addFileName(JSONArray arr, String fn, int cnt)
			throws JSONException {

		JSONObject jo = new JSONObject();
		String file = b64FileName(fn);
		jo.put(FSTRING + cnt, file);
		arr.put(jo);

	}

	public void postFiles(int mode, final Context context) {

		File storageDir = new File(FOLDER_LOC);
		final String[] fn = storageDir.list();
		// final String[]fn={"fromphone.txt"};
		if (fn == null || fn.length == 0)
			Toast.makeText(context, R.string.noUpload, Toast.LENGTH_LONG)
					.show();
		else {

			Toast.makeText(context, fn[0], Toast.LENGTH_LONG).show();
			new Thread() {
				public void run() {

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

	private static boolean post(final String fn, final String mime, boolean b,
			String fs, Context context) {

		try {
			HttpClient httpclient = new DefaultHttpClient();
			httpclient.getParams().setParameter(

			CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

			Log.d(fn, "posting to "+fs);
			String hn = fs;
			if (b) {
				hn = hn.substring(0, fs.length() - 5) + B64EX;
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

			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

	}

	private final static String encode(byte[] d) {
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
	
	/*
	 * static public String polygonData() { try { JSONObject geo = new
	 * JSONObject(); JSONObject featureCollection = new JSONObject();
	 * featureCollection.put(TYPE, FCOLL); JSONArray featureList = new
	 * JSONArray();
	 * 
	 * featureCollection.put(FEATURES, featureList); JSONObject polygon = new
	 * JSONObject(); polygon.put(TYPE, POLYGON); JSONArray coord = new
	 * JSONArray("[[[" + 1 + "," + 0 + "]" + ",[" + 2 + "," + 0 + "]" + ",[" + 3
	 * + "," + 0 + "],[" + 1 + "," + 0 + "]]]"); polygon.put(COORD, coord);
	 * JSONObject feature = new JSONObject(); feature.put(GEOMETRY, polygon);
	 * feature.put(TYPE, FEAT);
	 * 
	 * // JSONArray prop = properties(); // feature.put(PROP, prop);
	 * 
	 * featureList.put(feature);
	 * 
	 * return featureCollection.toString(4);
	 * 
	 * } catch (JSONException e) { e.printStackTrace(); } return null; }
	 */

	/*
	 * static private void geoJSON(String fn) { final File f = new File(fn);
	 * 
	 * if (f.exists()) f.delete();
	 * 
	 * try { f.createNewFile(); String s = polygonData(); PrintWriter pw = new
	 * PrintWriter(new FileOutputStream(f)); pw.println(s); pw.flush();
	 * pw.close(); } catch (IOException e) { e.printStackTrace(); }
	 * 
	 * }
	 */

}
