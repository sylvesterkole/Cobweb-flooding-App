package com.example.cobwebfloodreportapplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PolyImage {

	private final static String MPOINT = "MultiPoint";
	private final static String FNAME = "File Name";

	String file, polyline;

	public PolyImage(String fn, String line) {
		file = fn;
		polyline = line;
	}

	public void addName(JSONArray arr, int cnt) throws JSONException {
		GeoJSONHelper.addFileName(arr, file, cnt);
	}

	public JSONObject polylObj(String prop) throws JSONException {
		JSONObject pl = GeoJSONHelper.polygonPolyline(polyline, MPOINT);
		JSONObject flN = new JSONObject();
		flN.put(FNAME, GeoJSONHelper.b64FileName(file));

		JSONArray lProp = new JSONArray();
		lProp.put(flN);

		pl.put(prop, lProp);
		return pl;
	}
}
