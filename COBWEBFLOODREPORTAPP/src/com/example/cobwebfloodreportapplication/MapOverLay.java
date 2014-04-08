package com.example.cobwebfloodreportapplication;

import java.util.ArrayList;

import org.osmdroid.ResourceProxy;

import org.osmdroid.ResourceProxy.bitmap;
import org.osmdroid.api.IMapView;
import org.osmdroid.api.Marker;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.MapView.Projection;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlay;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;



import android.app.Activity;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.widget.Toast;

public class MapOverLay extends ItemizedOverlay<OverlayItem>  {

	 ArrayList<OverlayItem> items;
	Activity activity;
	ResourceProxy pResourceProxy;
	
	public MapOverLay(Drawable pDefaultMarker, ResourceProxy pResourceProxy,Activity act) {
		super(pDefaultMarker, pResourceProxy);
		this.pResourceProxy=pResourceProxy;
		activity=act;
		 items = new ArrayList<OverlayItem>();
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public boolean onSnapToItem(int arg0, int arg1, Point arg2, IMapView arg3) {
		// TODO Auto-generated method stub
	
		return false;
	}
	

	 public void addItem(GeoPoint p, String title, String snippet){
	  OverlayItem newItem = new OverlayItem(title, snippet, p);
	  items.add(newItem);
	  populate(); 
	 }
	 
	 
	@Override
	protected OverlayItem createItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int size() {
		
		
		// TODO Auto-generated method stub
		return 0;
	}
	
	
    public boolean onSingleTapConfirmed(MotionEvent e, MapView mapView) {

        Projection proj = mapView.getProjection();
        GeoPoint p = (GeoPoint) proj.fromPixels((int) e.getX(), (int) e.getY());
         proj = mapView.getProjection();
         GeoPoint loc = (GeoPoint) proj.fromPixels((int) e.getX(), (int) e.getY());
         String longitude = Double
         .toString(((double) loc.getLongitudeE6()) / 1000000);
         String latitude = Double
         .toString(((double) loc.getLatitudeE6()) / 1000000);
         Toast toast = Toast.makeText(activity,"Longitude: "+ longitude + " Latitude: " + latitude, Toast.LENGTH_SHORT);
         toast.show();
          //this.hitTest(getItem(0), pResourceProxy.getDrawable(bitmap.marker_default), loc.getLatitudeE6(), loc.getLongitudeE6()); 
         //Marker marker = new Marker(loc.getLatitude(),loc.getLongitude());
        
    
        
       
        
        return true;
    }

	

}
