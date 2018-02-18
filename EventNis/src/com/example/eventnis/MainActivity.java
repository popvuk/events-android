package com.example.eventnis;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

import com.example.tasks.GetTask;
import com.example.tasks.ImageTask;
import com.example.eventnis.R;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

	TextView tw1, tw2;
	Button btnn;
    Context ctx=this;
    View v;
    int page = 1;//trenutna strana
    int allpages = 1;//ukupan broj strana
    SimpleDateFormat sdf;
  

	int katid = 0;
    String korime=null;
    SharedPreferences pref;
    LinearLayout ll;
    ImageView img;
    boolean online = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        
        if(this.isNetworkAvailable())
        {      	
            this.online = true;
            pref = this.ctx.getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
    		this.korime = pref.getString("username", null);
            this.displayPosts(this.katid,1);
        }
        else       	
        {
   
        	AlertDialog.Builder alert = new AlertDialog.Builder(ctx);
			alert.setTitle("Obavestenje");
			alert.setMessage("Nema internet konekcije...");
			alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {

						finish();
					}
			});
				
			alert.create();
			alert.show();
        	      
        };
       // ObservableScrollView sv = (ObservableScrollView) findViewById(R.id.kontejner);
    	//sv.setScrollViewListener(this);  	
    }
    

	private void displayPosts(int katid, int page)
    {
    	GetTask ct = new GetTask(ctx, katid, page);    
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
		    
	    ll = (LinearLayout) findViewById(R.id.post_box);
	    	    
    	for(int i=1;i<json.length();i++)
    	{	  		   		
    		v = LayoutInflater.from(this).inflate(R.layout.post, ll, true);
    		
    	    TextView dat = (TextView) v.findViewById(R.id.datum_post);
        	TextView kat = (TextView) v.findViewById(R.id.kategorija_post);
            TextView nas = (TextView) v.findViewById(R.id.naslov_post);
            img = (ImageView) v.findViewById(R.id.img_post);
                 
        	try
        	{   
        		post = json.getJSONObject(i);
        		
        		final String id_slika  = post.get("slika").toString();
        		//id_slika.replaceAll(" ", "%20");
        		new ImageTask(this, id_slika, img,  false).execute(); 
        		
        		sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        	    Date parsed = sdf.parse(post.get("dat_objave").toString());
        	    sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        	    
        	    final int id_korisnik = post.getInt("id_korisnik");
        	    
        	    final String datum = sdf.format(parsed);
        	    dat.setText(datum);
        	    
        	    final String kategorija = post.get("naziv_kat").toString();
        	    kat.setText(kategorija);
        	    
        	    final String naslov = post.get("naslov").toString();
        	    nas.setText(naslov);
        	    
        	    final int id = Integer.parseInt(post.get("id_post").toString()); 
        	  
        	    dat.setId(id);   
        	    kat.setId(id);  	     
        	    nas.setId(id);   
        	    img.setId(id);
        	    
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
        		Log.d("Greska ovde", e.getMessage());
        	}
    	}
    
    	if(json.length()>1)
    	{
    		this.setPagingLayout();
    	}
    	
    }
    
	public void setPagingLayout()
	{
		final LinearLayout pl = (LinearLayout) findViewById(R.id.post_box);
		v = LayoutInflater.from(this).inflate(R.layout.paging, pl, true);
		TextView strana = (TextView) v.findViewById(R.id.strana);
		strana.setText(String.valueOf(this.page));
		Button prev = (Button) v.findViewById(R.id.prev_bttn);
		Button next = (Button) v.findViewById(R.id.next_bttn);
		prev.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				page--;
				pl.removeAllViews();
				displayPosts(katid, page);
			}
		});
        next.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				page++;
				pl.removeAllViews();
				displayPosts(katid, page);
			}
		});
		
		if(this.page == 1)
		{		
			prev.setVisibility(View.INVISIBLE);
		}
		if(this.allpages == this.page )
		{
			next.setVisibility(View.INVISIBLE);
		}
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		
		if(!this.online)
		{
			return true;
		}
		String meni [] = this.ctx.getResources().getStringArray(R.array.menu);
		//String kategorije [] = this.ctx.getResources().getStringArray(R.array.kategorije);
		
		menu.add(0, 0, 0, meni[0]);
		if(this.korime != null)
    	{
    		menu.add(0, 1, 1, this.korime);
    	}
    	else
    	{
    		menu.add(0, 2, 2, meni[1]);
    	}
    	
		GetTask ktg = new GetTask(ctx);
		String [] items=null;
		int [] values=null;
		JSONObject obj=null;
		try
		{
			JSONArray katjson = ktg.execute().get();
			items = new String[katjson.length()];
			values = new int[katjson.length()];
			
			for(int i = 0;i<katjson.length();i++)
			{
				obj = katjson.getJSONObject(i);
				
				items[i]=obj.get("item").toString();
				//Log.d("poruka", items[i]);
				values[i]=obj.getInt("value");
				
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
    	SubMenu kat = menu.addSubMenu(1, 3, 3, meni[2]);
    	for(int i = 0;i<items.length;i++)
		{
    		kat.add(1, values[i], values[i], items[i]);
			
		}
      //	kat.add(0, 4, 4, kategorije[0]);
      //	kat.add(0, 5, 5, kategorije[1]);
      //	kat.add(0, 6, 6, kategorije[2]);
    	
      	menu.add(0, 7, 7, meni[3]);
      	menu.add(0, 8, 8, meni[4]);
      	
      	SubMenu face = menu.addSubMenu(0, 9, 9, meni[5]);
        face.add(0, 10, 10, meni[6]);
      	face.add(0, 11, 11, meni[7]);
      	face.add(0, 12, 12, meni[8]);
      	if(this.korime != null)
      	{
      		menu.add(0, 13, 13, meni[9]);
      	}  	
    		   		
    	return true;
    }

    

	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
		LinearLayout ll = (LinearLayout) findViewById(R.id.post_box);
		Intent intent;
		String meni [] = this.ctx.getResources().getStringArray(R.array.menu);
		
		switch (item.getGroupId()){
		case 0:
			switch (item.getItemId()){
			case 0:
				//ll.removeAllViews();
				finish();
				startActivity(getIntent());
			    return true;
			case 1:
				Intent korisnik = new Intent("android.intent.action.USER");
				korisnik.putExtra("korime", this.korime);
				startActivity(korisnik);
				return true;			
			case 2:			
				FragmentManager fm = getFragmentManager();
				LoginDialog dialogFragment = new LoginDialog();
				dialogFragment.show(fm, "Login Dialog");
			    return true;		
			case 7:
				intent = new Intent("android.intent.action.REGISTER");
				startActivity(intent);
			    return true;			
			case 8:
				intent = new Intent("android.intent.action.CONTACT");
				startActivity(intent);
				return true;
			case 10:
				Intent face = new Intent("android.intent.action.FACE");
				face.putExtra("groupId","149480451883510");
				face.putExtra("title", meni[6]);
				startActivity(face);
				return true;
			case 11:
				Intent face2 = new Intent("android.intent.action.FACE");
				face2.putExtra("groupId","309874462392268");
				face2.putExtra("title", meni[7]);
				startActivity(face2);
				return true;
			case 12:
				Intent face3 = new Intent("android.intent.action.FACE");
				face3.putExtra("groupId","203592859777305");
				face3.putExtra("title", meni[8]);
				startActivity(face3);
				return true;
			case 13:
				pref = this.ctx.getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
				SharedPreferences.Editor edit = pref.edit();
				edit.remove("username");
				edit.commit();
				finish();
				Toast.makeText(this, this.ctx.getString(R.string.logout), Toast.LENGTH_LONG).show();
				startActivity(getIntent());//restart main activity
				return true;
			}
		case 1:
			ll.removeAllViews();
			this.katid = item.getItemId();
			this.displayPosts(this.katid, 1);
		    return true;
		}
		
		return false;
    }


	public void onRestart()
	{
		super.onRestart();
		img.setImageBitmap(null);
		img = null;
		this.finish();		
		startActivity(getIntent());
	}
	
	private boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
	
	
}
