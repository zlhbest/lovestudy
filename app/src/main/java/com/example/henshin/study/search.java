/* Copyright 2016 ESRI
 *
 * All rights reserved under the copyright laws of the United States
 * and applicable international laws, treaties, and conventions.
 *
 * You may freely redistribute and use this sample code, with or
 * without modification, provided you include the original copyright
 * notice and use restrictions.
 *
 * See the Sample code usage restrictions document for further information.
 *
 */

package com.example.henshin.study;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.DrawStatus;
import com.esri.arcgisruntime.mapping.view.DrawStatusChangedEvent;
import com.esri.arcgisruntime.mapping.view.DrawStatusChangedListener;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapScaleChangedEvent;
import com.esri.arcgisruntime.mapping.view.MapScaleChangedListener;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleRenderer;
import com.example.henshin.study.SpringActionMeau.ActionMenu;
import com.example.henshin.study.SpringActionMeau.OnActionItemClickListener;
import com.example.henshin.study.sanlianji.OptionsWindowHelper;

import java.io.IOException;
import java.util.Iterator;

import cn.jeesoft.widget.pickerview.CharacterPickerWindow;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class search extends AppCompatActivity {
    private ActionMenu actionMenuBottom;
    MapView mMapView;
    ServiceFeatureTable mServiceFeatureTable10000;
    ServiceFeatureTable mServiceFeatureTable50002;
    ServiceFeatureTable mServiceFeatureTable50003;
    ServiceFeatureTable mServiceFeatureTable50004;
    ServiceFeatureTable mServiceFeatureTable50005;
    ServiceFeatureTable mServiceFeatureTable50001;

    FeatureLayer mFeaturelayer10000;
    FeatureLayer mFeaturelayer50001;
    FeatureLayer mFeaturelayer50002;
    FeatureLayer mFeaturelayer50003;
    FeatureLayer mFeaturelayer50004;
    FeatureLayer mFeaturelayer50005;
    ProgressBar progressBar;
    ImageView button;
    private OkHttpClient client = new OkHttpClient();//创建okHttpClient对象
    private String result;
   private String zhouci;
    private String jieci;
    private  String xingqi;
    private String str;
    GraphicsOverlay overlay;
    Feature feature;
    String searchString;
    Boolean aBoolean = true;//用于检验是否有mFeaturelayer10000
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            result = (String) msg.obj;
            for(int i = 0;i<result.split(";").length;i++){
                searchForState(result.split(";")[i]);
            }

        }
    };//从服务器传过来教室的名字
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        actionMenuBottom = (ActionMenu) findViewById(R.id.actionMenuBottom);

        // add menu items
//按钮的显示
        actionMenuBottom.addView(R.drawable.one, getItemColor(R.color.menuNormalInfo), getItemColor(R.color.menuPressInfo));
        actionMenuBottom.addView(R.drawable.two, getItemColor(R.color.menuNormalRed), getItemColor(R.color.menuPressRed));
        actionMenuBottom.addView(R.drawable.three,getItemColor(R.color.swiperefresh_color1), getItemColor(R.color.swiperefresh_color1));
        actionMenuBottom.addView(R.drawable.fure,getItemColor(R.color.colorAccent), getItemColor(R.color.colorAccent));
        actionMenuBottom.addView(R.drawable.five,getItemColor(R.color.edit_hint_color1), getItemColor(R.color.edit_hint_color1));

        // inflate MapView from layout
        mMapView = (MapView) findViewById(R.id.mapView);
        progressBar = (ProgressBar) findViewById(R.id.wait3);

        // create a map with the topographic basemap
        final ArcGISMap map = new ArcGISMap("https://www.arcgis.com/home/webmap/viewer.html?webmap=fb84ad313bd3432983488ed1ba1d5bf3");
        // set the map to be displayed in the mapview
        mMapView.setMap(map);

        //使用其服务功能表创建要素图层
        //创建服务功能表
        mServiceFeatureTable10000 = new ServiceFeatureTable(getResources().getString(R.string.sample_service_url));
        //使用服务功能表创建要素图层
        mFeaturelayer10000 = new FeatureLayer(mServiceFeatureTable10000);
        mFeaturelayer10000.setOpacity(0.8f);
        //覆盖渲染器
        SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.BLACK, 1);
        SimpleFillSymbol fillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, Color.YELLOW, lineSymbol);
        mFeaturelayer10000.setRenderer(new SimpleRenderer(fillSymbol));

        //将图层添加到地图
        map.getOperationalLayers().add(mFeaturelayer10000);
        // 放大到山科的观点
        mMapView.setViewpointCenterAsync(new Point(120.1189, 36.0025, SpatialReferences.getWgs84()), 10000);
        mMapView.addDrawStatusChangedListener(new DrawStatusChangedListener() {
            @Override
            public void drawStatusChanged(DrawStatusChangedEvent drawStatusChangedEvent) {
                if (drawStatusChangedEvent.getDrawStatus() == DrawStatus.IN_PROGRESS) {
                    progressBar.setVisibility(View.VISIBLE);
                    Log.d("drawStatusChanged", "spinner visible");
                } else if (drawStatusChangedEvent.getDrawStatus() == DrawStatus.COMPLETED) {
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
        //设置监听来获取地图上的距离
        mMapView.addMapScaleChangedListener(new MapScaleChangedListener() {
            @Override
            public void mapScaleChanged(MapScaleChangedEvent mapScaleChangedEvent) {
                if(mMapView.getMapScale()<=5000){
                    mServiceFeatureTable50002 = new ServiceFeatureTable(getResources().getString(R.string.server_floor2));
                    //使用服务功能表创建要素图层
                    mFeaturelayer50002 = new FeatureLayer(mServiceFeatureTable50002);
                    mFeaturelayer50002.setOpacity(0.8f);
                    map.getOperationalLayers().remove(mFeaturelayer10000);
                    map.getOperationalLayers().add(mFeaturelayer50002);
                    actionMenuBottom.setItemClickListener(new OnActionItemClickListener() {
                        @Override
                        public void onItemClick(int index) {
                           switch (index){
                               case 1:
                                   showMessage("现在教室的层数为:"+index);
                                   mServiceFeatureTable50001 = new ServiceFeatureTable(getResources().getString(R.string.server_floor1));
                                   //使用服务功能表创建要素图层
                                   mFeaturelayer50001 = new FeatureLayer(mServiceFeatureTable50001);
                                   mFeaturelayer50001.setOpacity(0.8f);

                                   map.getOperationalLayers().add(mFeaturelayer50001);
                                   break;
                               case 2:
                                   showMessage("现在教室的层数为:"+index);
                                   mServiceFeatureTable50002 = new ServiceFeatureTable(getResources().getString(R.string.server_floor2));
                                   //使用服务功能表创建要素图层
                                   mFeaturelayer50002 = new FeatureLayer(mServiceFeatureTable50002);
                                   mFeaturelayer50002.setOpacity(0.8f);
                                   map.getOperationalLayers().remove(mFeaturelayer50001);
                                   map.getOperationalLayers().add(mFeaturelayer50002);
                                   break;
                               case 3:
                                   showMessage("现在教室的层数为:"+index);
                                   mServiceFeatureTable50003 = new ServiceFeatureTable(getResources().getString(R.string.server_floor3));
                                   //使用服务功能表创建要素图层
                                   mFeaturelayer50003 = new FeatureLayer(mServiceFeatureTable50003);
                                   mFeaturelayer50003.setOpacity(0.8f);
                                   map.getOperationalLayers().add(mFeaturelayer50003);
                                   break;
                               case 4:
                                   showMessage("现在教室的层数为:"+index);
                                   mServiceFeatureTable50004 = new ServiceFeatureTable(getResources().getString(R.string.server_floor4));
                                   //使用服务功能表创建要素图层
                                   mFeaturelayer50004 = new FeatureLayer(mServiceFeatureTable50004);
                                   mFeaturelayer50004.setOpacity(0.8f);
                                   map.getOperationalLayers().add(mFeaturelayer50004);
                                   break;
                               case 5:
                                   showMessage("现在教室的层数为:"+index);
                                   mServiceFeatureTable50005 = new ServiceFeatureTable(getResources().getString(R.string.server_floor5));
                                   //使用服务功能表创建要素图层
                                   mFeaturelayer50005 = new FeatureLayer(mServiceFeatureTable50005);
                                   mFeaturelayer50005.setOpacity(0.8f);
                                   map.getOperationalLayers().add(mFeaturelayer50005);
                                   break;
                           }
                        }

                        @Override
                        public void onAnimationEnd(boolean isOpen) {
                        }
                    });
                }else{
                    for(int i=0;i<map.getOperationalLayers().size();i++){
                        if(map.getOperationalLayers().get(i)==mFeaturelayer10000){
                            aBoolean = true;
                        } else{
                            aBoolean = false;
                        }
                    }
                    if(aBoolean ==false){
                        map.getOperationalLayers().remove(mFeaturelayer50001);
                        map.getOperationalLayers().add(mFeaturelayer10000);
                    }
                }
            }
        });
        mMapView.setOnTouchListener(new DefaultMapViewOnTouchListener(this, mMapView) {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                //得到被点击的点，并将其转换为地图坐标中的一个点
                Point clickPoint = mMapView.screenToLocation(new android.graphics.Point(Math.round(e.getX()), Math.round(e.getY())));
                int tolerance = 10;
                double mapTolerance = tolerance * mMapView.getUnitsPerDensityIndependentPixel();
                //创建使用查询进行选择所需的对象
                Envelope envelope = new Envelope(clickPoint.getX() - mapTolerance, clickPoint.getY() - mapTolerance, clickPoint.getX() + mapTolerance, clickPoint.getY() + mapTolerance, map.getSpatialReference());
                QueryParameters query = new QueryParameters();
                query.setGeometry(envelope);
                //呼叫选择功能
                final ListenableFuture<FeatureQueryResult> future = mFeaturelayer10000.selectFeaturesAsync(query, FeatureLayer.SelectionMode.NEW);
                //添加完成加载侦听器以在选择返回时触发
                future.addDoneListener(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //呼吁未来取得成果
                            FeatureQueryResult result = future.get();
                            // 创建一个迭代器
                            Iterator<Feature> iterator = result.iterator();
                            //Feature feature;
                            // 通过选择
                            int counter = 0;
                            while (iterator.hasNext()) {
                                feature = iterator.next();
                                counter++;
                                Log.d(getResources().getString(R.string.app_name), "Selection #: " + counter + " Table name: " + feature.getFeatureTable().getTableName());
                            }
                            new AlertDialog.Builder(search.this)
                                    .setTitle("查看您的选择")
                                    .setMessage("您选择的教室的名称是:" + feature.getAttributes().get("Name").toString())
                                    .setPositiveButton("确定", null)
                                    .show();
                            //Toast.makeText(getApplicationContext(), feature.getAttributes().get("Name") + " features selected", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Log.e(getResources().getString(R.string.app_name), "Select feature failed: " + e.getMessage());
                        }
                    }
                });
                return super.onSingleTapConfirmed(e);
            }
        });
        showWindow();


    }

    /**
     * 从搜索小部件处理搜索意图
     */
    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            searchString = intent.getStringExtra(SearchManager.QUERY);
        }
    }


    public void searchForState(final String searchString) {

        // 清除任何先前的选择
        mFeaturelayer50002.clearSelection();

        //创建使用查询进行选择所需的对象
        QueryParameters query = new QueryParameters();
        //使搜索不区分大小写
        query.setWhereClause("upper(name) LIKE '%" + searchString.toUpperCase() + "%'");
        //呼叫选择功能
        final ListenableFuture<FeatureQueryResult> future = mServiceFeatureTable50002.queryFeaturesAsync(query);
        //添加完成加载侦听器以在选择返回时触发
        future.addDoneListener(new Runnable() {
            @Override
            public void run() {
                try {
                    // 呼吁未来取得成果
                    FeatureQueryResult result = future.get();
                    Iterator<Feature> iterator = result.iterator();
                    // 检查有一些结果
                    if (iterator.hasNext()) {
                        //获得结果中第一个功能的扩展以放大到
                        while (iterator.hasNext()) {
                            feature = iterator.next();
                            feature.getFeatureTable().addFeatureAsync(feature);
                            Envelope envelope = feature.getGeometry().getExtent();
                            //mMapView.setViewpointGeometryAsync(envelope, 20000);
                        }
                        mFeaturelayer50002.setSelectionWidth(3);
                        //Select the feature
                        mFeaturelayer50002.selectFeature(feature);
                    } else {
                        //Toast.makeText(search.this, "No states found with name: " + searchString, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(search.this, "Feature search failed for: " + searchString + ". Error=" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(getResources().getString(R.string.app_name), "Feature search failed for: " + searchString + ". Error=" + e.getMessage());
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //充气菜单;如果存在，则会将项目添加到操作栏。
        getMenuInflater().inflate(R.menu.menu_main, menu);

        //获取SearchView并设置可搜索的配置
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        // 假设当前活动是可搜索的活动
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default

        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        // pause MapView
        mMapView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // resume MapView
        mMapView.resume();
    }

    private void showWindow() {
        button = (ImageView) findViewById(R.id.show);

        //初始化
        final CharacterPickerWindow window = OptionsWindowHelper.builder(search.this, new OptionsWindowHelper.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(String province, String city, String area) {
                Log.e("main", province + "," + city + "," + area);
                Toast.makeText(getApplicationContext(), province + city + area, Toast.LENGTH_SHORT).show();
                zhouci = province;xingqi = city;jieci = area;
                kzxscx();
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 弹出
                window.showAtLocation(v, Gravity.BOTTOM, 0, 0);
            }
        });
    }
    private void showMessage(String content) {
        Toast.makeText(this, content, Toast.LENGTH_SHORT).show();
    }
    private int getItemColor(int colorID) {
        return getResources().getColor(colorID);
    }
    private void kzxscx(){


        //把请求参数封装到RequestBody里面
        FormBody.Builder formBuilder = new FormBody.Builder();
        formBuilder.add("location","J14");
        formBuilder.add("zhoushu",zhouci);
        formBuilder.add("xingqi",xingqi);
        formBuilder.add("jieci",jieci);
        RequestBody requestBody = formBuilder.build();

        Request.Builder builder = new Request.Builder().url("http://idashuai.cf/LoveStudy/kzxscx").post(requestBody);
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
            String strr = new String(response.body().bytes(),"utf-8");
            Log.i("MainActivity","onResponse:"+strr);
            //通过handler更新UI
            Message message=handler.obtainMessage();
            message.obj=strr;
            message.sendToTarget();
        }
    };
}
