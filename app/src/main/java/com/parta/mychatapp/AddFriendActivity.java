package com.parta.mychatapp;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.parta.mychatapp.DataBaseBean.User;

import org.litepal.crud.DataSupport;

/**
 * Created by Vay on 2018/3/28.
 */

public class AddFriendActivity extends AppCompatActivity {
    private EditText friendName;
    private EditText reasonText;
    private Button sendButton;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.addfriend_layout);

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

        friendName = (EditText)findViewById(R.id.friendText);
        reasonText = (EditText)findViewById(R.id.reasonText);
        sendButton = (Button)findViewById(R.id.sendRequestButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(
                        new Runnable() {
                            @Override
                            public void run() {
                                try{
                                    EMClient.getInstance().contactManager().addContact(friendName.getText().toString(), reasonText.getText().toString());
                                    finish();
                                }catch (Exception e){
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(AddFriendActivity.this,"添加好友失败",Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        }
                ).start();
            }
        });
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
