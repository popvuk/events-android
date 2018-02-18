package com.example.tasks;


import org.json.JSONObject;

import com.example.custom.MyConnections;
import com.example.eventnis.R;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class LoginTask extends AsyncTask<Void,Void,Boolean>{

	ProgressDialog pd;
	Context ctx;
	String korime, sifra;
	MyConnections mycon;
	
	public LoginTask(Context con, String usr, String pass)
	{
		this.ctx = con;
		this.korime = usr;
		this.sifra = pass;
		mycon = new MyConnections(this.ctx);
	}
	@Override
	protected Boolean doInBackground(Void... params) {
		String urlS = this.ctx.getString(R.string.url_login);
		String result;
		Boolean valid=false;
		
		try 
		{
			JSONObject json = new JSONObject();		
			json.put("korime", this.korime);
			json.put("sifra", this.sifra);					
			
			result = mycon.postJson(json, urlS);
			valid = Boolean.valueOf(result);			
		} 
		catch (Exception e) 
		{
			Log.d("Json problem", e.getMessage());
		}
		
		return valid;
	}

	
	@Override
	protected void onPreExecute() {
		pd = ProgressDialog.show(this.ctx, this.ctx.getString(R.string.app_name), this.ctx.getString(R.string.app_name), true);
	}

	@Override
	protected void onPostExecute(Boolean result) {
		
		if(result)
		{
			SharedPreferences pref = this.ctx.getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
			SharedPreferences.Editor edit = pref.edit();
			edit.putString("username", this.korime);
			edit.commit();
			pd.cancel();
			super.onPostExecute(true);
			Toast.makeText(this.ctx, this.ctx.getString(R.string.log_success), Toast.LENGTH_LONG).show();
		}
		if(!result)
		{
			pd.cancel();
			Toast.makeText(this.ctx, this.ctx.getString(R.string.log_failed), Toast.LENGTH_LONG).show();
		}
		
	}

}
