package com.example.cobwebfloodreportapplication;

import java.util.List;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.MapView.Projection;
import org.osmdroid.views.overlay.Overlay;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
 
/**
 * @author Sylvester Eigbogba
 */
@SuppressWarnings("deprecation")
public class MyLocationOverlay extends Overlay implements SensorListener, LocationListener {
    private static final String TAG = "MyLocationOverlay";
//    private final Context context; 
    private final MapView mapView;
    private boolean compassEnabled;
    private boolean myLocationEnabled;
    private SensorManager sensorManager;
    private LocationManager locationManager;
    private float[] compassValues = {0.0f};
    private Runnable runOnFirstFix = null;
    private Location lastFix = null;
    private Bitmap compassArrow;
    private Bitmap compassBase;
    private Paint paint = new Paint();
 
    public MyLocationOverlay(Context context, MapView mapView) {
    	
    	super( context);
//      this.context = context;
        this.mapView = mapView;
        sensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
        locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        
      //  compassArrow = bitmap.direction_arrow;
        //compassBase = bitmap.ic_menu_compass;
        
        
       // getDrawable(bitmap.marker_default);
//        compassArrow = ((BitmapDrawable)context.getResources().getDrawable(com.android.internal.R.drawable.compass_arrow)).getBitmap();
//        compassBase = ((BitmapDrawable)context.getResources().getDrawable(com.android.internal.R.drawable.compass_base)).getBitmap();
    }
 
    public boolean isCompassEnabled() {
        return compassEnabled;
    }
    public synchronized boolean enableCompass() {
        return compassEnabled = sensorManager.registerListener(this, 
                SensorManager.SENSOR_ORIENTATION,
                SensorManager.SENSOR_DELAY_GAME);
    }
    public synchronized void disableCompass() {
        if (compassEnabled)
            sensorManager.unregisterListener(this);
        compassEnabled = false;
        lastFix = null;
    }
 
    public boolean isMyLocationEnabled() {
        return myLocationEnabled;
    }
    public synchronized boolean enableMyLocation() {
        List<String> providers = locationManager.getAllProviders();
        if (providers.contains(LocationManager.GPS_PROVIDER)) {
            myLocationEnabled = true;
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0L, this);
        } else if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
            myLocationEnabled = true;
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0L, this);
            
        } else {
            myLocationEnabled = false;
        }
        return myLocationEnabled;
    }
    public synchronized void disableMyLocation() {
        if (myLocationEnabled) 
            locationManager.removeUpdates(this);
        myLocationEnabled = false;
        lastFix = null;
    }
	
    @Override
	protected void draw(Canvas arg0, MapView arg1, boolean arg2) {
		draw (arg0, arg1, arg2);
		
	}
    
    
    public synchronized boolean draw(Canvas canvas, MapView mapView, boolean shadow, long when) {
        if (!shadow) {
            if (isMyLocationEnabled() && lastFix != null) {
                drawMyLocation(canvas, mapView, lastFix, getMyLocation(), when);
            }
            if (isCompassEnabled() && compassValues != null) {
                drawCompass(canvas, compassValues[0]);
            }
        }
        return false;
    }
 
    protected void drawCompass(Canvas canvas, float bearing) {
        int offset = Math.max(canvas.getHeight(), canvas.getWidth()) / 8;
        Rect r = new Rect(0, 0, 2*offset, 2*offset);
        canvas.drawBitmap(compassBase, null, r, paint);
        canvas.rotate(-bearing, offset, offset);
        canvas.drawBitmap(compassArrow, null, r, paint);
    }
 
    protected void drawMyLocation(Canvas canvas, MapView mapView, Location lastFix,
            GeoPoint myLocation, long when) {
        Projection p = mapView.getProjection();
        float accuracy = p.metersToEquatorPixels(lastFix.getAccuracy());
        Point loc = p.toPixels(myLocation, null);
        paint.setAntiAlias(true);
        paint.setColor(Color.BLUE);
        if (accuracy > 10.0f) {
            paint.setAlpha(50);
            canvas.drawCircle(loc.x, loc.y, accuracy, paint);
        }
        paint.setAlpha(255);
        canvas.drawCircle(loc.x, loc.y, 10, paint);
        
    }
 
 
    public Location getLastFix() {
        return lastFix;
    }
    public GeoPoint getMyLocation() {
        return new GeoPoint(lastFix.getLatitude(), lastFix.getLongitude());
    }
    public float getOrientation() {
        return compassValues[0];
    }
    public synchronized void onLocationChanged(Location location) {
        lastFix = location;
        if (runOnFirstFix != null) {
            runOnFirstFix.run();
            runOnFirstFix = null;
        }
        mapView.invalidate();
    }
    public void onProviderDisabled(String provider) {
    }
    public void onProviderEnabled(String provider) {
    }
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
    public synchronized boolean runOnFirstFix(Runnable runnable) {
        if (lastFix == null) {
            runOnFirstFix = runnable;
            return false;
        } else {
            runnable.run();
            return true;
        }
    }
    public void onSensorChanged(int sensor, float[] values) {
        compassValues = values;
    }
    public void onAccuracyChanged(int sensor, int accuracy) {
    }
    
  
    public boolean onTap(GeoPoint p, MapView map) {
        Projection projection = map.getProjection();
        Point tapPoint = projection.toPixels(p, null);
        Point myPoint = projection.toPixels(getMyLocation(), null);
        if (Math.pow(tapPoint.x - myPoint.x, 2.0) + Math.pow(tapPoint.y - myPoint.y, 2.0)  < Math.pow(20.0, 2)) {
            // Is it within 20 pixels?
            return dispatchTap();
        } else {
            return false;
        }
    }
    protected boolean dispatchTap() {
        return false;
    }


 
}