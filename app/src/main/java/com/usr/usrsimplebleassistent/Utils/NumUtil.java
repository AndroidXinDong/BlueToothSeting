package com.usr.usrsimplebleassistent.Utils;

import android.location.Location;
import android.location.LocationManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by dongXin on 2017/8/3.
 */

public class NumUtil {

    public static String getDateFormat(long mills) {
        Date date = new Date(mills);
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmm");
        String result = format.format(date);
        return result;
    }

    public static String getDate(String str) {
        long l = Long.parseLong(str);
        Date date = new Date((l * 1000 - 28800000));
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String result = format.format(date);
        return result;
    }

    /**
     * 对float类型进行四舍五入
     *
     * @param f 需要四舍五入的值
     * @param i 小数点后的位数 i 的 可能值为 0 1 2 4
     * @return
     */
    public static String floatRound(float f, int i) {
        if (i == 0) { // float 取整
            int s = (int) ((f * 10) + 5) / 10;
            return String.valueOf(s);
        } else if (i == 1) { // float 保留小数后一位
            int s = (int) (f * 100);
            double s1 = (s + 5) / 10;
            double d = s1 / 10;
            return String.valueOf(d);
        } else if (i == 2) { // float 保留小数后两位
            String str = String.valueOf(f);
            int a = str.indexOf(".") + 1;
            String str1 = str.substring(a);
            int length = str1.length();
            if (length == 0) {
                return str.concat("00");
            } else if (length == 1) {
                return str.concat("0");
            } else if (length >= 2) {
                int s = (int) (f * 1000);
                double s1 = (s + 5) / 10;
                double d = s1 / 100;
                return String.valueOf(d);
            }

        } else if (i == 4) {
            String str = String.valueOf(f);
            int start = (str.substring(0, str.indexOf("."))).length() + 5;// 总长度
            System.out.println(start);
            int a = str.indexOf(".") + 1;
            String str1 = str.substring(a);
            int length = str1.length();

            if (length == 0) {
                return str.concat("0000");
            } else if (length == 1) {
                return str.concat("000");
            } else if (length == 2) {
                return str.concat("00");
            } else if (length == 3) {
                return str.concat("0");
            } else if (length >= 4) {
                int i3 = (int) (f * 100000);
                float i1 = (i3 + 5) / 10;

                float i2 = i1 / 10000;
                String sEnd = String.valueOf(i2);
                int end = sEnd.length();
                int bu = start - end;
                if (bu == 0) {
                    return sEnd;
                } else {
                    for (int j = 0; j < bu; j++) {
                        sEnd = sEnd.concat("0");
                    }
                    return sEnd;
                }

            }
        }
        return null;
    }

    /**
     * 获取location对象，优先以GPS_PROVIDER获取location对象，当以GPS_PROVIDER获取到的locaiton为null时
     * ，则以NETWORK_PROVIDER获取location对象，这样可保证在室内开启网络连接的状态下获取到的location对象不为空
     *
     * @param locationManager
     * @return
     */
    public static Location getBestLocation(LocationManager locationManager) throws SecurityException {
        Location result = null;
        try {
            if (locationManager != null) {
                result = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (result != null) {
                    return result;
                } else {
                    result = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    return result;
                }
            }
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 去掉地址信息中重复的字符串
     *
     * @param regret 标准
     * @param str    要校准的字符串
     * @return
     */
    public static String stringNumbers(String regret, String str) {
        int i = str.indexOf(regret);
        if (i != -1) {
            String result = str.replaceFirst(regret, "");
            if (result.indexOf(regret) != -1) {

                return result;
            } else {
                return str;
            }
        }

        return null;
    }

    /**
     * 本方法用于检查小数点位数不足问题
     *
     * @param check 需要处理的字符串
     * @param point 保留小数点位数
     * @return
     */
    public static String checkPoint(String check, int point) {
        /**
         * 思路：
         * 1、首先将字符串小数点前和后进行分别截取，判断小数点后，不足给定位数的进行补零
         */
        int i = check.substring(check.indexOf(".") + 1, check.length()).length();
        int distance = point - i;
        if (distance == 0) {
            return check;
        } else {
            for (int i1 = 0; i1 < distance; i1++) {
                check = check.concat("0");
            }
            return check;
        }
    }

    /**
     * 本方法旨在将文件编号进行补足四位数字显示
     *
     * @param string 需要处理的文件编号
     * @param digits 文件需要保留的位数
     * @return
     */
    public static String fourNumber(String string, int digits) {
        /**
         * 1、计算string字符串长度
         * 2、用digits和 字符串长度进行比较，通过差值来进行补零
         */
        int num = string.length();
        int distance = digits - num;
        String result = "";
        if (distance <= 0) {
            return string;
        } else {
            for (int i = 0; i < distance; i++) {
                result = result.concat("0");
            }
            return result.concat(string);
        }
    }

    /**
     * 圆形点位计算方法
     *
     * @param neijing 圆形内径
     * @param taoguan 套管长度
     * @param dianshu 共计点数
     * @return
     */
    public static ArrayList<Float> getCircleStation(float neijing, float taoguan, int dianshu) {
        ArrayList<Float> list = new ArrayList<Float>();
        if (dianshu > 0 && dianshu <= 10) {
            switch (dianshu) {
                case 1:
                    float f11 = Float.parseFloat(floatRound(neijing * 0.146f + taoguan, 2));
                    float f12 = Float.parseFloat(floatRound(neijing * 0.854f + taoguan, 2));
                    list.add(f11);
                    list.add(f12);
                    break;
                case 2:
                    float f21 = Float.parseFloat(floatRound(neijing * 0.067f + taoguan, 2));
                    float f22 = Float.parseFloat(floatRound(neijing * 0.250f + taoguan, 2));
                    float f23 = Float.parseFloat(floatRound(neijing * 0.750f + taoguan, 2));
                    float f24 = Float.parseFloat(floatRound(neijing * 0.933f + taoguan, 2));
                    list.add(f21);
                    list.add(f22);
                    list.add(f23);
                    list.add(f24);
                    break;
                case 3:
                    float f31 = Float.parseFloat(floatRound(neijing * 0.044f + taoguan, 2));
                    float f32 = Float.parseFloat(floatRound(neijing * 0.146f + taoguan, 2));
                    float f33 = Float.parseFloat(floatRound(neijing * 0.296f + taoguan, 2));
                    float f34 = Float.parseFloat(floatRound(neijing * 0.704f + taoguan, 2));
                    float f35 = Float.parseFloat(floatRound(neijing * 0.854f + taoguan, 2));
                    float f36 = Float.parseFloat(floatRound(neijing * 0.956f + taoguan, 2));
                    list.add(f31);
                    list.add(f32);
                    list.add(f33);
                    list.add(f34);
                    list.add(f35);
                    list.add(f36);
                    break;
                case 4:
                    float f41 = Float.parseFloat(floatRound(neijing * 0.033f + taoguan, 2));
                    float f42 = Float.parseFloat(floatRound(neijing * 0.105f + taoguan, 2));
                    float f43 = Float.parseFloat(floatRound(neijing * 0.194f + taoguan, 2));
                    float f44 = Float.parseFloat(floatRound(neijing * 0.323f + taoguan, 2));
                    float f45 = Float.parseFloat(floatRound(neijing * 0.677f + taoguan, 2));
                    float f46 = Float.parseFloat(floatRound(neijing * 0.806f + taoguan, 2));
                    float f47 = Float.parseFloat(floatRound(neijing * 0.895f + taoguan, 2));
                    float f48 = Float.parseFloat(floatRound(neijing * 0.967f + taoguan, 2));
                    list.add(f41);
                    list.add(f42);
                    list.add(f43);
                    list.add(f44);
                    list.add(f45);
                    list.add(f46);
                    list.add(f47);
                    list.add(f48);
                    break;
                case 5:
                    float f51 = Float.parseFloat(floatRound(neijing * 0.026f + taoguan, 2));
                    float f52 = Float.parseFloat(floatRound(neijing * 0.082f + taoguan, 2));
                    float f53 = Float.parseFloat(floatRound(neijing * 0.146f + taoguan, 2));
                    float f54 = Float.parseFloat(floatRound(neijing * 0.226f + taoguan, 2));
                    float f55 = Float.parseFloat(floatRound(neijing * 0.342f + taoguan, 2));
                    float f56 = Float.parseFloat(floatRound(neijing * 0.658f + taoguan, 2));
                    float f57 = Float.parseFloat(floatRound(neijing * 0.774f + taoguan, 2));
                    float f58 = Float.parseFloat(floatRound(neijing * 0.854f + taoguan, 2));
                    float f59 = Float.parseFloat(floatRound(neijing * 0.918f + taoguan, 2));
                    float f60 = Float.parseFloat(floatRound(neijing * 0.974f + taoguan, 2));
                    list.add(f51);
                    list.add(f52);
                    list.add(f53);
                    list.add(f54);
                    list.add(f55);
                    list.add(f56);
                    list.add(f57);
                    list.add(f58);
                    list.add(f59);
                    list.add(f60);
                    break;
                default:
                    list.add(0.0f);
                    break;
            }
            return list;
        }

        return null;
    }

    /**
     * 矩形点位计算方法
     *
     * @param bianchangb 矩形长度B
     * @param cedianshu  测点数 0<cedianshu<=10
     * @param taoguan    套管
     * @return
     */
    public static ArrayList<Float> getRectanStation(float bianchangb, float taoguan, int cedianshu) {
        ArrayList<Float> list = new ArrayList<Float>();
        if (cedianshu > 0 && cedianshu <= 10) {
            for (int i = 0; i < cedianshu; i++) {
                float f = ((bianchangb / (2 * cedianshu)) * (2 * i + 1)) + taoguan;
                list.add(Float.parseFloat(floatRound(f, 2)));
            }
            return list;
        } else {
            list.add(0.0f);
            return list;
        }
    }

}
