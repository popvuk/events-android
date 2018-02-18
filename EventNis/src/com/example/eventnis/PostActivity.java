package com.example.eventnis;

import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.json.JSONArray;

import com.example.eventnis.R;
import com.example.tasks.GetTask;
import com.example.tasks.GoogleMapTask;
import com.example.tasks.ImageTask;
import com.example.tasks.PostTask;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.roomorama.caldroid.CaldroidFragment;


import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;



public class PostActivity extends AppCompatActivity{

	int id_posta;
	View v;
	SimpleDateFormat sdf;
	Bitmap bmp;
	Date parsed1, parsed2;
	Context ctx = this;
	String korime;
	int id_korisnik;
	ImageView slika;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_post);
		
		SharedPreferences pref = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
	    korime = pref.getString("username", null);
		
		String adresa = getIntent().getStringExtra("lokacija");
		String adr = adresa.replaceAll(" ", "%20");
		this.displayImage();
		this.displayMap(adr);	
		
		try 
		{
			sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
			parsed1 = sdf.parse(getIntent().getStringExtra("dat_od"));	    
		    parsed2 = sdf.parse(getIntent().getStringExtra("dat_do"));
			
		} 
		catch (Exception e1) 
		{
			Log.d("Greska1", e1.getMessage());
		}
		this.displayCalendar(parsed1, parsed2);
		
		this.id_korisnik = getIntent().getIntExtra("id_korisnik",0);	
		this.id_posta = getIntent().getIntExtra("id", 0);
		
		this.displayComments(this.id_posta);
		
		TextView datum = (TextView) findViewById(R.id.datum);
		datum.setText(getIntent().getStringExtra("datum"));
		
		TextView kategorija = (TextView) findViewById(R.id.kategorija);
		kategorija.setText(getIntent().getStringExtra("kategorija"));
		
		TextView naslov = (TextView) findViewById(R.id.naslov);
		naslov.setText(getIntent().getStringExtra("naslov"));
	 
		String vre = getIntent().getStringExtra("vreme");
		TextView vreme = (TextView) findViewById(R.id.vreme);	
		vreme.setText(vreme.getText()+vre);
		
		TextView lokacija = (TextView) findViewById(R.id.lokacija);	
		lokacija.setText(lokacija.getText()+adresa);		
		
		TextView tekst = (TextView) findViewById(R.id.tekst);
		tekst.setText(getIntent().getStringExtra("tekst"));
		
		Button kom = (Button) findViewById(R.id.bttn_kom);
		kom.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				FragmentManager fm = getFragmentManager();
				
				if(korime == null)
				{				
					LoginDialog dialogFragment = new LoginDialog();
					dialogFragment.show(fm, "Login Dialog");
				}
				else
				{
					CommentDialog commentFragment = new CommentDialog(ctx, id_posta, korime);
					commentFragment.show(fm, "Comment Dialog");
				}
				
			}
		});
	}
	
	private void displayCalendar(Date start, Date end)
	{
		CaldroidFragment caldroidFragment = new CaldroidFragment();
		Bundle args = new Bundle();
		Calendar cal = Calendar.getInstance();
		cal.setTime(start);
		args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH)+1);
		args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
		args.putInt("startDayOfWeek", 2);
		caldroidFragment.setArguments(args);
		caldroidFragment.setSelectedDates(start, end);

		FragmentTransaction t = getSupportFragmentManager().beginTransaction();
		t.replace(R.id.calendar1, caldroidFragment);
		t.commit();
	}

	private void displayImage()
	{
		String id_slika = getIntent().getStringExtra("id_slika");
		slika = (ImageView) findViewById(R.id.slika);
		ImageTask it = new ImageTask(this,id_slika, slika, false);
		try 
		{
			it.execute();
		} 
		catch (Exception e)
		{
		   Log.d("Greska2", e.getMessage());
		} 
	}
	
	private void displayComments(int id_posta) 
	{
		GetTask komentari = new GetTask(this, id_posta);
	
		JSONArray json=null;
		String dat_vreme;
		PostTask ut = new PostTask(this.ctx, this.korime);
        String korId=null;
        Boolean userPost=false;
		try 
		{
			json = komentari.execute().get();
			korId = ut.execute().get();
			if(korId != "")
			{
				userPost = this.id_korisnik == Integer.parseInt(korId);
			}
					
		} 
		catch (Exception e) 
		{
			Log.d("problem", e.getMessage());
		} 
		//Boolean userPost = this.id_korisnik == Integer.parseInt(korId);
		LinearLayout ll = (LinearLayout) findViewById(R.id.komentari_ll);
		
		for(int i=0;i<json.length();i++)
		{
			v = LayoutInflater.from(this).inflate(R.layout.komentar, ll, true);	
			TextView korisnik = (TextView) v.findViewById(R.id.korisnik_kom);
			TextView vreme = (TextView) v.findViewById(R.id.datum_kom);
			TextView komentar = (TextView) v.findViewById(R.id.text_kom);
			ImageView delete = (ImageView) v.findViewById(R.id.delimg);
			
			
			try
			{			

				korisnik.setText(json.getJSONObject(i).get("korime").toString());
				final int id = Integer.parseInt(json.getJSONObject(i).get("id_kom").toString());
				sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        	    Date parsed = sdf.parse(json.getJSONObject(i).get("vreme").toString());
        	    sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault());      	    
        	    dat_vreme = sdf.format(parsed);
        	    vreme.setText(dat_vreme);        	    
        	    komentar.setText(json.getJSONObject(i).get("komentar").toString());
        	           	    
        	    if(userPost)//ako je postovao korisnik
        	    {
        	    	delete.setVisibility(View.VISIBLE);
        	    	delete.setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View v) {
							AlertDialog.Builder alert = new AlertDialog.Builder(ctx);
	    					alert.setTitle(getResources().getString(R.string.alert));
	    					alert.setMessage(getResources().getString(R.string.del_kom));
	    					alert.setPositiveButton("Da", new DialogInterface.OnClickListener() {
	    						
	    						@Override
	    						public void onClick(DialogInterface dialog, int which) {
	    							PostTask delKom = new PostTask(ctx, id, false);
	    							delKom.execute();
	    	
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
        	    }
        	    
        	    korisnik.setId(id);
        	    vreme.setId(id);
        	    komentar.setId(id);
        	    delete.setId(id);
			}
			catch(Exception e)
			{
				Log.d("Greska datum", e.getMessage());
			}
			
		}
	}
	
	public void displayMap(String adresa)
	{				
		SupportMapFragment smf = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
		GoogleMap mapa = smf.getMap();
		mapa.setMyLocationEnabled(true);
		
		new GoogleMapTask(adresa, mapa, this.ctx).execute();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		slika.setImageBitmap(null);
		slika=null;
		this.finish();
		
	}


	
}
