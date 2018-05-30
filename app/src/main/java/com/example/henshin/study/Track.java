package com.example.henshin.study;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.esri.arcgisruntime.geometry.PointCollection;
import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.DrawStatus;
import com.esri.arcgisruntime.mapping.view.DrawStatusChangedEvent;
import com.esri.arcgisruntime.mapping.view.DrawStatusChangedListener;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.example.henshin.study.sanlianji.OptionsWindowHelper;
import com.example.henshin.study.sanlianji.OptionsWindowHelperd;

import java.io.IOException;
import java.util.Calendar;


import cn.jeesoft.widget.pickerview.CharacterPickerWindow;
import cn.jeesoft.widget.pickerview.CharacterPickerWindowd;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by henshin on 2017/8/7.
 */

public class Track extends AppCompatActivity {
    private MapView mapView1;
    public double[] Point;
    private Viewpoint vp;
    private ProgressBar wait2;
    private OkHttpClient client2 = new OkHttpClient();//创建okHttpClient对象
    private String result;
    private String username;
    private String str;
    GraphicsOverlay overlay;
       public PointCollection borderCAtoNV;
    ImageView find;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            result = (String) msg.obj;
            Point = new double[2 * result.split(";").length];
            if(result.length()==0){
                new AlertDialog.Builder(Track.this).setMessage("您今天没有轨迹哦出门走走吧~")
                 .setPositiveButton("OK", null).show();
            }else{
                int j = 0;
                for(int i=0;i<result.split(";").length;i++){
                    j=i*2;
                    Point[j]= Double.parseDouble(result.split(";")[i].split(",")[1]);
                    Point[j+1]= Double.parseDouble(result.split(";")[i].split(",")[0]);

                }
                overlay = new GraphicsOverlay();
                mapView1.getGraphicsOverlays().add(overlay);
                SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.BLUE, 3);
                overlay.getGraphics().add(new Graphic(createPolyline(), lineSymbol));
            }
            Point = null;
        }
    };//从服务器传过来点位
    private SharedPreferences sp;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.track);
        String year,month,day;
        Calendar c = Calendar.getInstance();
        year= String.valueOf(c.get(Calendar.YEAR));
        month= String.valueOf(c.get(Calendar.MONTH)+1);
        day= String.valueOf(c.get(Calendar.DAY_OF_MONTH));
        if(year.length()==4){
            year = year.substring(2,4);
        }
        if(month.length()==1){
            month = "0"+month;
        }
        if (day.length()==1){
            day = "0"+day;
        }
        str  = year+month +day;

        mapView1 = (MapView) findViewById(R.id.mapView2);
        wait2 = (ProgressBar) findViewById(R.id.wait2);
        String theURLString = "https://www.arcgis.com/home/webmap/viewer.html?webmap=fb84ad313bd3432983488ed1ba1d5bf3";//加载地图
        vp = new Viewpoint(36.0025, 120.1189, 12223.819286);
        ArcGISMap map = new ArcGISMap(theURLString);
        map.setInitialViewpoint(vp);
        mapView1.setMap(map);
        mapView1.addDrawStatusChangedListener(new DrawStatusChangedListener() {
            @Override
            public void drawStatusChanged(DrawStatusChangedEvent drawStatusChangedEvent) {
                if (drawStatusChangedEvent.getDrawStatus() == DrawStatus.IN_PROGRESS) {
                    wait2.setVisibility(View.VISIBLE);
                    Log.d("drawStatusChanged", "spinner visible");
                } else if (drawStatusChangedEvent.getDrawStatus() == DrawStatus.COMPLETED) {
                    wait2.setVisibility(View.INVISIBLE);
                }
            }
        });
        sp=getSharedPreferences("setting", 0);
       username=sp.getString("username",null);
        gettrack();
        showWindow();

    }
//        Point = getIntent().getDoubleArrayExtra("myarray");
//        if (Point.length > 2) {
//            GraphicsOverlay overlay = new GraphicsOverlay();
//            mapView1.getGraphicsOverlays().add(overlay);
//            SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.BLUE, 3);
//            overlay.getGraphics().add(new Graphic(createPolyline(), lineSymbol));
//        }else{
//            Toast.makeText(this, "您的轨迹是空的哦~赶紧去走走吧", Toast.LENGTH_SHORT).show();
//        }
//    }
//
    private Polyline createPolyline(){
        borderCAtoNV = new PointCollection(SpatialReferences.getWgs84());
        for(int i=0;i<Point.length;i++){
            borderCAtoNV.add(Point[i],Point[i+1]);
            i=i+1;
        }
        Polyline polyline = new Polyline(borderCAtoNV);
        return polyline;
    }
    private void gettrack(){


        //把请求参数封装到RequestBody里面
        FormBody.Builder formBuilder = new FormBody.Builder();
        formBuilder.add("username",username);//请求参数一      //该参数为手机号，用于传参
        formBuilder.add("data",str);
        RequestBody requestBody = formBuilder.build();

        Request.Builder builder = new Request.Builder().url("http://idashuai.cf/LoveStudy/getTrajectory").post(requestBody);
        execute(builder);
    }

    //执行请求
    private void execute(Request.Builder builder){
        Call call = client2.newCall(builder.build());
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
            String strr = new String(response.body().bytes(),"utf-8");
            Log.i("MainActivity","onResponse:"+strr);
            //通过handler更新UI
            Message message=handler.obtainMessage();
            message.obj=strr;
            message.sendToTarget();
        }
    };
    private void showWindow() {
        find = (ImageView) findViewById(R.id.find);

        //初始化
        final CharacterPickerWindowd window = OptionsWindowHelperd.builder(Track.this, new OptionsWindowHelperd.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(String province, String city, String area) {
                Log.e("main", province + "," + city + "," + area);
               // Toast.makeText(getApplicationContext(), province + city + area, Toast.LENGTH_SHORT).show();
                mapView1.getGraphicsOverlays().remove(overlay);
                str = "";
                str = province + city + area;
                gettrack();
            }
        });
        find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 弹出
                window.showAtLocation(v, Gravity.BOTTOM, 0, 0);
            }
        });
    }
}