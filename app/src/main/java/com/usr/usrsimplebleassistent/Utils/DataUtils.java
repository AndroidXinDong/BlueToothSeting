package com.usr.usrsimplebleassistent.Utils;

import android.util.Log;

import java.util.Arrays;

/**
 * create
 * on 2020-03-30 14:03
 * by xinDong
 **/
public class DataUtils {
    private static String TAG = "Tag";
    /**
     * 向设备发送指令 CRC校验内容：命令码 扩展码 数据长度 数据段
     * @param cmd 命令码
     * @param extension 扩展码
     * @param data 数据段
     * @return
     */
    public static byte[] sendCrCCMD(String cmd,String extension,String data){
        String result = null;
        String header = "7D7B";
        String tail = "7D7D";
        String datas = Utils.str2HexStr(data).replaceAll(" ","");
        int length = data.length();
        String dataSize = intToHex(length);
        String temp =cmd+extension+dataSize+datas; // 所有校验数据组成临时字符串
        String crc16 = intToHex(CRC.calcCrc16(temp.getBytes(), 0, temp.getBytes().length, 0xffff));
        result = header+cmd+extension+dataSize+datas+crc16+tail;
        Log.i(TAG, "sendCrCCMD: "+result);
        byte[] bytes = Utils.hexStringToByteArray(result);
        Log.i(TAG, "bytes: "+Arrays.toString(bytes));
        return bytes;
    }

    /**
     * 接收到的数据
     * @param receiver 数据原包
     * @return
     */
    public static boolean getResponse(String receiver){
        // 接收到的数据
        Log.i(TAG, "getResponse: "+receiver);
        String upperCase = receiver.toUpperCase();
        String header = upperCase.substring(0, 4); // 帧头
        int length = upperCase.length();
        String tail = upperCase.substring(length -4, length); // 帧尾
        if ("7D7B".equals(header) && "7D7D".equals(tail)){// 数据有效
            return true;
        }else {// 丢包，数据无效
         return false;
        }
    }

    private static String intToHex(int n) {
        StringBuffer s = new StringBuffer();
        String a;
        char []b = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
        while(n != 0){
            s = s.append(b[n%16]);
            n = n/16;
        }
        a = s.reverse().toString();
        if(a.length()==1) {
            return "000".concat(a);
        }else if (a.length() == 2){
            return "00".concat(a);
        }else if(a.length() == 3){
            return "0".concat(a);
        }else {
            return a;
        }
    }
}
