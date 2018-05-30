package com.example.henshin.study;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;

import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.ImageView;
import android.widget.ProgressBar;

import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.PointCollection;
import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.ArcGISMapImageLayer;
import com.esri.arcgisruntime.location.LocationDataSource;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.DrawStatus;
import com.esri.arcgisruntime.mapping.view.DrawStatusChangedEvent;
import com.esri.arcgisruntime.mapping.view.DrawStatusChangedListener;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;

import com.example.henshin.study.SpringActionMeau.ActionMenu;
import com.example.henshin.study.SpringActionMeau.OnActionItemClickListener;
import com.example.henshin.study.Switch.SwitchButton;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.IOException;

import com.nineoldandroids.view.ViewHelper;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
//import com.esri.arcgisruntime.data.TileCache;

public class MainActivity extends AppCompatActivity {
    Handler handler = new Handler();
    private Boolean bool;
    private ActionMenu actionMenuBottom;
     private MapView mMapView;
    private Runnable runnable;
    public PointCollection borderCAtoNV;
    private LocationDisplay mLocationDisplay;
    private com.esri.arcgisruntime.geometry.Point wgs84Point1;
    private Viewpoint vp;
    private ProgressBar wait;
    private ArcGISMap map;
    private LocationDataSource.Location location1;
    private com.esri.arcgisruntime.geometry.Point point;
    private SpatialReference SPATIAL_REFERENCE = SpatialReferences.getWgs84();
    private PointCollection points = new PointCollection(SPATIAL_REFERENCE);
    private int requestCode = 2;
    String[] reqPermissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission
            .ACCESS_COARSE_LOCATION};
    private DrawerLayout mDrawerLayout;
    private RelativeLayout imageView1;
    private RelativeLayout imageView2;
    private RelativeLayout imageView3;
    private RelativeLayout imageView5;
    private ImageView power;
    private ImageView personal;
    private ImageView sao;
    @Bind(R.id.switchButton)
    SwitchButton switchButton;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private OkHttpClient client2 = new OkHttpClient();//创建okHttpClient对象
    private SharedPreferences sp;
    private TextView tvResult;
    private Handler handler3 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            GraphicsOverlay overlay = new GraphicsOverlay();
            mMapView.getGraphicsOverlays().add(overlay);
            SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID                                                                                                             , Color.BLUE, 3);
            overlay.getGraphics().add(new Graphic(createPolyline(), lineSymbol));
        }
    };
    private Handler handler2=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            String result= (String) msg.obj;
            tvResult.setText(result);

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initViewData();
        mMapView = (MapView) findViewById(R.id.mapView);
        wait = (ProgressBar) findViewById(R.id.wait);
        vp = new Viewpoint(36.0025, 120.1189, 12223.819286);
        String theURLString = "https://www.arcgis.com/home/webmap/viewer.html?webmap=fb84ad313bd3432983488ed1ba1d5bf3";//加载地图
        map = new ArcGISMap(theURLString);
        ArcGISMapImageLayer censusLayer = new ArcGISMapImageLayer(getResources().getString(R.string.server));
        // Add layer to the map (by default, added as top layer)
        map.getOperationalLayers().add(censusLayer);
        map.setInitialViewpoint(vp);
        mMapView.setMap(map);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                // 1.获取系统服务
                ConnectivityManager cm = (ConnectivityManager)MainActivity.this
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
                // 2.获取net信息
                NetworkInfo info = cm.getActiveNetworkInfo();
                // 3.判断网络是否可用
                if (info != null && info.isConnected()) {
                } else {
                    Toast.makeText(MainActivity.this, "网络或者GPS当前不可用，请检查设置！",
                            Toast.LENGTH_SHORT).show();
                }


            }
        }, 1000);
        mMapView.addDrawStatusChangedListener(new DrawStatusChangedListener() {
            @Override
            public void drawStatusChanged(DrawStatusChangedEvent drawStatusChangedEvent) {
                if (drawStatusChangedEvent.getDrawStatus() == DrawStatus.IN_PROGRESS) {
                    wait.setVisibility(View.VISIBLE);
                    Log.d("drawStatusChanged", "spinner visible");
                } else if (drawStatusChangedEvent.getDrawStatus() == DrawStatus.COMPLETED) {
                    wait.setVisibility(View.INVISIBLE);
                }
            }
        });
        actionMenuBottom = (ActionMenu) findViewById(R.id.mainactionMenuBottom);
        actionMenuBottom.addView(R.drawable.earth, getItemColor(R.color.menuNormalInfo), getItemColor(R.color.menuPressInfo));
        actionMenuBottom.addView(R.drawable.location, getItemColor(R.color.menuNormalRed), getItemColor(R.color.menuPressRed));
        actionMenuBottom.addView(R.drawable.update,getItemColor(R.color.swiperefresh_color1), getItemColor(R.color.swiperefresh_color1));
        actionMenuBottom.setItemClickListener(new OnActionItemClickListener() {
            @Override
            public void onItemClick(int index) {
                switch (index){
                    case 1:
                        Intent intent = new Intent();
                        intent.setClass(MainActivity.this, earth.class);
                        startActivity(intent);
                        break;
                    case 2:
                        mLocationDisplay.startAsync();
                        point = mLocationDisplay.getMapLocation();
                        mMapView.setViewpointCenterAsync(point, 5000);
                        wgs84Point1 = (com.esri.arcgisruntime.geometry.Point) GeometryEngine.project(point, SpatialReferences.getWgs84());
                        getpoint();
                        break;
                    case 3:
                        Intent intent2 = new Intent();
                        intent2.setClass(MainActivity.this, MainActivity.class);
                        startActivity(intent2);
                        finish();
                        break;
                }
            }
            @Override
            public void onAnimationEnd(boolean isOpen) {
            }
        });
        mLocationDisplay = mMapView.getLocationDisplay();
        runnable = new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                //要做的事情
                if (bool == true) {
                    mLocationDisplay.startAsync();
                    mLocationDisplay.addLocationChangedListener(new LocationDisplay.LocationChangedListener() {
                        @Override
                        public void onLocationChanged(LocationDisplay.LocationChangedEvent locationChangedEvent) {
                            location1 = locationChangedEvent.getLocation();
                            point = location1.getPosition();
                            gettrack();
                            points.add(point);
                            point = null;
                            //通过handler更新UI
                            Message message = handler3.obtainMessage();
                            message.sendToTarget();
                        }
                    });
                }else{
                    mLocationDisplay.stop();
                }
            }
        };

        //判断开始定位时位置服务权限有没有开启
        mLocationDisplay.addDataSourceStatusChangedListener(new LocationDisplay.DataSourceStatusChangedListener() {
            @Override
            public void onStatusChanged(LocationDisplay.DataSourceStatusChangedEvent dataSourceStatusChangedEvent) {

                if (dataSourceStatusChangedEvent.isStarted())
                    return;
                if (dataSourceStatusChangedEvent.getError() == null)
                    return;
                boolean permissionCheck1 = ContextCompat.checkSelfPermission(MainActivity.this, reqPermissions[0]) == PackageManager.PERMISSION_GRANTED;
                boolean permissionCheck2 = ContextCompat.checkSelfPermission(MainActivity.this, reqPermissions[1]) == PackageManager.PERMISSION_GRANTED;
                if (!(permissionCheck1 && permissionCheck2)) {
                    ActivityCompat.requestPermissions(MainActivity.this, reqPermissions, requestCode);
                } else {
                    String message = String.format("Error in DataSourceStatusChangedListener: %s", dataSourceStatusChangedEvent
                            .getSource().getLocationDataSource().getError().getMessage());
                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                }
            }
        });
                imageView1 = (RelativeLayout)findViewById(R.id.guiji);
        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, Track.class);
                startActivity(intent);
                //finish();//停止当前的Activity,如果不写,则按返回键会跳转回原来的Activity

            }

        });
        imageView2=(RelativeLayout)findViewById(R.id.query);
        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, search.class);
                startActivity(intent);
            }
        });
        imageView3=(RelativeLayout)findViewById(R.id.route);
        imageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, route.class);
                startActivity(intent);
            }
        });
        personal = (ImageView)findViewById(R.id.sao);
        personal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, personal.class);
                startActivity(intent);
            }
        });
        imageView5=(RelativeLayout)findViewById(R.id.aboutus);
        imageView5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, about.class);
                startActivity(intent);
            }
        });
        power = (ImageView)findViewById(R.id.power);
        power.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences spout =getSharedPreferences("setting", 0);
                SharedPreferences.Editor ed =spout.edit();
                ed.clear();
                ed.apply();
                startActivity(new Intent(MainActivity.this,Welcome.class));
            }
        });
        sao=(ImageView)findViewById(R.id.saosao);
        sao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, ScanCodeActivity.class);
                startActivity(intent);
            }
        });
        tvResult = (TextView)findViewById(R.id.text) ;
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        initView();
        initEvents();

    }
    @Override
    protected void onPause() {
        mMapView.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.resume();
    }

    private Polyline createPolyline() {
        //从PointCollection创建一个折线
        borderCAtoNV = new PointCollection(SpatialReferences.getWgs84());
        for (int i = 4; i < points.size(); i++) {
            if ((points.get(i) != points.get(i - 1) || points.get(i) != points.get(i + 1))) {
                borderCAtoNV.add(i-4, points.get(i));//因为GPS定位前几个点定位不准确
            }
        }
        Polyline polyline = new Polyline(borderCAtoNV);
        //[DocRef: END]

        return polyline;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        //处理动作栏项点击此处。动作栏会
        //自动处理Home / Up按钮上的点击，这么久
        //在AndroidManifest.xml中指定父活动。
        int id = item.getItemId();
        if (id == R.id.shiliang) {
            vp = new Viewpoint(36.0025, 120.1189, 12223.819286);
            String theURLString = "https://www.arcgis.com/home/webmap/viewer.html?webmap=fb84ad313bd3432983488ed1ba1d5bf3";//加载地图
            map = new ArcGISMap(theURLString);
            ArcGISMapImageLayer censusLayer = new ArcGISMapImageLayer("http://123.206.28.56:6080/arcgis/rest/services/map/MapServer");
            // Add layer to the map (by default, added as top layer)
            map.getOperationalLayers().add(censusLayer);
            map.setInitialViewpoint(vp);
            mMapView.setMap(map);
        } else if (id == R.id.yingxaing) {
            vp = new Viewpoint(36.0025, 120.1189, 12223.819286);
            String theURLString = "https://www.arcgis.com/home/webmap/viewer.html?webmap=0f7479fb453c43139cb58fa4c1ea868c";//加载地图
            map = new ArcGISMap(theURLString);
            ArcGISMapImageLayer censusLayer = new ArcGISMapImageLayer("http://123.206.28.56:6080/arcgis/rest/services/map/MapServer");
            // Add layer to the map (by default, added as top layer)
            map.getOperationalLayers().add(censusLayer);
            map.setInitialViewpoint(vp);
            mMapView.setMap(map);
        }else if(id==R.id.dixing){
            vp = new Viewpoint(36.0025, 120.1189, 12223.819286);
            String theURLString = "https://www.arcgis.com/home/webmap/viewer.html?webmap=017a6ec857ec4150a1f6d51e74d755bb";//加载地图
            map = new ArcGISMap(theURLString);
            ArcGISMapImageLayer censusLayer = new ArcGISMapImageLayer("http://123.206.28.56:6080/arcgis/rest/services/map/MapServer");
            // Add layer to the map (by default, added as top layer)
            map.getOperationalLayers().add(censusLayer);
            map.setInitialViewpoint(vp);
            mMapView.setMap(map);
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
    public void OpenRightMenu(View view)
    {
        mDrawerLayout.openDrawer(Gravity.RIGHT);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED,
                Gravity.RIGHT);
    }
    public void OpenLeftMenu(View view)
    {
        mDrawerLayout.openDrawer(Gravity.LEFT);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED,
                Gravity.RIGHT);
    }



    private void initEvents()
    {
        mDrawerLayout.setDrawerListener(new DrawerLayout.DrawerListener()
        {
            @Override
            public void onDrawerStateChanged(int newState)
            {
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset)
            {
                View mContent = mDrawerLayout.getChildAt(0);
                View mMenu = drawerView;
                float scale = 1 - slideOffset;
                float rightScale = 0.8f + scale * 0.2f;

                if (drawerView.getTag().equals("LEFT"))
                {

                    float leftScale = 1 - 0.3f * scale;

                    ViewHelper.setScaleX(mMenu, leftScale);
                    ViewHelper.setScaleY(mMenu, leftScale);
                    ViewHelper.setAlpha(mMenu, 0.6f + 0.4f * (1 - scale));
                    ViewHelper.setTranslationX(mContent,
                            mMenu.getMeasuredWidth() * (1 - scale));
                    ViewHelper.setPivotX(mContent, 0);
                    ViewHelper.setPivotY(mContent,
                            mContent.getMeasuredHeight() / 2);
                    mContent.invalidate();
                    ViewHelper.setScaleX(mContent, rightScale);
                    ViewHelper.setScaleY(mContent, rightScale);
                } else
                {
                    ViewHelper.setTranslationX(mContent,
                            -mMenu.getMeasuredWidth() * slideOffset);
                    ViewHelper.setPivotX(mContent, mContent.getMeasuredWidth());
                    ViewHelper.setPivotY(mContent,
                            mContent.getMeasuredHeight() / 2);
                    mContent.invalidate();
                    ViewHelper.setScaleX(mContent, rightScale);
                    ViewHelper.setScaleY(mContent, rightScale);
                }

            }

            @Override
            public void onDrawerOpened(View drawerView)
            {
            }

            @Override
            public void onDrawerClosed(View drawerView)
            {
                mDrawerLayout.setDrawerLockMode(
                        DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.RIGHT);
            }
        });
    }

    private void initView()
    {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.id_drawerLayout);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED,
                Gravity.RIGHT);
    }
    private void getpoint(){
        sp=getSharedPreferences("setting", 0);
        String username =sp.getString("username",null);
        //把请求参数封装到RequestBody里面
        FormBody.Builder formBuilder = new FormBody.Builder();
        formBuilder.add("username",username);//请求参数一      //该参数为手机号，用于传参
        formBuilder.add("longitude",""+wgs84Point1.getY());//请求参数二
        formBuilder.add("latitude",""+wgs84Point1.getX());
        RequestBody requestBody = formBuilder.build();

        Request.Builder builder = new Request.Builder().url("http://idashuai.cf/LoveStudy/setPoint").post(requestBody);
        execute(builder);
    }
    private void gettrack(){
        sp=getSharedPreferences("setting", 0);
        String username =sp.getString("username",null);
        //把请求参数封装到RequestBody里面
        FormBody.Builder formBuilder = new FormBody.Builder();
        formBuilder.add("username",username);//请求参数一      //该参数为手机号，用于传参
        formBuilder.add("longitude",""+point.getY());//请求参数二
        formBuilder.add("latitude",""+point.getX());
        formBuilder.add("star","0");
        formBuilder.add("game","0");
        formBuilder.add("rest","0");
        RequestBody requestBody = formBuilder.build();

        Request.Builder builder = new Request.Builder().url("http://idashuai.cf/LoveStudy/setTrajectory").post(requestBody);
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
            String str = new String(response.body().bytes(),"utf-8");
            Log.i("MainActivity","onResponse:"+str);
            //通过handler更新UI
            Message message=handler2.obtainMessage();
            message.obj=str;
            message.sendToTarget();
        }
    };
    private void initViewData() {
        //switchButton.setToggleOn(false);//默认打开，参数传False,则打开页面初始化时不会有动画效果(改变状态会有动画)
        switchButton.setOnToggleChanged(new SwitchButton.OnToggleChanged(){
            @Override
            public void onToggle(boolean isOn) {
                if(isOn==true){
                    handler.postDelayed(runnable, 2000);//每兩秒執行一次runnable.
                    bool=true;
                }else{
                    bool=false;
                    handler.postDelayed(runnable,2000);//每兩秒執行一次runnable.
                }
            }
        });
    }
    private int getItemColor(int colorID) {
        return getResources().getColor(colorID);
    }
}
