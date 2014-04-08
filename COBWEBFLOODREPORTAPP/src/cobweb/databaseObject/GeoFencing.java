package cobweb.databaseObject;

import org.osmdroid.util.GeoPoint;



public class GeoFencing {

	GeoPoint[] polygon= new GeoPoint[5];
	
	
	public GeoFencing(){
		
		
		setGeoPoint();
		
	}
	
	private void setGeoPoint(){
		
		 
		GeoPoint pt1=new GeoPoint(53.308441, -6.223841);
		GeoPoint pt2=new GeoPoint(53.308672, -6.225353);
		GeoPoint pt3=new GeoPoint(53.309005, -6.225182);
		GeoPoint pt4=new GeoPoint(53.308826, -6.223551);
		
		polygon[0]=pt1;
		polygon[1]=pt2;
		polygon[2]=pt3;
		polygon[3]=pt4;
		polygon[4]=pt1;
		
		
	}
	
	
	public boolean FindPoint(double X, double Y)
	{
	            int sides = polygon.length - 1;
	            int j = sides - 1;
	            boolean pointStatus = false;
	            for (int i = 0; i < sides; i++)
	            {
	                if (polygon[i].getLatitude() < Y && polygon[j].getLatitude() >= Y || 
	                		polygon[j].getLatitude() < Y && polygon[i].getLatitude() >= Y)
	                {
	                    if (polygon[i].getLongitude() + (Y - polygon[i].getLatitude()) / 
				(polygon[j].getLatitude() - polygon[i].getLatitude()) * (polygon[j].getLongitude() - polygon[i].getLongitude()) < X)
	                    {
	                        pointStatus = !pointStatus ;                        
	                    }
	                }
	                j = i;
	            }
	            return pointStatus;
	}
	
	
	
	public static void main(String[] args){
	  
		GeoFencing geo= new GeoFencing();
		System.out.println(geo.FindPoint(53.308787,-6.223025));
		
	}

}
