package com.usr.usrsimplebleassistent.Utils;

import android.content.Context;

import com.usr.usrsimplebleassistent.application.MyApplication;
import com.usr.usrsimplebleassistent.bean.DaoSession;
import com.usr.usrsimplebleassistent.bean.SaveBean;
import com.usr.usrsimplebleassistent.bean.SaveBeanDao;



/**
 * create
 * on 2020-04-17 9:30
 * by xinDong
 **/
public class DbUtil {
    Context mContext;
    private final SaveBeanDao dao;
    private DaoSession mDaoSession;

    public DbUtil(Context context) {
        mContext = context;
        mDaoSession = MyApplication.getDaoSession();
        dao = mDaoSession.getSaveBeanDao();

    }

    /**
     * 加载指定条件的参数
     * @param id
     * @return
     */
    public SaveBean loadTargetParameter(long id){
        SaveBean saveBean = dao.load(id);
        return saveBean;
    }


    /**
     * 插入一条数据
     * @param bean
     * @return
     */
    public boolean insertTargetParameter(SaveBean bean){
        try {
            long insert = dao.insert(bean);
            return insert!=-1 ? true :false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 更新保存的数据
     * @param bean
     * @return
     */
    public boolean updateTargetParameter(SaveBean bean){
        try {
            dao.update(bean);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean deleteTargetParameter(){
        try {
            dao.deleteAll();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
