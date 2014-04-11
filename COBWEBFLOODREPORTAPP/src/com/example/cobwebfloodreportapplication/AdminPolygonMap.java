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
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.example.cobwebfloodreportapplication.COBWEBNotificationService.LocalBinder;

public class AdminPolygonMap extends Activity implements OnClickListener{
	private MapView map;
	MapView mapView;
	 Drawable marker;
	 DefaultResourceProxyImpl mResourceProxy;
	 MapOverLay overlay;
	 MyItemizedOverlay myItemizedOverlay = null;
	 MyLocationOverlay myLocationOverlay = null;
	 
	 
	 private  Button  addPolygon;
	 private  Button  deletePolygon;
	 private  Button  photViewSubmit;
	
	 private COBWEBNotificationService mService;
	 private boolean mBound = false;
	 
	 private AdminPolygonMap main=this;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_admin_polygon_map);
		mapView = (MapView) findViewById(R.id.adminmap);
        mResourceProxy = new DefaultResourceProxyImpl(getApplicationContext());
         
        Drawable marker= mResourceProxy.getDrawable(bitmap.marker_default);
       int markerWidth = marker.getIntrinsicWidth();
        int markerHeight = marker.getIntrinsicHeight();
        marker.setBounds(0, markerHeight, markerWidth, 0);
         
        ResourceProxy resourceProxy = new DefaultResourceProxyImpl(getApplicationContext());
         
        myItemizedOverlay = new MyItemizedOverlay(this,marker, resourceProxy);
        mapView.getOverlays().add(myItemizedOverlay);
        
        myItemizedOverlay.addMapView(mapView);
         
        mapView.setBuiltInZoomControls(true);
		mapView.setMultiTouchControls(true); 
		mapView.setTileSource(TileSourceFactory.MAPNIK);
        
        myLocationOverlay = new MyLocationOverlay(this, mapView);
        mapView.getOverlays().add(myLocationOverlay);
        myLocationOverlay.enableMyLocation();
        
        
        IMapController myMapController = mapView.getController();
        myMapController.setZoom(14);
        
        setClickableButtons();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.admin_polygon_map, menu);
		return true;
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
	
	
	 
	 private void setClickableButtons(){
			addPolygon=(Button)findViewById(R.id.add_Polygon);
			addPolygon.setOnClickListener(this);
			
			deletePolygon=(Button)findViewById(R.id.delete_Polygon);
			deletePolygon.setOnClickListener(this);
			
			
			photViewSubmit=(Button)findViewById(R.id.Photo_Preview);
			photViewSubmit.setOnClickListener(this);
	}
	 
	 
	 
	 
	 
	 
	 @Override
		public void onClick(View view) {
			
			switch (view.getId()) {  
		        
				case R.id.add_Polygon:  
					  myItemizedOverlay.AddPolygon();   
		            break;  
		        case R.id.delete_Polygon:  
		        	 myItemizedOverlay.removePolygon();   
		            break; 
		        case R.id.Photo_Preview: 
		        	ArrayList<GeoPoint> pointList=myItemizedOverlay.getpolygon();
		        	String polygon="";
		        	
		        	int noOfpoly= pointList.size();
		        	SharedPreferences.Editor editor = getSharedPreferences("COBWEB",MODE_PRIVATE).edit();
		        	 editor.putInt("AlerpolyNo", noOfpoly);
		        	for(int i=0; i<noOfpoly; i++){
		        		GeoPoint point=pointList.get(i);
		        		 polygon =""+ point.getLatitudeE6()+":" +point.getLongitudeE6();
		        		 editor.putString("Apolygon"+i, polygon);
		        		
		        	}

		        	editor.commit();
		        	Intent service = new Intent(this, GeoFencingService.class);   
		        	startService(service);
		        	
		        	
		        	callNoftification();
		        	
//		        	
//		        	Intent photo_intent = new Intent(this, PHOTOActivity.class);   			
//		        	startActivity(photo_intent);
		        	finish();
		        	
		        	
		            break; 
		     
			} 
		
		}
	 
	
	 private void callNoftification(){
		   if(mBound){
			 mService.updatePolygon();
		   }
		
	}
	 
	 
	  private ServiceConnection mConnection = new ServiceConnection() {

	        @Override
	        public void onServiceConnected(ComponentName className,
	                IBinder service) {
	            // We've bound to LocalService, cast the IBinder and get LocalService instance
	            LocalBinder binder = (LocalBinder) service;
	            mService = binder.getService();
	            mBound = true;
	            
	            Toast.makeText(main, "I did bind", Toast.LENGTH_SHORT).show();
	            
	        }

			@Override
			public void onServiceDisconnected(ComponentName name) {
				mBound = false;
				
			}
	  };
	 
//	   @Override
//	    protected void onStart() {
//	        
//	        // Bind to LocalService
//
//       	 	Intent intent = new Intent(this, COBWEBNotificationService.class);
//            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
//            
//            
//            
//            super.onStart();
//	    }
	   
	   
//	   
//	   
//	   @Override
//	    protected void onStop() {
//	        super.onStop();
//	        // Unbind from the service
//	        if (mBound) {
//	            unbindService(mConnection);
//	            mBound = false;
//	        }
//	    }


}
