package com.usr.firecheck.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * create
 * on 2020-04-17 9:03
 * by xinDong
 **/
@Entity
public class SaveBean {
    @Id(autoincrement = true)
    Long id;
    @Property(nameInDb = "type") // 参数类型
    String type;
    @Property(nameInDb = "date") // 日期
    String date;
    @Property(nameInDb = "bg") // 背景参数
    String bg;
    @Property(nameInDb = "lmd") // 灵敏度
    String lmd;
    @Property(nameInDb = "ldbd") // 零点标定
    String ldbd;
    @Property(nameInDb = "mdbd") // 满点标定
    String mdbd;

    @Generated(hash = 23188868)
    public SaveBean(Long id, String type, String date, String bg, String lmd,
            String ldbd, String mdbd) {
        this.id = id;
        this.type = type;
        this.date = date;
        this.bg = bg;
        this.lmd = lmd;
        this.ldbd = ldbd;
        this.mdbd = mdbd;
    }

    @Generated(hash = 127566870)
    public SaveBean() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getBg() {
        return bg;
    }

    public void setBg(String bg) {
        this.bg = bg;
    }

    public String getLmd() {
        return lmd;
    }

    public void setLmd(String lmd) {
        this.lmd = lmd;
    }

    public String getLdbd() {
        return ldbd;
    }

    public void setLdbd(String ldbd) {
        this.ldbd = ldbd;
    }

    public String getMdbd() {
        return mdbd;
    }

    public void setMdbd(String mdbd) {
        this.mdbd = mdbd;
    }

    @Override
    public String toString() {
        return "SaveBean{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", date='" + date + '\'' +
                ", bg='" + bg + '\'' +
                ", lmd='" + lmd + '\'' +
                ", ldbd='" + ldbd + '\'' +
                ", mdbd='" + mdbd + '\'' +
                '}';
    }
}
