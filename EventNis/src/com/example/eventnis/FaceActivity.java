package com.example.eventnis;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.tasks.ImageTask;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FaceActivity extends AppCompatActivity{

	String slikaUrl;
	int id = 0;
	Context con = this;
	String after = null;
	String before = null;
	String grupaId = null;
	View v;
	int page = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		FacebookSdk.sdkInitialize(getApplicationContext());
		
		this.grupaId = getIntent().getStringExtra("groupId");
		this.readGraph();
	}
	
	private void directionGraph( boolean next)
	{
		String graphPath = "/"+this.grupaId+"/photos";		
        AccessToken accessToken = this.setAccessToken();   
        GraphRequest request = GraphRequest.newGraphPathRequest(accessToken, graphPath,
    				new GraphRequest.Callback() {
    					
    					@Override
    					public void onCompleted(GraphResponse response) {						
    						    						
    						try 
    						{
    							JSONObject json = response.getJSONObject();
        						displayPosts(json);
    						}
    						catch (Exception e)
    						{
    							Log.d("Greska1", e.getMessage());
    						}  												
    					}
    				});
          Bundle parameters = new Bundle();
          parameters.putString("pretty", "0");
          parameters.putString("limit", "4");
          if(next)
          {
        	  parameters.putString("after", this.after);
          }
          else
          {
        	  parameters.putString("before", this.before);
          }
          parameters.putString("fields", "id,name,created_time");
          request.setParameters(parameters);
          request.executeAsync();
	}

	private void readGraph()
	{
		String graphPath = "/"+this.grupaId+"/photos";
		
        AccessToken accessToken = this.setAccessToken();
     
        GraphRequest request = GraphRequest.newGraphPathRequest(accessToken, graphPath,
    				new GraphRequest.Callback() {
    					
    					@Override
    					public void onCompleted(GraphResponse response) {						
    						    						
    						try 
    						{
    							JSONObject json = response.getJSONObject();
        						displayPosts(json);
    						}
    						catch (Exception e)
    						{
    							Log.d("Greska2", e.getMessage());
    						}  												
    					}
    				});
          Bundle parameters = new Bundle();
          parameters.putString("fields", "id,name,created_time");
          parameters.putString("limit", "4");
          request.setParameters(parameters);
          request.executeAsync();
	}
	
	private void readGraphImg(String slikaId, final ImageView img)
	{
		String graphPath = slikaId;	
        AccessToken accessToken = this.setAccessToken();
        GraphRequest request = GraphRequest.newGraphPathRequest(accessToken, graphPath,
    				new GraphRequest.Callback() {
    					
    					@Override
    					public void onCompleted(GraphResponse response) {						
    						    						
    						try 
    						{
    							JSONObject json = response.getJSONObject();
        						slikaUrl = json.getString("source");
        						ImageTask image = new ImageTask(con, slikaUrl, img, true);
        						image.execute();
    						}
    						catch (Exception e)
    						{
    							Log.d("Greskaaa3", e.getMessage());
    						}  												
    					}
    				});
          Bundle parameters = new Bundle();
          parameters.putString("fields", "id,source");
          request.setParameters(parameters);
          request.executeAsync();
	}
	
	private AccessToken setAccessToken()
	{
		 String token = this.getString(R.string.token);
		 String api_id = this.getString(R.string.facebook_app_id);
		 AccessToken accessToken = new AccessToken(token, api_id, "EventNis", null,null,null,null,null);
		 return accessToken;
	}
	
	public void displayPosts(JSONObject obj)
	{
		JSONArray niz=null;
		try 
		{
			JSONObject next = obj.getJSONObject("paging");
			JSONObject cursor = next.getJSONObject("cursors");
			this.after = cursor.get("after").toString();
			this.before = cursor.get("before").toString();
			niz = obj.getJSONArray("data");
		}
		catch (Exception e1) 
		{
			e1.printStackTrace();
		}
		
		LinearLayout ll = (LinearLayout) findViewById(R.id.post_box);
		 
		JSONObject post=null;
		SimpleDateFormat sdf;
	    
		for(int i=0;i<niz.length();i++)
		{	  		   		
		    v = LayoutInflater.from(this).inflate(R.layout.post, ll, true);
		    		
		    TextView dat = (TextView) v.findViewById(R.id.datum_post);
		    TextView kat = (TextView) v.findViewById(R.id.kategorija_post);
		    TextView nas = (TextView) v.findViewById(R.id.naslov_post);
		    ImageView img = (ImageView) v.findViewById(R.id.img_post);
		                 
		    try
		    {   
		      	post = niz.getJSONObject(i);
		        		
		       	String slikaId  = post.get("id").toString();
		       	this.readGraphImg(slikaId, img);
		        	
		       	sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
		       	Date parsed = sdf.parse(post.get("created_time").toString());
		       	sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
		        	    
		        String datum = sdf.format(parsed);
		       	dat.setText(datum);
		        	    
		       	kat.setText(getIntent().getStringExtra("title"));
		        try
		        {
		        	String tekst = post.get("name").toString();
			       	nas.setText(tekst);
		        }
		       	catch(JSONException je)
		       	{
		       		nas.setVisibility(View.GONE);
		       		//Log.d("no name", je.getMessage());
		       	}
		       	    
		        this.id++;
		       	dat.setId(this.id);   
		       	kat.setId(this.id);  	     
		       	nas.setId(this.id);   
		       	img.setId(this.id);
		        	    	       	 
		     }
		     catch(Exception e)
		     {
		        Log.d("Greska", e.getMessage());
		     }
		}
		
		this.setPagingLayout();
	}
	
	public void setPagingLayout()
	{
		final LinearLayout ll = (LinearLayout) findViewById(R.id.post_box);
		v = LayoutInflater.from(this).inflate(R.layout.paging, ll, true);
		TextView strana = (TextView) v.findViewById(R.id.strana);
		strana.setVisibility(View.INVISIBLE);
		Button prev = (Button) v.findViewById(R.id.prev_bttn);
		Button next = (Button) v.findViewById(R.id.next_bttn);
		prev.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ll.removeAllViews();
				page--;
				directionGraph(false);
			}
		});
        next.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {			
				ll.removeAllViews();
				page++;
				directionGraph(true);
			}
		});
		
		if(this.page == 1)
		{		
			prev.setVisibility(View.INVISIBLE);
		}	
	}
}
