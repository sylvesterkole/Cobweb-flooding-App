package cobweb.addons;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.example.cobwebfloodreportapplication.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.graphics.PorterDuff.Mode;

/*
 * This Activity takes in an image from a bundle and returns a String object in the format "x,y" in the bundle with key "MarkedArea" with where on the screen the user pointed too 
 * 
 * If this happens successful in finishes with the result code set to 1323
 * else 
 * it returns result code  = 3231 
 *  
 * 
 */
public class PolylineOnImage extends Activity implements OnTouchListener{

	private   MarkedPoint preview=null;            // The surface view that will contain the camera image 
	private Canvas DrawMarker;
	private Uri LocationForPhoto;
	private  File file;
	private  ImageView putPictureInToBeMarked;
	private Button SubmitPoints; 
	private Button Reset ;
	protected static final int POLYLINEDRAWNONIMAGE = 1324;
	private RelativeLayout MainPage;  
	private float[][] LineToBeDrawn = new float[400][2];
	private int CurrentPosition=0;  
	
	private boolean  SubmitPointsFlag=false;
	
	private int x1=-1;
	private int y1=-1;
	private int x2=-1;
	private int y2=-1;
	 
	
	 public void onCreate(Bundle savedInstanceState) {
		    super.onCreate(savedInstanceState);
		    
		    
		    setContentView(R.layout.draw_poly_image);
			 //   Log.d(" set layout  started ","MyCamera");
			    //preview= (SurfaceView)findViewById(R.id.livefeed);
		    
		    MainPage = (RelativeLayout) findViewById(R.id.SurfaceAreaOfPolyImage);
		   
			  preview = new MarkedPoint(this);
			 
			  preview.getHolder().setFormat(PixelFormat.TRANSLUCENT);
			  
			  preview.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
			  
			  preview.setAlpha(0.5f);
			  
			  MainPage.addView(preview);
			  
			  for(int i=0;i<400;i++)
			  {
				  LineToBeDrawn[i][0]=-1;
				  LineToBeDrawn[i][1]=-1;
			  }
			 
				
			    putPictureInToBeMarked = (ImageView) findViewById(R.id.imageView1);
			    
			    Bundle PassingInLocation = getIntent().getExtras();
			    LocationForPhoto =  (Uri) PassingInLocation.getParcelable(MediaStore.EXTRA_OUTPUT);
			    
			    Log.d("Setting up button Poly ","MyMarker"); 
			    SubmitPoints = (Button)findViewById(R.id.PolyImageHere);  
			    
			    SubmitPoints.setOnClickListener(new View.OnClickListener() {
			        @Override
			        public void onClick(View v) {
			        	SubmitPointsFlag=true;
			        	 PressButtonToStorePoints(v);
			        }
			    });
			    
			    Reset  = (Button)findViewById(R.id.Reset);  
			    Reset.setOnClickListener(new View.OnClickListener() {
			        @Override
			        public void onClick(View v) {
			        	
			        	PressButtonToReset(v);
			        	  
			        }
			    });
			    
			    
			    RelativeLayout v= (RelativeLayout) findViewById(R.id.AreaOfPolyImage);
			    v.setOnTouchListener(this);
			    
			 
			  setImage();
			    
			    //setimage
			     
		    
		    }
	 
	 
	 public Bitmap decodeFile(File f) {
		    Bitmap b = null;
		    try {
		        // Decode image size
		        BitmapFactory.Options o = new BitmapFactory.Options();
		        o.inJustDecodeBounds = true;

		        FileInputStream fis = new FileInputStream(f);
		        BitmapFactory.decodeStream(fis, null, o);
		        fis.close();
		        int IMAGE_MAX_SIZE = 900;
		        int scale = 1;
		        if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
		            scale = (int) Math.pow(
		                    2,
		                    (int) Math.round(Math.log(IMAGE_MAX_SIZE
		                            / (double) Math.max(o.outHeight, o.outWidth))
		                            / Math.log(0.5)));
		        }

		        // Decode with inSampleSize
		        BitmapFactory.Options o2 = new BitmapFactory.Options();
		        o2.inSampleSize = scale;
		        fis = new FileInputStream(f);
		        b = BitmapFactory.decodeStream(fis, null, o2);
		        fis.close();
		    } catch (IOException e) {
		        e.printStackTrace();
		    }
		    return b;
		}
	 
	 /*
	  * Reset lines 
	  */
	 public void  PressButtonToReset(View v)
	 {
		 for(int i=0;i<400;i++)
		  {
			  LineToBeDrawn[i][0]=-1;
			  LineToBeDrawn[i][1]=-1;
		  }
		 
		 CurrentPosition=0; 
		 preview.EmptyDraw();
	 }
	 
	 
	// two surface views one for image and one for drawing the point 
	
	 private void setImage() {
		 
		 file = new File(LocationForPhoto.getPath());
			 
	 BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		 
	 
	 Bitmap bitmap = decodeFile(file);
 
			
			if (bitmap==null)
			{
				Toast.makeText(this,
						"SOMETHING HAPPENED!!! PICTURE did not load correctly, your phone is not compatible with the Cobweb Mark item plugin  ",
						Toast.LENGTH_LONG).show();
			}
			
			
			putPictureInToBeMarked.setImageBitmap(bitmap); 
			
			 putPictureInToBeMarked.setAlpha(0.5f); //put on top then alpha it 
			 
			 
		}
	  
	 private void PressButtonToStorePoints(View v)
	 {
		setResult(POLYLINEDRAWNONIMAGE);  
		String GiveBackMarkerPostion =  "x1=" + x1 + ",y1=" + y1 + ":x2=" + x2 + ",y2=" +y2 ;
		
		
		 
		 String PolyLineToGiveBack = "";
		 
		 for (int i=0 ; i < 399 &&  (LineToBeDrawn[i][0]) !=-1 ; i++)
		 {
			
					 PolyLineToGiveBack= PolyLineToGiveBack + LineToBeDrawn[i][0] +","+ LineToBeDrawn[i][1];
					 
					 if(!(i ==398 || (LineToBeDrawn[i+1][0]  ==-1) ))
					 {
						 PolyLineToGiveBack= PolyLineToGiveBack + ",";
					 }
					 
		 }
		 
		this.getIntent().putExtra("PolyLine", PolyLineToGiveBack);
		
		
		
		 finish(); 
	 }

	 public boolean onTouchEvent(MotionEvent me) {
		  
		 if(!SubmitPointsFlag)
		 {
			if(me.getAction() == MotionEvent.ACTION_MOVE && (CurrentPosition<399))
			{
				
				LineToBeDrawn[CurrentPosition][0] =  (int) me.getRawX() ;
				LineToBeDrawn[CurrentPosition][1] =  (int) me.getRawY() ;
				CurrentPosition++;
				  
				//
				
				Log.d( Float.toString(LineToBeDrawn[CurrentPosition][0]) + ":" + Float.toString(LineToBeDrawn[CurrentPosition][1])  , "MyCamera");
				
				preview.PolyLineDraw(); //works for continuous  tracking  //add on points 
			}
		 }
		  return false;
	 }
	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		 
		return false;
	}
	 
	
	class MarkedPoint extends SurfaceView {

	    private final SurfaceHolder surfaceHolder;
	    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

	    public MarkedPoint(Context context) {
	        super(context);
	        surfaceHolder = getHolder();
	        surfaceHolder.setFormat(PixelFormat.TRANSLUCENT); 
	    //   paint.setColor(Color.RED);
	    //    paint.setStyle(Style.FILL); 
	     //   paint.setStrokeWidth(3f);
	        
	        
	      //  setEGLConfigChooser(8, 8, 8, 8, 16, 0);
			  setZOrderOnTop(true);
			  
	    }
	     

	    public void DrawLine(int x1,int y1,int x2,int y2)
	    {
	    	   paint.setColor(Color.RED);
		        paint.setStyle(Style.FILL); 
		        paint.setStrokeWidth(3f);
	    	if (surfaceHolder.getSurface().isValid()) {
                Canvas canvas = surfaceHolder.lockCanvas(); 
                canvas.drawColor( Color.TRANSPARENT, PorterDuff.Mode.CLEAR );
                surfaceHolder.setFormat(PixelFormat.TRANSLUCENT);  
                canvas.drawLine(x1, y1, x2, y2, paint);
                
                paint.setColor(Color.BLUE); 
                canvas.drawCircle(x1, y1, 30, paint);
                
               
                paint.setColor(0xffff6600); // orange 
                canvas.drawCircle(x2, y2, 30, paint);
                
                surfaceHolder.unlockCanvasAndPost(canvas);
            }
	    }
	     
	    public void  PolyLineDraw()
	    { 
	    	int check=1;
	    	 paint.setStyle(Style.FILL); 
		        paint.setStrokeWidth(4f);
		        
	    	if (surfaceHolder.getSurface().isValid()) {
                Canvas canvas = surfaceHolder.lockCanvas(); 
                canvas.drawColor( Color.TRANSPARENT, PorterDuff.Mode.CLEAR );
                surfaceHolder.setFormat(PixelFormat.TRANSLUCENT);  
                
                
                paint.setColor(Color.BLUE); 
                
                while(  (check !=399) && (LineToBeDrawn[check][0]!= -1 ) )
   	    	 {
                	
   	    		 canvas.drawLine(LineToBeDrawn[check-1][0], LineToBeDrawn[check-1][1], LineToBeDrawn[check][0], LineToBeDrawn[check][1], paint);
   	    		 check++;
   	    	 }
                
                surfaceHolder.unlockCanvasAndPost(canvas);
            }
	    	  
	    }
	    
	    
	    public void  EmptyDraw()
	    {
	    	if (surfaceHolder.getSurface().isValid()) {
                Canvas canvas = surfaceHolder.lockCanvas(); 
                canvas.drawColor( Color.TRANSPARENT, PorterDuff.Mode.CLEAR );
                surfaceHolder.setFormat(PixelFormat.TRANSLUCENT);
                paint.setColor(Color.BLUE);  
                surfaceHolder.unlockCanvasAndPost(canvas);
            }
	    }
	    
	    public void DrawPoint(int x,int y)
	    {
	    	if (surfaceHolder.getSurface().isValid()) {
                Canvas canvas = surfaceHolder.lockCanvas(); 
                canvas.drawColor( Color.TRANSPARENT, PorterDuff.Mode.CLEAR );
                surfaceHolder.setFormat(PixelFormat.TRANSLUCENT);
                paint.setColor(Color.BLUE); 
                canvas.drawCircle(x, y, 30, paint);
                surfaceHolder.unlockCanvasAndPost(canvas);
            }
	    }
	    

	}
	
		
	 
}
 


