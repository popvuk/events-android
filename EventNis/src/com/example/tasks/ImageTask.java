package com.example.tasks;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;

import com.example.custom.MyConnections;
import com.example.eventnis.R;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.AsyncTask;
import android.util.Log;
import android.util.TypedValue;
import android.widget.ImageView;

public class ImageTask extends AsyncTask<Void,Void,Bitmap>{

	private String slika;
	private final WeakReference<ImageView> img;
	static Context ctx;
	ProgressDialog pd;
	Boolean facebook;
	MyConnections mycon;

	public ImageTask(Context con, String slika, ImageView iv, Boolean face)
	{
		this.slika = slika.replaceAll(" ", "%20");
		this.img = new WeakReference<ImageView>(iv);
		ctx = con;
		this.facebook = face;
		this.mycon = new MyConnections(ctx);
	}
	
	@Override
	protected void onPreExecute() {
		
		pd = ProgressDialog.show(ctx, ctx.getString(R.string.app_name), ctx.getString(R.string.progress), true);
	}
	
	@Override
	protected Bitmap doInBackground(Void... params) {
		Bitmap bmp=null;
		String urlS = null;
		if(this.facebook)
		{
		   	 urlS = this.slika;
				 
		}
		else
		{
		    urlS = ctx.getString(R.string.url_image);
			urlS = urlS+"/"+this.slika;
		}
		
		InputStream in1 = null;
		InputStream in2 = null;
		HttpURLConnection httpConn1 = null;
		HttpURLConnection httpConn2 = null;
				
		try
		{		  
				    
			httpConn1 = mycon.getConnGet(urlS);
			httpConn1.connect();
			in1 = httpConn1.getInputStream();
				    
			final Options options = new Options();		    
			options.inJustDecodeBounds = true;	
			BitmapFactory.decodeStream(in1, null, options);
			in1.close();
			httpConn1.disconnect();
			options.inSampleSize = calculateInSampleSize(options, 340, 180);
				    
			// Decode bitmap with inSampleSize set
			options.inJustDecodeBounds = false;		
			httpConn2 = mycon.getConnGet(urlS);
			httpConn2.connect();
			in2 = httpConn2.getInputStream();	    
			bmp = BitmapFactory.decodeStream(in2, null, options);
			in2.close();
			httpConn2.disconnect();
					
				
		 }
		catch (Exception ex)
		{
			Log.d("Networking", ex.getLocalizedMessage());
		}
				
		return bmp;
	}
	
	public static int calculateInSampleSize(Options options, int widthDp, int heightDp) 
	{
    
		int height = options.outHeight;
        int width = options.outWidth;
        Resources r = ctx.getResources();
        int heightPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, heightDp, r.getDisplayMetrics());
        int widthPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, widthDp, r.getDisplayMetrics());
        
        int inSampleSize = 1;

        if (height > heightPx || width > widthPx)
        {
   
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
        
            while ((halfHeight / inSampleSize) > heightPx && (halfWidth / inSampleSize) > widthPx) 
            {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

	@Override
	protected void onPostExecute(Bitmap result) {
		final ImageView imageView = img.get();
		
		if(imageView != null)
		{
			imageView.setImageBitmap(result);
		}
		pd.cancel();
	}
}
