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


public class High_River_Levels_Dialog {
	
 
	private boolean setDialog= false;
	private EditText text;
	private EditText text2;
	
	private Activity activity;
	
	private View dialogView;
    private AlertDialog.Builder builder;
    private DialogueData data;
	
	public DialogueData getData() {
		return data;
	}
 
	public High_River_Levels_Dialog(Activity act){
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
	    dialogView = inflater.inflate(R.layout.high_river_levels_dialog, null);
	    
	    // Pass null as the parent view because its going in the dialog layout
	    builder.setView(dialogView)
	    
	    .setTitle(R.string.Observation)
	    // Add action buttons
	           .setPositiveButton("OK", new DialogInterface.OnClickListener() {
	        	   
	        	   @Override
	               public void onClick(DialogInterface dialog, int id) {
	        		   
	        		   setDialog=true;
	        		   text=(EditText) dialogView.findViewById(R.id.note_text);
	        		      
	        		   text2=(EditText) dialogView.findViewById(R.id.highriver_text);
	        		   
	        		   data.setNote(text.getEditableText().toString() + text2.getEditableText().toString());
	        		   
	               }
	           })
	    		.setNegativeButton("", new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int id) {
	                   //LoginDialogFragment.this.getDialog().cancel();	             
	            	   }
	           }); 	   
	      
	    builder.show().setCanceledOnTouchOutside(false);
	     
	}
	 
}