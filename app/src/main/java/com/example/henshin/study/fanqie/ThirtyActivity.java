package com.example.henshin.study.fanqie;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import com.example.henshin.study.R;

public class ThirtyActivity extends AppCompatActivity {

	private TextView jcyxText;
	private ImageButton buttonOk;
	private WaterWave waterWave;
	private Button workButton1,workButton2;
	private Animation myAnimation1,myAnimation2,myAnimation3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fanqie_thirty);

		buttonOk = (ImageButton) findViewById(R.id.button_ok);
		buttonOk.setTag(0);
		buttonOk.setOnClickListener(listener);

		waterWave = (WaterWave) findViewById(R.id.waterWave1);
		waterWave.setOnClickListener(listener);

		workButton1 = (Button) findViewById(R.id.work_button1);
		workButton2 = (Button) findViewById(R.id.work_button2);
		workButton1.setOnClickListener(listener);
		workButton2.setOnClickListener(listener);

		jcyxText = (TextView) findViewById(R.id.jcyx_text);

		myAnimation1 = AnimationUtils.loadAnimation(this,R.anim.textview_anim1);
		myAnimation1.setAnimationListener(animalistener);
		myAnimation2 = AnimationUtils.loadAnimation(this,R.anim.button_anim1);
		myAnimation2.setAnimationListener(animalistener);
		myAnimation3 = AnimationUtils.loadAnimation(this,R.anim.button_anim2);
		myAnimation3.setAnimationListener(animalistener);

		workButton1.startAnimation(myAnimation2);
		workButton2.startAnimation(myAnimation3);
	}


	private final AnimationListener animalistener = new AnimationListener() {
		@Override
		public void onAnimationStart(Animation animation) {
			// TODO Auto-generated method stub
		}
		@Override
		public void onAnimationRepeat(Animation animation) {
			// TODO Auto-generated method stub
		}
		@Override
		public void onAnimationEnd(Animation animation) {
			// TODO Auto-generated method stub
			int tag = (Integer)buttonOk.getTag();
			if(tag == 1){
				workButton1.setText("分享成就");
				workButton2.setText("开始新的");
				jcyxText.setText("我是超人，手指一戳，任务完成！");
			}
			else if(tag == 0){
				workButton1.setText("开始工作");
				workButton1.setText("开始休息");
				jcyxText.setText("坚持一下，胜利就在前方！");

			}
			workButton1.setTag(tag);
			workButton2.setTag(tag);
		}
	};

	private final OnClickListener listener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			// TODO Auto-generated method stub
			int id = view.getId();
			int tag = (Integer) view.getTag();
			if(id == R.id.button_ok)
				switchStatus((Integer)view.getTag());
			else if(id == R.id.work_button1 && tag == 0){
				if(AppApplication.getInstances().getWork().getStatus() == 0){
					AppApplication.getInstances().getWork().setStatus(1);
					AppApplication.getInstances().getWork().setCurrentlong(0);
					AppApplication.getInstances().getWork().setWorkname("休息中....");
				}
				toOtherActivity(SecordActivity.class, 1);
			}else if(id == R.id.work_button2 && tag == 0){
				if(AppApplication.getInstances().getWork().getStatus() == 1){
					AppApplication.getInstances().getWork().setStatus(0);
					AppApplication.getInstances().getWork().setCurrentlong(0);
					AppApplication.getInstances().getWork().setWorkname("工作中...");
				}
				toOtherActivity(SecordActivity.class, 0);
			}else{
				if(id == R.id.work_button2){
					AppApplication.getInstances().getWork().setStatus(0);
					AppApplication.getInstances().getWork().setCurrentlong(0);
					AppApplication.getInstances().getWork().setWorkname("工作中....");
					toOtherActivity(FanqieActivity.class, 0);
				}else if(id == R.id.work_button1){
					Intent intent=new Intent(Intent.ACTION_SEND);
					intent.setType("text/plain"); 
					intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(Intent.createChooser(intent, getTitle()));
				}
			}
		}
	};

	public void toOtherActivity(Class<?> cls, int flag){
		Intent intent = new Intent(ThirtyActivity.this, cls);
		intent.putExtra("flag", flag);
		startActivity(intent);
		this.finish();
	}

	public void switchStatus(int tag){
		if(tag == 0){
			buttonOk.setBackgroundResource(R.drawable.button_oval_finash);
			buttonOk.setImageResource(R.drawable.icon_status_finished);
			waterWave.setVisibility(View.INVISIBLE);
			buttonOk.setTag(1);
			jcyxText.startAnimation(myAnimation1);
			workButton1.startAnimation(myAnimation2);
			workButton2.startAnimation(myAnimation3);
		}else{
			buttonOk.setBackgroundResource(R.drawable.button_oval_bg4);
			buttonOk.setImageResource(R.drawable.icon_status_unfinished);
			waterWave.setVisibility(View.VISIBLE);
			buttonOk.setTag(0);
			jcyxText.startAnimation(myAnimation1);
			workButton1.startAnimation(myAnimation2);
			workButton2.startAnimation(myAnimation3);
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
}
