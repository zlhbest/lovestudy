package com.example.henshin.study;


import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.Toast;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.example.henshin.study.fanqie.FanqieActivity;

import java.util.Timer;


public class HomeActivity extends AppCompatActivity {


    private ViewPager mViewPager;

    private CardPagerAdapter mCardAdapter;
    private ShadowTransformer mCardShadowTransformer;

    private static Boolean isQuit = false;
    private long mExitTime = 0;
    Timer timer = new Timer();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        ArcGISRuntimeEnvironment.setLicense("runtimelite,1000,rud7281119085,none,3M2PMD17JYJXKXNCD250");
        mViewPager = (ViewPager) findViewById(R.id.viewPager);

        mCardAdapter = new CardPagerAdapter();
        mCardAdapter.addCardItem(new CardItem(R.string.title_1, R.string.text_1));
        mCardAdapter.addCardItem(new CardItem(R.string.title_2, R.string.text_2));
        mCardAdapter.addCardItem(new CardItem(R.string.title_5, R.string.text_5));
        mCardAdapter.addCardItem(new CardItem(R.string.title_3, R.string.text_3));
        mCardAdapter.addCardItem(new CardItem(R.string.title_4, R.string.text_4));

        initGPS();
        new Handler().postDelayed(new Runnable() {
            public void run() {
                // 1.获取系统服务
                ConnectivityManager cm = (ConnectivityManager) HomeActivity.this
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
                // 2.获取net信息
                NetworkInfo info = cm.getActiveNetworkInfo();
                // 3.判断网络是否可用
                if (info != null && info.isConnected()) {
                    Toast.makeText(HomeActivity.this, "网络可用",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(HomeActivity.this, "网络当前不可用，请检查设置！",
                            Toast.LENGTH_SHORT).show();
                    setNetwork(HomeActivity.this);
                }


            }
        }, 1000);

        mCardAdapter.setOnClickCallback(new CardPagerAdapter.OnClickCallback() {
            @Override
            public void onClick(int position) {
                switch (position){
                    case 0:
                        Intent intent = new Intent();
                        intent.setClass(HomeActivity.this, MainActivity.class);
                        startActivity(intent);
                        break;
                    case 1:
                        Intent intent2 = new Intent();
                        intent2.setClass(HomeActivity.this, reli.class);
                        startActivity(intent2);
                        break;
                    case 2:
                        Intent intent5 = new Intent();
                        intent5.setClass(HomeActivity.this, FanqieActivity.class);
                        startActivity(intent5);
                        break;
                    case 3:
                        Intent intent3 = new Intent();
                        intent3.setClass(HomeActivity.this, personal.class);
                        startActivity(intent3);
                        break;
                    case 4:
                        Intent intent4 = new Intent();
                        intent4.setClass(HomeActivity.this, about.class);
                        startActivity(intent4);
                }
                return;
            }
        });
        mCardShadowTransformer = new ShadowTransformer(mViewPager, mCardAdapter);

        mViewPager.setAdapter(mCardAdapter);
        mViewPager.setPageTransformer(false, mCardShadowTransformer);

        mViewPager.setOffscreenPageLimit(3);

        mCardShadowTransformer.enableScaling(true);
    }

    @Override
 public boolean onKeyDown(int keyCode, KeyEvent event) {
     if (keyCode == KeyEvent.KEYCODE_BACK) {
         if ((System.currentTimeMillis() - mExitTime) > 2000) {//
             // 如果两次按键时间间隔大于2000毫秒，则不退出
             Toast.makeText(this, "再按一次退出LoveStudy", Toast.LENGTH_SHORT).show();
             mExitTime = System.currentTimeMillis();// 更新mExitTime
         } else {
             System.exit(0);// 否则退出程序
          }
         return true;
     }
      return super.onKeyDown(keyCode, event);

  }
    private void initGPS() {
        LocationManager locationManager = (LocationManager) this
                .getSystemService(Context.LOCATION_SERVICE);
        // 判断GPS模块是否开启，如果没有则开启
        if (!locationManager
                .isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
            Toast.makeText(HomeActivity.this, "请打开GPS",
                    Toast.LENGTH_SHORT).show();
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage("请打开GPS");
            dialog.setPositiveButton("确定",
                    new android.content.DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {

                            // 转到手机设置界面，用户设置GPS
                            Intent intent = new Intent(
                                    Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult(intent, 0); // 设置完成后返回到原来的界面

                        }
                    });
            dialog.setNeutralButton("取消", new android.content.DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    arg0.dismiss();
                }
            } );
            dialog.show();
        } else {
            //弹出Toast
         Toast.makeText(HomeActivity.this, "GPS is ready",
                 Toast.LENGTH_LONG).show();
          // 弹出对话框
//         new AlertDialog.Builder(this).setMessage("GPS is ready")
//                  .setPositiveButton("OK", null).show();
        }
    }
    private void setNetwork(Context context){
        Intent intent=null;
        //判断手机系统的版本  即API大于10 就是3.0或以上版本
        if(android.os.Build.VERSION.SDK_INT>10){
            intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
        }else{
            intent = new Intent();
            ComponentName component = new ComponentName("com.android.settings","com.android.settings.WirelessSettings");
            intent.setComponent(component);
            intent.setAction("android.intent.action.VIEW");
        }
        context.startActivity(intent);
    }


}
