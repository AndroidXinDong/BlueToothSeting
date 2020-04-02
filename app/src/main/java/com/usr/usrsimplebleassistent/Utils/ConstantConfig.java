package com.usr.usrsimplebleassistent.Utils;

public class ConstantConfig {
    public static String EXTEND_WRITE_CODE = "66"; // 写
    public static String EXTEND_READ_CODE = "55"; // 读
    public static String EXTEND_WRITE_RESPONSE_CODE = "AA"; // 写回应
    public static String EXTEND_READ_RESPONSE_CODE = "99"; // 读回应

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
}
