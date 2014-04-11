package com.example.cobwebfloodreportapplication;



import java.util.ArrayList;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.PathOverlay;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;


	public class Map extends Activity {
		 private MapView map;
		 private enum Flood{SEVERE_FLOODING,FLOODING,TRASH_LINE,DEBRIS,DEFAULT};
		 private ArrayList<GeoPoint> waypoints = new ArrayList<GeoPoint>();
		 private ResourceProxy mResourceProxy;
		 private ArrayList<String> photos;
		 
		 
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			setContentView(R.layout.activity_map);
			
			photos= new ArrayList<String> ();
			  map = (MapView) findViewById(R.id.openmapview);
			  map.setTileSource(TileSourceFactory.MAPNIK);
			  map.setBuiltInZoomControls(true);
			  map.setMultiTouchControls(true);
			  
			 mResourceProxy = new DefaultResourceProxyImpl(getApplicationContext());
			  
			 
			 double latitude=0.0;
			 double longitude=0.0;
			 String type;
			 String photo;
			 int size;
			 
			 SharedPreferences prefs = getSharedPreferences("COBWEB",MODE_PRIVATE); 
			 size=prefs.getInt("noOfImage",0);
			 
			 if(size==0){
				 
				   GeoPoint point1 = new GeoPoint(52.484974,-3.983316);
				  //helps you control how the map is focused
				   IMapController mapController = map.getController();
				   mapController.setZoom(18);
				   mapController.setCenter(point1);
				 
				 
			 }else{
				 latitude = (double) prefs.getFloat("latitude",52.484974f);
				 longitude = (double) prefs.getFloat("longitude",-3.983316f);
				 type =  prefs.getString("Type",null);
				 
				
				 for(int i=0; i<size; i++){
					 photos.add(prefs.getString("photo"+i,null));
					 
				 }
				
				 GeoPoint point1 = new GeoPoint(latitude,longitude);	 
				 
				 
				 photo= size>0 ? photos.get(0): "";
				
			
				// GeoPoint point2 = new GeoPoint(52.485352,-3.982415);
				// GeoPoint point3 = new GeoPoint(52.48713,-3.981879);
				// GeoPoint point4 = new GeoPoint(52.48973,-3.98145);
				 
				
				 
				  
				  //helps you control how the map is focused
				   IMapController mapController = map.getController();
				   mapController.setZoom(14);
				   mapController.setCenter(point1);
				   
				  
				   double tempLat= latitude;
				    double templon= longitude;
						  
					 setMarkers(type, point1, photo);
				    for(int i=1; i<size; i++){
				    	
				    	if(i%2==1)
				    		tempLat=tempLat+0.0060000;
							else
								templon=templon+0.006000;
								
						photo =  photos.get(i);
						GeoPoint point = new GeoPoint(tempLat,templon);	
						setMarkers(type, point, photo);
				    }
				    	
						
				  			 
			
				   
				  
				   //waypoints.add(point1);
				   //waypoints.add(point2);
				   //waypoints.add(point3);
				   //waypoints.add(point4);
				  // AddPolygon();
				   
				   AddPolygon2();
			}

			   map.invalidate();
			  // Toast.makeText(this, type, Toast.LENGTH_LONG).show();
		}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.map, menu);
        return true;
    }
    
    
    private void setMarkers(String type, GeoPoint point1, String photo){
    	 //setting marker on the map
		   
		   if(type.trim().equals("Severe Flood")){
			   setMarker(point1,Flood.SEVERE_FLOODING,photo, "Severe Flooding captured Image");
		   }
		   
		   if(type.trim().equals("Flood")){
			   setMarker(point1,Flood.FLOODING,photo,"Flooding captured Image ");
			   //Toast.makeText(this, type, Toast.LENGTH_LONG).show();
		   }
		   
		   if(type.trim().equals("Debris")){
			   setMarker(point1,Flood.DEBRIS,photo,"Debris causing issues that could lead to flooding (blockage culvert)");
			   //Toast.makeText(this, type, Toast.LENGTH_LONG).show();
		   }
		   
		   if(type.trim().equals("Trash line")){
			   setMarker(point1,Flood.TRASH_LINE,photo,"Water Mark captured after flood event");
			   //Toast.makeText(this, type, Toast.LENGTH_LONG).show();
		   }

    	
    }
    
    
  //Marker overlay with a preferred icons
	
  	private void setMarker(GeoPoint point,Flood icon,String photo,String title){
  		
  		SharedPreferences prefs = getSharedPreferences("COBWEB",MODE_PRIVATE); 
		 String note =  prefs.getString("Note",null); 
		 String depth =  prefs.getString("Depth",null);
		 String flow =  prefs.getString("Flow",null);
		 String date =  prefs.getString("Date",null);
		 
		 	
			
			String out= "\n\nDate taken -> "+ date+ "\n\n";
			out += "Depth of the water -> " + depth + "\n\n"; 
			out += "How Fast is the water moving? -> " + flow + "\n\n"; 
			
			out += "Note :\n " + note; 
			
  		
  		//OSMMarker marker = new OSMMarker(map);
  		Marker marker = new Marker(map);
  		marker.setPosition(point);
  		
  		
  		marker.setAnchor(Marker.ANCHOR_CENTER, 1.0f);
  		
  		marker.setImage(setPhoto(photo));
  		marker.setSubDescription(out);
  		
  	
  		
  		
  		
  	//	marker.setImage(mResourceProxy.getDrawable(ResourceProxy.bitmap.marker_default));
  		map.getOverlays().add(marker);
  		
  		switch(icon){
  			
  		   case SEVERE_FLOODING: 
  			   marker.setIcon(getResources().getDrawable(R.drawable.severe_flood));
  			   break;
  			case FLOODING: 
  					marker.setIcon(getResources().getDrawable(R.drawable.flood));
  					break;
  			case TRASH_LINE: 
  				marker.setIcon(getResources().getDrawable(R.drawable.trash_line));
  				break;
  			case DEBRIS: 
  				marker.setIcon(getResources().getDrawable(R.drawable.debris));
  				break;
		default:
			break;
  		   
  	   }
  		   
  		  marker.setDraggable(true);
  		  
  		  
  		  
  		  
  		   marker.setTitle(title);
  		  
  		
  	}
  	
  	
  	private Drawable setPhoto(String path) {


		// Get the dimensions of the View
	

		// Get the dimensions of the bitmap
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, bmOptions);
		int photoW = bmOptions.outWidth;
		int photoH = bmOptions.outHeight;

		// Determine how much to scale down the image
		int scaleFactor = Math.min(photoW/200, photoH/200);

		// Decode the image file into a Bitmap sized to fill the View
		bmOptions.inJustDecodeBounds = false;
		bmOptions.inSampleSize = scaleFactor;
		bmOptions.inPurgeable = true;

		Bitmap bitmap = BitmapFactory.decodeFile(path, bmOptions);
		Drawable d = new BitmapDrawable(getResources(),bitmap);
		
		
		
		return d;
		
		
	}
  	
  	
  	
  	
  	
	private void AddPolygon() {
	
	GeoPoint pt1=new GeoPoint(53.307383,-6.226931);
	GeoPoint pt2=new GeoPoint(53.307639,-6.212597);
	GeoPoint pt3=new GeoPoint(53.30328,-6.212597);
	GeoPoint pt4=new GeoPoint(53.302818,-6.226759);
	
	
	/*
	GeoPoint pt2= new GeoPoint(pt1.getLatitudeE6()+diff, pt1.getLongitudeE6());
	GeoPoint pt3= new GeoPoint(pt1.getLatitudeE6()+diff, pt1.getLongitudeE6()+diff);
	GeoPoint pt4= new GeoPoint(pt1.getLatitudeE6(), pt1.getLongitudeE6()+diff);
	GeoPoint pt5= new GeoPoint(pt1);*/
		
		
	PathOverlay myOverlay= new PathOverlay(Color.RED, this);
	myOverlay.getPaint().setStyle(Paint.Style.STROKE);
	
	myOverlay.addPoint(pt1);
	myOverlay.addPoint(pt2);
	myOverlay.addPoint(pt3);
	myOverlay.addPoint(pt4);
	myOverlay.addPoint(pt1);
	
	map.getOverlays().add(myOverlay);
	}
	
	
	
	
	
	
	private void AddPolygon2() {
		PathOverlay myOverlay= new PathOverlay(Color.BLUE, this);
		myOverlay.getPaint().setStyle(Paint.Style.STROKE);
		
		
		 SharedPreferences prefs = getSharedPreferences("COBWEB",MODE_PRIVATE); 
		int size=prefs.getInt("noOfpoly",0);
		String[] point;
		GeoPoint pt1=null;
   	for(int i=0; i<size; i++){
  		point =prefs.getString("polygon"+i,"00.0:00.0").split(":");
 	    pt1=new GeoPoint(Integer.parseInt(point[0]),Integer.parseInt(point[1]));
 		myOverlay.addPoint(pt1);
 		
   	}

   if (size>1){
		point =prefs.getString("polygon"+0,"00.0:00.0").split(":");
	    pt1=new GeoPoint(Integer.parseInt(point[0]),Integer.parseInt(point[1]));
		myOverlay.addPoint(pt1);

		//Toast.makeText(this, ""+point[0]+""+point[1], Toast.LENGTH_SHORT).show();
   }
		
		
//		GeoPoint pt1=new GeoPoint(53.31128,-6.232853);
//		GeoPoint pt2=new GeoPoint(53.312408,-6.215343);
//		GeoPoint pt3=new GeoPoint(53.299792,-6.215515);
//		GeoPoint pt4=new GeoPoint(53.297894,-6.220922);
//		GeoPoint pt5=new GeoPoint(53.297894,-6.220922);
//		
//		
//		/*
//		GeoPoint pt2= new GeoPoint(pt1.getLatitudeE6()+diff, pt1.getLongitudeE6());
//		GeoPoint pt3= new GeoPoint(pt1.getLatitudeE6()+diff, pt1.getLongitudeE6()+diff);
//		GeoPoint pt4= new GeoPoint(pt1.getLatitudeE6(), pt1.getLongitudeE6()+diff);
//		GeoPoint pt5= new GeoPoint(pt1);*/
//			
//			
//		PathOverlay myOverlay= new PathOverlay(Color.BLUE, this);
//		myOverlay.getPaint().setStyle(Paint.Style.STROKE);
//		
//		myOverlay.addPoint(pt1);
//		myOverlay.addPoint(pt2);
//		myOverlay.addPoint(pt3);
//		myOverlay.addPoint(pt4);
//		myOverlay.addPoint(pt5);
//		myOverlay.addPoint(pt1);
		
		map.getOverlays().add(myOverlay);
		}
	  	
  	

  	
  	
    
    
    
}
