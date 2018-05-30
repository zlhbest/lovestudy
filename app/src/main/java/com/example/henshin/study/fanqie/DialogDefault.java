package com.example.henshin.study.fanqie;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import com.example.henshin.study.R;
public class DialogDefault {

	@SuppressLint("CutPasteId")
	public static AlertDialog createAlertDialog(Context mContext, String title, int layoutId,
												OnClickListener leftOnClickListener, OnClickListener rightOnClickListener){
		AlertDialog localAlertDialog = new AlertDialog.Builder(mContext).create();
		localAlertDialog.show();
		localAlertDialog.setContentView(layoutId);
		localAlertDialog.setCanceledOnTouchOutside(true);
		
		TextView textview1 = (TextView) localAlertDialog.findViewById(R.id.dialog_title);
		textview1.setText("设置番茄钟");
		Button yes = (Button) localAlertDialog.findViewById(R.id.button_yes);
		Button no = (Button) localAlertDialog.findViewById(R.id.button_no);
		yes.setOnClickListener(leftOnClickListener);
		no.setOnClickListener(rightOnClickListener);
		
		return localAlertDialog;
	}
}
