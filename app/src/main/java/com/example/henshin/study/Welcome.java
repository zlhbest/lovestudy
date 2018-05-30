package com.example.henshin.study;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class Welcome extends AppCompatActivity {

	private SharedPreferences sp;
	private TextView talk;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome);

		talk =(TextView) findViewById(R.id.talk);

		sp=getSharedPreferences("setting", 0);
		String username =sp.getString("username",null);
		if(username != null){
			talk.setText(username + "自动登录成功");

			new Handler().postDelayed(new Runnable() {  //开启线程阻塞，以便查看是否登录成功。做调试用
				public void run() {
					// TODO Auto-generated method stub
					Intent intent = new Intent(Welcome.this, HomeActivity.class);
					startActivity(intent);
					finish();
				}
			}, 3000);
		}
		else {
			new Handler().postDelayed(new Runnable() {  //开启线程阻塞，以便查看是否登录成功。做调试用
				public void run() {
					// TODO Auto-generated method stub
					Intent intent = new Intent(Welcome.this, Verification.class);
					startActivity(intent);
					finish();
				}
			}, 3000);
		}
	}
}
