package com.example.tasks;

import org.json.JSONObject;

import com.example.custom.MyConnections;
import com.example.eventnis.R;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;


public class ContactTask extends AsyncTask<Void,Void,JSONObject>{

	Context ctx;
	MyConnections mycon;
	
	public ContactTask(Context con)
	{
		this.ctx = con;
		mycon = new MyConnections(this.ctx);
	}
	
	@Override
	protected JSONObject doInBackground(Void... params) {
		
		String urlS = this.ctx.getString(R.string.url_kontakt);
		JSONObject json=null;

		try 
		{
	        String jsonText = mycon.getJson(urlS);			
			json = new JSONObject(jsonText);
		} 
		catch (Exception e) 
		{
			Log.d("Json problem", e.getMessage());
		}
		
		return json;
	}
	
	@Override
	protected void onPostExecute(JSONObject result) {
		try 
		{
			String[] emailTo = new String[]{result.get("username").toString()};
			String subject = ctx.getString(R.string.kontakt);
			
			Intent email = new Intent(Intent.ACTION_SEND);
			email.putExtra(Intent.EXTRA_EMAIL, emailTo);
			email.putExtra(Intent.EXTRA_SUBJECT, subject);
			email.setType("message/rfc822");
			this.ctx.startActivity(email);
		}
		catch (Exception e) 
		{
			
			Log.d("Json problem", e.getMessage());
		}
	}

}
