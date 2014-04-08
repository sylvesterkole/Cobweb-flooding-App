package com.example.cobwebfloodreportapplication;





import java.util.Locale;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class COBWEBMainActivity extends Activity implements OnClickListener {
	private Button takePhotoButton;
	private Button openMapButton;
	private Button oofflineButton;
	private Button profileButton;
	private Button enLangButton;
	private Button cyLangButton;
	private Button helpButton;
	private String lang;
	private TextView notice;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cobwebmain);
		
		SharedPreferences prefs = getPreferences(MODE_PRIVATE); 
		 lang = prefs.getString("lang", null);
		if (lang != null) setLocaleView(lang);
		
		setClickableButtons();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.cobwebmain, menu);
		return true;
	}
	
	
	private void setClickableButtons(){
		
		notice= (TextView)findViewById(R.id.warningTextview);
		SharedPreferences prefs = getSharedPreferences("COBWEB", MODE_PRIVATE); 
		
		int visibile = prefs.getInt("warning", View.GONE);
		
		notice.setVisibility(visibile);
		takePhotoButton=(Button)findViewById(R.id.take_photo_button);
		takePhotoButton.setOnClickListener(this);
		
		openMapButton=(Button)findViewById(R.id.flood_map_button);
		openMapButton.setOnClickListener(this);
		
		
		oofflineButton=(Button)findViewById(R.id.offline_button);
		oofflineButton.setOnClickListener(this);
		
		profileButton=(Button)findViewById(R.id.profile_update);
		profileButton.setOnClickListener(this);
		
		helpButton=(Button)findViewById(R.id.help_button);
		helpButton.setOnClickListener(this);
		
		enLangButton=(Button)findViewById(R.id.en_button);
		enLangButton.setOnClickListener(this);
		
		cyLangButton=(Button)findViewById(R.id.cy_button);
		cyLangButton.setOnClickListener(this);
		
		

		

		
		
	}

	@Override
	public void onClick(View v) {
		
		SharedPreferences prefs = getSharedPreferences("COBWEB",MODE_PRIVATE); 
		boolean isSignIn= prefs.getBoolean("isSignIn",false);
		
		
		switch (v.getId()) {  
		
        case R.id.take_photo_button:  
        	
   		    if(isSignIn){
   		    	Intent photo_intent = new Intent(this, PHOTOActivity.class);
   				startActivity(photo_intent);
   		    }else {
   		    	Intent photo_intent = new Intent(this, UserLogInActivity.class);
   		    	photo_intent.putExtra("CALLER", "PHOTOVIEW");
   				startActivity(photo_intent);
   		    }
        	    
            break;  
            
            
        case R.id.flood_map_button:  
            
//            if(isSignIn){
            	Intent map_intent = new Intent(this, Map.class);
    			startActivity(map_intent); 

        	    
            break;  
        case R.id.offline_button:  
        	 
   		    //int i= prefs.getInt("offlineData",0);
        	int i=0;
   		    if(i<1){
   		    	Toast.makeText(this,R.string.serverMsg , Toast.LENGTH_LONG).show();  
   		    	break;
   		    }
        	
        	sendOfflineData();
        	
            break; 
        case R.id.help_button:  
        	Toast.makeText(this, R.string.helpMsg , Toast.LENGTH_LONG).show();    
        	break; 
    	 
        case R.id.profile_update:  
       	 	Toast.makeText(this, R.string.profileMsg , Toast.LENGTH_LONG).show();    
       	 	break; 
       	 
        case R.id.en_button:  
          	setLocaleView("en");
          	setClickableButtons();
          	 break;
        case R.id.cy_button:  
        	setLocaleView("cy"); 
        	setClickableButtons();
         	 break;
    } 
	
		
	}
	
	
	private void sendOfflineData(){
		
		//TODO
		
		
	}
	
	@Override
	protected void onDestroy() {

		SharedPreferences.Editor editor = getSharedPreferences("COBWEB",MODE_PRIVATE).edit();  
		 editor.putInt("warning", View.GONE);
         editor.commit(); 
         
	    super.onDestroy();

	    //option 2: callback after super.onDestroy();
	}
	
	
	 private void setLocaleView(String lan){
		   Locale mLocale = new Locale(lan);
		   Locale.setDefault(mLocale); 
		   Configuration config = getBaseContext().getResources().getConfiguration();
	       if (!config.locale.equals(mLocale)) 
	       { 
	           config.locale = mLocale; 
	           getBaseContext().getResources().updateConfiguration(config, null); 
	           
	       } 
	       setContentView(R.layout.activity_cobwebmain);
	       SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
	       editor.putString("lang",lan);
	       editor.commit();
	   }
	   
	
	
		

}
