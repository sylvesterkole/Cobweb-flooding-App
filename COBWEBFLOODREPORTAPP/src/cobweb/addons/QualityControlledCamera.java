package cobweb.addons;
 
  
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.example.cobwebfloodreportapplication.R;
 
 
import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;



/*
 *  
 *  University College Dublin 
 *  Coded by Abraham Campbell for COBWEB FP 7 project 
 * 
 * 
 */

public class QualityControlledCamera extends Activity implements SensorEventListener{
 
	private boolean OkayToTakePhoto = false;     // Flag to check  if the phone is in the right orientation 
	private SurfaceView preview=null;            // The surface view that will contain the camera image 
	private SurfaceHolder previewHolder=null;	
	private Camera camera=null;					  
	private boolean inPreview=false;             //Flag to check if in preview 
	private boolean cameraConfigured=false;		 //Flag to check if the camera has been configured 
	private Button TakeButton;                   // Button to take the picture   
	private ImageView StatusToTakeImage;         // Side bar goes from RED-> GREEN when you can take an image 
	private SensorManager mSensorManager;        // sensor manager to gain access to the phone sensors 
	private Sensor mAccelerometer; 				 //  Sensor Accelerometer to gain access to the phone sensors 
	private String NameOfAccelerometer;          // check in case we add more sensors 
	private float[] Accel_Readings;              // storing current Accelerometer data 
	private TextView TextButton;  				 // test TextView to show Accelerometer data , commented out at present 
	private Uri LocationForPhoto; 				 // Location where the photo should be stored 
	private boolean WaitForPhotoToBeStored=true; // once the photo is taken , don't allow any other photo's to be taken. 
	private boolean TimeOut=false; 				 //if autofocus takes too long and times out 
	 
	  @Override
	  public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setupCamera();
	    
	    setupUI(); 
	  //  Log.d( "grabing location" ,"MyCamera"); 
	    Bundle PassingInLocation = getIntent().getExtras();
	    LocationForPhoto =  (Uri) PassingInLocation.getParcelable(MediaStore.EXTRA_OUTPUT);
	  //  Log.d( "grabbed location" ,"MyCamera");
	  //  Log.d( LocationForPhoto.toString(),"MyCamera");
	  setupSensors();
	     
	  }
	  
	  @Override
	  public void onResume() {
	    super.onResume();
	    
	   // Log.e(" about to open camera ","MyCamera");
	    camera=Camera.open();
	    if (camera==null)
	    {
	  //  Log.e(" Camera object null ","MyCamera");
	    }
	    startPreview();
	  }
	    
	  @Override
	  public void onPause() {
	    if (inPreview) {
	      camera.stopPreview();
	    }
	    camera.release();
	    camera=null;
	    inPreview=false;
	          
	    super.onPause();
	  }
	  
	  private void TakePhoto()
	  {
		  
		  //test code 
		   
		  if (WaitForPhotoToBeStored)
		  {
			//  Log.d("Taking picture ","MyCamera");
			  WaitForPhotoToBeStored=false;
		 	
			  camera.autoFocus(AutoFocusCheck)  ;
		  }else
		  {
			  if (TimeOut)
			  {
				  WaitForPhotoToBeStored=true;  
			  }
		  } 
	  }
	  
	  private void  FinishtakingPhoto()
	  {
		//	Log.d("About to exit", "MyCamera");
			  //Informing the previous activity that photo was taken 
			//setResult(0);   
			setResult(RESULT_OK);  
			  //Finishing this activity and going back to PHOTOActivity 
			  finish();
	  }
	  
	  private Camera.Size getBestPreviewSize(int width, int height,
	                                         Camera.Parameters parameters) {
	    Camera.Size result=null;
	    
	    for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
	      if (size.width<=width && size.height<=height) {
	        if (result==null) {
	          result=size;
	        }
	        else {
	          int resultArea=result.width*result.height;
	          int newArea=size.width*size.height;
	          
	          if (newArea>resultArea) {
	            result=size;
	          }
	        }
	      }
	    }
	    
	    return(result);
	  }

	private Camera.Size getBestFullPhotoSize(Camera.Parameters parameters) {
		Camera.Size result = null;
	 
		for (Camera.Size size : parameters.getSupportedPictureSizes()) {

			int newArea = size.width * size.height;
			
			if (result == null  &&    (newArea < 2100000) ) {
				result = size;
			  
			} 
			  
			if(result != null ) 
				
			if (size.width >= result.width
					&& size.height >= result.height) {

				int resultArea = result.width * result.height;
			

				if (newArea > resultArea ) {
					result = size; 
				}
			}
			 
		}

		if (result == null) {
			 
			Toast.makeText(getApplicationContext(), "Cobweb Citzen Observation Camera is not compatible with your phone",
					   Toast.LENGTH_LONG).show();
			 
		//	Log.d("Did not setup image size right", "MyCamera");
		} else {
	 	Log.d(" width =" + result.width + " Height = " + result.height,
					"MyCamera");
		}
		return (result);
	}
	  
	  
	  public void PressButtonToTakePhoto(View v)
	  {
		  if(OkayToTakePhoto){
		  Toast.makeText(getApplicationContext(), "Taking Photo", Toast.LENGTH_SHORT).show();
		  TakePhoto();
		  
	  }else
	  {
		  //safety line 
		  //just in case button does not disappear in a given android version 
		  Toast.makeText(getApplicationContext(), "Please only press the button when sidebar is Green", Toast.LENGTH_LONG).show();
	 
	  }	   
	  }
	   
	  private void CheckIfRight(float x,float y, float z)
	     {
	    	 if(x <10 && x>8 && y<1 && y>-1 && z< 1 && z> -1  )
	    	 {
	    		 OkayToTakePhoto=true;
	    			   Drawable GreenAfter = getResources().getDrawable(R.drawable.green);
	    			   StatusToTakeImage.setImageDrawable(GreenAfter);
	    			   if( TakeButton!=null)
	    			   {
	    			   TakeButton.setVisibility(Button.VISIBLE);
	    			   }else
	    			   {
	    				   TakeButton = (Button)findViewById(R.id.takebutton); 
	    				   TakeButton.setVisibility(Button.VISIBLE);
	    			   }
	    		 
	    		 
	    	 }else
	    	 {
	    		 OkayToTakePhoto=false;
	    		 Drawable RedAfter = getResources().getDrawable(R.drawable.red);
	  		     StatusToTakeImage.setImageDrawable(RedAfter);
	  		     
	  		   if( TakeButton!=null)
			   {
			   TakeButton.setVisibility(Button.INVISIBLE);
			   }else
			   {
				   TakeButton = (Button)findViewById(R.id.takebutton); 
				   
				    if ( TakeButton!=null)
				    {
				   TakeButton.setVisibility(Button.INVISIBLE); // crashing for some reasome
				    }
			   }
	    	 }
	    	 
	     }
			   
	  private void initPreview(int width, int height) {
		  Camera.Parameters parameters=camera.getParameters();
		  
		  /*
		   * Setup for preview screen
		   */
	    if (camera!=null && previewHolder.getSurface()!=null) {
	      try {
	        camera.setPreviewDisplay(previewHolder);
	      }
	      catch (Throwable t) {
	          Log.e("error in init","myCamera");
	      }

	      if (!cameraConfigured) {
	       
	        Camera.Size size=getBestPreviewSize(width, height,
	                                            parameters);
	        
	        if (size!=null) {
	          parameters.setPreviewSize(size.width, size.height);
	          
	          
              /*
               * Setting up camera format and future picture to be taken 	           
               */
	         
			  Camera.Size BestPicture = getBestFullPhotoSize( parameters);  
			  parameters.setPictureSize(BestPicture.width, BestPicture.height); 
			 // parameters.setPictureFormat(ImageFormat.YUY2); // if needed to change the format of the image 
	          camera.setParameters(parameters); 
	          cameraConfigured=true;
	        }
	      }
	    }
	    
	    
	    
	  }
	  
	  private void startPreview() {
	    if (cameraConfigured && camera!=null) {
	      camera.startPreview();
	      inPreview=true;
	    }
	  }
	   
	 /*
	  * Auto focus check on image 
	  */
	  
	   AutoFocusCallback AutoFocusCheck = new  AutoFocusCallback ()
	   {
		   @Override
		    public void onAutoFocus (boolean success, Camera camera) 
		   {
			   if (success)
			   {
				   camera.takePicture(null, null ,png);
			   }else
			   {
				   TimeOut=true;
			   }
		   }
	   };
			   
			   
		/*
		 * Storing as .png but really its already been through jpeg compression, we would have to go hardware native to access raw image so that something we need to think about in the future . 	   
		 */
			   
         PictureCallback png = new PictureCallback(){ 
             @Override 
             public void onPictureTaken(byte[] data, Camera camera) { 
                 // TODO Auto-generated method stub 
            	  Log.d("onPicture", "MyCamera");
            	  camera.stopPreview();
            	  if(data==null)
            	  {
            	  Log.d("Nothing in the Data", "MyCamera");
            	  }
            	  FileOutputStream outStream = null;   
                  File file = new File(LocationForPhoto.getPath());
                 Log.d("path set to " + LocationForPhoto.getPath(), "MyCamera");
                 try {                       
                     outStream = new FileOutputStream(file.toString());  
                     Log.d("About to  Writing ", "MyCamera");
                       outStream.write(data);  
                     Log.d("Finished Writing ", "MyCamera");
                     outStream.close(); 
                     FinishtakingPhoto();
                 } catch (FileNotFoundException e) { 
                	 Log.d("Cant find file", "MyCamera"); 
                 } catch (IOException e) { 
                     // TODO Auto-generated catch block 
                	 Log.d("IO exception", "MyCamera"); 
                 } 
             }}; 
         
             /*
       	   * Trying new method , my old preview code and android main site does not work so see if this creates the right surface 
       	   * 
       	   */
	  SurfaceHolder.Callback surfaceCallback=new SurfaceHolder.Callback() {
	    public void surfaceCreated(SurfaceHolder holder) {
	     
	    }
	    
	    public void surfaceChanged(SurfaceHolder holder,
	                               int format, int width,
	                               int height) {
	      initPreview(width, height);
	      startPreview();
	    }
	    
	    public void surfaceDestroyed(SurfaceHolder holder) {
	      
	    }
	  };
	  
	  
	  /*
	   * Filter class if need to average out readings for any sensor ,delete if not needed later 
	   */
	   
		private class Filter {
			static final int AVERAGE_BUFFER = 3;
			float[] m_arr = new float[AVERAGE_BUFFER];
			int m_idx = 0;

			public float append(float val) {
				m_arr[m_idx] = val;
				m_idx++;
				if (m_idx == AVERAGE_BUFFER)
					m_idx = 0;
				return avg();
			}

			public float avg() {
				float sum = 0;
				for (float x : m_arr)
					sum += x;
				return sum / AVERAGE_BUFFER;
			}

		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub
			
		}

		/*
		 *  
		 * This method is called whenever the sensors on the accelerometer changes, they record the values and then call  CheckIfRight to see if the readings 
		 * are in the right range , if so the take picture button will show
		 * 
		 */
		@Override
		public void onSensorChanged(SensorEvent event) {
			String SensorName = event.sensor.getName();
			
			//if check just in case we later add more sensors to this class 
         if (SensorName.compareTo(NameOfAccelerometer) == 0) {
        	 
        		if (Accel_Readings == null) {
        			Accel_Readings = new float[3];
				}
        	 System.arraycopy(event.values, 0, Accel_Readings, 0, 3);
				 
			 CheckIfRight(Accel_Readings[0],Accel_Readings[1],Accel_Readings[2]);
				
			} 

				
			
		}
		
		private void setupSensors() {
		 
			mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
			mAccelerometer = mSensorManager
					.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		  
			NameOfAccelerometer = mAccelerometer.getName();
		 
			mSensorManager.registerListener(this, mAccelerometer,
					SensorManager.SENSOR_STATUS_ACCURACY_HIGH);
			 
 
		}
		
		private void setupCamera()
		{
			// Log.d(" Camera started ","MyCamera");
			    setContentView(R.layout.newcamera);
			 //   Log.d(" set layout  started ","MyCamera");
			    preview=(SurfaceView)findViewById(R.id.livefeed);
			//    Log.d(" get surface view   started ","MyCamera");
			    previewHolder=preview.getHolder();
			    previewHolder.addCallback(surfaceCallback);
			 //   Log.d(" Adding it  started ","MyCamera");
			    
			    // need for old android 3.0 phones  
			    if (android.os.Build.VERSION.SDK_INT< android.os.Build.VERSION_CODES.HONEYCOMB) {
			    	 previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
			    	} 
		}
		
		private void setupUI()
		{
			 
			  TakeButton = (Button)findViewById(R.id.takebutton); 
			  StatusToTakeImage = (ImageView)findViewById(R.id.StatusToTakeImage);
			  TextButton = (TextView)findViewById(R.id.AccelReadings);
		}
		 
	}


/*

                                     :+,~=+,:.+~:.                                                  
                                     :=:+~+.,.+=:,          ...  ...                                
                         .        ,=?III$7O8IOM7$7$I+=?$$I+=+7$$$IIIIIIIII?.                        
            ~:+====~~=~+I~=IZO?~=+I$Z$7777II????III??+~=====?77=~+==?77+~=+=+.                      
        .:MMMMMMMMMMMMMMMM=~~~~~=+7Z+::~~=?7?::~===I$+.:=~~+I$=,:=+=+II==++++++.                    
       .~MMMMMMMMMMMMMMMMMMM==,,~===IZ+.,=~==I$?=~====~~=================+++++++                    
      .~NNNMMMMMMMMMMMMMMMMMMM~===============================+====++:.,=+==??~:,                   
      +NNNMMMMMMMMMMMMM,::::::MM~===~=~=~=~~~==~,..:====7:..,~+==+I~:::=++=I~=:,=I                  
     .DNNNMMMMMMMMMM=ODM~:::::~N:~I7,..,~==~+I,,..~==~??,~,,:===+=,=.,=+=++~+,:~+?=                 
    .+DNNNMMMMMMMM=O8::::.:,~::=,D?I=,,,,~~~=+=,:,,~~=~+~~~,,~===+:?:.~==+++=~~~~~=7?.              
    .NNNNNMMMMMMM+ZN::,,:~::~==??I~?=?,::.,~~~=+,+:.:~====~~::~:~::::~:::::::::::::~~=7.            
    .DNNNNMMMMMM=$.:,,,.::~.~.?++77=~==~:::::::::::::,:,::::::,,,:::,:,:,:::::::::::..=Z            
    ~NNNNNMMMMM?$M:::.::.~==++7I777ZZ:,,:,,,,,,,,,,,,,,,,,,,,,,,,,,:,,:,,,::::,:::::::=O            
    ~DNNNNMMMMM$M:,:::.~~$ODNMM$DZ$$I:,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,::::::::::~~::~:+Z            
    .NNNMMMMMM?$M:~::~~7$+?$Z8DMZMM$7$I.,,,,,,,,,,,,,,,,,,,:,,:,:::::::~=7I+~:,?,=~~=:+7            
    .MINDDNNMM77~:,.:~7I+?7O88DNMOMMNMZ,,,,,,,,,,,,,,,:::~7II::,+~:~~~=:+O~~~~~~~~=~:~++            
    .OIDDDNNMMIM~~::~~$+I7ZO8NNNMM8MZIM?,,,+II+:,,=::~~:~::$7:~~~~~~~~~~~~ND8N:M+NM~:~?.            
     ??NDDDNMM?M~~,:,~OIIZ8DDNND8MMMZ$M?~::.=8~~~:~~~~:~~~~~~~~~~~~~~~~~~=N~MM$~~~~~:~7             
     .INDDDNMM+M~~:~~~O$$8DNMMMMMMMMM$??~::~~~=~~~~~~~~~:DZ=+~$N=:~~~~~~~~~~~I,===~=?+I.            
     .I?DDDNMM+M~===:=DZODNMMMMMMMMMDO??=~~~:$:~.~~I~~~~~~~~~:~:+?==~===?7~~:::==+==++7.            
     .7IDDDNMM+=~~==~=N8NDMMMMMMMMMMM8N?+~:::~~~::~~Z.~:~~~~+7~::,:~~~==~=+==+=+=+++7~I?            
      ?N=INNDN=?M~=:~?7MMMMMMMMMMMMM8$MM=.~::::~+I~::,,:~~~~=~~~=~======?7I????+,=Z,:$~:,           
      .N8=7.. .ID~==~?I8MMMMMMMMMMMDMMNZM ,:~~~~~~~~~~==~~==+7+I+++++++=,,+=+++:,?$,,Z~:.           
              .:IM=?+~$I~MMMMMMNMMM88MM:=?:~~~=~~?I?+=+++++=++=+++++++=:,,++++I+:,.,:,?7.           
               .,I77O78DNNM$8M8N8$NMMM?:~~~~~===Z=D========++++++?????:::+IIIZ7=,,.,:???.           
                 =7N8D$~$ZNMMMNNM8MDMZ8~::::==??=I=+M+=++=???IIII777++=II777$7?=,..,O.I$.           
                 .:7MDMMMN=Z7N8MNDMM~Z:::::=+==+D=+M++?++I77$$$ZOO77I7ZOZ=+I7I?~,,,,Z,==,           
                  .+I7MMMMMMNNNDMMN,?===~~=++++8??+????77777IZZZZZ$ZI ?=~~=?I?+=,,.,$:+=:           
                    .:777MMMMMMMI,+7II??+?IIO?7M8?III.       .        ?~~~~+???=,.,,$~=~=           
                       ..~~77::,OOOZ$$7I~II8I7??I,                    I~~~=+???~,,.:7===+           
                          .NNNDDD88O?I.. ..                          .I~~~==???=:,.:7===I           
                            .  .                                     .I~:~==???~,,,:?++=I           
                                                                    .I8,:~~=+I?+:,,:7==+7           
                                                               . +:.+I:,::~++??=:..,,?++?           
                                                              .?$ZI?7DD8OO8O:?+I?==~~=+?$I???+.     
                                                             .=?~:~=I7OOO??+,IO:O7I???++++?I7ZZ.    
                                                             .++~:,::::~=+III?+==,........,:=7Z.    
                                                             .?+~::::::~==?+?++:,,.....,,.:~=?$.    
                                                              ..DZ::::~~=+?+?==~:,,......::~=?Z     
                                                                    ....~DM... MMMMMMMMMNM=...       
*/