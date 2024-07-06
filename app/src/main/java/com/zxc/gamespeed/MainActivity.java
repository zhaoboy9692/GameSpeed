package com.zxc.gamespeed;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.zxc.gamespeed.databinding.ActivityMainBinding;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {


    private ActivityMainBinding binding;
    private TextView tv;
    private WindowManager wm;
    private View windowLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        tv = binding.sampleText;
        tv.setText((Settings.canDrawOverlays(MainActivity.this) ? "奔放吧骚年" : "未获取悬浮窗权限，请给予悬浮窗权限") + "\n微信关注《小白技术社》更新\n仅用于学习交流，请勿用于非法用途。\n");
    }

    @SuppressLint("ClickableViewAccessibility")
    public void WindowManagerShow() throws UnknownHostException {

        if (wm != null) return;

        windowLayout = LayoutInflater.from(this).inflate(R.layout.window_layout, null);
        // 窗口的标题
        TextView windowBar = windowLayout.findViewById(R.id.window_bar);
        TextView speedView = windowLayout.findViewById(R.id.speed);
        // 窗口的取消和确定按钮
        Button windowRemove = windowLayout.findViewById(R.id.window_remove);
        Button addSpeed = windowLayout.findViewById(R.id.add);

        // 获取WindowManager
        wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        // 创建LayoutParams
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();


        // 定义Window类型
        layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        ;
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE    // 设置窗口不接受输入焦点
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED; // 设置锁屏时也显示窗口

        layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT; // window的宽度
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;// window的高度

        layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        // window的位置
        layoutParams.x = 0;
        layoutParams.y = 100;
        // 往window中添加内容
        wm.addView(windowLayout, layoutParams);

        // 屏幕宽度（应用显示部分，不包括系统顶部状态栏）
        int screenWidth = getResources().getDisplayMetrics().widthPixels;

        // 设置拖动窗口标题移动
        windowBar.setOnTouchListener(new View.OnTouchListener() {
            float oldX;  // 上一次鼠标点击时的X坐标
            float oldY;  // 上一次鼠标点击时的Y坐标
            float windowX;  // 弹出窗口的X坐标
            float windowY; // 弹出窗口的Y坐标

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        // event.getRawX()-oldX为偏移量
                        // 新位置 = 原始位置 + 偏移量
                        layoutParams.x = (int) (windowX + event.getRawX() - oldX);
                        layoutParams.y = (int) (windowY + event.getRawY() - oldY);
                        wm.updateViewLayout(windowLayout, layoutParams);
                        break;
                    // 鼠标弹起时判断窗口X轴的位置，然后将其贴边
                    case MotionEvent.ACTION_UP:
                        double halfScreenWidth = screenWidth / 2.0;
                        // 如果X轴坐标大于屏幕宽度的一半，则将其置于右边，否则左边
                        if (layoutParams.x >= halfScreenWidth) {
                            // layoutParams.x = screenWidth - windowLayout.getMeasuredWidth();
                            // 执行动画
                            ValueAnimator valueAnimator = ValueAnimator.ofInt(layoutParams.x, screenWidth - windowLayout.getMeasuredWidth());
                            valueAnimator.setDuration(200);
                            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator animation) {
                                    int x = (int) animation.getAnimatedValue();
                                    layoutParams.x = (int) x;
                                    wm.updateViewLayout(windowLayout, layoutParams);
                                }
                            });
                            valueAnimator.start();
                        } else {
                            // 执行动画
                            ValueAnimator valueAnimator = ValueAnimator.ofInt(layoutParams.x, 0);
                            valueAnimator.setDuration(200);
                            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator animation) {
                                    int x = (int) animation.getAnimatedValue();
                                    layoutParams.x = (int) x;
                                    wm.updateViewLayout(windowLayout, layoutParams);
                                }
                            });

                            valueAnimator.start();
                        }
                        // windowManager.updateViewLayout(windowLayout, layoutParams);
                    default:
                        oldX = event.getRawX();
                        oldY = event.getRawY();
                        // 计算移动后的位置（左上角的坐标）
                        windowX = layoutParams.x;
                        windowY = layoutParams.y;
                        break;
                }
                return true;
            }

        });
        // 点击取消删除窗口
        windowRemove.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                addSpeed.setEnabled(true);
                Log.i("asdadasd", speedView.getText().toString());
                float speedNum = Float.parseFloat(speedView.getText().toString());
                if (speedNum <= 1 && speedNum > 0) {
                    speedNum -= 0.1f;
                    speedNum = (float) Math.round(speedNum * 10) / 10;
                } else if (speedNum > 1 && speedNum < 10) {
                    speedNum -= 1f;
                } else if (speedNum >= 10) {
                    speedNum -= 5f;
                }

                if (speedNum <= 0) {
                    speedNum = 0;
                    windowRemove.setEnabled(false);
                }
                int ss = 0;
                if (speedNum >= 1) {
                    ss = (int) speedNum;
                    speedView.setText(ss + "");
                } else {
                    speedView.setText(speedNum + "");
                }
                float finalSpeedNum = speedNum;
                SharedPreferences sharedPreferences = getSharedPreferences("Speed", Context.MODE_WORLD_READABLE);
                sharedPreferences.edit().putFloat("speed", finalSpeedNum).commit();
            }
        });

        // 点击取消删除窗口
        addSpeed.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                windowRemove.setEnabled(true);
                float speedNum = Float.parseFloat(speedView.getText().toString());
                speedNum += 1;
                if (speedNum < 1 && speedNum > 0) {
                    speedNum += 0.1f;
                    speedNum = (float) Math.round(speedNum * 10) / 10;
                } else if (speedNum >= 1 && speedNum < 10) {
                    speedNum += 1f;
                } else if (speedNum >= 10) {
                    speedNum += 5f;
                }
                if (speedNum >= 100) {
                    speedNum = 100;
                    addSpeed.setEnabled(false);
                }
                if (speedNum >= 1) {
                    speedNum = (int) speedNum;
                }
                speedView.setText(speedNum + "");
                float finalSpeedNum = speedNum;
                SharedPreferences sharedPreferences = getSharedPreferences("Speed", Context.MODE_WORLD_READABLE);
                sharedPreferences.edit().putFloat("speed", finalSpeedNum).commit();
            }
        });


    }

    //判断是否开启悬浮窗权限   context可以用你的Activity.或者tiis
    public static boolean checkFloatPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AppOpsManager appOpsMgr = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            if (appOpsMgr == null)
                return false;
            int mode = appOpsMgr.checkOpNoThrow("android:system_alert_window", android.os.Process.myUid(), context
                    .getPackageName());
            return mode == AppOpsManager.MODE_ALLOWED || mode == AppOpsManager.MODE_IGNORED;
        } else {
            return Settings.canDrawOverlays(context);
        }
    }


    public void startSpeed(View view) throws UnknownHostException {
        if (!checkFloatPermission(MainActivity.this)) {
            AlertDialog alertDialog2 = new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage("没有悬浮窗权限，请授予悬浮窗权限后开始")
                    .setIcon(R.mipmap.ic_launcher)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {//添加"Yes"按钮
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                            startActivity(intent);
                        }
                    })
                    .create();
            alertDialog2.show();
            return;
        }
        WindowManagerShow();
    }


    public void stopSpeed(View view) throws InterruptedException {
        if (wm != null && windowLayout != null) {
            wm.removeView(windowLayout);
            wm = null;
            windowLayout = null;

        }

    }
}