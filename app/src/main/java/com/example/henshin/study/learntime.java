package com.example.henshin.study;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.henshin.study.leida.RadarChartView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by henshin on 2017/8/26.
 */

public class learntime extends AppCompatActivity {
    static final String[] KEYS = { "LT", "RT", "GT", "GS", "RK"};
    static final float[] VALUES = { 127.346F, 196.676F, 267.249F, 20,100, };
    static final String[] TIME = new String[] { "学习时间:", "游戏时间:", "休息时间:",
            "获得星数:","我的排名"};
    static final String[] VALUES1= { "100", "100", "100", "20","100", };
    RadarChartView chartView;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.learntime);
        chartView = (RadarChartView) findViewById(R.id.radar_chart);
        final Map<String, Float> axis = new LinkedHashMap<>(VALUES.length); // in 1,000 pounds (Sep 19, 2016)
        axis.put(KEYS[0], VALUES[0]);
        axis.put(KEYS[1], VALUES[1]);
        axis.put(KEYS[2], VALUES[2]);
        axis.put(KEYS[3], VALUES[3]);
        axis.put(KEYS[4], VALUES[4]);
        chartView.setAxis(axis);
        ListView listView = (ListView) findViewById(R.id.List);
        List<HashMap<String , String>> list = new ArrayList<>();
        for(int i = 0 ; i < TIME.length ; i++){
            HashMap<String , String> hashMap = new HashMap<>();
            hashMap.put("TIME" , TIME[i]);
            hashMap.put("VALUES" , VALUES1[i]);
            //把title , text存入HashMap之中
            list.add(hashMap);
            //把HashMap存入list之
        }
        ListAdapter listAdapter = new SimpleAdapter(
                this,
                list,
                android.R.layout.simple_list_item_2 ,
                new String[]{"TIME" , "VALUES"} ,
                new int[]{android.R.id.text1 , android.R.id.text2});
        //使用ListAdapter來顯示你輸入的文字

        listView.setAdapter(listAdapter);
        //將ListAdapter設定至ListView裡面
    }
}
