package com.example.cobwebfloodreportapplication;

import java.util.ArrayList;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.ResourceProxy.bitmap;
import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MyLocationOverlay;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class PolygonLayerMap extends Activity implements OnClickListener {

	private MapView map;
	MapView mapView;
	Drawable marker;
	DefaultResourceProxyImpl mResourceProxy;
	MapOverLay overlay;
	MyItemizedOverlay myItemizedOverlay = null;
	MyLocationOverlay myLocationOverlay = null;

	private Button addPolygon;
	private Button deletePolygon;
	private Button backToSubmit;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_polygon_layer_map);
		mapView = (MapView) findViewById(R.id.openmapview);
		// mapView.setBuiltInZoomControls(true);

		mResourceProxy = new DefaultResourceProxyImpl(getApplicationContext());

		// Drawable
		// marker=getResources().getDrawable(android.R.drawable.ic_menu_myplaces);
		Drawable marker = mResourceProxy.getDrawable(bitmap.marker_default);
		// Drawable marker= mResourceProxy.getDrawable(bitmap.person);

		int markerWidth = marker.getIntrinsicWidth();
		int markerHeight = marker.getIntrinsicHeight();
		marker.setBounds(0, markerHeight, markerWidth, 0);

		ResourceProxy resourceProxy = new DefaultResourceProxyImpl(
				getApplicationContext());

		myItemizedOverlay = new MyItemizedOverlay(this, marker, resourceProxy);
		mapView.getOverlays().add(myItemizedOverlay);

		myItemizedOverlay.addMapView(mapView);

		mapView.setBuiltInZoomControls(true);
		mapView.setMultiTouchControls(true);
		mapView.setTileSource(TileSourceFactory.MAPNIK);

		myLocationOverlay = new MyLocationOverlay(this, mapView);
		mapView.getOverlays().add(myLocationOverlay);
		myLocationOverlay.enableMyLocation();

		IMapController myMapController = mapView.getController();
		myMapController.setZoom(20);

		setClickableButtons();

		// ScaleBarOverlay myScaleBarOverlay = new ScaleBarOverlay(this);
		// mapView.getOverlays().add(myScaleBarOverlay);
		//
		//
		// myLocationOverlay.runOnFirstFix(new Runnable() {
		// public void run() {
		// mapView.getController().animateTo(myLocationOverlay.getMyLocation());
		// }
		// });

		/*
		 * mResourceProxy = new
		 * DefaultResourceProxyImpl(getApplicationContext()); marker =
		 * getResources().getDrawable(android.R.drawable.star_big_on); overlay
		 * =new MapOverLay(marker,mResourceProxy,this); map = (MapView)
		 * findViewById(R.id.openmapview);
		 * map.setTileSource(TileSourceFactory.MAPNIK);
		 * map.setBuiltInZoomControls(true); map.setMultiTouchControls(true);
		 * 
		 * 
		 * 
		 * GeoPoint point1 = new GeoPoint(52.484974,-3.983316); //helps you
		 * control how the map is focused IMapController mapController =
		 * map.getController(); mapController.setZoom(30);
		 * mapController.setCenter(point1);
		 * 
		 * map.getOverlays().add(overlay);
		 * 
		 * 
		 * 
		 * // //// Marker marker = new Marker(map); ////
		 * marker.setPosition(point1); //// //// //
		 * map.getOverlays().add(marker); //// //// //// ////
		 * marker.setDraggable(true); // map.invalidate();
		 */

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.polygon_layer_map, menu);
		return true;
	}

	protected MapView getMapView() {

		return mapView;
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		myLocationOverlay.enableCompass();
		myLocationOverlay.enableMyLocation();
		myLocationOverlay.enableFollowLocation();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		myLocationOverlay.disableCompass();
		myLocationOverlay.disableMyLocation();
		myLocationOverlay.disableFollowLocation();
	}

	private void setClickableButtons() {
		addPolygon = (Button) findViewById(R.id.addPolygon);
		addPolygon.setOnClickListener(this);

		deletePolygon = (Button) findViewById(R.id.deletePolygon);
		deletePolygon.setOnClickListener(this);

		backToSubmit = (Button) findViewById(R.id.backToSubmit);
		backToSubmit.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {

		switch (view.getId()) {

		case R.id.addPolygon:
			myItemizedOverlay.AddPolygon();
			break;
		case R.id.deletePolygon:
			myItemizedOverlay.removePolygon();
			break;
		case R.id.backToSubmit:
			ArrayList<GeoPoint> pointList = myItemizedOverlay.getpolygon();
			String polygon = "";

			int noOfpoly = pointList.size();
			StringBuilder sb=new StringBuilder(String.valueOf(noOfpoly));
			
			for(GeoPoint gp:pointList){
				sb.append(' ');
				sb.append(gp.getLatitudeE6());
				sb.append(':');
				sb.append(gp.getLongitude());
			}
			SharedPreferences.Editor editor = getSharedPreferences(Constant.SFOLDER,
					MODE_PRIVATE).edit();
			editor.putString(Constant.POLYPR, sb.toString());
			editor.commit();

			// int noOfpoly=
			// for(int i=0; i<pointList.size(); i++){
			// GeoPoint point=pointList.get(i);
			// polygon += point.getLatitude()+"," +point.getLongitude();
			// if(i!=(pointList.size()-1))
			// polygon += ":";
			// }
			//
			//
			// SharedPreferences.Editor editor =
			// getSharedPreferences("COBWEB",MODE_PRIVATE).edit();
			// editor.putString("polygon", polygon);
			// editor.commit();
			//
			finish();
			break;

		}

	}

}
