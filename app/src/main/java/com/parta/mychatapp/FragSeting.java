package com.parta.mychatapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.parta.mychatapp.DataBaseBean.User;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by Vay on 2018/3/17.
 */

public class FragSeting extends Fragment {

    private String UserID =EMClient.getInstance().getCurrentUser();

    private Button loginOut;

    private ImageView nickNameImg;
    private ImageView backPhotoImg;
    private ImageView personalTalkImg;
    private ImageView loveThingImg;

    //个人中心界面的文本控件
    private TextView headNickName;
    private TextView headUserId;
    private TextView contentUserId;
    private TextView nickNameView;
    private TextView contentBackPhoto;
    private TextView contentPersonalTalk;
    private TextView contentLoveThing;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragementseting_layout,container,false);
        loginOut = (Button)view.findViewById(R.id.loginOutId);
        loginOut.setOnClickListener(new OnLoginOutButtonListener());

        //个人中心界面的文本信息
        headNickName = (TextView)view.findViewById(R.id.head_userNickName);
        // 这个是不会改变的
        headUserId = (TextView)view.findViewById(R.id.head_userID);
        headUserId.setText(UserID);
        contentUserId = (TextView)view.findViewById(R.id.content_userId);
        contentUserId.setText(UserID);

        contentBackPhoto = (TextView)view.findViewById(R.id.content_backPhoto);
        contentPersonalTalk = (TextView)view.findViewById(R.id.content_personalTalk);
        contentLoveThing = (TextView)view.findViewById(R.id.content_loveThing);
        nickNameView = (TextView)view.findViewById(R.id.nickNameText);

        // 为图片控件绑定修改事件
        ImgViewOnClickListener imgViewOnClickListener = new ImgViewOnClickListener();
        nickNameImg = (ImageView)view.findViewById(R.id.nickNameImg);
        nickNameImg.setOnClickListener(imgViewOnClickListener);
        backPhotoImg = (ImageView)view.findViewById(R.id.backPhotoImg);
        backPhotoImg.setOnClickListener(imgViewOnClickListener);
        personalTalkImg = (ImageView)view.findViewById(R.id.personalTalkImg);
        personalTalkImg.setOnClickListener(imgViewOnClickListener);
        loveThingImg = (ImageView)view.findViewById(R.id.loveThingImg);
        loveThingImg.setOnClickListener(imgViewOnClickListener);

        return view;
    }

    public class OnLoginOutButtonListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            EMClient.getInstance().logout(false, new EMCallBack() {

                @Override
                public void onSuccess() {
                    startActivity(new Intent(getActivity(),MainActivity.class));
                }

                @Override
                public void onProgress(int progress, String status) {
                }

                @Override
                public void onError(int code, String message) {
                }
            });
        }
    }

    class ImgViewOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            int viewId = v.getId();
            Intent intent = new Intent(getActivity(),UpdateSettingActivity.class);
            Bundle bundle = new Bundle();
            String userId = EMClient.getInstance().getCurrentUser();
            switch (viewId){
                case R.id.nickNameImg :
                    bundle.putString("userId",userId);
                    bundle.putString("item","nickName");
                    intent.putExtras(bundle);
                    startActivity(intent);
                    break;
                case R.id.personalTalkImg :
                    bundle.putString("userId",userId);
                    bundle.putString("item","personaltTalk");
                    intent.putExtras(bundle);
                    startActivity(intent);
                    break;
                case R.id.backPhotoImg :
                    bundle.putString("userId",userId);
                    bundle.putString("item","backPhotoName");
                    intent.putExtras(bundle);
                    startActivity(intent);
                    break;
                case R.id.loveThingImg :
                    bundle.putString("userId",userId);
                    bundle.putString("item","loveThing");
                    intent.putExtras(bundle);
                    startActivity(intent);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    // 此方法用于每次打开设置界面都会更新里面的数据
    public void onStart() {
        super.onStart();
        List<User> userList = DataSupport.where("userId = ?",EMClient.getInstance().getCurrentUser() ).find(User.class);
            // 获取此用户在数据库存放的信息，并且全部刷新,因为只会有一条这样的记录，所以只用提取第一条
        if(userList.size()>0) {
            headNickName.setText(userList.get(0).getNickName());
            nickNameView.setText(userList.get(0).getNickName());
            contentBackPhoto.setText(userList.get(0).getBackPhotoName());
            contentPersonalTalk.setText(userList.get(0).getPersonaltTalk());
            contentLoveThing.setText(userList.get(0).getLoveThing());
        }
    }
}
