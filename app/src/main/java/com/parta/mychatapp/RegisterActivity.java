package com.parta.mychatapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.parta.mychatapp.DataBaseBean.User;
import com.parta.mychatapp.DataBaseBean.UserFriends;


/**
 * Created by Vay on 2018/3/16.
 */

public class RegisterActivity extends Activity {

    private EditText editTextUser;
    private EditText editTextPwd;
    private EditText editTextPwd2;
    private TextView textViewLogin;
    private Button buttonSubmit;
    private Handler MyHandler;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);
        // 设置状态栏颜色
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        ViewGroup decorViewGroup = (ViewGroup) window.getDecorView();
        View statusBarView = new View(window.getContext());
        int statusBarHeight = getStatusBarHeight(window.getContext());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, statusBarHeight);
        params.gravity = Gravity.TOP;
        statusBarView.setLayoutParams(params);
        statusBarView.setBackgroundColor(Color.parseColor("#0397e7"));
        decorViewGroup.addView(statusBarView);

        MyHandler = new Handler();
        editTextUser = (EditText)findViewById(R.id.userId);
        editTextPwd = (EditText)findViewById(R.id.pwdId);
        editTextPwd2 = (EditText)findViewById(R.id.pwd2Id);
        textViewLogin = (TextView)findViewById(R.id.loginId);
        buttonSubmit = (Button)findViewById(R.id.submitId);
        buttonSubmit.setOnClickListener(new SubmitButtonClickListener());

        textViewLogin = (TextView)findViewById(R.id.loginId);
        String registerText = "已有账号？速速登录";
        SpannableString spannableString = new SpannableString(registerText);
        spannableString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(RegisterActivity.this,MainActivity.class);
                startActivity(intent);

            }
        }, 0, registerText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textViewLogin.setText(spannableString);
        textViewLogin.setMovementMethod(LinkMovementMethod.getInstance());

        Drawable leftDrawableUser = editTextUser.getCompoundDrawables()[0];
        if(leftDrawableUser!=null){
            leftDrawableUser.setBounds(0, 8, 40, 50);
            editTextUser.setCompoundDrawables(leftDrawableUser, editTextUser.getCompoundDrawables()[1],
                    editTextUser.getCompoundDrawables()[2], editTextUser.getCompoundDrawables()[3]);
        }

        Drawable leftDrawablePwd = editTextPwd.getCompoundDrawables()[0];
        if(leftDrawablePwd!=null){
            leftDrawablePwd.setBounds(0, 8, 40, 50);
            editTextPwd.setCompoundDrawables(leftDrawablePwd, editTextPwd.getCompoundDrawables()[1],
                    editTextPwd.getCompoundDrawables()[2], editTextPwd.getCompoundDrawables()[3]);
        }

        Drawable leftDrawablePwd2 = editTextPwd2.getCompoundDrawables()[0];
        if(leftDrawablePwd2!=null){
            leftDrawablePwd2.setBounds(0, 8, 40, 50);
            editTextPwd2.setCompoundDrawables(leftDrawablePwd2, editTextPwd2.getCompoundDrawables()[1],
                    editTextPwd2.getCompoundDrawables()[2], editTextPwd2.getCompoundDrawables()[3]);
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

    // 提交按钮的监听事件
    class SubmitButtonClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
              register();
        }
    }

    // 这注册时初始化用户数据库数据
    public  void register(){
        final String username = editTextUser.getText().toString().trim();
        final String passWord = editTextPwd.getText().toString().trim();
        final String passSureWord = editTextPwd2.getText().toString().trim();
        if( passSureWord.equals(passWord) && username != null && passSureWord != null  ){
            new Thread(
                    new Runnable() {
                        @Override
                        public void run() {
                            try{
                                // 调用环信的方法注册用户到服务器
                                EMClient.getInstance().createAccount(username, passWord);
                                MyHandler.post(
                                        new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(RegisterActivity.this,"注册成功，正在为您跳转主界面",Toast.LENGTH_SHORT).show();

                                                // 首次注册时，会为用户创建两个基本表，会将用户账号等默认信息保存到数据库
                                                User user = new User();
                                                user.setUserId(username);
                                                user.setNickName("昵称");
                                                user.setBackPhotoName("大海");
                                                user.setPersonaltTalk("说点想说的");
                                                user.setLoveThing("您还没填写过昵称呢");
                                                user.save();

                                                UserFriends userFriends = new UserFriends();
                                                userFriends.setUserId(username);
                                                userFriends.save();

                                                EMClient.getInstance().login(username,passWord,
                                                        new EMCallBack(){
                                                            @Override
                                                            public void onSuccess() {
                                                                // 登陆成功
                                                                System.out.println("登录成功");
                                                                startActivity(new Intent(RegisterActivity.this,PersonalActivity.class));
                                                                finish();
                                                            }
                                                            @Override
                                                            public void onError(int i, String s) {
                                                            }
                                                            @Override
                                                            public void onProgress(int i, String s) {
                                                            }
                                                        });
                                            }
                                        }
                                );
                            }catch (HyphenateException e){
//                                e.printStackTrace();
                                MyHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(RegisterActivity.this,"用户名重复",Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }
            ).start();
        }else{
            if(username == null){
                Toast.makeText(RegisterActivity.this,"用户名不能为空",Toast.LENGTH_SHORT).show();
            }else{
                if(passSureWord == null ||passWord == null){
                    Toast.makeText(RegisterActivity.this,"密码不能为空",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(RegisterActivity.this,"密码不一致",Toast.LENGTH_SHORT).show();
                    editTextPwd2.setText(null);
                }
            }
        }
    }
}
