package com.parta.mychatapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.hyphenate.EMContactListener;
import com.hyphenate.chat.EMClient;
import com.parta.mychatapp.DataBaseBean.UserFriends;
import com.parta.mychatapp.POJO.ChatUser;
import com.parta.mychatapp.adapters.UserFriendsAdapter;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vay on 2018/3/17.
 */

public class FragFriends extends Fragment  {
    private FloatingActionButton fab;
    private List<String> friendsList = new ArrayList<String>();
    private List<ChatUser> userList = new ArrayList<ChatUser>();
    private RecyclerView recyclerView;
    private UserFriendsAdapter userFriendsAdapter;
    private Handler handler = new Handler();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragementfriends_layout,container,false);
//        initChatUser();
        recyclerView = (RecyclerView)view.findViewById(R.id.friendsRec);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        // 第一次打开此页面
        createInit();
        recyclerView.setLayoutManager(linearLayoutManager);
        userFriendsAdapter = new UserFriendsAdapter(userList);
        recyclerView.setAdapter(userFriendsAdapter);


        // 发送好友请求后的监听
        EMClient.getInstance().contactManager().setContactListener(new EMContactListener() {
            public void onContactAgreed(String username) {
                //好友请求被同意
                System.out.println("好友同意");
            }

            public void onContactRefused(String username) {
                //好友请求被拒绝
                System.out.println("好友请求被拒绝");
            }

            @Override
            public void onContactInvited(String username, String reason) {
                //收到好友邀请
                System.out.println("收到好友邀请");
            }

            @Override
            public void onContactDeleted(String username) {
                //删除好友时回调此方法
//                System.out.println("被删除时回调此方法:"+username);
                UserFriends userFriends = new UserFriends();
                String userFriendsString = DataSupport.where("userId = ?",EMClient.getInstance().getCurrentUser()).find(UserFriends.class).get(0).getFriends();
                if(userFriendsString!=null){
                    if(userFriendsString.contains(username)){
                        userFriends.setFriends(userFriendsString.replaceAll(username+",",""));
                        userFriends.updateAll("userId = ?",EMClient.getInstance().getCurrentUser());
                    }
                }
                view.getContext().startActivity(new Intent(view.getContext(),UselessActivity.class));
            }


            @Override
            public void onContactAdded(String username) {
                //增加了联系人时回调此方法
                System.out.println("加了联系人时回调此方法");
                userList.add(new ChatUser(username,R.drawable.head));

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        userFriendsAdapter.notifyItemInserted(userList.size()-1);
                        recyclerView.scrollToPosition(0);
                    }
                });

            }

            @Override
            public void onFriendRequestAccepted(String s) {
                System.out.println("好友请求接收");
            }

            @Override
            public void onFriendRequestDeclined(String s) {
                System.out.println("好友请求拒绝");
            }
        });

        fab = (FloatingActionButton) view.findViewById(R.id.addFriends);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),AddFriendActivity.class));
            }
        });
        return view;
    }

    public void createInit(){
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        try{
                            friendsList = EMClient.getInstance().contactManager().getAllContactsFromServer();
                            for(String username : friendsList ){
                                userList.add(new ChatUser(username,R.drawable.head));
                            }
                        }catch ( final Exception e){
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    e.printStackTrace();
                                    Toast.makeText(getActivity(),"获取好友列表失败",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }
        ).start();
    }
}
