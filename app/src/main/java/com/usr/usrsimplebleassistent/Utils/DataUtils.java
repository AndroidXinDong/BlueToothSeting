package com.usr.usrsimplebleassistent.Utils;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * create
 * on 2020-03-30 14:03
 * by xinDong
 **/
public class DataUtils {
    private static String TAG = "Tag";
    // 命令码
    // 仪器 ID
    public static String CMD_ID_CODE = "20";
    // 仪器版本
    public static String CMD_VERSION_CODE = "21";
    //  实时紫外信号
    public static String CMD_PURPLE_CODE = "22";
    //  实时红外信号
    public static String CMD_RED_CODE = "23";
    // 实时可见光信号
    public static String CMD_LIGHT_CODE = "24";
    // 多数据参数读取(主要针对四字节浮点数参数读取)
    public static String CMD_MOREDATA_CODE = "25";
    // 继电器状态
    public static String CMD_JIDIANQI_CODE = "26";
    // 参数标定读取与设置
    public static String CMD_PARAMETER_SET_CODE = "27";
    // 扩展码
    public static String EXTEND_WRITE_CODE = "66"; // 写
    public static String EXTEND_READ_CODE = "55"; // 读
    public static String EXTEND_WRITE_RESPONSE_CODE = "99"; // 写回应
    public static String EXTEND_READ_RESPONSE_CODE = "AA"; // 读回应

    public static String HEADER = "7D7B";
    public static String TAIL = "7D7D";
    /**
     *  hex 组成方式： headr 2 + 命令码 1 +扩展码 1 + 数据长度 2 + 数据区 N +校验位 2 + tail
     *  CRC校验内容：命令码 扩展码 数据长度 数据段
     */

    /*****************************仪器ID********************************/

    /**
     * 向设备发送指令 CRC校验内容：命令码 扩展码 数据长度 数据段
     *
     * @param data 数据段
     * @return
     */
    public static byte[] sendWriteIDCMD(String data) {
        try {
            String datas = Utils.str2HexStr(data).replaceAll(" ", "");
            String dataSize = intToHex(data.length());
            // crc 校验字符串
            String temp = CMD_ID_CODE + EXTEND_WRITE_CODE + dataSize + datas;
            byte[] crc_bytes = Utils.hexStringToByteArray(temp);
            String crc16 = CRC.getCRC(crc_bytes).replaceAll(" ", "");
            String result = HEADER + temp+ crc16 + TAIL;
            byte[] bytes = Utils.hexStringToByteArray(result);
            return bytes;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * @param receiver 数据原包
     * @return
     */
    public static boolean getWriteResponse(String receiver) {
        boolean b = responseCheck(receiver);
        return b;
    }

    /**
     * 读仪器ID
     *
     * @return
     */
    public static byte[] sendReadIDCMD() {
        String result = null;
        try{
            String temp = CMD_ID_CODE + EXTEND_READ_CODE + "0000";
            byte[] bytes = Utils.hexStringToByteArray(temp);
            String crc = CRC.getCRC(bytes).replaceAll(" ", "");
             result = HEADER + temp + crc + TAIL;
        }catch (Exception e){
            Log.i(TAG, "sendReadIDCMD: "+e.getMessage());
            return null;
        }
        return result.getBytes();
    }

    /**
     * 读的响应
     * 返回 id值
     * @param hex
     */
    public static String getReadIDResponse(String hex) {
        try{
            boolean b = responseCheck(hex);
            if (b) {
                int length = hex.length();
                String hexID = hex.substring(length - 36, length - 8);
                String result = Utils.hexStringToString(hexID);
                return result;
            }
        }catch (Exception e){
            return null;
        }

        return null;
    }

    /**
     * 校验响应CRC是否正确
     *
     * @param hex
     * @return
     */
    public static boolean responseCheck(String hex) {
        try {
            String upperCase = hex.toUpperCase().replaceAll(" ", "");
            int length = upperCase.length();
            String header = upperCase.substring(0, 4); // 帧头
            String tail = upperCase.substring(length - 4, length); // 帧尾

            String check = upperCase.substring(4, length - 8); // 校验内容
            String crc = upperCase.substring(length - 8, length - 4);
            byte[] crc_bytes = Utils.hexStringToByteArray(check);
            String crc16 = CRC.getCRC(crc_bytes).replaceAll(" ", "");
            if (crc.equals(crc16)) {
                return true;
            }
        }catch (Exception e){
            return false;
        }

        return false;
    }

        /*****************************仪器版本********************************/

    /**
     * 向设备写入版本号
     * @param data
     * @return
     */
    public static byte[] sendWriteVersionCMD(String data) {
            try {
                String datas = Utils.str2HexStr(data).replaceAll(" ", "");
                String dataSize = intToHex(data.length());
                // crc 校验字符串
                String temp = CMD_VERSION_CODE + EXTEND_WRITE_CODE + dataSize + datas.replaceAll(" ", "");
                byte[] crc_bytes = Utils.hexStringToByteArray(temp);
                String crc16 = CRC.getCRC(crc_bytes).replaceAll(" ", "");
                String result = HEADER + CMD_VERSION_CODE + EXTEND_WRITE_CODE + dataSize + datas + crc16 + TAIL;
                byte[] bytes = Utils.hexStringToByteArray(result);
                return bytes;
            } catch (Exception e) {
                return null;
            }
        }

    /**
     * 读版本号
     * @return
     */
    public static byte[] sendReadVersionCMD() {
        String result = null;
        try{
            String temp = CMD_VERSION_CODE + EXTEND_READ_CODE + "0000";
            byte[] bytes = Utils.hexStringToByteArray(temp);
            String crc = CRC.getCRC(bytes).replaceAll(" ", "");
             result = HEADER + temp + crc + TAIL;
        }catch (Exception e){
            return null;
        }
        return result.getBytes();
    }

    /**
     * 版本 读的响应
     * 返回 版本号
     * @param hex
     */
    public static String getReadVersionResponse(String hex) {
        try{
            boolean b = responseCheck(hex);
            if (b) {
                int length = hex.length();
                String hexID = hex.substring(length - 24, length - 8);
                String result = Utils.hexStringToString(hexID);
                return result;
            }
        }catch (Exception e){
            return null;
        }

        return null;
    }
    /*****************************实时紫外 、红外 、可见光信号********************************/

    /**
     * 光线类型
     * @param lightType
     * @return
     */
    public static byte[] sendReadPurpleCMD(String lightType) {
        try{
            String temp = lightType + EXTEND_READ_CODE + "0000";
            byte[] bytes = Utils.hexStringToByteArray(temp);
            String crc = CRC.getCRC(bytes).replaceAll(" ", "");
            String result = HEADER + temp + crc + TAIL;
            return result.getBytes();
        }catch (Exception e){
            return null;
        }
    }



    public static String getReadFloatResponse(String hex) {
        boolean b = responseCheck(hex);
        try{
            if (b) {
                int length = hex.length();
                String hexID = hex.substring(length - 16, length - 8);
                String result = Utils.hexStringToString(hexID);
                String aFloat = C2JUtils.hex2Float(result,4);
                return aFloat;
            }
        }catch (Exception e){
            return "0.0";
        }
        return "0.0";
    }
    /*****************************多数据参数读取(主要针对四字节浮点数参数读取)********************************/
    public static String sendMoreParameterCMD(String count) {
        String temp = CMD_MOREDATA_CODE + EXTEND_READ_CODE + "0001"+count;
        byte[] bytes = Utils.hexStringToByteArray(temp);
        String crc = CRC.getCRC(bytes).replaceAll(" ", "");
        String result = HEADER + temp + crc + TAIL;
        return result;
    }

    /**
     * 获取多参数的响应
     * @param hex
     * @return
     */
    public static List<String> getReadMoreFloatResponse(String hex) {
        boolean b = responseCheck(hex);
        List<String> list = new ArrayList<>();
        try{
            if (b) {
                int length = hex.length();
                String substring = hex.substring(12, length - 8); // 参数个数 和 所有返回float数
                String data = hex.substring(14, length - 8); //  所有返回float数
                int  count = Integer.parseInt(hex.substring(12, 14),16); // 参数个数
                for (int i = 0; i < count; i++) {
                    String s = data.substring(i * 8, 8 * (i + 1));
                    String hex2Float = C2JUtils.hex2Float(s, 4);
                    list.add(hex2Float);
                }
                return list;
            }
        }catch (Exception e){
            e.getMessage();
        }

        return list;
    }
    /*****************************继电器状态********************************/

    /**
     * 向设备写入继电器状态
     * @param data
     * @return
     */
    public static byte[] sendWriteJIDIANQICMD(String data) {
        try {
            // crc 校验字符串
            String temp = CMD_JIDIANQI_CODE + EXTEND_WRITE_CODE + "0002" + data;
            byte[] crc_bytes = Utils.hexStringToByteArray(temp);
            String crc16 = CRC.getCRC(crc_bytes).replaceAll(" ", "");
            String result = HEADER + temp + crc16 + TAIL;
            byte[] bytes = Utils.hexStringToByteArray(result);
            return bytes;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 读继电器状态
     * @return
     */
    public static byte[] sendReadJIDIANQICMD() {
        try {
            String temp = CMD_JIDIANQI_CODE+ EXTEND_READ_CODE + "0000";
            byte[] bytes = Utils.hexStringToByteArray(temp);
            String crc = CRC.getCRC(bytes).replaceAll(" ", "");
            String result = HEADER + temp + crc + TAIL;
            return result.getBytes();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 写    继电器响应
     * @return
     */
    public static boolean getWriteJIDIANQIResponse(String hex) {
        try {
            String temp = CMD_JIDIANQI_CODE+ EXTEND_WRITE_RESPONSE_CODE + "0000";
            byte[] bytes = Utils.hexStringToByteArray(temp);
            String crc = CRC.getCRC(bytes).replaceAll(" ", "");
            String result = HEADER + temp + crc + TAIL;
            if (result.equals(hex)){
                return true;
            }

        } catch (Exception e) {
            return false;
        }
        return false;
    }

    /**
     * 读 继电器响应
     * @param hex
     * @return
     */
    public static String getReadJIDIANQIResponse(String hex) {
        try {
            String temp = CMD_JIDIANQI_CODE+ EXTEND_READ_RESPONSE_CODE + "0002";
            byte[] bytes = Utils.hexStringToByteArray(temp);
            String crc = CRC.getCRC(bytes).replaceAll(" ", "");
            String result = HEADER + temp + crc + TAIL;
            return result;
        } catch (Exception e) {
            return "0000";
        }
    }

    /**
     * 十进制转标准格式十六进制
     *
     * @param n
     * @return
     */
    private static String intToHex(int n) {
        try{
            StringBuffer s = new StringBuffer();
            String a;
            char[] b = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
            while (n != 0) {
                s = s.append(b[n % 16]);
                n = n / 16;
            }
            a = s.reverse().toString();
            if (a.length() == 1) {
                return "000".concat(a);
            } else if (a.length() == 2) {
                return "00".concat(a);
            } else if (a.length() == 3) {
                return "0".concat(a);
            } else {
                return a;
            }
        }catch (Exception e){
            Log.i(TAG, "intToHex: "+e.getMessage());
            return "0";
        }

    }
}
