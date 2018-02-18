package com.example.custom;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
//import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;


import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

//import com.example.eventnis.R;

public class MyConnections {
	
	Context ctx;

	public MyConnections(Context con)
	{
		this.ctx = con;
	}

	public String postJson(JSONObject json, String urlStr)
	{
		String response=null;
		HttpURLConnection con;
	
		try 
		{		
			con = this.getConnPost(urlStr);
			con.connect();
			OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
			wr.write(json.toString());
			wr.flush();
			
			InputStream is = con.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            response = this.getStringResponse(rd);
            is.close();
			wr.close();
			con.disconnect();			
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		return response;
	}
	
	public HttpURLConnection getConnPost(String urlS)
	{
		HttpURLConnection con = null;
		//String mycookie = this.ctx.getString(R.string.http_coockie);
	    
		try
		{			
			URL url = new URL(urlS);
			con = (HttpURLConnection) url.openConnection();
			con.setDoOutput(true);
			con.setDoInput(true);
			con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			con.setRequestMethod("POST");
			//con.setRequestProperty("Cookie", mycookie);
		}
		catch(Exception ex)
		{
			Log.d("Networking", ex.getLocalizedMessage());
		}
	    
	    return con;
	}
	
	public String getJson(String urlStr)
	{
		String response = null;
		try 
		{	
			HttpURLConnection urlConn = this.getConnGet(urlStr);	
			urlConn.connect();
				
			InputStream is = urlConn.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			response = this.getStringResponse(rd);
		
			is.close();
			urlConn.disconnect();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}

		return response;
	}
	
	
	public HttpURLConnection getConnGet(String urlString) 
	{
		HttpURLConnection httpConn = null;
		//String mycookie = ctx.getString(R.string.http_coockie);
		
		try
		{
			URL url = new URL(urlString);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();			
			httpConn = (HttpURLConnection) conn;
			
           // httpConn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			httpConn.setReadTimeout(10000 /* milliseconds */);
	        httpConn.setConnectTimeout(15000 /* milliseconds */);
		    httpConn.setAllowUserInteraction(false);
		    httpConn.setRequestMethod("GET");		    
		    //httpConn.setRequestProperty("Cookie", mycookie);	
		}
		catch(Exception ex)
		{
			Log.d("Networking", ex.getLocalizedMessage());
		}
	    
	    return httpConn;
	}
	
	public String getStringResponse(BufferedReader rd) throws IOException
	{
		StringBuilder response = new StringBuilder();
		String line;
		while((line = rd.readLine()) != null) 
		{
		    response.append(line);
		}
		return response.toString();
	}
}
