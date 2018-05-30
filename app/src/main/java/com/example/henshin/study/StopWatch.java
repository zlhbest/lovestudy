package com.example.henshin.study;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/*秒表功能
*
*
* */

public class StopWatch extends AppCompatActivity {

    private TextView mint;
    private TextView sec;
    private Button start;
    private Button reset;
    private long timeusedinsec;
    private boolean isstop = false;
    private Handler mHandler = new Handler() {
        /*
         * edit by bestYou 2017-06-26 21:28
         */
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    // 添加更新ui的代码
                    if (!isstop) {
                        updateView();
                        mHandler.sendEmptyMessageDelayed(1, 1000);
                    }
                    break;
                case 0:
                    break;
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stop_watch);
        initViews();
    }

    private void initViews() {
        mint = (TextView) findViewById(R.id.mint);
        sec = (TextView) findViewById(R.id.sec);
        reset = (Button) findViewById(R.id.reset);
        start = (Button) findViewById(R.id.start);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                mint.setText("00");
                sec.setText("00");
                start.setText("start");
                timeusedinsec=0;
                isstop=true;
            }
        });
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                mHandler.removeMessages(1);
                String aaa=start.getText().toString();
                if(aaa.equals("start")){
                    mHandler.sendEmptyMessage(1);
                    isstop = false;
                    start.setText("pause");
                }else {
                    mHandler.sendEmptyMessage(0);
                    isstop = true;
                    start.setText("start");
                }

            }
        });
    }
    private void updateView() {
        timeusedinsec += 1;
        int minute = (int) (timeusedinsec / 60)%60;
        int second = (int) (timeusedinsec % 60);
        if (minute < 10)
            mint.setText("0" + minute);
        else
            mint.setText("" + minute);
        if (second < 10)
            sec.setText("0" + second);
        else
            sec.setText("" + second);
    }
}


