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
			  
			  preview.setAlpha(0.5f);
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
		 
	 BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		  
	 /*
			Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(),
					bmOptions);
			*/
	 
			Bitmap bitmap =  decodeFile(file);
			
			if (bitmap==null)
			{
				Toast.makeText(this,
						"SOMETHING HAPPENED!!! PICTURE did not load correctly, your phone is not compatible with the Cobweb Mark item plugin  ",
						Toast.LENGTH_LONG).show();
			}
			
			
			putPictureInToBeMarked.setImageBitmap(bitmap); 
			putPictureInToBeMarked.setAlpha(0.5f);
 
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
		preview.DrawPoint(x,y);
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



