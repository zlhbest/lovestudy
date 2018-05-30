package com.example.henshin.study.fanqie;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import com.example.henshin.study.R;
@SuppressLint("HandlerLeak")
public class SecordActivity extends AppCompatActivity {

	private ImageButton workStop;
	private com.example.henshin.study.fanqie.OvalTimeView ovalTimeView1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fanqie_sercond);

		ovalTimeView1 = (com.example.henshin.study.fanqie.OvalTimeView) findViewById(R.id.ovalTimeView1);
		workStop = (ImageButton) findViewById(R.id.work_stop);
		workStop.setOnClickListener(listener);

		ChangeModel(getIntent());
	}

	private final OnClickListener listener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			// TODO Auto-generated method stub

			AppApplication.getInstances().getWork().setCurrentlong(ovalTimeView1.finaltime - ovalTimeView1.longtime);
			Intent intent = new Intent(SecordActivity.this, ThirtyActivity.class);
			startActivity(intent);
			SecordActivity.this.finish();
		}
	};

	public void ChangeModel(Intent intent){
		int flag = intent.getIntExtra("flag", 0);
		RelativeLayout relative = (RelativeLayout) findViewById(R.id.relative_secord);
		if(flag == 1){
			relative.setBackgroundColor(Color.parseColor("#3CB371"));
			ovalTimeView1.setTHandler(handler);
			ovalTimeView1.setLongtime(AppApplication.getInstances().getWork().getFoodlong(), 
					AppApplication.getInstances().getWork().getCurrentlong(), Color.parseColor("#3CB371"));
		}else if(flag == 2){
			relative.setBackgroundColor(Color.parseColor("#FFA500"));
			ovalTimeView1.setTHandler(handler);
			ovalTimeView1.setLongtime(AppApplication.getInstances().getWork().getGamelong(), 
					AppApplication.getInstances().getWork().getCurrentlong(), Color.parseColor("#FFA500"));
		}else{
			relative.setBackgroundColor(Color.parseColor("#FF0000"));
			ovalTimeView1.setTHandler(handler);
			ovalTimeView1.setLongtime(AppApplication.getInstances().getWork().getWorklong(), 
					AppApplication.getInstances().getWork().getCurrentlong(), Color.parseColor("#FF0000"));
		}
	}

	private final Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			if(msg.what == 1001){
				int status = AppApplication.getInstances().getWork().status;
				status = status + 1;
				status = status % 3;
				AppApplication.getInstances().getWork().setStatus(status);
				AppApplication.getInstances().getWork().setCurrentlong(0);
				if(status == 0)
					AppApplication.getInstances().getWork().setWorkname("工作中...");
				if(status == 1)
					AppApplication.getInstances().getWork().setWorkname("休息中...");
				if(status == 2)
					AppApplication.getInstances().getWork().setWorkname("游戏中...");
				Intent intent = new Intent(SecordActivity.this,SecordActivity.class);
				intent.putExtra("flag", status);
				startActivity(intent);
				SecordActivity.this.finish();
			}
			super.handleMessage(msg);
		}

	};

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

}
