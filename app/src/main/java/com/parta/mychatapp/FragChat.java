package com.parta.mychatapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hyphenate.chat.EMClient;
import com.parta.mychatapp.DataBaseBean.UserFriends;
import com.parta.mychatapp.POJO.ChatUser;
import com.parta.mychatapp.adapters.UserChatAdapter;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vay on 2018/3/17.
 */

public class FragChat extends Fragment {

    private List<ChatUser> userList = new ArrayList<ChatUser>();
    private UserChatAdapter adapter;
    private RecyclerView recyclerView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragementchat_layout,container,false);
        initChatUser();
        recyclerView = (RecyclerView)view.findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new UserChatAdapter(userList);
        recyclerView.setAdapter(adapter);
        return view;
    }

    // 每次启动都从数据库中读取最近聊天的用户
    public void initChatUser(){
        List<UserFriends> userFriendsList = DataSupport.where("userId = ?",EMClient.getInstance().getCurrentUser()).find(UserFriends.class);
        if(userFriendsList.size()>0 && userFriendsList.get(0).getFriends() != null){
            String[] userArray = userFriendsList.get(0).getFriends().split(",");
            for(String friends : userArray){
                if(friends!="")
                userList.add(new ChatUser(friends,R.drawable.head));
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        String friends = DataSupport.where("userId = ?",EMClient.getInstance().getCurrentUser()).find(UserFriends.class).get(0).getFriends();
        userList.clear();
        if(friends!=null){
            String[] friendArray = friends.split(",");
            for(String friendString : friendArray ){
                if(friendString!="")
                userList.add(new ChatUser(friendString,R.drawable.head));
            }
        }
        adapter.notifyDataSetChanged();
    }

}
