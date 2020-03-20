package com.usr.usrsimplebleassistent.firmware.util;

import java.io.File;
import java.io.FileInputStream;

/**
 * 从path获取文件的大小KB
 *
 * @author 李雷红 2017/12/22  version 1.0
 */
public class GetFileSizeFromPath {


    public static String getFileSize(String filePath) {
        // 创建File
        File mFile = new File(filePath);
        try {
            // 取得文件大小
            long size = getFileSize(mFile);
            return size / 1024 + "KB";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0 + "KB";
    }

    /**
     * 获取指定文件大小(单位：字节)
     *
     * @param file
     * @return
     * @throws Exception
     */
    public static long getFileSize(File file) throws Exception {
        if (file == null) {
            return 0;
        }
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            size = fis.available();
        }
        return size;
    }
}
