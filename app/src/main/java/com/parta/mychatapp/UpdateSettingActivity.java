package com.parta.mychatapp;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.parta.mychatapp.DataBaseBean.User;

/**
 * Created by Vay on 2018/3/23.
 */

public class UpdateSettingActivity extends Activity {
    private EditText editText;
    private Button button;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.updatesetting_layout);

        editText = (EditText)findViewById(R.id.addEdit);
        button = (Button)findViewById(R.id.addId);

        // 设置状态栏颜色
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        ViewGroup decorViewGroup = (ViewGroup) window.getDecorView();
        View statusBarView = new View(window.getContext());
        int statusBarHeight = getStatusBarHeight(window.getContext());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, statusBarHeight);
        params.gravity = Gravity.TOP;
        statusBarView.setLayoutParams(params);
        statusBarView.setBackgroundColor(getResources().getColor(R.color.stateColor));
        decorViewGroup.addView(statusBarView);

        button.setOnClickListener(new ButtonUpdateListener());


    }

    class ButtonUpdateListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            User user = new User();
            Bundle bundle = getIntent().getExtras();
            String userId = bundle.getString("userId");
            String item = bundle.getString("item");
            switch (item){
                case "nickName" :
                    user.setNickName(editText.getText().toString().trim());
                    user.updateAll("userId = ?",userId);
                    break;
                case "personaltTalk" :
                    user.setPersonaltTalk(editText.getText().toString().trim());
                    user.updateAll("userId = ?",userId);
                    break;
                case "backPhotoName" :
                    user.setBackPhotoName(editText.getText().toString().trim());
                    user.updateAll("userId = ?",userId);
                    break;
                case "loveThing" :
                    user.setLoveThing(editText.getText().toString().trim());
                    user.updateAll("userId = ?",userId);
                    break;
                default:
                    break;
            }
            finish();
        }
    }
    // 获取通知栏高度的方法
    private static int getStatusBarHeight(Context context) {
        int statusBarHeight = 0;
        Resources res = context.getResources();
        int resourceId = res.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = res.getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }
}
