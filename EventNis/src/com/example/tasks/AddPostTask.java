package com.example.tasks;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.example.custom.MultipartUtility;
import com.example.eventnis.R;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

public class AddPostTask extends AsyncTask<Void,Void,String>{

	String filePath;
	ProgressDialog pd;
	Context ctx;
	String naslov, kategorija, dat_poc, dat_zav, vreme, lokacija, tekst, korime;
	
	public AddPostTask(Context con, String file)
	{
		this.ctx = con;
		this.filePath = file;
	}
	
	public void setFormData(String nas, String kat, String dat_poc, String dat_zav, String vr, String lok, String txt, String kor)
	{
		this.naslov = nas;
		this.kategorija = kat;
		this.dat_poc = dat_poc;
		this.dat_zav = dat_zav;
		this.vreme = vr;
		this.lokacija = lok;
		this.tekst = txt;
		this.korime = kor;
	}
	
	@Override
	protected String doInBackground(Void... params) {
		
		String charset = "UTF-8";
		File uploadFile = new File(this.filePath);
		StringBuilder res = new StringBuilder(); 
		String requestURL = this.ctx.getString(R.string.url_post);
		String cookie = this.ctx.getString(R.string.http_coockie);

		try 
		{
			MultipartUtility multipart = new MultipartUtility(requestURL, charset, cookie);
			
			multipart.addHeaderField("User-Agent", this.ctx.getString(R.string.app_name));
			multipart.addHeaderField("Test-Header", "Header-Value");
			
			multipart.addFormField("id_kat", this.kategorija);
			multipart.addFormField("naslov", this.naslov);
			multipart.addFormField("dat_od", this.dat_poc);
			multipart.addFormField("dat_do", this.dat_zav);
			multipart.addFormField("vreme", this.vreme);
			multipart.addFormField("lokacija", this.lokacija);
			multipart.addFormField("tekst", this.tekst);
			multipart.addFormField("korime", this.korime);
			
			multipart.addFilePart("slika", uploadFile);

			List<String> response = multipart.finish();
						
			for (String line : response) {
				res.append(line);
				res.append("/r");
			}
		} 
		catch (IOException ex)
		{
			return this.ctx.getString(R.string.filenotfound);
		}
		
		return res.toString();
	}

	@Override
	protected void onPreExecute() {
		pd = ProgressDialog.show(this.ctx, this.ctx.getString(R.string.app_name), this.ctx.getString(R.string.progress), true);
	}

	@Override
	protected void onPostExecute(String result) {
		pd.cancel();
		Toast.makeText(ctx, result, Toast.LENGTH_LONG).show();
	}

}
