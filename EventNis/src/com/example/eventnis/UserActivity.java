package com.example.eventnis;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

import com.example.tasks.GetTask;
import com.example.tasks.ImageTask;
import com.example.tasks.PostTask;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class UserActivity extends AppCompatActivity {

	TextView tw1, tw2;
	Button btnn;
    Context ctx=this;
    View v;
    int page = 1;
    int allpages = 1;
    SimpleDateFormat sdf;
    String korisnik = null;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
	     setContentView(R.layout.activity_user);
	     this.korisnik = getIntent().getStringExtra("korime");
	     setTitle(this.korisnik);
	     displayPosts(korisnik, 1);
	     
	     Button bttn_post = (Button) findViewById(R.id.bttn_post);
	     bttn_post.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent("android.intent.action.ADDPOST");
				startActivity(intent);
			}
		});
	}

	private void displayPosts(String korisnik, int page)
    {
		GetTask ct = new GetTask(ctx, korisnik, page);    
		JSONArray json=null;		
		JSONObject post=null;   
		
		try 			    
		{					
			json = ct.execute().get();
			post = json.getJSONObject(0);
			this.allpages = post.getInt("brstrana");//nulti objekat je broj strana, zatim idu postovi
		}			    
		catch (Exception e) 			  
		{			
    		Log.d("Greska2", e.getMessage());
		}	
		    
	    LinearLayout ll = (LinearLayout) findViewById(R.id.post_box_user);
	    
    	for(int i=1;i<json.length();i++)
    	{	  		   		
    		v = LayoutInflater.from(this).inflate(R.layout.post, ll, true);
    		
    	    TextView dat = (TextView) v.findViewById(R.id.datum_post);
        	TextView kat = (TextView) v.findViewById(R.id.kategorija_post);
            TextView nas = (TextView) v.findViewById(R.id.naslov_post);
            ImageView img = (ImageView) v.findViewById(R.id.img_post);
            ImageButton meni = (ImageButton) v.findViewById(R.id.imageMeni);
            
        	try
        	{   
        		post = json.getJSONObject(i);
        		
        		final String id_slika  = post.get("slika").toString();
        		new ImageTask(this, id_slika, img, false).execute(); 
        		
        		final int id_korisnik = post.getInt("id_korisnik");
        		sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        	    Date parsed = sdf.parse(post.get("dat_objave").toString());
        	    sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        	    
        	    final String datum = sdf.format(parsed);
        	    dat.setText(datum);
        	    
        	    final String kategorija = post.get("naziv_kat").toString();
        	    kat.setText(kategorija);
        	    
        	    final String naslov = post.get("naslov").toString();
        	    nas.setText(naslov);
        	    
        	    final int id = Integer.parseInt(post.get("id_post").toString());
        	   
        	    meni.setVisibility(View.VISIBLE);
                meni.setOnClickListener(new View.OnClickListener() {
    				
    				@Override
    				public void onClick(View v) {
    					AlertDialog.Builder alert = new AlertDialog.Builder(ctx);
    					alert.setTitle(getResources().getString(R.string.alert));
    					alert.setMessage(getResources().getString(R.string.del_post));
    					alert.setPositiveButton("Da", new DialogInterface.OnClickListener() {
    						
    						@Override
    						public void onClick(DialogInterface dialog, int which) {
    							PostTask delPost = new PostTask(ctx, id, true);
    							delPost.execute();
    							//LinearLayout ll = (LinearLayout) findViewById(R.id.post_box_user);
    							//ll.removeAllViews();
    							finish();
    							startActivity(getIntent());
    						}
    					});
    					alert.setNegativeButton("Ne", new DialogInterface.OnClickListener() {
    						
    						@Override
    						public void onClick(DialogInterface dialog, int which) {
    						
    							dialog.cancel();
    						}
    					});
    					
    					alert.create();
    					alert.show();
    					
    				}
    			});
        	    
        	    
        	    dat.setId(id);   
        	    kat.setId(id);  	     
        	    nas.setId(id);   
        	    img.setId(id);
        	    meni.setId(id);
        	    
        	    final String dat_od = post.get("dat_od").toString();
        	    final String dat_do = post.get("dat_do").toString();
        	    final String vreme = post.get("vreme").toString();
        	    final String lokacija = post.get("lokacija").toString();
        	    final String tekst = post.get("tekst").toString();
        	    
        	    img.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) 
					{
						// TODO Auto-generated method stub
						Intent intent = new Intent("android.intent.action.POST");
			            intent.putExtra("datum", datum);
			            intent.putExtra("kategorija", kategorija);
			            intent.putExtra("dat_od", dat_od);
			            intent.putExtra("dat_do", dat_do);
			            intent.putExtra("vreme", vreme);
			            intent.putExtra("lokacija", lokacija);
			            intent.putExtra("id_slika", id_slika);
			            intent.putExtra("tekst", tekst);
			            intent.putExtra("naslov", naslov);
			            intent.putExtra("id", id);
			            intent.putExtra("id_korisnik", id_korisnik);
			                    
						startActivity(intent);			
					}
				});
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
		strana.setText(String.valueOf(this.page));
		Button prev = (Button) v.findViewById(R.id.prev_bttn);
		Button next = (Button) v.findViewById(R.id.next_bttn);
		prev.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				page--;
				ll.removeAllViews();
				displayPosts(korisnik, page);
			}
		});
        next.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				page++;
				ll.removeAllViews();
				displayPosts(korisnik, page);
			}
		});
		
		if(this.page == 1)
		{		
			prev.setVisibility(View.INVISIBLE);
		}
		if(this.allpages == this.page)
		{
			next.setVisibility(View.INVISIBLE);
		}
	}

	
}

