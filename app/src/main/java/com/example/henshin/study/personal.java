package com.example.henshin.study;


import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dean.library.FloatBackground;
import com.example.henshin.study.floatbackground.FloatText;
import com.example.henshin.study.touxiang.CropOption;
import com.example.henshin.study.touxiang.CropOptionAdapter;
import com.example.henshin.study.touxiang.ZQRoundOvalImageView;

import java.io.File;

import java.util.ArrayList;
import java.util.List;

import cn.sharesdk.onekeyshare.OnekeyShare;


public class personal extends AppCompatActivity {
    private static final int PICK_FROM_CAMERA = 1;
    private static final int CROP_FROM_CAMERA = 2;
    private static final int PICK_FROM_FILE = 3;
    private ZQRoundOvalImageView mImageView;
    private ImageView share;
    private ImageView shezhi;
    private Uri imgUri;
    private SharedPreferences sp;
    private TextView talk;
    private LinearLayout travk;
    private LinearLayout learntime;
    private LinearLayout paiming;
    private ImageView erweima;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal);
        sp=getSharedPreferences("setting", 0);
        String username =sp.getString("username",null);
        final FloatBackground floatBackground = (FloatBackground) this.findViewById(R.id.user_view_solar_system);
        mImageView = (ZQRoundOvalImageView) findViewById(R.id.iv_portrait);
        erweima = (ImageView)findViewById(R.id.iv_logo_zxing) ;
        share = (ImageView)findViewById(R.id.share) ;
        travk=(LinearLayout)findViewById(R.id.rl_message);
        paiming = (LinearLayout)findViewById(R.id.rl_blog) ;
        shezhi = (ImageView)findViewById(R.id.iv_logo_setting);
        learntime = (LinearLayout)findViewById(R.id.rl_info_question);
        talk = (TextView)findViewById(R.id.tv_nick) ;
        talk.setText(username);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                floatBackground.startFloat();
            }
        }, 1000);
        floatBackground.addFloatView(new FloatText( 0.1f, 0.3f, "L"));
        floatBackground.addFloatView(new FloatText( 0.2f, 0.3f, "o"));
        floatBackground.addFloatView(new FloatText( 0.7f, 0.3f, "v"));
        floatBackground.addFloatView(new FloatText( 0.8f, 0.3f, "e"));
        floatBackground.addFloatView(new FloatText( 0.1f, 0.6f, "S"));
        floatBackground.addFloatView(new FloatText( 0.2f, 0.6f, "t"));
        floatBackground.addFloatView(new FloatText( 0.3f, 0.6f, "u"));
        floatBackground.addFloatView(new FloatText( 0.7f, 0.6f, "d"));
        floatBackground.addFloatView(new FloatText( 0.8f, 0.6f, "y"));
        travk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(personal.this, Track.class);
                startActivity(intent);
            }
        });
        shezhi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog();
            }
        });
        learntime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(personal.this, learntime.class);
                startActivity(intent);
            }
        });
        paiming.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(personal.this, paiming.class);
                startActivity(intent);
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"正在启动分享",Toast.LENGTH_SHORT).show();
                showShare();
            }
        });
        erweima.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(personal.this, ScanCodeActivity.class);
                startActivity(intent);
            }
        });
    }
    public void btnClick(View view) {
        new AlertDialog.Builder(personal.this).setTitle("选择头像")
                .setPositiveButton("相册", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 方式1，直接打开图库，只能选择图库的图片
                        Intent i = new Intent(Intent.ACTION_PICK,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        // 方式2，会先让用户选择接收到该请求的APP，可以从文件系统直接选取图片
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType("image/*");
                        startActivityForResult(intent, PICK_FROM_FILE);

                    }
                }).setNegativeButton("拍照", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(
                        MediaStore.ACTION_IMAGE_CAPTURE);
                imgUri = Uri.fromFile(new File(Environment
                        .getExternalStorageDirectory(), "avatar_"
                        + String.valueOf(System.currentTimeMillis())
                        + ".png"));
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
                startActivityForResult(intent, PICK_FROM_CAMERA);
            }
        }).create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case PICK_FROM_CAMERA:
                doCrop();
                break;
            case PICK_FROM_FILE:
                imgUri = data.getData();
                doCrop();
                break;
            case CROP_FROM_CAMERA:
                if (null != data) {
                    setCropImg(data);
                }
                break;
        }
    }

    private void doCrop() {

        final ArrayList<CropOption> cropOptions = new ArrayList<CropOption>();
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");
        List<ResolveInfo> list = getPackageManager().queryIntentActivities(
                intent, 0);
        int size = list.size();

        if (size == 0) {
            Toast.makeText(this, "can't find crop app", Toast.LENGTH_SHORT)
                    .show();
            return;
        } else {
            intent.setData(imgUri);
            intent.putExtra("outputX", 300);
            intent.putExtra("outputY", 300);
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("scale", true);
            intent.putExtra("return-data", true);

            // only one
            if (size == 1) {
                Intent i = new Intent(intent);
                ResolveInfo res = list.get(0);
                i.setComponent(new ComponentName(res.activityInfo.packageName,
                        res.activityInfo.name));
                startActivityForResult(i, CROP_FROM_CAMERA);
            } else {
                // many crop app
                for (ResolveInfo res : list) {
                    final CropOption co = new CropOption();
                    co.title = getPackageManager().getApplicationLabel(
                            res.activityInfo.applicationInfo);
                    co.icon = getPackageManager().getApplicationIcon(
                            res.activityInfo.applicationInfo);
                    co.appIntent = new Intent(intent);
                    co.appIntent
                            .setComponent(new ComponentName(
                                    res.activityInfo.packageName,
                                    res.activityInfo.name));
                    cropOptions.add(co);
                }

                CropOptionAdapter adapter = new CropOptionAdapter(
                        getApplicationContext(), cropOptions);

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("choose a app");
                builder.setAdapter(adapter,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                startActivityForResult(
                                        cropOptions.get(item).appIntent,
                                        CROP_FROM_CAMERA);
                            }
                        });

                builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {

                        if (imgUri != null) {
                            getContentResolver().delete(imgUri, null, null);
                            imgUri = null;
                        }
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
            }
        }
    }

    /**
     * set the bitmap
     *
     * @param picdata
     */
    private void setCropImg(Intent picdata) {
        Bundle bundle = picdata.getExtras();
        if (null != bundle) {
            Bitmap mBitmap = bundle.getParcelable("data");
            mImageView.setImageBitmap(mBitmap);
        }
    }
    private void showShare() {
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间等使用
        oks.setTitle("Love Study");

        // titleUrl是标题的网络链接，QQ和QQ空间等使用
        oks.setTitleUrl("http://lovestudy.ml");

        // text是分享文本，所有平台都需要这个字段
        oks.setText("爱生活，我们更爱学习！");

        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        oks.setImagePath(getRealFilePath(this,imgUri));//确保SDcard下面存在此张图片

        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://lovestudy.ml");

        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("评论文本(调试时使用)");

        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));

        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://lovestudy.ml");

        // 启动分享GUI
        oks.show(this);
    }
    public static String getRealFilePath(final Context context, final Uri uri ) {
        if ( null == uri ) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if ( scheme == null )
            data = uri.getPath();
        else if ( ContentResolver.SCHEME_FILE.equals( scheme ) ) {
            data = uri.getPath();
        } else if ( ContentResolver.SCHEME_CONTENT.equals( scheme ) ) {
            Cursor cursor = context.getContentResolver().query( uri, new String[] { MediaStore.Images.ImageColumns.DATA }, null, null, null );
            if ( null != cursor ) {
                if ( cursor.moveToFirst() ) {
                    int index = cursor.getColumnIndex( MediaStore.Images.ImageColumns.DATA );
                    if ( index > -1 ) {
                        data = cursor.getString( index );
                    }
                }
                cursor.close();
            }
        }
        return data;
    }
    protected void dialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(personal.this);
        builder.setMessage("您确认要退出登陆吗？");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
         @Override
         public void onClick(DialogInterface dialog, int which) {
         dialog.dismiss();
             SharedPreferences spout =getSharedPreferences("setting", 0);
             SharedPreferences.Editor ed =spout.edit();
             ed.clear();
             ed.apply();
             startActivity(new Intent(personal.this,Welcome.class));
         }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
       @Override
     public void onClick(DialogInterface dialog, int which) {
          dialog.dismiss();
              }
            });
        builder.create().show();
       }
}
