package com.example.eventnis;

import com.example.tasks.PostTask;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class RegisterActivity extends AppCompatActivity{

	Context ctx=this;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		
		Button bttn = (Button) findViewById(R.id.reg_bttn);
		bttn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				EditText email = (EditText) findViewById(R.id.email);				
				String em = email.getText().toString();
				
				EditText korime = (EditText) findViewById(R.id.username);
				EditText sifra = (EditText) findViewById(R.id.password);
				EditText ime = (EditText) findViewById(R.id.name);
				EditText prezime = (EditText) findViewById(R.id.lastname);
					
				String user = korime.getText().toString();
				String pass = sifra.getText().toString();
				String name = ime.getText().toString();
				String last = prezime.getText().toString();
					
				String empty = ctx.getString(R.string.empty);
				if(user.matches(""))
				{
					korime.setError(empty);
					return;
				}
				if(pass.matches(""))
				{
					sifra.setError(empty);
					return;
				}
				if(name.matches(""))
				{
					ime.setError(empty);
					return;
				}
				if(last.matches(""))
				{
					prezime.setError(empty);
					return;
				}
				if(!isValid(em))
				{
					email.setError(ctx.getString(R.string.invalid_email));
					return;
				}
					
				PostTask register = new PostTask(ctx,user,pass,name,last,em);
				register.execute();
				
			}

			private boolean isValid(String em) {				
				return !TextUtils.isEmpty(em) && android.util.Patterns.EMAIL_ADDRESS.matcher(em).matches();
			}
		});
	}


}
