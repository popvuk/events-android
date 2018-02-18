package com.example.eventnis;

import com.example.tasks.LoginTask;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

public class LoginDialog extends DialogFragment{

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.login_dialog, container, false);
		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

		final EditText user = (EditText) rootView.findViewById(R.id.log_korime);				
		final EditText pass = (EditText) rootView.findViewById(R.id.log_sifra);
		
		Button diss = (Button) rootView.findViewById(R.id.bttn_dis);
		diss.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		
		Button log = (Button) rootView.findViewById(R.id.bttn_log);
		log.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
			
				if(TextUtils.isEmpty(user.getText()))
				{
					user.setError(getResources().getString(R.string.empty_user));
					return;
				}											
				if(TextUtils.isEmpty(pass.getText()))
				{
					pass.setError(getResources().getString(R.string.empty_pass));
					return;
				}
				
				LoginTask login = new LoginTask(getActivity(),user.getText().toString(),pass.getText().toString());
				try 
				{
					Boolean logovan = login.execute().get();
					if(logovan)
					{
						Intent intent = new Intent("android.intent.action.USER");
						intent.putExtra("korime", user.getText().toString());
						dismiss();
						startActivity(intent);
					}					
				} 
				catch (Exception e) 
				{
					e.printStackTrace();
				} 
				
			}
		});
		
        return rootView; 
	}

	
}
