package cobweb.databaseObject;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;

import android.content.Context;

public class DatabaseHelper {

	private String url= "https://pcapiserver/pcapi/auth/providers";
	private Context context;
	
	
	private List<BasicNameValuePair> nameValuePairs ;
	
	
	public DatabaseHelper(Context context){
		nameValuePairs = new ArrayList<BasicNameValuePair> ();
		
		
	}
	

	
	protected boolean submitPhoto(Submission data){
		return false;
		
       /*
		nameValuePairs.add(new BasicNameValuePair("userID", data.getUseID()));
		nameValuePairs.add(new BasicNameValuePair("long", "" + data.getLongitude()));
		nameValuePairs.add(new BasicNameValuePair("lat", "" + data.getLatitude()));
		nameValuePairs.add(new BasicNameValuePair("date", data.getDate()));
		nameValuePairs.add(new BasicNameValuePair("note", data.getNote()));
		nameValuePairs.add(new BasicNameValuePair("depth", data.getDepth()));
		nameValuePairs.add(new BasicNameValuePair("flow", data.getVelocity()));
		
		for(images =data.getPhotoPaths())
		
		nameValuePairs.add(new BasicNameValuePair("time", formattedTime));
		
		// Toast.makeText(context, "Location Updated",
		// Toast.LENGTH_LONG).show();
		try {
			HttpClient httpclient = new DefaultHttpClient();
			// HttpPost httppost = new
			//
			// HttpPost("http://10.0.2.2:8080/tableQuery.php");
			HttpPost httppost = new HttpPost("http://waist.ucd.ie/tableQuery.php");
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse responce = httpclient.execute(httppost);
			HttpEntity entity = responce.getEntity();
			InputStream is = entity.getContent();
			Log.i("Postdata", responce.getStatusLine().toString());
			nameValuePairs.clear();*/
		
		
	}
	
	
	
	
	
	
	
	
}
