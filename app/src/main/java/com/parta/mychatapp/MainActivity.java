package com.parta.mychatapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.LayoutTransition;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMChatManager;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.adapter.EMAChatClient;
import com.parta.mychatapp.DataBaseBean.User;
import com.parta.mychatapp.DataBaseBean.UserFriends;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    // 定义控件对象
    private EditText editTextUser;
    private EditText editTextPwd;
    private TextView registerTextView;
    private Button loginButton;
    private Handler mHandler;
    private ProgressBar progressBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
//        全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mHandler = new Handler();


        // 判断是否已经登录过了 如果已经登录则跳转主页面
        if(EMClient.getInstance().isLoggedInBefore()){
            //enter to main activity directly if you logged in before.
            startActivity(new Intent(this, PersonalActivity.class));
            finish();
        }

        editTextUser = (EditText)findViewById(R.id.userId);
        editTextPwd = (EditText)findViewById(R.id.pwdId);

        progressBar = (ProgressBar)findViewById(R.id.loadBarId);
        progressBar.setVisibility(View.INVISIBLE);

        registerTextView = (TextView)findViewById(R.id.registerId);
        String registerText = "没有账号？立即创建";
        SpannableString spannableString = new SpannableString(registerText);
        spannableString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,RegisterActivity.class);
                startActivity(intent);

            }
        }, 0, registerText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        registerTextView.setText(spannableString);
        registerTextView.setMovementMethod(LinkMovementMethod.getInstance());

        loginButton = (Button)findViewById(R.id.loginId);
        loginButton.setOnClickListener(new LoginButtonOnClickListener());

        Drawable leftDrawableUser = editTextUser.getCompoundDrawables()[0];
        if(leftDrawableUser!=null){
            leftDrawableUser.setBounds(0, 8, 40, 50);
//            leftDrawableUser.setColorFilter(Color.parseColor("#FFFAF0"),PorterDuff.Mode.SRC_IN);
            editTextUser.setCompoundDrawables(leftDrawableUser, editTextUser.getCompoundDrawables()[1],
                    editTextUser.getCompoundDrawables()[2], editTextUser.getCompoundDrawables()[3]);
        }

        Drawable leftDrawablePwd = editTextPwd.getCompoundDrawables()[0];
        if(leftDrawablePwd!=null){
            leftDrawablePwd.setBounds(0, 8, 40, 50);
            editTextPwd.setCompoundDrawables(leftDrawablePwd, editTextPwd.getCompoundDrawables()[1],
                    editTextPwd.getCompoundDrawables()[2], editTextPwd.getCompoundDrawables()[3]);
        }
    }

    // 点击登录按钮后显示错误信息
    class LoginButtonOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            final String inputUser = editTextUser.getText().toString();
            String inputPwd = editTextPwd.getText().toString();

            //点击之后进度条显现
            progressBar.setVisibility(View.VISIBLE);

            if(inputUser.length()>1 && inputPwd.length()>1){
                // 发送登录信息
                EMClient.getInstance().login(inputUser,inputPwd,
                        new EMCallBack(){
                            @Override
                            public void onSuccess() {
                                // 登陆成功
                                System.out.println("登录成功");
                                EMClient.getInstance().groupManager().loadAllGroups();
                                EMClient.getInstance().chatManager().loadAllConversations();
                                Intent intent = new Intent(MainActivity.this,PersonalActivity.class);
                                intent.putExtra("userName",inputUser);

                                // 如果是在别的客户端上注册的用户，则第一次在本机登陆创建基本表
                                List<User> userList = DataSupport.where("userId = ?",inputUser).find(User.class);
                                // 判断是否在本地创建过数据
                                if(userList.size()<1){
                                    User user = new User();
                                    user.setUserId(inputUser);
                                    user.setNickName("昵称");
                                    user.setBackPhotoName("大海");
                                    user.setPersonaltTalk("说点想说的");
                                    user.setLoveThing("您还没填写过昵称呢");
                                    user.save();

                                    UserFriends userFriends = new UserFriends();
                                    userFriends.setUserId(inputUser);
                                    userFriends.save();

                                }

                                startActivity(intent);
                                finish();
                            }

                            @Override
                            public void onError(int i, String s) {
                                System.out.println(i + " " + s);
                                final int errorType = i;
                                mHandler.post(
                                        new Runnable() {
                                            @Override
                                            public void run() {
                                                progressBar.setVisibility(View.GONE);
                                                switch ( errorType ){
                                                    case 204 :
                                                        Toast.makeText(MainActivity.this,"用户名不存在",Toast.LENGTH_SHORT).show();
                                                        break;
                                                    case 202 :
                                                        Toast.makeText(MainActivity.this,"密码错误",Toast.LENGTH_SHORT).show();
                                                        break;
                                                    default:
                                                        Toast.makeText(MainActivity.this,"登录出错",Toast.LENGTH_SHORT).show();
                                                        break;
                                                }
                                            }
                                        }
                                );
                            }
                            @Override
                            public void onProgress(int i, String s) {
                            }
                        });
            }else{
                if( inputUser.length() < 1 || inputPwd.length()<1 ){
                    Toast.makeText(MainActivity.this,"用户名或密码不可为空",Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }

        }
    }


}
