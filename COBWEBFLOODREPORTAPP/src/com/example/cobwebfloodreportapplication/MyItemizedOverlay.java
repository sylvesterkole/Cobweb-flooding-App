package com.example.cobwebfloodreportapplication;

import java.util.ArrayList;

import org.osmdroid.ResourceProxy;
import org.osmdroid.api.IMapController;
import org.osmdroid.api.IMapView;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.MapView.Projection;
import org.osmdroid.views.overlay.ItemizedOverlay;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.PathOverlay;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
 
public class MyItemizedOverlay extends ItemizedOverlay<OverlayItem> {
  
 private ArrayList<OverlayItem> overlayItemList = new ArrayList<OverlayItem>();
 private ArrayList<GeoPoint> poly_points = new ArrayList<GeoPoint>();
 private ArrayList<OSMMarker> markers = new ArrayList<OSMMarker>();
 
 
 private Context context;
 private MapView mapView;
 private PathOverlay  myOverlay;
 
 
 public MyItemizedOverlay(Context context,Drawable pDefaultMarker,
   ResourceProxy pResourceProxy) {
  super(pDefaultMarker, pResourceProxy);
  this.context=context;
  myOverlay= new PathOverlay(Color.BLUE, context);
  myOverlay.getPaint().setStyle(Paint.Style.STROKE);
  
 }
  
 public void addItem(GeoPoint p, String title, String snippet){
  OverlayItem newItem = new OverlayItem(title, snippet, p);
  overlayItemList.add(newItem);
  populate(); 
  IMapController mapController = mapView.getController();
  mapController.setCenter(p);
  AddPolygon();
  
  
 }
 
 
 protected void addMapView(MapView map){
	 
	 mapView= map;
	 
 }
 
 @Override
 public boolean onSnapToItem(int arg0, int arg1, Point arg2, IMapView arg3) {

  return false;
 }
 
 @Override
 protected OverlayItem createItem(int arg0) {
 
  return overlayItemList.get(arg0);
 }
 
 @Override
 public int size() {
  
  return overlayItemList.size();
 }
 
 
 @Override
 protected boolean onTap(int index) {
   OverlayItem item = overlayItemList.get(index);
   AlertDialog.Builder dialog = new AlertDialog.Builder(context);
   dialog.setTitle(item.getTitle());
   dialog.setMessage(item.getSnippet());
   dialog.show();
   return true;
 }
	
 
 @Override
 public boolean onSingleTapConfirmed(MotionEvent e, MapView mapView) {

     Projection proj = mapView.getProjection();
     GeoPoint p = (GeoPoint) proj.fromPixels((int) e.getX(), (int) e.getY());
      proj = mapView.getProjection();
      GeoPoint loc = (GeoPoint) proj.fromPixels((int) e.getX(), (int) e.getY());
      String longitude = Double
      .toString(((double) loc.getLongitudeE6()) / 1000000);
      String latitude = Double
      .toString(((double) loc.getLatitudeE6()) / 1000000);
    //  Toast toast = Toast.makeText(context,"Longitude: "+ longitude + " Latitude: " + latitude, Toast.LENGTH_SHORT);
    //  toast.show();
      
      	//addItem(loc, "myPoint2", "myPoint2");
      	
      setMarker(loc);
    
      
 
     
     return true;
 }
 
 
 
 
 private void setMarker(GeoPoint point){ 

	 
	 OSMMarker marker= new OSMMarker(mapView);
	 marker.setPosition(point);
	 marker.setDraggable(true);
		 
	 mapView.getOverlays().add(marker);
	 IMapController mapController = mapView.getController();
     mapController.setCenter(point);
   
 }
 
 protected ArrayList<GeoPoint> getpolygon(){ 

	
   return poly_points;
 }
 
 
 
 
 
 
 protected void removePolygon(){
	 //GeoPoint point=null;
	 myOverlay.clearPath();
	// if(!poly_points.isEmpty()) point = new GeoPoint(poly_points.get(0));
	 poly_points.clear();
	 for(OSMMarker marker: markers) setMarker(marker.getPosition());
	 
	 markers.clear();
	// if(point!=null)  mapView.getController().setCenter(point);
     
 }
 
 protected void AddPolygon() {
	

	 
	 //poly_points;
	// (!(mapView.getOverlays()).isEmpty()) &&
	  
	  //mapView.getOverlays().contains(myOverlay)
	 
	 if (poly_points.size()>0) return;
	 
	 

		  for( Overlay lay :  mapView.getOverlays()){  
			  
			  if(lay instanceof OSMMarker){ 
				  myOverlay.addPoint(((OSMMarker) lay).getPosition());
				  poly_points.add(((OSMMarker) lay).getPosition());
				  markers.add(((OSMMarker) lay));
			   }
			  
		  
		  }
		 if(poly_points.size()>1){
			  myOverlay.addPoint(poly_points.get(0));
			  mapView.getOverlays().add(myOverlay);
			  mapView.getController().setCenter(poly_points.get(0));
		 }
		 
		 
		 for(OSMMarker marker: markers) marker.remove(mapView);
		 
		 
		  
		 //myOverlay.addPoint( poly_points.get(0));
		  
		  
		  
//		  for(int i=0;i< poly_points.size(); i++){
//				 myOverlay.addPoint( poly_points.get(i));
//			 
//		  } 
//		  
//		  
//	 
//	 
//	 if(mapView.getOverlays().contains(myOverlay) ){
//		// mapView.getOverlays().remove(myOverlay);
//		
//		 //mapView.invalidate();
//	 }
//	 
//	 
//	
//	 
//	 if ( poly_points.size()>1){
//		 
//		 //myOverlay.addPoint(overlayItemList.get(0).getPoint());
//		 mapView.getOverlays().add(myOverlay);
//		 
//		 
//	 }
//	 

	 
	 
	 
	 
//	 
//	 for(int i=0;i<overlayItemList.size(); i++){
//		 
//		 
//		 //myOverlay.addPoint(overlayItemList.get(i).getPoint());
//		 
//		 
//		 
//	 } 
//	 
//	 if (overlayItemList.size()>1){
//		 
//		 //myOverlay.addPoint(overlayItemList.get(0).getPoint());
//		 mapView.getOverlays().add(myOverlay);
//		 
//		 
//	 }
//	 
	
	 /*
		GeoPoint pt1=new GeoPoint(53.31128,-6.232853);
		GeoPoint pt2=new GeoPoint(53.312408,-6.215343);
		GeoPoint pt3=new GeoPoint(53.299792,-6.215515);
		GeoPoint pt4=new GeoPoint(53.297894,-6.220922);
		GeoPoint pt5=new GeoPoint(53.297894,-6.220922);*/
		
		
		/*
		GeoPoint pt2= new GeoPoint(pt1.getLatitudeE6()+diff, pt1.getLongitudeE6());
		GeoPoint pt3= new GeoPoint(pt1.getLatitudeE6()+diff, pt1.getLongitudeE6()+diff);
		GeoPoint pt4= new GeoPoint(pt1.getLatitudeE6(), pt1.getLongitudeE6()+diff);
		GeoPoint pt5= new GeoPoint(pt1);*/
		
		/*
		myOverlay.addPoint(pt1);
		myOverlay.addPoint(pt2);
		myOverlay.addPoint(pt3);
		myOverlay.addPoint(pt4);
		myOverlay.addPoint(pt5);
		myOverlay.addPoint(pt1); */
		
		//mapView.getOverlays().add(myOverlay);
		}
 
 
 
 
 
 
}
