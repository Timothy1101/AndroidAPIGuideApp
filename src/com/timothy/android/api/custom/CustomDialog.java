package com.timothy.android.api.custom;

import com.timothy.android.api.activity.R;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomDialog extends ProgressDialog {
	private String content;
	
	public CustomDialog(Context context) {
		super(context);
	}

	public CustomDialog(Context context, int theme,String content) {
		super(context, theme);
		this.content = content;
	}

	TextView contentTV;
	ImageView closeIV;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.custom_dialog);
		
		contentTV = (TextView) findViewById(R.id.contentTV);
		closeIV = (ImageView) findViewById(R.id.closeIV);
		
		contentTV.setMovementMethod(ScrollingMovementMethod.getInstance());
		contentTV.setText(content);
//		contentTV.setOnTouchListener(new View.OnTouchListener() {
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				closeIV.setAlpha(0.9f);
//				return false;
//			}
//		});
		
		closeIV.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				close();
			}
		});
	}
	
	public void close(){
		this.dismiss();
	}

/*	public static CustomDialog show(Context context,String content) {
		CustomDialog dialog = new CustomDialog(context,R.style.custom_dialog_style,content);
		dialog.show();
		return dialog;
	}*/
}
