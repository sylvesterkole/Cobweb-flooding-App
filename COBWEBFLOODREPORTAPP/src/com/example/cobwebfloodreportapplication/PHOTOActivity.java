package com.example.cobwebfloodreportapplication;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
 
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
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
import android.widget.Spinner;
import android.widget.Toast;
import cobweb.databaseObject.Submission;

public class PHOTOActivity extends Activity implements OnClickListener {

	public final static String SFOLDER = "COBWEB";

	private static final int REQUEST_IMAGE_CAPTURE = 100;
	protected static final int POLYGON = 101;
	private File photoFile = null;
	private Bitmap photoBit = null;
	private Button newButton;
	private Button addImage;
	private Button nextButton;
	private Button deleteImage;
	private Button submitImage;
	private ImageView imageView;

	// private LocationFinder loc;
	private File storageDir;
	private ArrayList<Bitmap> imageItem = new ArrayList<Bitmap>();
	private GridView gridview;
	private ImageAdapter imageAdapter;

	private Spinner type;
	private View dialogView;
	private boolean addInfo = false;
	private boolean askPoly = false;
	private DialogueData data;
	private GPSTracker location;
	private PHOTOActivity main = this;
	private Submission submitData;

	String pp;

	private HashMap<Bitmap, String> photoPath;
	ProgressDialog progress;

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
		submitData = new Submission();

		photoPath = new HashMap<Bitmap, String>();
		location = new GPSTracker(this);

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
			
			 controlledTakePictureIntent();
			// dispatchTakePictureIntent();
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

	}

	/*
	 * New way of taking an image using a controlled activity 
	 */
	
	private void controlledTakePictureIntent()
	{
		  
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
				
				//puts in file into the intent to get back the image 
				takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
						Uri.fromFile(photoFile));
				startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
			}
		}
	 
	}
	
	/*
	 * Old way of telling android to go grab a picture for us 
	 */
	private void dispatchTakePictureIntent() {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
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
				
				//puts in file into the intent to get back the image 
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
				+ "/" + SFOLDER + "/");

		// Create the storage directory if it does not exist
		if (!storageDir.exists()) {
			if (!storageDir.mkdirs()) {
				Log.d(SFOLDER, "failed to create directory");
				return null;
			}
		}

		// Create a media file name

		// imageFile = new File(storageDir.getAbsolutePath()+ "IMG_"+ timeStamp
		// + ".png");

		imageFile = File.createTempFile("IMG_" + timeStamp, ".png", storageDir);

		return imageFile;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
			setImage();
		} else if (requestCode == POLYGON && resultCode == RESULT_OK) {
			setsubmitButton();

		} else if (requestCode == POLYGON && resultCode == RESULT_CANCELED) {

			setsubmitButton();
		} else if (resultCode == RESULT_CANCELED) {
			Toast.makeText(this, R.string.photoCancel, Toast.LENGTH_LONG)
					.show();
		}

		else {
			Toast.makeText(this,
					"SOMETHING HAPPENED!!! PICTURE WAS NOT CAPTURED",
					Toast.LENGTH_LONG).show();
		}

	}

	 
	 
	/*
	 * Old set Image method 
	 */
	private void setImage() {

		// Get the dimensions of the View
		int targetW = imageView.getWidth();
		int targetH = imageView.getHeight();

		// Get the dimensions of the bitmap
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(photoFile.getAbsolutePath(), bmOptions);
		pp = photoFile.getAbsolutePath();
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
		setPhotoPaths(bitmap, photoFile.getAbsolutePath());

	}

	private void setPhotoPaths(Bitmap bitmap, String path) {

		if (!photoPath.containsKey(bitmap))
			photoPath.put(bitmap, path);
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
		if (!imageItem.isEmpty() && imageItem.size() > 1) {
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

		submitData.setVelocity(data.getFlowVelocity());
		submitData.setDepth(data.getFloodDepth());
		submitData.setType(data.getFloodType());
		submitData.setNote(data.getNote());
		submitData.setDate(new Date().toGMTString());

		SharedPreferences prefs = getSharedPreferences(SFOLDER, MODE_PRIVATE);
		submitData.setPolygone(prefs.getString("polygon", null));

		System.out.println("polygon " + prefs.getString("polygon", null));

		// Toast.makeText(this, "READY TO SEND \n Polygone :"+
		// submitData.getPolygon(), Toast.LENGTH_LONG).show();

		Toast.makeText(this, R.string.database, Toast.LENGTH_LONG).show();

		

		setData();

		this.finish();

	}

	private void setData() {

		SharedPreferences.Editor editor = getSharedPreferences(SFOLDER,
				MODE_PRIVATE).edit();

		editor.putBoolean("offline", true);
		editor.putString("Depth", submitData.getDepth());
		editor.putString("Note", submitData.getNote());
		editor.putString("Type", submitData.getType());
		editor.putString("Date", submitData.getDate());
		editor.putString("Flow", submitData.getVelocity());

		editor.putFloat("latitude", (float) submitData.getLatitude());
		editor.putFloat("longitude", (float) submitData.getLongitude());

		int size = submitData.getPhotoPaths().size();
		editor.putInt("noOfImage", size);

		Iterator<String> set = submitData.getPhotoPaths().iterator();
		int i = 0;
		while (set.hasNext()) {

			editor.putString("photo" + i, set.next());

			i++;
		}

		editor.commit();

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
				"Please draw a rough polygon sketch of the flooding area")
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
		submitData.setlocation(location.getLatitude(), location.getLongitude());

		return true;
	}

	private boolean setImages() {

		if (imageItem.isEmpty())
			return false;

		for (int i = 0; i < imageItem.size(); i++) {

			Bitmap bit = imageItem.get(i);
			String temp = photoPath.get(bit);
			submitData.addPhoto(temp, bit);
		}
		return true;

	}

}
