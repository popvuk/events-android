package com.example.tasks;


import org.json.JSONObject;

import com.example.custom.MyConnections;
import com.example.eventnis.R;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

public class PostTask extends AsyncTask<Void,Void,String>{

	Context ctx;
	String komentar,korime, sifra, ime, prezime, email;
	int id_post, id;
	Boolean post;
	int task;
	ProgressDialog pd;
	MyConnections mycon;
	
	public PostTask(Context con, String kom, int post, String korime)//CommentTask
	{
		this.ctx = con;
		this.komentar = kom;
		this.id_post = post;
		this.korime = korime;
		this.task = 0;
		mycon = new MyConnections(this.ctx);
	}
	
	public PostTask(Context con, int id, Boolean post)//DelKom/DelPost 
	{
		this.ctx = con;
		this.id = id;
		this.post = post;		
		this.task = 1;
		mycon = new MyConnections(this.ctx);
	}
	
	public PostTask(Context con, String korime, String pass, String ime, String prez, String email)//RegisterTask
	{
		this.ctx = con;
		this.korime = korime;
		this.sifra = pass;
		this.ime = ime;
		this.prezime = prez;
		this.email = email;
		this.task = 2;
		mycon = new MyConnections(this.ctx);
	}
	
	public PostTask(Context con, String korime)//UserTask
	{
		this.ctx = con;
		this.korime = korime;
		this.task = 3;
		mycon = new MyConnections(this.ctx);
	}
	
	@Override
	protected void onPreExecute() {
		pd = ProgressDialog.show(this.ctx, this.ctx.getString(R.string.app_name), this.ctx.getString(R.string.app_name), true);
	}
	
	@Override
	protected String doInBackground(Void... params) {
		
		String response;
		JSONObject json = new JSONObject();
		String url;
        switch(this.task)
        {
        case 0:
        	try
        	{
        		url = this.ctx.getString(R.string.url_post_komentar);
				json.put("id_post", this.id_post);
				json.put("komentar", this.komentar);
				json.put("korime", this.korime);
	        	response = mycon.postJson(json,url);
				return response;
			}
        	catch (Exception e) 
        	{
				e.printStackTrace();
			}
        	
        case 1:
    		if(this.post)
    		{
    			url = this.ctx.getString(R.string.url_delete_post);
    			url = url+"/"+this.id;
    		}
    		else
    		{
    			url = this.ctx.getString(R.string.url_delete_komentar);
    			url = url+"/"+this.id;
    		}
    		response = mycon.getJson(url);
    		return response;
        case 2:
        	try
        	{
        		url = this.ctx.getString(R.string.url_registracija);
        		json.put("korime", this.korime);
    			json.put("sifra", this.sifra);
    			json.put("ime", this.ime);
    			json.put("prezime", this.prezime);
    			json.put("email", this.email);
	        	response = mycon.postJson(json,url);
				return response;
			}
        	catch (Exception e) 
        	{
				e.printStackTrace();
			}
        case 3:
        	url = this.ctx.getString(R.string.url_vrati_korisnika);
    		url = url+"/"+this.korime;
        	response = mycon.getJson(url);
        	return response;
        }
		return null;
	}
		
	
	@Override
	protected void onPostExecute(String result) {
		pd.cancel();
		if(this.task != 3)
		{
			Toast.makeText(ctx, result, Toast.LENGTH_LONG).show();
		}		
	}

}
