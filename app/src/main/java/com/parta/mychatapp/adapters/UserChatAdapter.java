package com.parta.mychatapp.adapters;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.parta.mychatapp.Chating_Activity;
import com.parta.mychatapp.DataBaseBean.UserFriends;
import com.parta.mychatapp.POJO.ChatUser;
import com.parta.mychatapp.R;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by Vay on 2018/3/18.
 */

public class UserChatAdapter extends RecyclerView.Adapter<UserChatAdapter.ViewHolder> {
    private List<ChatUser> chatUserList;

    static class ViewHolder extends RecyclerView.ViewHolder{
        View chatUserView;
        ImageView userImage ;
        TextView userText;
        LinearLayout chatUserItem;

        public ViewHolder(View view){
            super(view);
            chatUserView = view;
            userImage = (ImageView) view.findViewById(R.id.user_img);
            userText = (TextView) view.findViewById(R.id.user_name);
            chatUserItem =(LinearLayout) view.findViewById(R.id.chatUserItem);
        }
    }

    public UserChatAdapter(List<ChatUser> chatUserList){
        this.chatUserList = chatUserList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chatuser_layout,parent,false);
        final ViewHolder holder = new ViewHolder(view);
        holder.chatUserItem.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                ChatUser chatUser = chatUserList.get(position);
                Intent intent = new Intent(view.getContext(),Chating_Activity.class);
                intent.putExtra("username",chatUser.getUsername());
                view.getContext().startActivity(intent);
            }
        });
        holder.chatUserItem.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(v.getContext());
                final int position = holder.getAdapterPosition();
                final String willDeleteFriend = chatUserList.get(position).getUsername();
                dialog.setTitle("删除最近聊天好友 "+willDeleteFriend);
                dialog.setCancelable(false);
                dialog.setPositiveButton("确定删除",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String friendFromData = DataSupport.where("userId = ?",EMClient.getInstance().getCurrentUser()).
                                find(UserFriends.class).get(0).getFriends();
                        UserFriends userFriends = new UserFriends();
                        userFriends.setFriends(friendFromData.replaceAll(willDeleteFriend+",",""));
                        userFriends.updateAll("userId = ?", EMClient.getInstance().getCurrentUser());

                        chatUserList.remove(position);
                        notifyItemRemoved(position);
                    }
                });
                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialog.show();
                return false;
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ChatUser chatUser = chatUserList.get(position);
        holder.userImage.setImageResource(chatUser.getImageId());
        holder.userText.setText(chatUser.getUsername());
    }

    @Override
    public int getItemCount() {
        return chatUserList.size();
    }
}

