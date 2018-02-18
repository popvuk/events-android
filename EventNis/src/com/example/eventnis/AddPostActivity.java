package com.example.eventnis;

import java.io.File;

import org.json.JSONArray;
import org.json.JSONObject;

import com.example.tasks.GetTask;

import com.example.tasks.AddPostTask;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
//import android.util.Log;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;


public class AddPostActivity extends AppCompatActivity{

	String filePath;
	String [] items;
	int [] values;
	Context ctx = this;
	JSONObject obj=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_addpost);
		GetTask kategorije = new GetTask(ctx);
	
		try
		{
			JSONArray katjson = kategorije.execute().get();
			items = new String[katjson.length()];
			values = new int[katjson.length()];
			
			for(int i = 0;i<katjson.length();i++)
			{
				obj = katjson.getJSONObject(i);
				
				items[i]=obj.get("item").toString();
				Log.d("poruka", items[i]);
				values[i]=obj.getInt("value");
				
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		final Spinner kat = (Spinner) findViewById(R.id.kategorije);
		ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items);
		spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
		kat.setAdapter(spinnerArrayAdapter);
		
		Button bttn_select = (Button) findViewById(R.id.select);
		bttn_select.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {

				Intent intent = new Intent();
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
			}
		});
		
		Button bttn_add = (Button) findViewById(R.id.bttn_addpost);
		bttn_add.setOnClickListener(new View.OnClickListener() {
		
			@Override
			public void onClick(View v) {
								
				EditText nas = (EditText) findViewById(R.id.naslov);		
				EditText poc = (EditText) findViewById(R.id.dat_poc);
				EditText zav = (EditText) findViewById(R.id.dat_zav);
				EditText vre = (EditText) findViewById(R.id.vreme);
				EditText lok = (EditText) findViewById(R.id.lokacija);
			    EditText sl = (EditText) findViewById(R.id.slika);
				EditText txt = (EditText) findViewById(R.id.post_tekst);
				
				String naslov = nas.getText().toString();
				int item = (int) kat.getSelectedItemId();//id pocinje od 0
				String kategorija = String.valueOf(values[item]);
				String dat_poc = poc.getText().toString();
			    String dat_zav = zav.getText().toString();
			    String vreme = vre.getText().toString();
			    String lokacija = lok.getText().toString();
			    String slika = sl.getText().toString();
			    String tekst = txt.getText().toString();
			    String empty = ctx.getString(R.string.empty);
			    if(naslov.matches(""))
			    {
			    	nas.setError(empty);
			    	return;
			    }
			    if(dat_poc.matches(""))
			    {
			    	poc.setError(empty);
			    	return;
			    }
			    if(dat_zav.matches(""))
			    {
			    	zav.setError(empty);
			    	return;
			    }
			    if(vreme.matches(""))
			    {
			    	vre.setError(empty);
			    	return;
			    }
			    if(lokacija.matches(""))
			    {
			    	lok.setError(empty);
			    	return;
			    }
			    if(slika.matches(""))
			    {
			    	sl.setError(empty);
			    	return;
			    }
			    if(tekst.matches(""))
			    {
			    	txt.setError(empty);
			    	return;
			    }
			    
			    SharedPreferences pref = ctx.getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
				String korime = pref.getString("username", null);
			    AddPostTask post = new AddPostTask(ctx, filePath);
			    post.setFormData(naslov, kategorija, dat_poc, dat_zav, vreme, lokacija, tekst, korime);
			    post.execute();
			    
			}
		});
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
	    if(resultCode==RESULT_OK && resultCode == RESULT_OK && null != data)
	    {
	        Uri selectedimg = data.getData();
	        filePath = this.getRealPathFromURI(this,selectedimg);
	        File file = new File(filePath);
	        EditText slika = (EditText) findViewById(R.id.slika);
	        slika.setText(file.getName());
	    }
	}
	
	public String getRealPathFromURI(Context context, Uri contentUri) 
	{
		  Cursor cursor = null;
		  try 
		  { 
		    String[] proj = { MediaStore.Images.Media.DATA };
		    cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
		    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		    cursor.moveToFirst();
		    
		    return cursor.getString(column_index);
		  } 
		  finally 
		  {
		    if (cursor != null)
		    {
		      cursor.close();
		    }
		  }
	}

}
