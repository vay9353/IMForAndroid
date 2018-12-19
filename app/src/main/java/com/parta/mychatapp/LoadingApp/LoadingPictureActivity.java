package com.parta.mychatapp.LoadingApp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.Window;
import android.view.WindowManager;

import com.hyphenate.chat.EMClient;
import com.parta.mychatapp.MainActivity;
import com.parta.mychatapp.R;

/**
 * Created by Vay on 2018/3/21.
 */

public class LoadingPictureActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.loading_picture_layout);
        //        全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        EMClient.getInstance().groupManager().loadAllGroups();
        EMClient.getInstance().chatManager().loadAllConversations();

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                Intent mainIntent = new Intent(LoadingPictureActivity.this,MainActivity.class);
                LoadingPictureActivity.this.startActivity(mainIntent);
                LoadingPictureActivity.this.finish();
            }
        },2000);
    }
}
