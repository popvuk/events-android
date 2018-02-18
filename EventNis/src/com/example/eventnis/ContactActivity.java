package com.example.eventnis;

import com.example.tasks.ContactTask;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class ContactActivity extends AppCompatActivity{

	Context ctx = this;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact);
		
		Button bttn = (Button) findViewById(R.id.contact_bttn);
		bttn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ContactTask kontakt = new ContactTask(ctx);
				kontakt.execute();
			}
		});
	}

}
