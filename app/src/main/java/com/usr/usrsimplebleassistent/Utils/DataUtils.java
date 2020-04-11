package com.usr.usrsimplebleassistent.Utils;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
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
    // 电流转换量 输入读取
    public static String CMD_ELECTRICTRANSLATE_CODE = "28";
    // 写入当前电流
    public static String CMD_CURRENTELECTIC_CODE = "29";
    // 模式切换
    public static String CMD_MODEL_CODE = "40";
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
            String result = HEADER + temp + crc16 + TAIL;
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
        try {
            String temp = CMD_ID_CODE + EXTEND_READ_CODE + "0000";
            byte[] bytes = Utils.hexStringToByteArray(temp);
            String crc = CRC.getCRC(bytes).replaceAll(" ", "");
            result = HEADER + temp + crc + TAIL;
        } catch (Exception e) {
            Log.i(TAG, "sendReadIDCMD: " + e.getMessage());
            return null;
        }
        return result.getBytes();
    }

    /**
     * 读的响应
     * 返回 id值
     *
     * @param hex
     */
    public static String getReadIDResponse(String hex) {
        try {
            boolean b = responseCheck(hex);
            if (b) {
                int length = hex.length();
                String hexID = hex.substring(length - 36, length - 8);
                String result = Utils.hexStringToString(hexID);
                return result;
            }
        } catch (Exception e) {
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
        } catch (Exception e) {
            return false;
        }

        return false;
    }

    /*****************************仪器版本********************************/

    /**
     * 向设备写入版本号
     *
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
     *
     * @return
     */
    public static byte[] sendReadVersionCMD() {
        String result = null;
        try {
            String temp = CMD_VERSION_CODE + EXTEND_READ_CODE + "0000";
            byte[] bytes = Utils.hexStringToByteArray(temp);
            String crc = CRC.getCRC(bytes).replaceAll(" ", "");
            result = HEADER + temp + crc + TAIL;
        } catch (Exception e) {
            return null;
        }
        return result.getBytes();
    }

    /**
     * 版本 读的响应
     * 返回 版本号
     *
     * @param hex
     */
    public static String getReadVersionResponse(String hex) {
        try {
            boolean b = responseCheck(hex);
            if (b) {
                int length = hex.length();
                String hexID = hex.substring(length - 24, length - 8);
                String result = Utils.hexStringToString(hexID);
                return result;
            }
        } catch (Exception e) {
            return null;
        }

        return null;
    }
    /*****************************实时紫外 、红外 、可见光信号********************************/

    /**
     * 光线类型
     *
     * @param lightType
     * @return
     */
    public static byte[] sendReadPurpleCMD(String lightType) {
        try {
            String temp = lightType + EXTEND_READ_CODE + "0000";
            byte[] bytes = Utils.hexStringToByteArray(temp);
            String crc = CRC.getCRC(bytes).replaceAll(" ", "");
            String result = HEADER + temp + crc + TAIL;
            return result.getBytes();
        } catch (Exception e) {
            return null;
        }
    }


    public static String getReadFloatResponse(String hex) {
        boolean b = responseCheck(hex);
        try {
            if (b) {
                int length = hex.length();
                String hexID = hex.substring(length - 16, length - 8);
                String result = Utils.hexStringToString(hexID);
                String aFloat = C2JUtils.hex2Float(result, 4);
                return aFloat;
            }
        } catch (Exception e) {
            return "0.0";
        }
        return "0.0";
    }

    /*****************************多数据参数读取(主要针对四字节浮点数参数读取)********************************/
    public static String sendMoreParameterCMD(String count) {
        String temp = CMD_MOREDATA_CODE + EXTEND_READ_CODE + "0001" + count;
        byte[] bytes = Utils.hexStringToByteArray(temp);
        String crc = CRC.getCRC(bytes).replaceAll(" ", "");
        String result = HEADER + temp + crc + TAIL;
        return result;
    }

    /**
     * 获取多参数的响应
     *
     * @param hex
     * @return
     */
    public static List<String> getReadMoreFloatResponse(String hex) {
        boolean b = responseCheck(hex);
        List<String> list = new ArrayList<>();
        try {
            if (b) {
                int length = hex.length();
                String substring = hex.substring(12, length - 8); // 参数个数 和 所有返回float数
                String data = hex.substring(14, length - 8); //  所有返回float数
                int count = Integer.parseInt(hex.substring(12, 14), 16); // 参数个数
                for (int i = 0; i < count; i++) {
                    String s = data.substring(i * 8, 8 * (i + 1));
                    String hex2Float = C2JUtils.hex2Float(s, 4);
                    list.add(hex2Float);
                }
                return list;
            }
        } catch (Exception e) {
            e.getMessage();
        }

        return list;
    }
    /*****************************继电器状态********************************/

    /**
     * 向设备写入继电器状态
     *
     * @param data
     * @return
     */
    public static String sendWriteJIDIANQICMD(String data) {
        try {
            // crc 校验字符串
            String temp = CMD_JIDIANQI_CODE + EXTEND_WRITE_CODE + "0002" + data;
            byte[] crc_bytes = Utils.hexStringToByteArray(temp);
            String crc16 = CRC.getCRC(crc_bytes).replaceAll(" ", "");
            String result = HEADER + temp + crc16 + TAIL;
            return result;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 读继电器状态
     *
     * @return
     */
    public static String sendReadJIDIANQICMD() {
        try {
            String temp = CMD_JIDIANQI_CODE + EXTEND_READ_CODE + "0000";
            byte[] bytes = Utils.hexStringToByteArray(temp);
            String crc = CRC.getCRC(bytes).replaceAll(" ", "");
            String result = HEADER + temp + crc + TAIL;
            return result;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 写    继电器响应
     *
     * @return
     */
    public static boolean getWriteJIDIANQIResponse(String hex) {
        try {
            String temp = CMD_JIDIANQI_CODE + EXTEND_WRITE_RESPONSE_CODE + "0000";
            byte[] bytes = Utils.hexStringToByteArray(temp);
            String crc = CRC.getCRC(bytes).replaceAll(" ", "");
            String result = HEADER + temp + crc + TAIL;
            if (result.equals(hex)) {
                return true;
            }

        } catch (Exception e) {
            return false;
        }
        return false;
    }

    /**
     * 读 继电器响应
     *
     * @param hex
     * @return
     */
    public static String getReadJIDIANQIResponse(String hex) {
        try {
            String response = null;
            if (hex.length() > 16) {
                response = hex.substring(12, 16);
                if (response.length() == 4) {
                    return response;
                }
            }
        } catch (Exception e) {
            return "0000";
        }
        return "0000";
    }
    /*****************************参数标定********************************/

    /**
     * 发送参数标定命令
     *
     * @param data
     * @return
     */
    public static String sendWriteParameter(String data) {
        try {
            String temp = CMD_PARAMETER_SET_CODE + EXTEND_WRITE_CODE + "0006" + data;
            byte[] bytes = Utils.hexStringToByteArray(temp);
            String crc = CRC.getCRC(bytes).replaceAll(" ", "");
            return HEADER + temp + crc + TAIL;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 发送读取参数数据命令
     *
     * @param type 传感器类型
     * @return
     */
    public static String sendReadParameter(String type) {
        try {
            String temp = CMD_PARAMETER_SET_CODE + EXTEND_READ_CODE + "0001" + type;
            byte[] bytes = Utils.hexStringToByteArray(temp);
            String crc = CRC.getCRC(bytes).replaceAll(" ", "");
            String result = HEADER + temp + crc + TAIL;
            return result;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 解析参数写入成功的响应
     *
     * @param response
     * @return
     */
    public static boolean getWriteParameterResponse(String response) {
        try {
            String temp = CMD_PARAMETER_SET_CODE + EXTEND_WRITE_RESPONSE_CODE + "0000";
            byte[] bytes = Utils.hexStringToByteArray(temp);
            String crc = CRC.getCRC(bytes).replaceAll(" ", "");
            String result = HEADER + temp + crc + TAIL;
            if (result.equals(response)) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }

    }

    /**
     * 解析读取传感器参数的响应
     *
     * @param hex
     * @return
     */
    public static HashMap<String, String> getReadParameterResponse(String hex) {
        HashMap<String, String> hashMap = new HashMap<>();
        try {
            int length = hex.length();
            String type = hex.substring(12, 14); // 传感器类型
            String data = hex.substring(14, length - 8); //  所有返回float数
            String bg = data.substring(0, 8);
            String lmd = data.substring(8, 16);
            String ldbd = data.substring(16, 24);
            String mdbd = data.substring(24, 32);
            hashMap.put("type", type);
            hashMap.put("bg", C2JUtils.hex2Float(bg, 4));
            hashMap.put("lmd", C2JUtils.hex2Float(lmd, 4));
            hashMap.put("ldbd", C2JUtils.hex2Float(ldbd, 4));
            hashMap.put("mdbd", C2JUtils.hex2Float(mdbd, 4));
            return hashMap;
        } catch (Exception e) {
            return hashMap;
        }

    }

    /*****************************模式切换********************************/

    /**
     * 写入 维护模式
     * @param model 00 测量 01 维护模式
     * @return
     */
    public static String sendWriteModelCmd(String model) {
        try {
            String temp = CMD_MODEL_CODE + EXTEND_WRITE_CODE + "0001" + model;
            byte[] bytes = Utils.hexStringToByteArray(temp);
            String crc = CRC.getCRC(bytes).replaceAll(" ", "");
            String result = HEADER + temp + crc + TAIL;
            return result;
        } catch (Exception ex) {
            return null;
        }

    }

    /**
     * 读取当前模式
     * @return
     */
    public static String sendReadModelCmd() {
        try {
            String temp = CMD_MODEL_CODE + EXTEND_READ_CODE + "0000";
            byte[] bytes = Utils.hexStringToByteArray(temp);
            String crc = CRC.getCRC(bytes).replaceAll(" ", "");
            String result = HEADER + temp + crc + TAIL;
            return result;
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * 获取写入的响应
     * @param hex
     * @return
     */
    public static boolean getCmdWriteModelResponse(String hex) {
        try {
            String temp = CMD_MODEL_CODE + EXTEND_WRITE_RESPONSE_CODE +"0000";
            byte[] bytes = Utils.hexStringToByteArray(temp);
            String crc = CRC.getCRC(bytes).replaceAll(" ", "");
            String result = HEADER + temp + crc + TAIL;
            if (result.equals(hex)){
                return true;
            }else {
                return false;
            }
        } catch (Exception ex) {
            return false;
        }

    }

    /**
     * 读 模式的响应
     * @param hex
     * @return
     */
    public static boolean getCmdReadModelResponse(String hex) {
        try {
            String substring = hex.substring(12, 14);
            if ("01".equals(substring)){ // 维护模式
                return true;
            }else { // 测量模式
                return false;
            }
        } catch (Exception ex) {
            ex.getMessage();
        }
        return false;
    }
    /*****************************电流转换********************************/

    public static String sendWriteTranslate(String hex){
        try {
            String temp = CMD_ELECTRICTRANSLATE_CODE + EXTEND_WRITE_CODE + "0002"+hex;
            byte[] bytes = Utils.hexStringToByteArray(temp);
            String crc = CRC.getCRC(bytes).replaceAll(" ","");
            String result = HEADER + temp + crc + TAIL;
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * 读取当前转换量
     * @return
     */
    public static String sendReadTranslate() {
        try {
            String temp = CMD_ELECTRICTRANSLATE_CODE + EXTEND_READ_CODE + "0000";
            byte[] bytes = Utils.hexStringToByteArray(temp);
            String crc = CRC.getCRC(bytes).replaceAll(" ", "");
            String result = HEADER + temp + crc + TAIL;
            return result;
        } catch (Exception ex) {
            return null;
        }
    }

    public static boolean getWriteTranslateResponse(String hex){
        try {
            String temp = CMD_ELECTRICTRANSLATE_CODE + EXTEND_WRITE_RESPONSE_CODE + "0000";
            byte[] bytes = Utils.hexStringToByteArray(temp);
            String crc = CRC.getCRC(bytes).replaceAll(" ","");
            String result = HEADER + temp + crc + TAIL;
            if (result.equals(hex)){
                return true;
            }else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getReadTranslateResponse(String hex){

        try {
            String translate = hex.substring(12, 16);
            return C2JUtils.hex2Float(translate,2);
        } catch (Exception e) {
            e.printStackTrace();
            return null ;
        }

    }

    /*****************************电流写入********************************/

    /**
     * 四字节浮点数转换的hex
     * @param hex
     * @return
     */
    public static String sendWriteCurrentElectric(String hex){
        try {
            String temp = CMD_CURRENTELECTIC_CODE + EXTEND_WRITE_CODE + "0004"+hex;
            byte[] bytes = Utils.hexStringToByteArray(temp);
            String crc = CRC.getCRC(bytes).replaceAll(" ","");
            String result = HEADER + temp + crc + TAIL;
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * 读取当前转换量
     * @return
     */
    public static String sendReadCurrentElectric() {
        try {
            String temp = CMD_CURRENTELECTIC_CODE + EXTEND_READ_CODE + "0000";
            byte[] bytes = Utils.hexStringToByteArray(temp);
            String crc = CRC.getCRC(bytes).replaceAll(" ", "");
            String result = HEADER + temp + crc + TAIL;
            return result;
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * 获取读当前值的响应
     * @param hex
     * @return
     */
    public static boolean getWriteCurrentResponse(String hex){
        try {
            String temp = CMD_CURRENTELECTIC_CODE + EXTEND_WRITE_RESPONSE_CODE + "0000";
            byte[] bytes = Utils.hexStringToByteArray(temp);
            String crc = CRC.getCRC(bytes).replaceAll(" ","");
            String result = HEADER + temp + crc + TAIL;
            if (result.equals(hex)){
                return true;
            }else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取读当前电流的响应
     * @param hex
     * @return
     */
    public static String getReadCurrentResponse(String hex){
        try {
            boolean b = responseCheck(hex);
            if (b){
                String translate = hex.substring(12, 20);
                String hex2Float = C2JUtils.hex2Float(translate, 4);
                return hex2Float;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null ;
        }
            return null;
    }
    /**
     * 十进制转标准格式十六进制
     *
     * @param n
     * @return
     */
    private static String intToHex(int n) {
        try {
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
        } catch (Exception e) {
            Log.i(TAG, "intToHex: " + e.getMessage());
            return "0";
        }

    }
}
