package com.example.henshin.study;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;

import com.example.henshin.study.qiushiye.TagCloudView;
import com.example.henshin.study.qiushiye.TextTagsAdapter;
import com.yasic.library.particletextview.MovingStrategy.RandomMovingStrategy;
import com.yasic.library.particletextview.Object.ParticleTextViewConfig;
import com.yasic.library.particletextview.View.ParticleTextView;


/**
 * Created by henshin on 2017/8/27.
 */

public class paiming  extends AppCompatActivity {
    static final String[] KEYS = { "NO.1", "NO.1", "NO.1", "NO.1", "NO.1", "NO.1", "NO.1", "NO.1", "NO.1", "NO.1", "NO.1", "NO.1", "NO.1", "NO.1", "NO.1", "NO.1"};
    private TagCloudView tagCloudView;
    private TextTagsAdapter textTagsAdapter;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.paiming);
        tagCloudView = (TagCloudView) findViewById(R.id.tag_cloud);
        tagCloudView.setBackgroundColor(Color.LTGRAY);
        textTagsAdapter = new TextTagsAdapter(KEYS);
        tagCloudView.setAdapter(textTagsAdapter);
    }
    @Override
    protected void onResume() {
        super.onResume();
        ParticleTextView particleTextView = (ParticleTextView) findViewById(R.id.particleTextView);
        RandomMovingStrategy randomMovingStrategy = new RandomMovingStrategy();
        ParticleTextViewConfig config = new ParticleTextViewConfig.Builder()
                .setRowStep(8)
                .setColumnStep(8)
                .setTargetText("NO.1")
                .setReleasing(0.2)
                .setParticleRadius(4)
                .setMiniDistance(0.1)
                .setTextSize(150)
                .setMovingStrategy(randomMovingStrategy)
                .instance();
        particleTextView.setConfig(config);
        particleTextView.startAnimation();
    }

}
