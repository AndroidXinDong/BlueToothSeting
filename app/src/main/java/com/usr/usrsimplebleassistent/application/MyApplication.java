package com.usr.usrsimplebleassistent.application;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import com.usr.usrsimplebleassistent.bean.DaoMaster;
import com.usr.usrsimplebleassistent.bean.DaoSession;
import com.usr.usrsimplebleassistent.views.CrashHandler;

public class MyApplication extends Application {
    public static DaoSession daoSession;
    public static DaoSession getDaoSession() {
        return daoSession;
    }
    private boolean isConnect = false;
    private boolean currentModel = false;

    @Override
    public void onCreate() {
        super.onCreate();
        initGreenDao();
        // 添加异常打印反馈回执
        CrashHandler handler = CrashHandler.getInstance();
        handler.init(this);

    } /**
     * 初始化GreenDao,直接在Application中进行初始化操作
     */
    private void initGreenDao() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "set.db");
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }

    public boolean isConnect() {
        return isConnect;
    }

    public void setConnect(boolean connect) {
        isConnect = connect;
    }

    public boolean isCurrentModel() {
        return currentModel;
    }

    public void setCurrentModel(boolean currentModel) {
        this.currentModel = currentModel;
    }
}
