package com.parta.mychatapp.POJO;

/**
 * Created by Vay on 2018/3/27.
 */

public class ChatingMsg  {
    public static final int TYPE_RECEIVED = 0;
    public static final int TYPE_SENT = 1;

    private String content;
    private int type;
    private String typeItem;

    public ChatingMsg(String content , int type,String typeItem){
        this.content=content;
        this.type=type;
        this.typeItem=typeItem;

    }

    public String getTypeItem() {
        return typeItem;
    }
    public String getContent() {
        return content;
    }
    public int getType() {
        return type;
    }
}
