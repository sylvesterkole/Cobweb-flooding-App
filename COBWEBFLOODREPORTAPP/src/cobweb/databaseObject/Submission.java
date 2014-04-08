package cobweb.databaseObject;


import java.util.Hashtable;
import java.util.Set;

import android.graphics.Bitmap;

/**
 * @author sylvestereigbogba
 *
 */
public class Submission {
	
	public Submission(){
		
		photo= new Hashtable<String,Bitmap>();
		
		
	}
	
	public String getUseID() {
		return useID;
	}

	
	public double getLatitude() {
		return latitude;
	}
	

	public double getLongitude() {
		return longitude;
	}
	
	public void setlocation(double lat, double lon) {
		latitude = lat;
		longitude = lon;
	}
	
	
	public Hashtable<String, Bitmap> getPhoto() {
		return photo;
	}
	
	
	public void addPhoto(String path, Bitmap image) {
		if(!photo.contains(path))
			photo.put(path,image);
	}
	
	public Bitmap[] getPhotoFils() {
		return (Bitmap[]) photo.values().toArray();
	}
	
	
	public Set<String> getPhotoPaths() {
		return  photo.keySet();
	}
	
	
	public String getType() {
		return type;
	}
	
	
	public void setType(String type) {
		this.type = type;
	}
	
	
	public String getVelocity() {
		return velocity;
	}
	
	
	public void setVelocity(String velocity) {
		this.velocity = velocity;
	}
	
	
	public String getDepth() {
		return depth;
	}
	public void setDepth(String depth) {
		this.depth = depth;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	
	public void setPolygone(String polygon) {
		this.polygon = polygon;
	}
	
	public String getPolygon() {
		return polygon;
	}
	
	
	private String useID="55";
	private double latitude;
	private double longitude;
	private Hashtable<String,Bitmap> photo;
	private String type;
	private String velocity;
	private String depth;
	private String note;
	private String date;
	private String polygon;
	
	
	
	

}
