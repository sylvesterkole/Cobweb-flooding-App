package cobweb.addons;

import java.io.File;

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
	protected static final int POLYLINEDRAWNONIMAGE = 1324;
	private RelativeLayout MainPage; 
	private boolean WhatPointsImIOn= false;
	private boolean GotTwoPoints = false;
	
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
			  
			  MainPage.addView(preview);
			  
				
			    putPictureInToBeMarked = (ImageView) findViewById(R.id.imageView1);
			    
			    Bundle PassingInLocation = getIntent().getExtras();
			    LocationForPhoto =  (Uri) PassingInLocation.getParcelable(MediaStore.EXTRA_OUTPUT);
			    
			    Log.d("Setting up button Poly ","MyMarker"); 
			    SubmitPoints = (Button)findViewById(R.id.PolyImageHere);  
			    
			    SubmitPoints.setOnClickListener(new View.OnClickListener() {
			        @Override
			        public void onClick(View v) {
			        	 PressButtonToStorePoints(v);
			        }
			    });
			    
			    RelativeLayout v= (RelativeLayout) findViewById(R.id.AreaOfPolyImage);
			    v.setOnTouchListener(this);
			    
			 
			  setImage();
			    
			    //setimage
			     
		    
		    }
	// two surface views one for image and one for drawing the point 
	
	 private void setImage() {
		 
		 file = new File(LocationForPhoto.getPath());
			 
	 BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		 

			Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(),
					bmOptions);
			putPictureInToBeMarked.setImageBitmap(bitmap); 
			 
		}
	  
	 private void PressButtonToStorePoints(View v)
	 {
		setResult(POLYLINEDRAWNONIMAGE);  
		String GiveBackMarkerPostion =  "x1=" + x1 + ",y1=" + y1 + ":x2=" + x2 + ",y2=" +y2 ;
		this.getIntent().putExtra("LinePostion", GiveBackMarkerPostion);
		 finish(); 
	 }

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		// TODO Auto-generated method stub
		Log.d("Touching","MyMarker"); 
		if(!WhatPointsImIOn)
		{
			x1 = (int) arg1.getRawX() ;
			y1 = (int) arg1.getRawY();
			WhatPointsImIOn=!WhatPointsImIOn;
		}else
		{
			x2 = (int) arg1.getRawX() ;
			y2 = (int) arg1.getRawY();
			WhatPointsImIOn=!WhatPointsImIOn;
			GotTwoPoints=true;
		}
		
		if(GotTwoPoints)
		{
		preview.DrawLine(x1, y1, x2, y2);
		}
		
		SubmitPoints.setVisibility(Button.VISIBLE);
		return false;
	}
	 
	
	class MarkedPoint extends SurfaceView {

	    private final SurfaceHolder surfaceHolder;
	    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

	    public MarkedPoint(Context context) {
	        super(context);
	        surfaceHolder = getHolder();
	        surfaceHolder.setFormat(PixelFormat.TRANSLUCENT); 
	       paint.setColor(Color.RED);
	        paint.setStyle(Style.FILL);
	      //  setEGLConfigChooser(8, 8, 8, 8, 16, 0);
			  setZOrderOnTop(true);
			  
	    }

	    public void DrawLine(int x1,int y1,int x2,int y2)
	    {
	    	if (surfaceHolder.getSurface().isValid()) {
                Canvas canvas = surfaceHolder.lockCanvas(); 
                canvas.drawColor( Color.TRANSPARENT, PorterDuff.Mode.CLEAR );
                surfaceHolder.setFormat(PixelFormat.TRANSLUCENT);  
                canvas.drawLine(x1, y1, x2, y2, paint);
                
                surfaceHolder.unlockCanvasAndPost(canvas);
            }
	    }
	    

	}
	
			
	 
}



