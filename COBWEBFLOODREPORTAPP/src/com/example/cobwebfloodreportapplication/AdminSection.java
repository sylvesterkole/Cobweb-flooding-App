package com.example.cobwebfloodreportapplication;

import android.os.Bundle;

import android.app.Activity;
import android.content.Intent;

import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


public class AdminSection extends Activity implements OnClickListener {
	private Button takePhotoButton;
	private Button openMapButton;
	private Button polygonsetiing;

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setClickableButtons();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	
	
	
	
private void setClickableButtons(){
		
	
		takePhotoButton=(Button)findViewById(R.id.photopreview);
		takePhotoButton.setOnClickListener(this);
		
		openMapButton=(Button)findViewById(R.id.fldmap);
		openMapButton.setOnClickListener(this);
		
		
		polygonsetiing=(Button)findViewById(R.id.polygonMap);
		polygonsetiing.setOnClickListener(this);
		
		
		
	}


@Override
public void onClick(View v) {

	
	
	switch (v.getId()) {  
	
    case R.id.polygonMap:  
    	Intent map_intent = new Intent(this, AdminPolygonMap.class);
		startActivity(map_intent); 
		finish();
    	
        break;  
        
        
    case R.id.fldmap:  
      	Intent intent = new Intent(this, Map.class);
		startActivity(intent); 
		finish();
    	    
        break;  
    case R.id.photopreview:  
    	Intent intent1 = new Intent(this, PHOTOActivity.class);
		startActivity(intent1); 
		finish();
		break;
	}
	
}
	
	

}
