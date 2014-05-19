package com.example.cobwebfloodreportapplication.DataTypeDialogs;



import com.example.cobwebfloodreportapplication.DialogueData;
import com.example.cobwebfloodreportapplication.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;


public class InitialDialog implements OnItemSelectedListener{
	

	private Spinner type,flow,depth;
	private boolean setDialog= false;
	private EditText text;
	
	private Activity activity;
	
	private View dialogView;
    private AlertDialog.Builder builder;
    private DialogueData data;
	
	public DialogueData getData() {
		return data;
	}
 
	public InitialDialog(Activity act){
		activity=act;
		data=new DialogueData();
		showDiaglog();
	}
	
	  
	public boolean isSetDialog() {
		return setDialog;
	}

 
private void showDiaglog(){
			
		builder = new AlertDialog.Builder(activity);
	    // Get the layout inflater
		LayoutInflater inflater = activity.getLayoutInflater();
	    dialogView = inflater.inflate(R.layout.initial_dialog, null);
	    
	    // Pass null as the parent view because its going in the dialog layout
	    builder.setView(dialogView)
	    
	    .setTitle(R.string.nature_of_observation)
	    // Add action buttons
	           .setPositiveButton("OK", new DialogInterface.OnClickListener() {
	        	   
	        	   @Override
	               public void onClick(DialogInterface dialog, int id) {
	        		   setDialog=true; 
	               }
	           })
	    		.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int id) {
	                   //LoginDialogFragment.this.getDialog().cancel();	             
	            	   }
	           }); 	   
	    
	    
	    
	    builder.show().setCanceledOnTouchOutside(false);
	   
	    
	    setDropDownLists();
	   
	}
	
	
	
	private void setDropDownLists(){
		
		
		type = (Spinner) dialogView.findViewById(R.id.dialog_type_spinner);	
	 
		
		 // Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> type_adapter = ArrayAdapter.createFromResource(activity,R.array.dialog_type_spinnerText, android.R.layout.simple_spinner_item);
	   	
		type_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		 
		type.setOnItemSelectedListener(this);
	 
		//Apply the adapter to the spinner
	    type.setAdapter(type_adapter);
	     
		  
//		
		
	
}

	
	    @Override 
	    public void onItemSelected(AdapterView<?> parent, View view, 
	            int pos, long id) {
	    	
	    		switch (parent.getId()) {  
	    		case R.id.dialog_type_spinner:  
	    			data.setFloodType(parent.getItemAtPosition(pos).toString());
	    			break;  
	    	  
	    		}  

	    	
	    }
	        // An item was selected. You can retrieve the selected item using
	        // parent.getItemAtPosition(pos)
	    

	    public void onNothingSelected(AdapterView<?> parent) {
	        // Another interface callback
	    }




	
	

}