package com.example.cobwebfloodreportapplication;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.ResourceProxy.bitmap;
import org.osmdroid.bonuspack.overlays.*;
import org.osmdroid.bonuspack.utils.BonusPackHelper;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.MapView.Projection;
import org.osmdroid.views.overlay.SafeDrawOverlay;
import org.osmdroid.views.safecanvas.ISafeCanvas;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.MotionEvent;


public class OSMMarker extends SafeDrawOverlay  {

	/*attributes for standard features:*/
	protected Drawable mIcon;
	protected GeoPoint mPosition;
	protected float mBearing;
	protected float mAnchorU, mAnchorV;
	protected float mIWAnchorU, mIWAnchorV;
	protected float mAlpha;
	protected String mTitle, mSnippet;
	protected boolean mDraggable, mIsDragged;
	protected InfoWindow mInfoWindow;
	protected boolean mFlat;
	protected OnMarkerClickListener mOnMarkerClickListener;
	protected OnMarkerDragListener mOnMarkerDragListener;

	/*attributes for non-standard features:*/
	protected Drawable mImage;
	protected String mSubDescription;
	protected boolean mPanToView;
	protected Object mRelatedObject;

	/*internals*/
	protected Point mPositionPixels;
	protected ResourceProxy mResourceProxy;
	protected static MarkerInfoWindow mDefaultInfoWindow = null;


	/** Usual values in the (U,V) coordinates system of the icon image */
	public static final float ANCHOR_CENTER=0.5f, ANCHOR_LEFT=0.0f, 
			ANCHOR_TOP=0.0f, ANCHOR_RIGHT=1.0f, ANCHOR_BOTTOM=1.0f;



	public OSMMarker(MapView mapView) {
		this(mapView, new DefaultResourceProxyImpl(mapView.getContext()));
	}


	public OSMMarker(MapView mapView, final ResourceProxy resourceProxy) {
		super(resourceProxy);
		mResourceProxy = resourceProxy;
		mBearing = 0.0f;
		mAlpha = 1.0f; //opaque
		mPosition = new GeoPoint(0.0, 0.0);
		mAnchorU = ANCHOR_CENTER;
		mAnchorV = ANCHOR_CENTER;
		mIWAnchorU = ANCHOR_CENTER;
		mIWAnchorV = ANCHOR_TOP;
		mDraggable = false;
		mIsDragged = false;
		mPositionPixels = new Point();
		mPanToView = true;
		mFlat = false; //billboard
	
		//Listerner that will open when the icon is clicked
		mOnMarkerClickListener = null;
		mOnMarkerDragListener = null;
		
		//Default Icon
		mIcon = resourceProxy.getDrawable(bitmap.marker_default);
		if (mDefaultInfoWindow == null){
			//build default bubble, that will be shared between all markers using the default one:
			Context context = mapView.getContext();
			String packageName = context.getPackageName();
			int defaultLayoutResId = context.getResources().getIdentifier("layout/bonuspack_bubble", null, packageName);
			
			if (defaultLayoutResId == 0)
				Log.e(BonusPackHelper.LOG_TAG, "Marker: layout/bonuspack_bubble not found in "+packageName);
			else 
				
				mDefaultInfoWindow = new MarkerInfoWindow(defaultLayoutResId, mapView);
		}
		mInfoWindow = mDefaultInfoWindow;
	}

	

	
	/** Sets the icon for the marker. Can be changed at any time. 
	 * @param icon if null, the default osmdroid marker is used. 
	 */
	public void setIcon(Drawable icon){
			
		if (icon != null)
			mIcon = icon;
		else 
			mIcon = mResourceProxy.getDrawable(bitmap.marker_default);
	}

	public GeoPoint getPosition(){
		return mPosition.clone();
	}

	public void setPosition(GeoPoint position){
		mPosition = position.clone();
	}

	public float getRotation(){
		return mBearing;
	}

	public void setRotation(float rotation){
		mBearing = rotation;
	}

	public void setAnchor(float anchorU, float anchorV){
		mAnchorU = anchorU;
		mAnchorV= anchorV;
	}

	public void setInfoWindowAnchor(float anchorU, float anchorV){
		mIWAnchorU = anchorU;
		mIWAnchorV= anchorV;
	}

	public void setAlpha(float alpha){
		mAlpha = alpha;
	}

	public float getAlpha(){
		return mAlpha;
	}

	public void setTitle(String title){
		mTitle = title;
	}

	public String getTitle(){
		return mTitle;
	}

	public void setSnippet(String snippet){
		mSnippet= snippet;
	}

	public String getSnippet(){
		return mSnippet;
	}

	public void setDraggable(boolean draggable){
		mDraggable = draggable;
	}

	public boolean isDraggable(){
		return mDraggable;
	}

	public void setFlat(boolean flat){
		mFlat = flat;
	}

	public boolean isFlat(){
		return mFlat;
	}

	/** 
	 * Removes this marker from the map. 
	 * Note that this method will not operate if the Marker is in a FolderOverlay. 
	 * @param mapView the map
	 */
	public void remove(MapView mapView){
		
		mapView.getOverlays().remove(this);
		GeoPoint point = new GeoPoint(getPosition().getLatitudeE6(),(getPosition().getLongitudeE6()+10));
		mapView.getController().setCenter(point);
	}

	public void setOnMarkerClickListener(OnMarkerClickListener listener){
		mOnMarkerClickListener = listener;
	}

	public void setOnMarkerDragListener(OnMarkerDragListener listener){
		mOnMarkerDragListener = listener;
	}

	/** set the "sub-description", an optional text to be shown in the InfoWindow, below the snippet, in a smaller text size */
	public void setSubDescription(String subDescription){
		mSubDescription = subDescription;
	}

	public String getSubDescription(){
		return mSubDescription;
	}

	/** set an image to be shown in the InfoWindow  - this is not the marker icon */
	public void setImage(Drawable image){
		mImage = image;
	}

	/** get the image to be shown in the InfoWindow - this is not the marker icon */
	public Drawable getImage(){
		return mImage;
	}

	/** Set the InfoWindow to be used. 
	 * Default is a MarkerInfoWindow, with the layout named "bonuspack_bubble". 
	 * You can use this method either to use your own layout, or to use your own sub-class of InfoWindow. 
	 * Note that this InfoWindow will receive the Marker object as an input, so it MUST be able to handle Marker attributes. 
	 * If you don't want any InfoWindow to open, you can set it to null. */
	public void setInfoWindow(InfoWindow infoWindow){
		mInfoWindow = infoWindow;
	}

	/** If set to true, when clicking the marker, the map will be centered on the marker position. 
	 * Default is true. */
	public void setPanToView(boolean panToView){
		mPanToView = panToView;
	}

	/** Allows to link an Object (any Object) to this marker. 
	 * This is particularly useful to handle custom InfoWindow. */
	public void setRelatedObject(Object relatedObject){
		mRelatedObject = relatedObject;
	}

	/** @return the related object. */
	public Object getRelatedObject(){
		return mRelatedObject;
	}

	public void showInfoWindow(){
		if (mInfoWindow == null)
			return;
		int markerWidth = 0, markerHeight = 0;
		markerWidth = mIcon.getIntrinsicWidth(); 
		markerHeight = mIcon.getIntrinsicHeight();

		int offsetX = (int)(mIWAnchorU*markerWidth) - (int)(mAnchorU*markerWidth);
		int offsetY = (int)(mIWAnchorV*markerHeight) - (int)(mAnchorV*markerHeight);

		mInfoWindow.open(this, mPosition, offsetX, offsetY);
	}

	public void hideInfoWindow(){
		if (mInfoWindow != null)
			mInfoWindow.close();
	}

	public boolean isInfoWindowShown(){
		return (mInfoWindow != null) && mInfoWindow.isOpen();
	}

	@Override public void drawSafe(ISafeCanvas canvas, MapView mapView, boolean shadow) {
		if (shadow)
			return;
		if (mIcon == null)
			return;

		final Projection pj = mapView.getProjection();

		pj.toMapPixels(mPosition, mPositionPixels);
		int width = mIcon.getIntrinsicWidth();
		int height = mIcon.getIntrinsicHeight();
		Rect rect = new Rect(0, 0, width, height);
		rect.offset(-(int)(mAnchorU*width), -(int)(mAnchorV*height));
		mIcon.setBounds(rect);

		mIcon.setAlpha((int)(mAlpha*255));

		float rotationOnScreen = (mFlat ? -mBearing : mapView.getMapOrientation()-mBearing);
		drawAt(canvas.getSafeCanvas(), mIcon, mPositionPixels.x, mPositionPixels.y, false, rotationOnScreen);
	}

	public boolean hitTest(final MotionEvent event, final MapView mapView){
		final Projection pj = mapView.getProjection();
		pj.toMapPixels(mPosition, mPositionPixels);
		final Rect screenRect = pj.getIntrinsicScreenRect();
		int x = -mPositionPixels.x + screenRect.left + (int) event.getX();
		int y = -mPositionPixels.y + screenRect.top + (int) event.getY();
		boolean hit = mIcon.getBounds().contains(x, y);
		return hit;
	}
	
	
	@Override 
	public boolean onDoubleTap(final MotionEvent event, final MapView mapView){
		remove(mapView);
		return true;
	}
	
	
	
	
	

	@Override public boolean onSingleTapConfirmed(final MotionEvent event, final MapView mapView){
		boolean touched = hitTest(event, mapView);
		if (touched){
			if (mOnMarkerClickListener == null){
				return onMarkerClickDefault(this, mapView);
			} else {
				return mOnMarkerClickListener.onMarkerClick(this, mapView);
			}
		} else
			return touched;
	}

	public void moveToEventPosition(final MotionEvent event, final MapView mapView){
		final Projection pj = mapView.getProjection();
		mPosition = (GeoPoint) pj.fromPixels(event.getX(), event.getY());
		mapView.invalidate();
	}

	@Override public boolean onLongPress(final MotionEvent event, final MapView mapView) {
		boolean touched = hitTest(event, mapView);
		if (touched){
			if (mDraggable){
				//starts dragging mode:
					mIsDragged = true;
			hideInfoWindow();
			if (mOnMarkerDragListener != null)
				mOnMarkerDragListener.onMarkerDragStart(this);
			moveToEventPosition(event, mapView);
			}
		}
		return touched;
	}

	@Override public boolean onTouchEvent(final MotionEvent event, final MapView mapView) {
		if (mDraggable && mIsDragged){
			if (event.getAction() == MotionEvent.ACTION_UP) {
				mIsDragged = false;
				if (mOnMarkerDragListener != null)
					mOnMarkerDragListener.onMarkerDragEnd(this);
				return true;
			} else if (event.getAction() == MotionEvent.ACTION_MOVE){
				moveToEventPosition(event, mapView);
				if (mOnMarkerDragListener != null)
					mOnMarkerDragListener.onMarkerDrag(this);
				return true;
			} else 
				return false;
		} else 
			return false;
	}

	//-- Marker events listener interfaces ------------------------------------

	public interface OnMarkerClickListener{
		abstract boolean onMarkerClick(OSMMarker marker, MapView mapView); 
	}

	public interface OnMarkerDragListener{
		abstract void onMarkerDrag(OSMMarker marker);
		abstract void onMarkerDragEnd(OSMMarker marker);
		abstract void onMarkerDragStart(OSMMarker marker);
	}

	/** default behaviour when no click listener is set */
	protected boolean onMarkerClickDefault(OSMMarker marker, MapView mapView) {
		/*
		marker.showInfoWindow();
		if (marker.mPanToView)
			mapView.getController().animateTo(marker.getPosition());*/
		return true;
	}

}