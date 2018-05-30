package com.example.henshin.study;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.esri.arcgisruntime.geometry.Multipoint;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.PointCollection;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.ArcGISMapImageLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.DrawStatus;
import com.esri.arcgisruntime.mapping.view.DrawStatusChangedEvent;
import com.esri.arcgisruntime.mapping.view.DrawStatusChangedListener;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Created by henshin on 2017/8/21.
 */

public class reli extends AppCompatActivity {
    private MapView mapView;
    private Viewpoint vp;
    private ArcGISMap map;
    //向服务器插入手机号码，用作身份标识
    private OkHttpClient client = new OkHttpClient();//创建okHttpClient对象
    private TextView tvResult;
    String result;
    PointCollection stateCapitalsPST;
    Multipoint multipoint;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            result = (String) msg.obj;
            stateCapitalsPST = new PointCollection(SpatialReferences.getWgs84());
            for (int i = 0; i < result.split(";").length; i++) {
                stateCapitalsPST.add(Double.parseDouble(result.split(";")[i].split(",")[1]), Double.parseDouble(result.split(";")[i].split(",")[0]));
            }
            BitmapDrawable pinStarBlueDrawable = (BitmapDrawable) ContextCompat.getDrawable(reli.this, R.drawable.pin_star_blue2);
            final PictureMarkerSymbol campsiteSymbol = new PictureMarkerSymbol(pinStarBlueDrawable);
            campsiteSymbol.setHeight(20);
            campsiteSymbol.setWidth(20);
            campsiteSymbol.loadAsync();
            campsiteSymbol.addDoneLoadingListener(new Runnable() {
                @Override
                public void run() {
                    multipoint = new Multipoint(stateCapitalsPST);
                    GraphicsOverlay overlay = new GraphicsOverlay();
                    mapView.getGraphicsOverlays().add(overlay);
                    overlay.getGraphics().add(new Graphic(multipoint, campsiteSymbol));
                }
            });
        }
    };//从服务器传过来点位
    private SharedPreferences sp;
    String username;
    private ProgressBar wait1;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reli);
        mapView = (MapView) findViewById(R.id.mmapView);
        tvResult = (TextView) findViewById(R.id.text);
        vp = new Viewpoint(36.0025, 120.1189, 12223.819286);
        String theURLString = "https://www.arcgis.com/home/webmap/viewer.html?webmap=fb84ad313bd3432983488ed1ba1d5bf3";//加载地图
        map = new ArcGISMap(theURLString);
        map.setInitialViewpoint(vp);
        mapView.setMap(map);
        wait1 = (ProgressBar)findViewById(R.id.progressBar) ;
        mapView.addDrawStatusChangedListener(new DrawStatusChangedListener() {
            @Override
            public void drawStatusChanged(DrawStatusChangedEvent drawStatusChangedEvent) {
                if (drawStatusChangedEvent.getDrawStatus() == DrawStatus.IN_PROGRESS) {
                    wait1.setVisibility(View.VISIBLE);
                    Log.d("drawStatusChanged", "spinner visible");
                } else if (drawStatusChangedEvent.getDrawStatus() == DrawStatus.COMPLETED) {
                    wait1.setVisibility(View.INVISIBLE);
                }
            }
        });
        sp = getSharedPreferences("setting", 0);
        username = sp.getString("username", null);
        login();
    }
    private void login(){
        //把请求参数封装到RequestBody里面
        FormBody.Builder formBuilder = new FormBody.Builder();
        formBuilder.add("username",username);//请求参数一      //该参数为手机号，用于传参
        RequestBody requestBody = formBuilder.build();
        Request.Builder builder = new Request.Builder().url("http://idashuai.cf/LoveStudy/getPoint").post(requestBody);
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
