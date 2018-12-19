package com.parta.mychatapp.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parta.mychatapp.POJO.ChatingMsg;
import com.parta.mychatapp.R;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;

/**
 * Created by Vay on 2018/3/27.
 */

public class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.ViewHolder> {
    private List<ChatingMsg> msgList;

    static class ViewHolder extends RecyclerView.ViewHolder{
        LinearLayout leftLayout;
        LinearLayout rightLayout;

        LinearLayout innerLeftLayout;
        LinearLayout innerRightLayout;

        TextView leftMsg;
        TextView rightMsg;

        ImageView leftImg;
        ImageView rightImg;

        ImageView leftPhoto;
        ImageView rightPhoto;

        public ViewHolder(View view){
            super(view);
            leftLayout = (LinearLayout) view.findViewById(R.id.left_layout);
            rightLayout = (LinearLayout) view.findViewById(R.id.right_layout);

            leftMsg = (TextView) view.findViewById(R.id.left_msg);
            rightMsg = (TextView) view.findViewById(R.id.right_msg);

            leftImg = (ImageView) view.findViewById(R.id.voiceImgLeft);
            rightImg = (ImageView) view.findViewById(R.id.voiceImgRight);

            leftPhoto = (ImageView) view.findViewById(R.id.photoImgLeft);
            rightPhoto = (ImageView) view.findViewById(R.id.photoImgRight);

            innerLeftLayout = (LinearLayout) view.findViewById(R.id.leftInner_layout);
            innerRightLayout = (LinearLayout) view.findViewById(R.id.rightInner_layout);



        }
    }

    public MsgAdapter(List<ChatingMsg> msgList){
        this.msgList=msgList;
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chating_msg_layout, parent , false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
           final ChatingMsg chatingMsg = msgList.get(position);
           // 如果是发出去的消息
           if(chatingMsg.getType() == chatingMsg.TYPE_SENT){
               holder.leftLayout.setVisibility(View.GONE);
               holder.rightLayout.setVisibility(View.VISIBLE);
               switch (chatingMsg.getTypeItem()){
                   case "text":
                       holder.rightMsg.setVisibility(View.VISIBLE);
                       holder.rightMsg.setText(chatingMsg.getContent());
                       holder.rightImg.setVisibility(View.GONE);
                       holder.rightPhoto.setVisibility(View.GONE);
                       break;
                   case "voice":
                       holder.rightImg.setVisibility(View.VISIBLE);
                       holder.rightMsg.setVisibility(View.GONE);
                       holder.rightPhoto.setVisibility(View.GONE);
                       holder.rightImg.setOnClickListener(new View.OnClickListener() {
                           @Override
                           public void onClick(View v) {
                               MediaPlayer mediaPlayer=  new MediaPlayer();
                               try{
                                   mediaPlayer.setDataSource(chatingMsg.getContent());
                                   mediaPlayer.prepare();
                                   mediaPlayer.start();
                               }catch (Exception e){
                                   System.out.println("播放出错");
                               }
                           }
                       });
                       break;
                   case "img":
                       holder.innerRightLayout.setVisibility(View.GONE);
                       // 根据图片路径显示图片
                       File file = new File(chatingMsg.getContent());
                       if(file.exists()){
//                           Bitmap bitmap = BitmapFactory.decodeFile(chatingMsg.getContent());
//                           bitmap = comp(bitmap);
                           Bitmap bitmap = getimage(chatingMsg.getContent());
                           holder.rightPhoto.setImageBitmap(bitmap);
                       }
                       holder.rightPhoto.setVisibility(View.VISIBLE);
                       break;
                   default:
                       break;
               }
           // 如果是接收的消息
           }else{
               holder.rightLayout.setVisibility(View.GONE);
               holder.leftLayout.setVisibility(View.VISIBLE);
               switch (chatingMsg.getTypeItem()){
                   case "text":
                       holder.leftMsg.setText(chatingMsg.getContent());
                       holder.leftMsg.setVisibility(View.VISIBLE);
                       holder.leftImg.setVisibility(View.GONE);
                       holder.leftPhoto.setVisibility(View.GONE);
                       break;
                   case "voice":
                       holder.leftMsg.setVisibility(View.GONE);
                       holder.leftImg.setVisibility(View.VISIBLE);
                       holder.leftPhoto.setVisibility(View.GONE);
                       holder.leftImg.setOnClickListener(new View.OnClickListener() {
                           @Override
                           public void onClick(View v) {
                               try{
                                   MediaPlayer mediaPlayer = new MediaPlayer();
                                   mediaPlayer.setDataSource(chatingMsg.getContent());
                                   mediaPlayer.prepare();
                                   mediaPlayer.start();
                               }catch (Exception e){
                                   System.out.println("播放出错");
                               }
                           }
                       });
                       break;
                   case "img":
                       holder.innerLeftLayout.setVisibility(View.GONE);
                       // 根据图片路径显示图片
                       File file = new File(chatingMsg.getContent());
                       System.out.println(chatingMsg.getContent());
                       if(file.exists()){
                           Bitmap bitmap = BitmapFactory.decodeFile(chatingMsg.getContent());
//                           bitmap = compressImage(bitmap);
                           holder.leftPhoto.setImageBitmap(bitmap);
                       }
                       holder.leftPhoto.setVisibility(View.VISIBLE);
                       break;
                   default:
                       break;
               }
           }
    }

    @Override
    public int getItemCount() {
        return msgList.size();
    }

    // 图片压缩
    private Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 10;
        while ( baos.toByteArray().length / 1024>100) { //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    private Bitmap getimage(String srcPath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath,newOpts);//此时返回bm为空

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 800f;//这里设置高度为800f
        float ww = 480f;//这里设置宽度为480f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        return compressImage(bitmap);//压缩好比例大小后再进行质量压缩
    }


}
