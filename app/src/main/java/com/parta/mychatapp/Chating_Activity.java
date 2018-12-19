package com.parta.mychatapp;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMChatManager;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessageBody;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.chat.EMVoiceMessageBody;
import com.hyphenate.util.PathUtil;
import com.parta.mychatapp.DataBaseBean.UserFriends;
import com.parta.mychatapp.POJO.ChatingMsg;
import com.parta.mychatapp.R;
import com.parta.mychatapp.adapters.MsgAdapter;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.hyphenate.chat.EMMessage.Type.VOICE;
import static com.parta.mychatapp.POJO.ChatingMsg.TYPE_SENT;

/**
 * Created by Vay on 2018/3/27.
 */

public class Chating_Activity extends AppCompatActivity{
    private List<ChatingMsg> msgList = new ArrayList<ChatingMsg>();
    public static final int TAKE_PHOTO = 1;
    public static final int CHOOSE_PHOTO = 2;
    private EditText inputText;
    private Button  send;
    private RecyclerView msgRecyclerView;
    private ImageView backArrow;
    private TextView usernameText;

    private ImageView voiceImg;
    private ImageView cameraImg;
    private ImageView albmImg;

    private Uri cameraUri;
    private File voiceFile;

    private MediaRecorder voiceRecorder;
    private Handler handler = new Handler();



    String userToName = "";
    MyMessageListener myMessageListener;
    private MsgAdapter adapter;

    // 打开照相机和相册确认照片后的操作
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            // 使用拍照选取的图片
            case TAKE_PHOTO:
                if(resultCode == RESULT_OK){
                    try{
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                // 发送消息
                                EMMessage emMessage = EMMessage.createImageSendMessage(cameraUri.getPath(), false, userToName);
                                EMClient.getInstance().chatManager().sendMessage(emMessage);
                                ChatingMsg chatingMsg = new ChatingMsg(cameraUri.getPath(),TYPE_SENT,"img");
                                msgList.add(chatingMsg);
                            }
                        }).start();
                                adapter.notifyDataSetChanged();
                                msgRecyclerView.scrollToPosition(msgList.size()-1);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                break;
            // 使用相册选取的图片
            case CHOOSE_PHOTO:
                if(resultCode == RESULT_OK){
                    final String path;
                    // 判断手机版本号
                    if(Build.VERSION.SDK_INT>=19){
                        path = handleImageOnKitKat(data);
                    }else{
                        path = handleImageBeforeKitKat(data);
                    }
                    try{
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                // 发送消息
                                EMMessage emMessage = EMMessage.createImageSendMessage(path, false, userToName);
                                EMClient.getInstance().chatManager().sendMessage(emMessage);
                                ChatingMsg chatingMsg = new ChatingMsg(path,TYPE_SENT,"img");
                                msgList.add(chatingMsg);
                            }
                        }).start();
                                adapter.notifyDataSetChanged();
                                msgRecyclerView.scrollToPosition(msgList.size()-1);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.chat_layout);

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

        myMessageListener = new MyMessageListener();
        EMClient.getInstance().chatManager().addMessageListener(myMessageListener);

        backArrow = (ImageView)findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        voiceImg = (ImageView)findViewById(R.id.voiceBtn);
        voiceImg.setOnTouchListener(new VoiceOnTouchListener());


        cameraImg = (ImageView)findViewById(R.id.cameraImg);
        cameraImg.setOnClickListener(new CameraOnClickListener());

        albmImg = (ImageView)findViewById(R.id.albmImg);
        albmImg.setOnClickListener(new AlbmOnClickListener());

        Intent intent = getIntent();
        userToName = intent.getStringExtra("username");
        usernameText = (TextView)findViewById(R.id.usernameId);
        usernameText.setText(userToName);

        inputText = (EditText) findViewById(R.id.input_text);
        send = (Button) findViewById(R.id.send_Button);



        msgRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        msgRecyclerView.setLayoutManager(linearLayoutManager);
        adapter = new MsgAdapter(msgList);
        msgRecyclerView.setAdapter(adapter);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String content = inputText.getText().toString();
                if(!"".equals(content)){
                    ChatingMsg chatingMsg = new ChatingMsg(content, TYPE_SENT,"text");
                    msgList.add(chatingMsg);
                    // 发送消息
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            EMMessage emMessage = EMMessage.createTxtSendMessage(content,userToName);
                            EMClient.getInstance().chatManager().sendMessage(emMessage);
                        }
                    }).start();
                    adapter.notifyItemInserted(msgList.size()-1);
                    msgRecyclerView.scrollToPosition(msgList.size()-1);
                    inputText.setText("");

                    // 将发送消息的好友加入常用聊天数据库
                    UserFriends userFriends = new UserFriends();
                    String recentlyFriends = DataSupport.where("userId = ?",EMClient.getInstance().
                            getCurrentUser()).find(UserFriends.class).get(0).getFriends();
                    if(recentlyFriends != null){
                        if(!recentlyFriends.contains(userToName)){
                            userFriends.setFriends(recentlyFriends+userToName+",");
                            userFriends.updateAll("userId = ? ",EMClient.getInstance().getCurrentUser());
                        }
                    }else{
                        userFriends.setFriends(userToName+",");
                        userFriends.updateAll("userId = ? ",EMClient.getInstance().getCurrentUser());
                    }
                }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EMClient.getInstance().chatManager().removeMessageListener(myMessageListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        EMClient.getInstance().chatManager().addMessageListener(myMessageListener);
    }

    // 创建接收消息的监听器
    class MyMessageListener implements EMMessageListener {

        @Override
        public void onMessageReceived(List<EMMessage> list) {
            // 获取接收到的消息
            for(final EMMessage node : list){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // 获取消息类型
                        EMMessage.Type type = node.getType();
                        switch (type){
                            // 文本信息
                            case TXT:
                                EMTextMessageBody body = (EMTextMessageBody)node.getBody();
                                final String content = body.getMessage();
                                ChatingMsg chatingMsgText = new ChatingMsg(content,ChatingMsg.TYPE_RECEIVED,"text");
                                msgList.add(chatingMsgText);
                                adapter.notifyItemInserted(msgList.size()-1);
                                msgRecyclerView.scrollToPosition(msgList.size()-1);
                                break;
                            // 语音信息
                            case VOICE:
                                final EMVoiceMessageBody voiceBody = (EMVoiceMessageBody)node.getBody();
                                ChatingMsg chatingMsgVoice = new ChatingMsg(voiceBody.getLocalUrl(),ChatingMsg.TYPE_RECEIVED,"voice");
                                msgList.add(chatingMsgVoice);
                                adapter.notifyItemInserted(msgList.size()-1);
                                msgRecyclerView.scrollToPosition(msgList.size()-1);
                                break;
                            // 图片信息
                            case IMAGE:
                                final EMImageMessageBody imgBody = (EMImageMessageBody)node.getBody();
                                String imageName= imgBody.getRemoteUrl().substring(imgBody.getRemoteUrl().lastIndexOf("/") + 1, imgBody.getRemoteUrl().length());
                                String path =PathUtil.getInstance().getImagePath()+"/"+ imageName;
                                ChatingMsg chatingMsgImg = new ChatingMsg(path,ChatingMsg.TYPE_RECEIVED,"img");
                                msgList.add(chatingMsgImg);
                                adapter.notifyItemInserted(msgList.size()-1);
                                msgRecyclerView.scrollToPosition(msgList.size()-1);
                            default:
                                break;
                        }
                    }
                });
            }
        }


        @Override
        public void onMessageChanged(EMMessage emMessage, Object o) {

        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> list) {

        }

        @Override
        public void onMessageRead(List<EMMessage> list) {

        }

        @Override
        public void onMessageDelivered(List<EMMessage> list) {

        }

        @Override
        public void onMessageRecalled(List<EMMessage> list) {

        }

    }

    // 每次启动都更新页面的聊天列表
    @Override
    protected void onStart() {
        super.onStart();
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        EMConversation conversation = EMClient.getInstance().chatManager().getConversation(userToName);
                        //获取此会话的所有消息
                        if(conversation!=null&&conversation.getLastMessage()!=null) {
                            List<EMMessage> messages;
                            if(conversation.getAllMsgCount()<=15){
                                messages = conversation.loadMoreMsgFromDB(conversation.getLastMessage().getMsgId(), conversation.getAllMsgCount());
                            }else{
                                messages = conversation.loadMoreMsgFromDB(conversation.getLastMessage().getMsgId(), 15);
                            }
                            messages.add(conversation.getLastMessage());
                            for (EMMessage emMessage : messages) {
                                if (emMessage.getTo().equals(userToName)) {
                                    switch (emMessage.getType()){
                                        case TXT:
                                            ChatingMsg chatingMsgText = new ChatingMsg(((EMTextMessageBody) emMessage.getBody()).getMessage(), TYPE_SENT,"text");
                                            msgList.add(chatingMsgText);
                                            break;
                                        case VOICE:
                                            ChatingMsg chatingMsgVoice = new ChatingMsg(((EMVoiceMessageBody)emMessage.getBody()).getLocalUrl(), TYPE_SENT,"voice");
                                            msgList.add(chatingMsgVoice);
                                            break;
                                        case IMAGE:
                                            ChatingMsg chatingMsgImg = new ChatingMsg(((EMImageMessageBody)emMessage.getBody()).getLocalUrl(), TYPE_SENT,"img");
                                            msgList.add(chatingMsgImg);
                                            break;
                                        default:
                                            break;
                                    }
                                } else {
                                    switch (emMessage.getType()){
                                        case TXT:
                                            ChatingMsg chatingMsgText = new ChatingMsg(((EMTextMessageBody) emMessage.getBody()).getMessage(), ChatingMsg.TYPE_RECEIVED,"text");
                                            msgList.add(chatingMsgText);
                                            break;
                                        case VOICE:
                                            ChatingMsg chatingMsgVoice = new ChatingMsg(((EMVoiceMessageBody)emMessage.getBody()).getLocalUrl(), ChatingMsg.TYPE_RECEIVED,"voice");
                                            msgList.add(chatingMsgVoice);
                                            break;
                                        case IMAGE:
                                            ChatingMsg chatingMsgImg = new ChatingMsg(((EMImageMessageBody)emMessage.getBody()).getLocalUrl(),ChatingMsg.TYPE_RECEIVED,"img");
                                            msgList.add(chatingMsgImg);
                                            break;
                                        default:
                                            break;
                                    }
                                }
                            }
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.notifyDataSetChanged();
                                    msgRecyclerView.scrollToPosition(msgList.size()-1);
                                }
                            });
                        }

                    }
                }
        ).start();
    }

    // 语音录制
    class VoiceOnTouchListener implements View.OnTouchListener{
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();
            switch (action) {
                //手指按下
                case MotionEvent.ACTION_DOWN:
                    // 开始语音录制
                    voiceStart();
                    Toast.makeText(Chating_Activity.this,"录音开始",Toast.LENGTH_SHORT).show();
                    break;

                //手指移动
                case MotionEvent.ACTION_MOVE:
                    break;

                //手指抬起
                case MotionEvent.ACTION_UP:
                    Toast.makeText(Chating_Activity.this,"录音结束",Toast.LENGTH_SHORT).show();
                    // 发送消息
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            EMMessage emMessage = EMMessage.createVoiceSendMessage(voiceFile.getAbsolutePath(),10,userToName);
                            EMClient.getInstance().chatManager().sendMessage(emMessage);
                        }
                    }).start();
                    ChatingMsg chatingMsg = new ChatingMsg(voiceFile.getAbsolutePath(), TYPE_SENT,"voice");
                    msgList.add(chatingMsg);
                    adapter.notifyItemInserted(msgList.size()-1);
                    msgRecyclerView.scrollToPosition(msgList.size()-1);
                    // 关闭语音录制，释放资源
                    voiceEnd();
                    break;
            }
            return true;
        }
    }

    // 打开照相机选取图片
    class CameraOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            File cameraFile = new File(getExternalCacheDir(),"camera_image.jpg");
            try{
                if(cameraFile.exists()){
                    cameraFile.delete();
                }
                cameraFile.createNewFile();
            }catch (Exception e){
                e.printStackTrace();
            }
            if(Build.VERSION.SDK_INT >= 24 ){
                cameraUri = FileProvider.getUriForFile(Chating_Activity.this,"com.parta.mychatapp.fileprovider",cameraFile);
            }else{
                cameraUri = Uri.fromFile(cameraFile);
            }

            // 启动相机程序
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            intent.putExtra(MediaStore.EXTRA_OUTPUT,cameraUri);
            startActivityForResult(intent,TAKE_PHOTO);
        }
    }

    // 打开相册的监听器
    class AlbmOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            if (ContextCompat.checkSelfPermission(Chating_Activity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(Chating_Activity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE },1);
            }else{
                openAlbm();
            }
        }
    }



    // 开始录制
    public void voiceStart(){
        if(voiceRecorder == null){
            File dir = new File(Environment.getExternalStorageDirectory(),"sounds");
            if(!dir.exists()){
                dir.mkdirs();
            }
            voiceFile = new File(dir,System.currentTimeMillis()+".avoiceRecorder");
            if(!voiceFile.exists()){
                try {
                    voiceFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            voiceRecorder = new MediaRecorder();
            voiceRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);  //音频输入源
            voiceRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_WB);   //设置输出格式
            voiceRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);   //设置编码格式
            voiceRecorder.setOutputFile(voiceFile.getAbsolutePath());
            try {
                voiceRecorder.prepare();
                voiceRecorder.start();  //开始录制
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    //停止录制，资源释放
    private void voiceEnd(){
        if(voiceRecorder != null){
            voiceRecorder.stop();
            voiceRecorder.release();
            voiceRecorder = null;
        }
    }

    // 打开相册的方法
    private void openAlbm(){
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent,CHOOSE_PHOTO);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    openAlbm();
                }else{
                    Toast.makeText(this,"您未授权",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    @TargetApi(19)
    private String handleImageOnKitKat(Intent data){
        String imagePath = null;
        Uri uri = data.getData();
        if(DocumentsContract.isDocumentUri(this,uri)){
            // 如果是document类型的Uri,则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id = docId.split(":")[1]; // 解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }else if("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),Long.valueOf(docId));
                imagePath = getImagePath(contentUri,null);
            }
        }else if("content".equalsIgnoreCase(uri.getScheme())){
            // 如果是content类型的uri，用普通方式处理
            imagePath = getImagePath(uri,null);
        }else if("file".equalsIgnoreCase(uri.getScheme())){
            imagePath = uri.getPath();
        }
        return imagePath;
    }

    private String handleImageBeforeKitKat(Intent data){
        Uri uri = data.getData();
        String imagePath = getImagePath(uri,null);
        return imagePath;
    }

    private String getImagePath(Uri uri,String selection){
        String path = null;
        // 通过Uri和selection来获取图片的真实路径
        Cursor cursor = getContentResolver().query(uri,null,selection,null,null);
        if(cursor!=null){
            if(cursor.moveToFirst()){
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

}
