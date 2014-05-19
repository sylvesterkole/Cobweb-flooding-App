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
public class MarkItemOfInterestOnImage extends Activity implements OnTouchListener{

	private   MarkedPoint preview=null;            // The surface view that will contain the camera image 
	private Canvas DrawMarker;
	private Uri LocationForPhoto;
	private  File file;
	private  ImageView putPictureInToBeMarked;
	private Button SubmitPoints;
	protected static final int MARKERPLACEDONIMAGE = 1323; 
	private RelativeLayout MainPage; 
	
	private int x;
	private int y;
	
	 public void onCreate(Bundle savedInstanceState) {
		    super.onCreate(savedInstanceState);
		    
		    
		    setContentView(R.layout.mark_image);
			 //   Log.d(" set layout  started ","MyCamera");
			    //preview= (SurfaceView)findViewById(R.id.livefeed);
		    
		    MainPage = (RelativeLayout) findViewById(R.id.SurfaceAreaOfMarkedImage);
		   
			  preview = new MarkedPoint(this);
			 
			  preview.getHolder().setFormat(PixelFormat.TRANSLUCENT);
			  
			  preview.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
			  
			  MainPage.addView(preview);
			  
				
			    putPictureInToBeMarked = (ImageView) findViewById(R.id.imageView1);
			    
			    Bundle PassingInLocation = getIntent().getExtras();
			    LocationForPhoto =  (Uri) PassingInLocation.getParcelable(MediaStore.EXTRA_OUTPUT);
			    
			    Log.d("Setting up button ","MyMarker"); 
			    SubmitPoints = (Button)findViewById(R.id.MarkImageHere);  
			    
			    SubmitPoints.setOnClickListener(new View.OnClickListener() {
			        @Override
			        public void onClick(View v) {
			        	 PressButtonToStorePoints(v);
			        }
			    });
			    
			    RelativeLayout v= (RelativeLayout) findViewById(R.id.AreaOfMarkedImage);
			    v.setOnTouchListener(this);
			    
			 
			  setImage();
			    
			    //setimage
			     
		    
		    }
	// two surface views one for image and one for drawing the point 
	
	 private void setImage() {
		 
		 file = new File(LocationForPhoto.getPath());
			// Get the dimensions of the View
		 
			//int targetW = putPictureInToBeMarked.getWidth();
		//	int targetH = putPictureInToBeMarked.getHeight();

			// Get the dimensions of the bitmap
	 BitmapFactory.Options bmOptions = new BitmapFactory.Options();
			//bmOptions.inJustDecodeBounds = true;
		//	BitmapFactory.decodeFile( file.getAbsolutePath(), bmOptions);
			// pp = photoFile.getAbsolutePath();
		//	int photoW = bmOptions.outWidth;
	//		int photoH = bmOptions.outHeight;

			// Determine how much to scale down the image
			//int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

			// Decode the image file into a Bitmap sized to fill the View
		//	bmOptions.inJustDecodeBounds = false;
			//bmOptions.inSampleSize = scaleFactor;
		//	bmOptions.inPurgeable = true;

			Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(),
					bmOptions);
			putPictureInToBeMarked.setImageBitmap(bitmap); 
			 
		}
	 
	 
	 
	 private void PressButtonToStorePoints(View v)
	 {
		setResult(MARKERPLACEDONIMAGE);
		String GiveBackMarkerPostion =  "x=" + x + ",y=" + y ;
		this.getIntent().putExtra("MarkedArea", GiveBackMarkerPostion);
		 finish(); 
	 }

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		// TODO Auto-generated method stub
		Log.d("Touching","MyMarker"); 
		x = (int) arg1.getRawX() ;
		y = (int) arg1.getRawY();
		preview.DrawPoint( x,y);
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

	    public void DrawPoint(int x,int y)
	    {
	    	if (surfaceHolder.getSurface().isValid()) {
                Canvas canvas = surfaceHolder.lockCanvas(); 
                canvas.drawColor( Color.TRANSPARENT, PorterDuff.Mode.CLEAR );
                surfaceHolder.setFormat(PixelFormat.TRANSLUCENT); 
                canvas.drawCircle(x, y, 30, paint);
                surfaceHolder.unlockCanvasAndPost(canvas);
            }
	    }
	    

	}
	
			
	 
}



