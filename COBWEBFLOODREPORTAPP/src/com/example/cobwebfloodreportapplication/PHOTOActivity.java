package com.example.cobwebfloodreportapplication;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

import com.example.cobwebfloodreportapplication.DataTypeDialogs.Flood_Damage_Dialog;
import com.example.cobwebfloodreportapplication.DataTypeDialogs.High_River_Levels_Dialog;
import com.example.cobwebfloodreportapplication.DataTypeDialogs.InitialDialog;
import com.example.cobwebfloodreportapplication.DataTypeDialogs.Notes_Dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

public class PHOTOActivity extends Activity implements OnClickListener {

	private static final int REQUEST_IMAGE_CAPTURE = 100;
	protected static final int POLYGON = 101;
	protected static final int MARKERPLACEDONIMAGE = 1323; 
	protected static final int POLYLINEDRAWNONIMAGE = 1324;
	private File photoFile = null;
	private Bitmap photoBit = null;
	private Button newButton;
	private Button addImage;
	private Button nextButton;
	private Button deleteImage;
	private Button submitImage;
	private ImageView imageView; 
	private File storageDir;
	private ArrayList<Bitmap> imageItem = new ArrayList<Bitmap>();
	private GridView gridview;
	private ImageAdapter imageAdapter;

	private boolean addInfo = false;
	private boolean askPoly = false;
	private DialogueData data;
	private DialogueData type_data;
	private GPSTracker location;
	private PHOTOActivity main = this;
	private  String MarkedPositon = ""; 
	private  String LinePositon = ""; 

	// String pp;

	private HashMap<String, Bitmap> photoPath;
	ProgressDialog progress;

	HashMap<String, String> polyLine;
	String fVel, fDepth, fType, fNote, fDate, polygon;
	double lat, lon;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo);
		imageView = (ImageView) findViewById(R.id.big_imageView);
		imageAdapter = new ImageAdapter(this, imageItem);
		gridview = (GridView) findViewById(R.id.gridView);
		gridview.setAdapter(imageAdapter);
		setGridView();
		setClickableButtons();
		// submitData = new Submission();

		polyLine = new HashMap<String, String>();
		photoPath = new HashMap<String, Bitmap>();
		location = new GPSTracker(this);
		
		/*
		 * Start initial dialog to get type 
		 */
		
		SetupInitalData_type(); 
		
		
		/*
		 * if (savedInstanceState == null) imageFile = null; else imageFile =
		 * new File(savedInstanceState.getString(IMAGE));
		 */

	}

	/*
	 * 
	 * @Override protected void onSaveInstanceState(Bundle outState) {
	 * super.onSaveInstanceState(outState); if (imageFile != null)
	 * outState.putString(IMAGE, imageFile.getAbsolutePath()); }
	 */

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.photo, menu);
		return true;
	}

	private void setClickableButtons() {

		newButton = (Button) findViewById(R.id.retake_button);
		newButton.setOnClickListener(this);

		addImage = (Button) findViewById(R.id.add_button);
		addImage.setOnClickListener(this);

		nextButton = (Button) findViewById(R.id.next_button);
		nextButton.setOnClickListener(this);

		deleteImage = (Button) findViewById(R.id.delete_button);
		deleteImage.setOnClickListener(this);

		submitImage = (Button) findViewById(R.id.submit_photo_button);
		submitImage.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.retake_button:
			if (photoBit != null)
				addItemsToList();
			dispatchTakePictureIntent();
			break;
		case R.id.add_button:
			addItemsToList();
			break;
		case R.id.delete_button:
			deleteItemsInList();
			break;
		case R.id.next_button:
			showNextItemsOnList();
			break;
		case R.id.submit_photo_button:

			if (!setImages()) {
				Toast.makeText(this, R.string.addPhot, Toast.LENGTH_SHORT)
						.show();
				break;
			}

			if (!setLocation()) {
				Toast.makeText(this, R.string.locUnknow, Toast.LENGTH_SHORT)
						.show();
				break;
			}

			if (!addInfo) {

				setAttachedInfo();
				break;
			}
			//
			// if(!askPoly){
			// requestPolygon();
			// break;
			// }

			setsubmitButton();
			break;

		}

	}

	private void setAttachedInfo() {

		// this help set addInfo completed if ok button is click in dialog
		if((type_data.getFloodType().compareTo("Flood water")== 0) || (type_data.getFloodType().compareTo("dwr llifogydd")==0)  )
		{
		
		final ImageDescriptionDialog dialog = new ImageDescriptionDialog(this);

		new Thread(new Runnable() {
			public void run() {
				while (!dialog.isSetDialog())
					;

				addInfo = dialog.isSetDialog();
				data = dialog.getData();

				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						requestPolygon();

					}
				});
			}
		}).start();
		
		}else if ((type_data.getFloodType().compareTo("High River Levels")== 0) || (type_data.getFloodType().compareTo("Lefelau Afonydd Uchel")==0)  )
		{
			final High_River_Levels_Dialog dialog = new High_River_Levels_Dialog(this);

			new Thread(new Runnable() {
				public void run() {
					while (!dialog.isSetDialog())
						; 
					addInfo = dialog.isSetDialog();
					data = dialog.getData();
					data.setFloodDepth("0");
					data.setFlowVelocity("0"); 
					runOnUiThread(new Runnable() { 
						@Override
						public void run() {
							requestPolygon();

						}
					});
				}
			}).start();
		}
		else if ((type_data.getFloodType().compareTo("Flood Damage")== 0) || (type_data.getFloodType().compareTo("Difrod llifogydd")==0)  )
		{
			final Flood_Damage_Dialog dialog = new Flood_Damage_Dialog(this);

			new Thread(new Runnable() {
				public void run() {
					while (!dialog.isSetDialog())
						; 
					addInfo = dialog.isSetDialog();
					data = dialog.getData();
					data.setFloodDepth("0");
					data.setFlowVelocity("0"); 
					runOnUiThread(new Runnable() { 
						@Override
						public void run() {
							requestPolygon();

						}
					});
				}
			}).start();
		}
		
		else
			
		{
			final Notes_Dialog dialog = new Notes_Dialog(this);

			new Thread(new Runnable() {
				public void run() {
					while (!dialog.isSetDialog())
						; 
					addInfo = dialog.isSetDialog();
					data = dialog.getData();
					data.setFloodDepth("0");
					data.setFlowVelocity("0"); 
					runOnUiThread(new Runnable() { 
						@Override
						public void run() {
							requestPolygon();

						}
					});
				}
			}).start();
		}
		
	}
	
	private void SetupInitalData_type() {

		// intitial Type to start the flow 
		  
	final InitialDialog openingdialog = new InitialDialog(this);
		
		
		type_data = openingdialog.getData();
		

		new Thread(new Runnable() {
			public void run() {
				while (!openingdialog.isSetDialog())
					;
 
				type_data = openingdialog.getData();

				 
			}
		}).start();

	}

	private void dispatchTakePictureIntent() {
		 Intent takePictureIntent = new Intent(PHOTOActivity.this,cobweb.addons.QualityControlledCamera.class);
		// Ensure that there's a camera activity to handle the intent
		if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
			// Create the File where the photo should go
			try {
				photoFile = createImageFile();
				// Toast.makeText(this, photoFile.getAbsolutePath() ,
				// Toast.LENGTH_LONG).show();
			} catch (IOException ex) {
				// Error occurred while creating the File

			}
			// Continue only if the File was successfully created
			if (photoFile != null) {
				takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
						Uri.fromFile(photoFile));
				startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
			}
		}
	}

	private File createImageFile() throws IOException {
		File imageFile = null;

		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());

		storageDir = new File(Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_PICTURES).getAbsolutePath()
				+ "/" + Constant.SFOLDER + "/");

		// Create the storage directory if it does not exist
		if (!storageDir.exists()) {
			if (!storageDir.mkdirs()) {
				Log.d(Constant.SFOLDER, "failed to create directory");
				return null;
			}
		}

		// Create a media file name

		// imageFile = new File(storageDir.getAbsolutePath()+ "IMG_"+ timeStamp
		// + ".png");

		imageFile = File.createTempFile("IMG_" + timeStamp, ".png", storageDir);

		return imageFile;
	}

	void addPolyLine(String path, String line) {
		polyLine.put(path, line);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
			setImage();
			
			//call activity to mark the image here
			
			//call activity to generate a polyline here
			
			
			
		} else if (requestCode == POLYGON && resultCode == RESULT_OK) {
			setsubmitButton();

		} else if (requestCode == POLYGON && resultCode == RESULT_CANCELED) {

			setsubmitButton();
		} else if (resultCode == RESULT_CANCELED) {
			Toast.makeText(this, R.string.photoCancel, Toast.LENGTH_LONG)
					.show();
		} else if (resultCode == MARKERPLACEDONIMAGE)
		{
			Toast.makeText(this, R.string.watermarked, Toast.LENGTH_LONG)
			.show();
			
		//	 MarkedPositon = data.getStringExtra("MarkedArea");
			
		 	 Intent LineImage = new Intent(PHOTOActivity.this,cobweb.addons.PolylineOnImage.class);
			 
		 	 
		 	 Log.d("test","myApp");
		 	 
		 	 if (photoFile != null) {
					LineImage.putExtra(MediaStore.EXTRA_OUTPUT,
							Uri.fromFile(photoFile));
					startActivityForResult(LineImage, REQUEST_IMAGE_CAPTURE);
				}
			
		}else if (resultCode == POLYLINEDRAWNONIMAGE)

		{
			//LinePositon = data.getStringExtra("LinePostion");
			
			Toast.makeText(this, R.string.polyline, Toast.LENGTH_LONG)
			.show();
		}
		else {
			Toast.makeText(this,
					"SOMETHING HAPPENED!!! PICTURE WAS NOT CAPTURED",
					Toast.LENGTH_LONG).show();
		}

	}

	private void setImage() {

		// Get the dimensions of the View
		int targetW = imageView.getWidth();
		int targetH = imageView.getHeight();

		// Get the dimensions of the bitmap
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(photoFile.getAbsolutePath(), bmOptions);
		// pp = photoFile.getAbsolutePath();
		int photoW = bmOptions.outWidth;
		int photoH = bmOptions.outHeight;

		// Determine how much to scale down the image
		int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

		// Decode the image file into a Bitmap sized to fill the View
		bmOptions.inJustDecodeBounds = false;
		bmOptions.inSampleSize = scaleFactor;
		bmOptions.inPurgeable = true;

		Bitmap bitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath(),
				bmOptions);
		imageView.setImageBitmap(bitmap);
		photoBit = bitmap;
		String path = photoFile.getAbsolutePath();
		if (!photoPath.containsKey(path))
			photoPath.put(path, bitmap);
		
		/*
		 * We have gotten image so now to add on a polyline or polypoint 
		 */
		if((type_data.getFloodType().compareTo("Flood water")== 0) || (type_data.getFloodType().compareTo("dwr llifogydd")==0)  )
		{
			 Intent MarkImage = new Intent(PHOTOActivity.this,cobweb.addons.MarkItemOfInterestOnImage.class);
		    
			if (photoFile != null) {
				MarkImage.putExtra(MediaStore.EXTRA_OUTPUT,
						Uri.fromFile(photoFile));
				startActivityForResult(MarkImage, REQUEST_IMAGE_CAPTURE);
			}
			
			
				
			
		}

	}

	private void setGridView() {

		gridview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int pos,
					long id) {

				// Toast.makeText(main, "" + photoPath.get(imageItem.get(pos)),
				// Toast.LENGTH_SHORT).show();
			}
		});

	}

	private void addItemsToList() {
		if (photoBit != null && !imageItem.contains(photoBit)) {
			imageItem.add(photoBit);

			imageAdapter.notifyDataSetChanged();
		}
	}

	private void deleteItemsInList() {
		Bitmap temp = photoBit;
		imageView.setImageBitmap(null);
		if (!imageItem.contains(photoBit))
			if (imageItem.size() == 0)
				photoBit = null;
			else {
				showNextItemsOnList();
				imageItem.remove(temp);
			}
		else {
			if (imageItem.size() == 1) {
				imageItem.remove(temp);
				photoBit = null;
			} else {
				showNextItemsOnList();
				imageItem.remove(temp);
			}
		}
		

		imageAdapter.notifyDataSetChanged();

	}

	private void showNextItemsOnList() {
		addItemsToList();
		if ( imageItem.size() > 1) {
			int i = imageItem.indexOf(photoBit);
			if (i < (imageItem.size() - 1)) {
				photoBit = imageItem.get(++i);
				imageView.setImageBitmap(photoBit);

			} else if (i == (imageItem.size() - 1)) {

				photoBit = imageItem.get(0);
				imageView.setImageBitmap(photoBit);
			}
		} else if (imageItem.size() == 1) {
			photoBit = imageItem.get(0);
			imageView.setImageBitmap(photoBit);
		} else
			imageView.setImageBitmap(null);

	}

	private void setsubmitButton() {

		// fVel,fDepth,fType,fNote,fDate;
		fVel = data.getFlowVelocity();
		fDepth = data.getFloodDepth();
		fType = type_data.getFloodType();
		fNote = data.getNote();
		fDate = new Date().toGMTString();

		SharedPreferences prefs = getSharedPreferences(Constant.SFOLDER,
				MODE_PRIVATE);
		polygon = prefs.getString(Constant.POLYPR, null);

		// System.out.println("polygon " + prefs.getString("polygon", null));

		// Toast.makeText(this, "READY TO SEND \n Polygone :"+
		// submitData.getPolygon(), Toast.LENGTH_LONG).show();

		// Toast.makeText(this, R.string.database, Toast.LENGTH_LONG).show();

		setData();

		imageItem.clear();
		imageAdapter.notifyDataSetChanged();
		this.finish();

	}

	private void setData() {

		final Set<String> phPath = photoPath.keySet();

		SharedPreferences prefs = getSharedPreferences(Constant.SFOLDER,
				MODE_PRIVATE);

		final int oid = prefs.getInt(Constant.NUMOBS, 0);
		
		final Context context = this;
		new Thread() {
			public void run() {

				DatabaseHelper db = new DatabaseHelper(context);
				for (String s : phPath) {
					String line = polyLine.get(s);
					if (line == null)
						db.insertImageTable(s, oid);
					else
						db.insertImagePoly(s, oid, line);
				}
				
				fNote = fNote + MarkedPositon + LinePositon;
				db.updateMetaObs(oid, fDepth, fNote, fType, fDate, fVel, lat,
						lon, polygon);
				db.close();
			}
		}.start();

	}

	private boolean requestPolygon() {

		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				/*
				 * switch (which) { case DialogInterface.BUTTON_POSITIVE:
				 */
				Intent mapintent = new Intent(main, PolygonLayerMap.class);

				startActivityForResult(mapintent, POLYGON);
				// start for result
				/*
				 * break;
				 * 
				 * case DialogInterface.BUTTON_NEGATIVE: // No button clicked //
				 * Submit everything setsubmitButton(); break; }
				 */

			}
		};

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(
		// "Do you want to draw a rough polygon sketch of flooding area?").
				R.string.pleasedrawapolygon)
				.setNeutralButton("Ok", dialogClickListener).show()
				// .setNegativeButton("No", dialogClickListener).show()
				.setCanceledOnTouchOutside(false);

		askPoly = true;
		return askPoly;
	}

	private boolean setLocation() {

		if (!location.canGetLocation()) {
			// can't get location
			// GPS or Network is not enabled
			// Ask user to enable GPS/network in settings
			location.showSettingsAlert();

			return false;
		}
		lat = location.getLatitude();
		lon = location.getLongitude();

		return true;
	}

	private boolean setImages() {

		if (imageItem.isEmpty())
			return false;

		return true;

	}

}
