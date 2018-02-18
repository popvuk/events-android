package com.example.eventnis;

import com.example.tasks.PostTask;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

public class CommentDialog extends DialogFragment{

	String  korime;
	int id_posta;
	Context ctx;
	
	public CommentDialog(Context con, int id, String usr)
	{
		this.ctx = con;
		this.id_posta = id;
		this.korime = usr;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.comment_dialog,container, false);
		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		
		final EditText komentar = (EditText) root.findViewById(R.id.kom_tekst);
		
		Button otkazi = (Button) root.findViewById(R.id.bttn_otkazi);
		otkazi.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		
		Button postavi = (Button) root.findViewById(R.id.bttn_postavi);
		postavi.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String kom = komentar.getText().toString();
				if(kom.matches(""))
				{
					komentar.setError(ctx.getString(R.string.empty));
					return;
				}
				PostTask komTask = new PostTask(ctx, kom , id_posta, korime);
				komTask.execute();
				dismiss();
				//refresh activity
				((PostActivity) ctx).finish();
				startActivity(((PostActivity) ctx).getIntent());
				
			}
		});
		
		return root;
	}

}
