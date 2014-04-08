package com.example.cobwebfloodreportapplication;



import java.util.Date;

import org.osmdroid.util.GeoPoint;

import geofence.*;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;
import android.view.View;



public class GeoFencingService extends IntentService {
	GPSTracker gps;
	GeoFencing geofence= new GeoFencing();
	Polygon polygon;
	
	
	
	public GeoFencingService() {
		super("GeoFencingService");
		
	}

	@Override
    protected void onHandleIntent(Intent workIntent) {
        
		polygon=getPolygone();
		//Thread t= new Thread(this);
		//t.start();
		gps= new GPSTracker(this);
		SharedPreferences.Editor editor = getSharedPreferences("COBWEB",MODE_PRIVATE).edit();
		editor.putBoolean("isNotify", false);
		editor.commit();
		run();
		
        
        
    }
	
	
	private  Polygon getPolygone(){
			 SharedPreferences prefs = getSharedPreferences("COBWEB",MODE_PRIVATE); 
		int size=prefs.getInt("AlerpolyNo",0);
		
		if(size<3){
			return presetPolygon();
		}
		
		
		String[] point;
		
		
		Point[] polygondata= new Point[size];
		GeoPoint pt1=null;
		
		
  	for(int i=0; i<size; i++){
 		point =prefs.getString("Apolygon"+i,"00.0:00.0").split(":");
	    pt1=new GeoPoint(Integer.parseInt(point[0]),Integer.parseInt(point[1]));
	    polygondata[i]= new Point(pt1.getLatitude(),pt1.getLongitude());
 	}

		
  	Polygon polygon=new Polygon(polygondata);

		
		
		
	     
		return polygon;
		
	}

	private Polygon presetPolygon(){
		
	  	//pre-set polygon
		
		Point pt1=new Point(53.31128,-6.232853);
		Point pt2=new Point(53.312408,-6.215343);
		Point pt3=new Point(53.299792,-6.215515);
		Point pt4=new Point(53.297894,-6.220922);
		Point pt5=new Point(53.297894,-6.220922);
		
		Point[] polygondata= new Point[5];
		
		polygondata[0]=pt1;
		polygondata[1]=pt2;
		polygondata[2]=pt3;
		polygondata[3]=pt4;
		polygondata[4]=pt5;
		
		
		Polygon polygon=new Polygon(polygondata);
		
		return polygon;
		
	}
	
	
	
	
	public synchronized  void run() {
		boolean isNotify;
		while(true){
			SharedPreferences prefs = getSharedPreferences("COBWEB",MODE_PRIVATE); 
			isNotify =  prefs.getBoolean("isNotify",false);		
		
			if(geofence.checkInside(polygon, gps.getLatitude(), gps.getLongitude())){
				if(!isNotify){
					createNotification();
					SharedPreferences.Editor editor = getSharedPreferences("COBWEB",MODE_PRIVATE).edit();
					editor.putBoolean("isNotify", true);
					editor.commit();
				}
	
			}else {
				SharedPreferences.Editor editor = getSharedPreferences("COBWEB",MODE_PRIVATE).edit();
				editor.putBoolean("isNotify", false);
				editor.commit();
			}
			
			Log.d("myApp", ""+gps.canGetLocation());
			
			try {
				wait(10000);
			} catch (InterruptedException e) {
				
				e.printStackTrace();
			}
	 }
			
	}
	
	
	
	@SuppressLint("NewApi")
	public void createNotification() {
	    // Prepare intent which is triggered if the
	    // notification is selected
		Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

	    Intent intent = new Intent(this, COBWEBMainActivity.class);
	    PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

	    // Build notification
	    // Actions are just fake
	    Notification noti = new Notification.Builder(this)
	        .setContentTitle("COBWEB")
	        .setSmallIcon(R.drawable.imagelogo)
	        .setSound(soundUri)
	        .setContentIntent(pIntent).build();
	        
//	        .setContentIntent(pIntent)
//	        .addAction(R.drawable.imagelogo, "Call", pIntent)
//	        .addAction(R.drawable.imagelogo, "More", pIntent)
//	        .addAction(R.drawable.imagelogo, "And more", pIntent).build();
	    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	    // hide the notification after its selected
	    noti.flags |= Notification.FLAG_AUTO_CANCEL;
	    SharedPreferences.Editor editor = getSharedPreferences("COBWEB",MODE_PRIVATE).edit();  
	    editor.putInt("warning", View.VISIBLE);
        editor.commit();
	    notificationManager.notify(0, noti);
	  }
	
	
	
	
}


