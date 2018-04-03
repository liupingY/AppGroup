package com.prize.app.beans;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.io.Serializable;


/**
 * 已领取应用存入数据的对象
 */
@Table(name = "EarnPointsBean")
public class EarnPointsBean implements Serializable{
    @Column(name = "id",autoGen = true, isId = true)
    public int id;
    @Column(name = "packageName")
    public String packageName;
    @Column(name = "timeStamp")
    public long timeStamp;


}
