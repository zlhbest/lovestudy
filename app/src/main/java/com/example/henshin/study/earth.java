package com.example.henshin.study;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.WebTiledLayer;
import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.AtmosphereEffect;
import com.esri.arcgisruntime.mapping.view.Camera;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.LightingMode;
import com.esri.arcgisruntime.mapping.view.SceneView;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;

import java.util.Calendar;

import static com.esri.arcgisruntime.mapping.view.AtmosphereEffect.HORIZON_ONLY;
import static com.esri.arcgisruntime.mapping.view.LightingMode.LIGHT_AND_SHADOWS;


public class earth extends AppCompatActivity {
    private SceneView mainSceneView;//用于3维场景
    private LightingMode lig = LIGHT_AND_SHADOWS;//定义灯光
    private Calendar rightNow = Calendar.getInstance();//定义此时的时间点
    public static final AtmosphereEffect atm = HORIZON_ONLY;//选择一个场景的气氛
    private boolean flag;
    private ImageButton zoomout;
    private ImageButton zoomin;
    private ImageButton elocation;
    private Location gps;
    private Location net;
    private Location best;
    private static Context context;
    ArcGISScene scene;
    GraphicsOverlay overlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earth);
        //加载3D地球
        mainSceneView = (SceneView) findViewById(R.id.mainSceneView);
        zoomout = (ImageButton) findViewById(R.id.zoomoute);
        zoomin = (ImageButton) findViewById(R.id.zoomine);
        scene = new ArcGISScene();
        WebTiledLayer webTiledLayer = TianDiTuMethodsClass.CreateTianDiTuTiledLayer(TianDiTuMethodsClass.LayerType.TIANDITU_IMAGE_MERCATOR);//加载天地图墨卡托地形图
        Basemap tdtBasemap = new Basemap(webTiledLayer);
        WebTiledLayer webTiledLayer1 = TianDiTuMethodsClass.CreateTianDiTuTiledLayer(TianDiTuMethodsClass.LayerType.TIANDITU_VECTOR_ANNOTATION_CHINESE_MERCATOR);//加载中文标注
        tdtBasemap.getBaseLayers().add(webTiledLayer1);
        scene.setBasemap(tdtBasemap);
        Camera camera = new Camera(new Point(36.000, 120.000, 2000000, SpatialReferences.getWgs84()), 0, 0, 0);
        mainSceneView.setViewpointCamera(camera);
        mainSceneView.setScene(scene);//加载场景
        mainSceneView.setAtmosphereEffect(atm);//加载氛围
        mainSceneView.setSunLighting(lig);
        mainSceneView.setSunTime(rightNow);
        mainSceneView.setAttributionTextVisible(false);
        elocation = (ImageButton) findViewById(R.id.elocation);
        elocation.setOnClickListener(myAdapterBtnListener);
        zoomout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Point point;
                point = mainSceneView.getCurrentViewpointCamera().getLocation();
                Camera camera = new Camera(point, 0, 0, 0);
                mainSceneView.setViewpointCamera(camera.moveForward(500000));
            }
        });
        zoomin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Point point;
                point = mainSceneView.getCurrentViewpointCamera().getLocation();
                Camera camera = new Camera(point, 0, 0, 0);
                mainSceneView.setViewpointCamera(camera.moveForward(-500000));
            }
        });
    }

    private View.OnClickListener myAdapterBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (flag) {
                getBestLocation();
            } else {
                showToast("定位中");
            }
        }
    };
        /**
     * A placeholder fragment containing a simple view.
     */

    @Override
    protected void onResume() {
        super.onResume();
        initPermission();//针对6.0以上版本做权限适配
    }


    private void initPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //检查权限
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //请求权限
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                flag = true;
            }
        } else {
            flag = true;
        }
    }

    /**
     * 权限的结果回调函数
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            flag = grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED;
        }
    }

    /**
     * 通过GPS获取定位信息
     */
    public void getGPSLocation() {
         gps = LocationUtils.getGPSLocation(this);
        if (gps == null) {
            //设置定位监听，因为GPS定位，第一次进来可能获取不到，通过设置监听，可以在有效的时间范围内获取定位信息
            LocationUtils.addLocationListener(context, LocationManager.GPS_PROVIDER, new LocationUtils.ILocationListener() {
                @Override
                public void onSuccessLocation(Location location) {
                    if (location != null) {
                        Toast.makeText(earth.this, "gps onSuccessLocation location:  lat==" + location.getLatitude() + "     lng==" + location.getLongitude(), Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(earth.this, "gps location is null", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(this, "gps location: lat==" + gps.getLatitude() + "  lng==" + gps.getLongitude(), Toast.LENGTH_SHORT).show();
            Camera camera = new Camera(new Point(gps.getLongitude(), gps.getLatitude(), 2000, SpatialReferences.getWgs84()), 0, 0, 0);
            mainSceneView.setViewpointCamera(camera);
            mainSceneView.getGraphicsOverlays().remove(overlay);
            Point pt = new Point(gps.getLongitude(),gps.getLatitude(), SpatialReferences.getWgs84());
            SimpleMarkerSymbol markerSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.RED,15);
            overlay = new GraphicsOverlay();
            mainSceneView.getGraphicsOverlays().add(overlay);
            overlay.getGraphics().add(new Graphic(pt, markerSymbol));
        }
    }

    /**
     * 通过网络等获取定位信息
     */
    private void getNetworkLocation() {
         net = LocationUtils.getNetWorkLocation(this);
        if (net == null) {
            Toast.makeText(this, "net location is null", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "network location: lat==" + net.getLatitude() + "  lng==" + net.getLongitude(), Toast.LENGTH_SHORT).show();
            Camera camera = new Camera(new Point(net.getLongitude(), net.getLatitude(), 2000, SpatialReferences.getWgs84()), 0, 0, 0);
            mainSceneView.setViewpointCamera(camera);
            mainSceneView.getGraphicsOverlays().remove(overlay);
            Point pt = new Point(net.getLongitude(),net.getLatitude(), SpatialReferences.getWgs84());
            SimpleMarkerSymbol markerSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.RED, 15);
            overlay = new GraphicsOverlay();
            mainSceneView.getGraphicsOverlays().add(overlay);
            overlay.getGraphics().add(new Graphic(pt, markerSymbol));
        }
    }

    /**
     * 采用最好的方式获取定位信息
     */
    private void getBestLocation() {
        Criteria c = new Criteria();//Criteria类是设置定位的标准信息（系统会根据你的要求，匹配最适合你的定位供应商），一个定位的辅助信息的类
        c.setPowerRequirement(Criteria.POWER_LOW);//设置低耗电
        c.setAltitudeRequired(true);//设置需要海拔
        c.setBearingAccuracy(Criteria.ACCURACY_COARSE);//设置COARSE精度标准
        c.setAccuracy(Criteria.ACCURACY_LOW);//设置高精度
        //... Criteria 还有其他属性，就不一一介绍了
         best = LocationUtils.getBestLocation(this, c);
        if (best == null) {
            Toast.makeText(this, " best location is null", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "best location: lat==" + best.getLatitude() + " lng==" + best.getLongitude(), Toast.LENGTH_SHORT).show();
            Camera camera = new Camera(new Point(best.getLongitude(), best.getLatitude(), 2000, SpatialReferences.getWgs84()), 0, 0, 0);
            mainSceneView.setViewpointCamera(camera);
            mainSceneView.getGraphicsOverlays().remove(overlay);
            Point pt = new Point(best.getLongitude(),best.getLatitude(), SpatialReferences.getWgs84());
            SimpleMarkerSymbol markerSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.RED, 15);
            overlay = new GraphicsOverlay();
            mainSceneView.getGraphicsOverlays().add(overlay);
            overlay.getGraphics().add(new Graphic(pt, markerSymbol));
        }
    }
    private void showToast(CharSequence msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        //处理动作栏项点击此处。动作栏会
        //自动处理Home / Up按钮上的点击，这么久
        //在AndroidManifest.xml中指定父活动。
        int id = item.getItemId();
        if (id == R.id.shiliang2) {
            WebTiledLayer webTiledLayer = TianDiTuMethodsClass.CreateTianDiTuTiledLayer(TianDiTuMethodsClass.LayerType.TIANDITU_VECTOR_MERCATOR);//加载天地图墨卡托地形图
            Basemap tdtBasemap = new Basemap(webTiledLayer);
            scene.setBasemap(tdtBasemap);
            mainSceneView.setScene(scene);
        } else if (id == R.id.yingxaing2) {
            WebTiledLayer webTiledLayer = TianDiTuMethodsClass.CreateTianDiTuTiledLayer(TianDiTuMethodsClass.LayerType.TIANDITU_IMAGE_MERCATOR);//加载天地图墨卡托地形图
            Basemap tdtBasemap = new Basemap(webTiledLayer);
            scene.setBasemap(tdtBasemap);
            mainSceneView.setScene(scene);
        } else if (id == R.id.dixing2) {
            WebTiledLayer webTiledLayer = TianDiTuMethodsClass.CreateTianDiTuTiledLayer(TianDiTuMethodsClass.LayerType.TIANDITU_TERRAIN_MERCATOR);//加载天地图墨卡托地形图
            Basemap tdtBasemap = new Basemap(webTiledLayer);
            scene.setBasemap(tdtBasemap);
            mainSceneView.setScene(scene);
        }else if(id ==R.id.gps){
            if (flag) {
                getGPSLocation();
            } else {
                showToast("定位中");
            }
        }else if(id ==R.id.net){
            if (flag) {
                getNetworkLocation();
            } else {
                showToast("定位中");
            }
        }else if(id==R.id.best){
            if (flag) {
                getBestLocation();
            } else {
                showToast("定位中");
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.earth, menu);
        return true;
    }
}



