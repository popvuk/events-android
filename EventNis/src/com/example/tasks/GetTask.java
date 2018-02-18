package com.example.tasks;


import org.json.JSONArray;
import org.json.JSONException;

import com.example.custom.MyConnections;
import com.example.eventnis.R;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;


public class GetTask extends AsyncTask<Void, Void, JSONArray>{

	String response, korisnik;
	ProgressDialog pd;
	Context ctx;
	int page, katid, postid, task;
	MyConnections mycon;
		
	public GetTask(Context con, int id, int page)//citanje task
	{
		this.ctx = con;
		this.katid = id;
		this.page = page;
		this.task = 0;
		this.mycon = new MyConnections(this.ctx);
	}
	
	public GetTask(Context con, int post_id)//komentari task
	{
		this.ctx = con;
		this.postid = post_id;
		this.task = 1;
		this.mycon = new MyConnections(this.ctx);
	}
	
	public GetTask(Context con, String kor, int page)//korisnik task
	{
		this.ctx = con;
		this.korisnik = kor;
		this.page = page;
		this.task = 2;
		this.mycon = new MyConnections(this.ctx);
	}
	
	public GetTask(Context con)//kategorije task
	{
		this.ctx = con;
		this.task = 3;
		this.mycon = new MyConnections(this.ctx);
	}
	
	@Override
	protected void onPreExecute() {
		pd = ProgressDialog.show(this.ctx, this.ctx.getString(R.string.app_name), this.ctx.getString(R.string.progress), true);
	}

	
	@Override
	protected JSONArray doInBackground(Void... params) {

		String url=null;
		JSONArray json = null;
		switch(this.task)
		{
		case 0:
			if(this.katid > 0)
			{
				url = this.ctx.getString(R.string.url_postovi_kategorija); 
				url = url+"/"+this.katid+"/"+this.page;
			}
			else
			{
				url = this.ctx.getString(R.string.url_postovi);
			    url = url+"/"+this.page;
			}
			break;
		case 1:
			url = this.ctx.getString(R.string.url_get_komentari); 
			url = url+"/"+this.postid;
			break;
		case 2:
			url = this.ctx.getString(R.string.url_post_korisnik); 
			url = url+"/"+this.korisnik+"/"+this.page;
			break;
		case 3:
			url = this.ctx.getString(R.string.url_kategorije); 
			break;
		}
		String jsonText = mycon.getJson(url);
		try
		{
			json = new JSONArray(jsonText);
		} 
		catch (JSONException e) 
		{
			Log.d("poruka", jsonText.toString());
		}
		return json;
	}

	

	@Override
	protected void onPostExecute(JSONArray result) {
		
		pd.cancel();
	}
}
