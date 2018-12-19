package com.parta.mychatapp;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;

/**
 * Created by Vay on 2018/3/17.
 */

public class PersonalActivity extends AppCompatActivity {
    private BottomNavigationBar mBottomNavigationBar;
    private FragChat fragChat ;
    private FragFriends fragFriends ;
    private FragBrowser fragBrowser;
    private FragSeting fragSeting ;
    private TextView titleText;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.personalcenter_layout);

        titleText = (TextView) findViewById(R.id.textTitle);


        // 初始化需要填充的fragments
        initializeFragments();

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

        mBottomNavigationBar = (BottomNavigationBar) findViewById(R.id.bottom_navigation_bar);
        mBottomNavigationBar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener(){
            @Override
            public void onTabSelected(int position) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                //隐藏所有fragment
                hideFragments(transaction);

                switch (position) {
                    case 0:
                        titleText.setText("正在聊天");
                        //显示需要显示的fragment
                        transaction.show(fragChat);
                        break;
                    case 1:
                        titleText.setText("好友列表");
                        transaction.show(fragFriends);
                        break;
                    case 2:
                        titleText.setText("浏览器");
                        transaction.show(fragBrowser);
                        break;
                    case 3:
                        titleText.setText("设置");
                        transaction.show(fragSeting);
                        break;
                    default:
                        titleText.setText("正在聊天");
                        transaction.show(fragChat);
                        break;
                }
                transaction.commit();
            }
            @Override
            public void onTabUnselected(int position) {
                //选中->未选中
            }
            @Override
            public void onTabReselected(int position) {
                //选中->选中
            }
        });
        // 设置按钮不位移
        mBottomNavigationBar.setMode(BottomNavigationBar.MODE_FIXED);
        // 背景无动画
        mBottomNavigationBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);
        mBottomNavigationBar.setBarBackgroundColor(R.color.bottomBarBack);
        mBottomNavigationBar.setActiveColor(R.color.stateColor);
        mBottomNavigationBar.setInActiveColor(R.color.bottomBarBackUnFoucs);//unSelected icon color
        mBottomNavigationBar
                .addItem(new BottomNavigationItem(R.drawable.icon_chat2,"聊天"))
                .addItem(new BottomNavigationItem(R.drawable.icon_friend,"好友"))
                .addItem(new BottomNavigationItem(R.drawable.icon_personal,"浏览器"))
                .addItem(new BottomNavigationItem(R.drawable.icon_set,"个人中心"))
                .setFirstSelectedPosition(0)//设置默认选择item
                .initialise();//初始化
        titleText.setText("正在聊天");
//        mBottomNavigationBar.setTabSelectedListener(this);
        setDefaultFragment();
    }
    private void setDefaultFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        fragChat = fragChat.newInstance("First Fragment");
        transaction.add(R.id.mainContent, fragChat);
        transaction.add(R.id.mainContent,fragFriends);
        transaction.add(R.id.mainContent, fragBrowser);
        transaction.add(R.id.mainContent,fragSeting).commit();

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

    private void initializeFragments(){
        fragChat = new FragChat();
        fragFriends = new FragFriends();
        fragBrowser = new FragBrowser();
        fragSeting = new FragSeting();

    }

    public void hideFragments(FragmentTransaction fragmentTransaction){
        if(fragChat!=null){
            fragmentTransaction.hide(fragChat);
        }
        if(fragFriends!=null){
            fragmentTransaction.hide(fragFriends);
        }
        if(fragBrowser !=null){
            fragmentTransaction.hide(fragBrowser);
        }
        if(fragSeting!=null){
            fragmentTransaction.hide(fragSeting);
        }
    }
}
