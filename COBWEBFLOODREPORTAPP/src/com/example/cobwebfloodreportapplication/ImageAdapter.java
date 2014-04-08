package com.example.cobwebfloodreportapplication;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {
    
	private Context mContext;
	private List< Bitmap> items;

    public ImageAdapter(Context c, List< Bitmap> items) {
        mContext = c;
        this.items=items;
    }

    public int getCount() {
        return items.size();
    }

    public Object getItem(int pos) {  
        return items.get(pos);  
   } 

    public long getItemId(int position) {  
        return position;  
   }

 // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(100, 100));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
           // imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageBitmap(items.get(position));
        return imageView;
    }
}
