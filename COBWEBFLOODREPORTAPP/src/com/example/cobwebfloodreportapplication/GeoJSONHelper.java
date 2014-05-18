package com.example.cobwebfloodreportapplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;

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
	private final static String LOCVAL = "GPS Corrdinates";

	// Properties General
	private final static String TFLOOD = "Flood Type";
	private final static String FVEL = "Flow Velocity";
	private final static String DEST = "Depth Estimate";
	private final static String NOTE = "Note";

	private static HashMap<Integer, List<String>> image = new HashMap<Integer, List<String>>();
	private static HashMap<Integer, List<PolyImage>> polImage = new HashMap<Integer, List<PolyImage>>();

	static public void loadImages(Context context) {

		DatabaseHelper db = new DatabaseHelper(context);
		Cursor cur = db.noLineImage();
		if (cur != null) {
			while (!cur.isAfterLast()) {

				int oid = cur.getInt(0);
				String fn = cur.getString(1);
				List<String> list = image.get(oid);
				if (list == null) {
					list = new ArrayList<String>();
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
				List<PolyImage> list = polImage.get(oid);
				if (list == null) {
					list = new ArrayList<PolyImage>();
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

	static public void nPolyObs(Cursor cursor) {

		try {

			JSONObject point = pointGeo(cursor);

			JSONArray properties = props(cursor);

			JSONArray features = handleImages(cursor, properties);

			JSONObject gpsProp = new JSONObject();
			gpsProp.put(LOC, LOCVAL);

			if (features.length() == 0) {
				properties.put(gpsProp);
				point.put(PROP, properties); // Send point
			} else {
				JSONArray pProp = new JSONArray();
				pProp.put(gpsProp);
				point.put(PROP, pProp);

				features.put(point);
				JSONObject featureCollection = new JSONObject();
				featureCollection.put(TYPE, FCOLL);
				featureCollection.put(FEATURES, features);
				featureCollection.put(PROP, properties);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	static public void processObsPoly(Cursor cursor, DatabaseHelper db) {

		try {

			JSONObject point = pointGeo(cursor);

			String poly = cursor.getString(9);
			JSONObject polygon = polygonPolyline(poly, POLYGON);

			JSONArray properties = props(cursor);

			JSONArray features = handleImages(cursor, properties);

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

		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	static private JSONArray handleImages(Cursor cursor, JSONArray properties)
			throws JSONException {
		int oid = cursor.getInt(0);

		List<String> img = image.get(oid);

		int cnt = 0;
		if (img != null)
			for (String fn : img) {
				addFileName(properties, fn, cnt);

				// send file
				cnt++;

			}

		List<PolyImage> pi = polImage.get(oid);

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
		point.put(COORD, '[' + lat + ',' + lon + ']');
		feature.put(GEOMETRY, point);
		return feature;

	}

	public static void addFileName(JSONArray arr, String fn, int cnt)
			throws JSONException {

		JSONObject jo = new JSONObject();
		jo.put("File name " + cnt, fn);
		arr.put(jo);

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
