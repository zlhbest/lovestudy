package com.example.henshin.study;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.henshin.study.fanqie.FanqieActivity;

import java.io.IOException;

import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.ZXingView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ScanCodeActivity extends Activity implements QRCodeView.Delegate {
    private static final int REQUEST_CODE_CAMERA = 999;
    private static final String TAG = ScanCodeActivity.class.getSimpleName();

    private QRCodeView mQRCodeView;

    private OkHttpClient client = new OkHttpClient();//创建okHttpClient对象
    private TextView tvResult;

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            String result= (String) msg.obj;
            tvResult.setText(result);
        }
    };

    private String scanResult;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_code);

        tvResult = (TextView)findViewById(R.id.text) ;

        mQRCodeView = (ZXingView) findViewById(R.id.zxingview);
        mQRCodeView.setDelegate(this);

        Log.e(TAG, "扫码:01");
    }

    @Override
    protected void onStart() {
        super.onStart();
        mQRCodeView.startCamera();
//        mQRCodeView.startCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);
        mQRCodeView.startSpot();

        Log.e(TAG, "扫码:02");
    }

    @Override
    protected void onStop() {
        mQRCodeView.stopCamera();
        super.onStop();

        Log.e(TAG, "扫码:03");
    }

    @Override
    protected void onDestroy() {
        mQRCodeView.onDestroy();
        super.onDestroy();
        Log.e(TAG, "扫码:04");
    }

    //震动器
    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(100);

        Log.e(TAG, "扫码:05");
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        scanResult = Base64Utils.getBase64(result);
        Log.i(TAG, "result:" + result);
        //Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
        vibrate();//震动
        mQRCodeView.stopSpot();

        if (!TextUtils.isEmpty(result)) {
            mQRCodeView.stopCamera();
            mQRCodeView.onDestroy();

            login();

            new Handler().postDelayed(new Runnable() {  //开启线程阻塞，以便查看是否登录成功。做调试用
                public void run() {
                    // TODO Auto-generated method stub
                }
            }, 10000);

            Intent intent = new Intent(ScanCodeActivity.this, FanqieActivity.class);
            intent.putExtra("url", result);
            //intent.setData(Uri.parse(result));
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "链接无效,请重新扫描", Toast.LENGTH_SHORT).show();
            mQRCodeView.startSpot();
        }
        Log.e(TAG, "扫码:06");
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        Log.e(TAG, "无相机权限,打开相机出错");

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_CAMERA);
        Log.e(TAG, "扫码:07");
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_spot:
                mQRCodeView.startSpot();
                break;
            case R.id.stop_spot:
                mQRCodeView.stopSpot();
                break;
            case R.id.open_flashlight:
                mQRCodeView.openFlashlight();
                break;
            case R.id.close_flashlight:
                mQRCodeView.closeFlashlight();
                break;
            case R.id.rl_back:
                //onDestroy();
                mQRCodeView.stopCamera();
                mQRCodeView.onDestroy();
                finish();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CODE_CAMERA) {
            mQRCodeView.startCamera();
            mQRCodeView.startSpot();
        }
    }

    private void login(){
        //把请求参数封装到RequestBody里面
        FormBody.Builder formBuilder = new FormBody.Builder();
        formBuilder.add("classRoom",scanResult);//请求参数一
        RequestBody requestBody = formBuilder.build();

        Request.Builder builder = new Request.Builder().url("http://idashuai.cf/LoveStudy/setClassRoom").post(requestBody);
        execute(builder);
    }

    //执行请求
    private void execute(Request.Builder builder){
        Call call = client.newCall(builder.build());
        call.enqueue(callback);//加入调度队列
    }

    //请求回调
    private Callback callback=new Callback(){
        @Override
        public void onFailure(Call call, IOException e) {
            Log.i("MainActivity","onFailure");
            e.printStackTrace();
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            //从response从获取服务器返回的数据，转成字符串处理
            String str = new String(response.body().bytes(),"utf-8");
            Log.i("MainActivity","onResponse:"+str);

            //通过handler更新UI
            Message message=handler.obtainMessage();
            message.obj=str;
            message.sendToTarget();
        }
    };



}
