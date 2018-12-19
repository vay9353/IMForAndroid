package com.parta.mychatapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Vay on 2018/3/31.
 */

// 此activity是为了辅助刷新最近聊天列表的页面数据 无其他作用
public class UselessActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_layout);
        finish();
    }
}
