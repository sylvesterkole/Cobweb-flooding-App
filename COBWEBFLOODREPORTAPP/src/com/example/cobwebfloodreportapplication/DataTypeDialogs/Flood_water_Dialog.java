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


public class Flood_water_Dialog implements OnItemSelectedListener{
	

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
 
	public Flood_water_Dialog(Activity act){
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
	    dialogView = inflater.inflate(R.layout.flood_water_dialog, null);
	    
	    // Pass null as the parent view because its going in the dialog layout
	    builder.setView(dialogView)
	    
	    .setTitle(R.string.flood_additional_information)
	    // Add action buttons
	           .setPositiveButton("OK", new DialogInterface.OnClickListener() {
	        	   
	        	   @Override
	               public void onClick(DialogInterface dialog, int id) {
	        		   setDialog=true;
	        		   text=(EditText) dialogView.findViewById(R.id.note_text);
	        		   data.setNote(text.getEditableText().toString());
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
		
		
		//type = (Spinner) dialogView.findViewById(R.id.dialog_type_spinner);	
		flow = (Spinner)  dialogView.findViewById(R.id.dialog_velo_spinner);
		depth = (Spinner)  dialogView.findViewById(R.id.dialog_depth_spinner);
		
		 // Create an ArrayAdapter using the string array and a default spinner layout
	//	ArrayAdapter<CharSequence> type_adapter = ArrayAdapter.createFromResource(activity,R.array.dialog_type_spinnerText, android.R.layout.simple_spinner_item);
	   	ArrayAdapter<CharSequence> flow_adapter = ArrayAdapter.createFromResource(activity,R.array.dialog_velo_spinner, android.R.layout.simple_spinner_item);
	    ArrayAdapter<CharSequence> depth_adapter = ArrayAdapter.createFromResource(activity, R.array.dialog_depth_spinner, android.R.layout.simple_spinner_item);
		
		//type_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		flow_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		depth_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
	//	type.setOnItemSelectedListener(this);
		flow.setOnItemSelectedListener(this);
		depth.setOnItemSelectedListener(this);

		//Apply the adapter to the spinner
	//    type.setAdapter(type_adapter);
	    flow.setAdapter(flow_adapter);
	    depth.setAdapter(depth_adapter);
		 
		 
//		    Spinner flow = (Spinner) findViewById(R.id.dialog_velo_spinner);
//		    Spinner depth = (Spinner) findViewById(R.id.dialog_depth_spinner);
//		  
//		    // Create an ArrayAdapter using the string array and a default spinner layout
//		    ArrayAdapter<CharSequence> type_adapter = ArrayAdapter.createFromResource(this,
//	        R.array.dialog_Type_spinner, android.R.layout.simple_spinner_item);
//		   
//		    ArrayAdapter<CharSequence> flow_adapter = ArrayAdapter.createFromResource(this,
//		            R.array.dialog_velo_spinner, android.R.layout.simple_spinner_item);
//		    
//		    ArrayAdapter<CharSequence> depth_adapter = ArrayAdapter.createFromResource(this,
//		            R.array.dialog_depth_spinner, android.R.layout.simple_spinner_item);
//		    
//		    
//		    // Specify the layout to use when the list of choices appears
//		    type_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//		    flow_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//		    depth_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//		    
//		    
//		    // Apply the adapter to the spinner
//		    type.setAdapter(type_adapter);
//		    flow.setAdapter(flow_adapter);
//		    depth.setAdapter(depth_adapter);
//		
		
	
}

	
	    @Override 
	    public void onItemSelected(AdapterView<?> parent, View view, 
	            int pos, long id) {
	    	
	    		switch (parent.getId()) {  
	    	//	case R.id.dialog_type_spinner:  
	    	//		data.setFloodType(parent.getItemAtPosition(pos).toString());
	    	//		break;  
	    		case R.id.dialog_velo_spinner:  
	    			data.setFlowVelocity(parent.getItemAtPosition(pos).toString());
	    			break;  	
	    		case R.id.dialog_depth_spinner:  
	    			data.setFloodDepth(parent.getItemAtPosition(pos).toString());
	    			break;  
	    		
	    		}  

	    	
	    }
	        // An item was selected. You can retrieve the selected item using
	        // parent.getItemAtPosition(pos)
	    

	    public void onNothingSelected(AdapterView<?> parent) {
	        // Another interface callback
	    }




	
	

}