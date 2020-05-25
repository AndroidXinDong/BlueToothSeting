package com.usr.firecheck.Utils;

import android.R.integer;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.view.View;
import com.usr.firecheck.BlueToothLeService.BluetoothLeService;
import com.usr.firecheck.R;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
/**
 * Class for commonly used methods in the project
 */
public class Utils{
    SharedPreferences sFlowalert;
    // Shared preference constant
    private static String SHARED_PREF_NAME = "CySmart Shared Preference";


    /**
     * Returns the manufacture name from the given characteristic
     *
     * @param characteristic
     * @return manfacture_name_string
     */
    public static String getManufacturerNameString(
            BluetoothGattCharacteristic characteristic) {
        String manfacture_name_string = characteristic.getStringValue(0);
        return manfacture_name_string;
    }

    /**
     * Returns the model number from the given characteristic
     *
     * @param characteristic
     * @return model_name_string
     */

    public static String getModelNumberString(BluetoothGattCharacteristic characteristic) {
        String model_name_string = characteristic.getStringValue(0);

        return model_name_string;
    }

    /**
     * Returns the serial number from the given characteristic
     *
     * @param characteristic
     * @return serial_number_string
     */
    public static String getSerialNumberString(BluetoothGattCharacteristic characteristic) {
        String serial_number_string = characteristic.getStringValue(0);

        return serial_number_string;
    }

    /**
     * Returns the hardware number from the given characteristic
     *
     * @param characteristic
     * @return hardware_revision_name_string
     */
    public static String getHardwareRevisionString(BluetoothGattCharacteristic characteristic) {
        String hardware_revision_name_string = characteristic.getStringValue(0);

        return hardware_revision_name_string;
    }

    /**
     * Returns the Firmware number from the given characteristic
     *
     * @param characteristic
     * @return hardware_revision_name_string
     */
    public static String getFirmwareRevisionString(BluetoothGattCharacteristic characteristic) {
        String firmware_revision_name_string = characteristic.getStringValue(0);

        return firmware_revision_name_string;
    }

    /**
     * Returns the software revision number from the given characteristic
     *
     * @param characteristic
     * @return hardware_revision_name_string
     */
    public static String getSoftwareRevisionString(BluetoothGattCharacteristic characteristic) {
        String hardware_revision_name_string = characteristic.getStringValue(0);

        return hardware_revision_name_string;
    }

    /**
     * Returns the PNP ID from the given characteristic
     *
     * @param characteristic
     * @return {@link String}
     */
    public static String getPNPID(BluetoothGattCharacteristic characteristic) {
        final byte[] data = characteristic.getValue();
        final StringBuilder stringBuilder = new StringBuilder(data.length);
        if (data != null && data.length > 0) {
            for (byte byteChar : data)
                stringBuilder.append(String.format("%02X ", byteChar));
        }

        return String.valueOf(stringBuilder);
    }

    /**
     * Returns the SystemID from the given characteristic
     *
     * @param characteristic
     * @return {@link String}
     */
    public static String getSYSID(BluetoothGattCharacteristic characteristic) {
        final byte[] data = characteristic.getValue();

        final StringBuilder stringBuilder = new StringBuilder(data.length);
        if (data != null && data.length > 0) {
            for (byte byteChar : data)
                stringBuilder.append(String.format("%02X ", byteChar));
        }

        return String.valueOf(stringBuilder);
    }

    /**
     * Adding the necessary INtent filters for Broadcast receivers
     *
     * @return {@link IntentFilter}
     */
    public static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED_CAROUSEL);
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CHARACTERISTIC_ERROR);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CHARACTERISTIC_WRITE_SUCCESS);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DESCRIPTORWRITE_RESULT);
        return intentFilter;
    }

    /**
     * 获取运行时间
     */
    public String getTime() {
        String time1 = sFlowalert.getString("time", "");
        long time = System.currentTimeMillis() - Long.parseLong(time1);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
        String hms = sdf.format(time);
        return hms;
    }


    /**
     * bytes to hex string
     *
     * @param
     * @return
     * */
    public static String ByteArraytoHex(byte[] bytes) {
        String result = "";
        try {
            for (int i = 0; i < bytes.length; i++) {
                String hexString = Integer.toHexString(bytes[i] & 0xFF);
                if (hexString.length() == 1) {
                    hexString = '0' + hexString;
                }
                result += hexString.toUpperCase();
            }
            return result;
        }catch (Exception e){

        }
        return result;
    }

    /**
     * 字节数组转字符串
     * @param bytes
     * @return
     */
    public static String ByteArrToIntStr(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.valueOf(b & 0xff) + " ");
        }
        return sb.toString();
    }


    /**
     * 十六进制数转字节数组
     * @param s
     * @return
     */
    public static byte[] hexStringToByteArray(String s) {
        if (s.length() % 2 != 0) {
            StringBuilder stringBuilder = new StringBuilder(s);
            stringBuilder.insert(s.length() - 1, "0");
            s = stringBuilder.toString();
        }
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    /**
     * 检查是否是标准格式的hex数据
     * @param str
     * @return
     */
    public static boolean isRightHexStr(String str) {
        String reg = "^[0-9a-fA-F]+$";
        return str.matches(reg);
    }


    public static String getMSB(String string) {
        StringBuilder msbString = new StringBuilder();

        for (int i = string.length(); i > 0; i -= 2) {
            String str = string.substring(i - 2, i);
            msbString.append(str);
        }
        return msbString.toString();
    }

    /**
     * Converting the Byte to binary
     *
     * @param bytes
     * @return {@link String}
     */
    public static String BytetoBinary(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * Byte.SIZE);
        for (int i = 0; i < Byte.SIZE * bytes.length; i++)
            sb.append((bytes[i / Byte.SIZE] << i % Byte.SIZE & 0x80) == 0 ? '0'
                    : '1');
        return sb.toString();
    }

    public static String byteToASCII(byte[] array) {
        StringBuffer sb = new StringBuffer();
        for (byte byteChar : array) {
            if (byteChar >= 32 && byteChar < 127) {
                sb.append(String.format("%c", byteChar));
            } else {
                sb.append(String.format("%d ", byteChar & 0xFF)); // to convert
            }
        }

        return sb.toString();
    }

    /**
     * Returns the battery level information from the characteristics
     *
     * @param characteristics
     * @return {@link String}
     */
    public static String getBatteryLevel(BluetoothGattCharacteristic characteristics) {
        int battery_level = characteristics.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
        return String.valueOf(battery_level);
    }

    /**
     * Returns the Alert level information from the characteristics
     *
     * @param characteristics
     * @return {@link String}
     */
    public static String getAlertLevel(BluetoothGattCharacteristic characteristics) {
        int alert_level = characteristics.getIntValue(
                BluetoothGattCharacteristic.FORMAT_UINT8, 0);
        return String.valueOf(alert_level);
    }

    /**
     * Returns the Transmission power information from the characteristic
     *
     * @param characteristics
     * @return {@link integer}
     */
    public static int getTransmissionPower(
            BluetoothGattCharacteristic characteristics) {
        int power_level = characteristics.getIntValue(
                BluetoothGattCharacteristic.FORMAT_SINT8, 0);
        return power_level;
    }


    /**
     * Get the data from milliseconds
     *
     * @return {@link String}
     */
    public static String GetDateFromMilliseconds() {
        DateFormat formatter = new SimpleDateFormat("dd MMM yyyy");
        Calendar calendar = Calendar.getInstance();
        return formatter.format(calendar.getTime());

    }

    /**
     * Get the date
     *
     * @return {@link String}
     */
    public static String GetDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        return formatter.format(calendar.getTime());

    }

    /**
     * Get the seven days before date
     *
     * @return {@link String}
     */

    public static String GetDateSevenDaysBack() {
        DateFormat formatter = new SimpleDateFormat("dd_MMM_yyyy");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -7);
        return formatter.format(calendar.getTime());

    }

    /**
     * Get the time from milliseconds
     *
     * @return {@link String}
     */
    public static String GetTimeFromMilliseconds() {
        DateFormat formatter = new SimpleDateFormat("HH:mm ss SSS");
        Calendar calendar = Calendar.getInstance();
        return formatter.format(calendar.getTime());

    }

    /**
     * Get time and date
     *
     * @return {@link String}
     */

    public static String GetTimeandDate() {
        DateFormat formatter = new SimpleDateFormat("[dd-MMM-yyyy|HH:mm:ss]");
        Calendar calendar = Calendar.getInstance();
        return formatter.format(calendar.getTime());

    }

    /**
     * Get time and date without datalogger format
     *
     * @return {@link String}
     */

    public static String GetTimeandDateUpdate() {
        DateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        return formatter.format(calendar.getTime());

    }


    /**
     * Take the screen shot of the device
     *
     * @param view
     */
    public static void screenShotMethod(View view) {
        Bitmap bitmap;
        if (view != null) {
            View v1 = view;
            v1.setDrawingCacheEnabled(true);
            v1.buildDrawingCache(true);
            bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "CySmart" + File.separator + "file.jpg");
            try {
                FileOutputStream fo = new FileOutputStream(f);
                fo.write(bytes.toByteArray());
                fo.flush();
                fo.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Method to detect whether the device is phone or tablet
     */
    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }


    /**
     * Check whether Internet connection is enabled on the device
     *
     * @param context
     * @return
     */
    public static final boolean checkNetwork(Context context) {
        if (context != null) {
            boolean result = true;
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo == null || !networkInfo.isConnectedOrConnecting()) {
                result = false;
            }
            return result;
        } else {
            return false;
        }
    }

    public static String getPorperties(Context context, BluetoothGattCharacteristic item) {
        String proprties;
        String read = null, write = null, notify = null;

        /**
         * Checking the various GattCharacteristics and listing in the ListView
         */
        int properties = item.getProperties();
        if (getGattCharacteristicsPropertices(properties, BluetoothGattCharacteristic.PROPERTY_READ)) {
            read = context.getString(R.string.gatt_services_read);
        }
        if (getGattCharacteristicsPropertices(properties, BluetoothGattCharacteristic.PROPERTY_WRITE) | getGattCharacteristicsPropertices(properties,
                BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE)) {
            write = context.getString(R.string.gatt_services_write);
        }
        if (getGattCharacteristicsPropertices(properties, BluetoothGattCharacteristic.PROPERTY_NOTIFY)) {
            notify = context.getString(R.string.gatt_services_notify);
        }
        if (getGattCharacteristicsPropertices(properties, BluetoothGattCharacteristic.PROPERTY_INDICATE)) {
            notify = context.getString(R.string.gatt_services_indicate);
        }
        // Handling multiple properties listing in the ListView
        if (read != null) {
            proprties = read;
            if (write != null) {
                proprties = proprties + " & " + write;
            }
            if (notify != null) {
                proprties = proprties + " & " + notify;
            }
        } else {
            if (write != null) {
                proprties = write;

                if (notify != null) {
                    proprties = proprties + " & " + notify;
                }
            } else {
                proprties = notify;
            }
        }

        return proprties;
    }


    // Return the properties of mGattCharacteristics
    public static boolean getGattCharacteristicsPropertices(int characteristics, int characteristicsSearch) {

        if ((characteristics & characteristicsSearch) == characteristicsSearch) {
            return true;
        }
        return false;
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }


    public static boolean isAtCmd(String text) {
        //000000,AT+ 或者 000000,at+   6位数字开头+","+"at+或AT+"+任意字符结尾
        String regx = "^[0-9]{6},(at|AT)\\+.*";
        return text.matches(regx);
    }

    /**
     * 字符串转换成十六进制字符串
     *
     * @param str 待转换的ASCII字符串
     * @return String 每个Byte之间空格分隔，如: [61 6C 6B]
     */
    public static String str2HexStr(String str) {
        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder("");
        byte[] bs = str.getBytes();
        int bit;

        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(chars[bit]);
            bit = bs[i] & 0x0f;
            sb.append(chars[bit]);
            sb.append(' ');
        }
        return sb.toString().trim();
    }

    /**
     * 16进制字符串转换为字符串
     *
     * @param s
     * @return
     */
    public static String hexStringToString(String s) {
        if (s == null || s.equals("")) {
            return null;
        }
        s = s.replace(" ", "");
        byte[] baKeyword = new byte[s.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(
                        s.substring(i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            s = new String(baKeyword, "gbk");
            new String();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return s;
    }


    /**
     * 16进制字符串转换为字符串
     *
     * @param s
     * @return
     */
    public static String hexStringToString2(String s) {
        if (s == null || s.equals("")) {
            return null;
        }
        s = s.replace(" ", "");
        byte[] baKeyword = new byte[s.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(
                        s.substring(i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            s = new String(baKeyword, "gbk");
            s += "\r\n";
            System.out.println(s + "命令行模式发送");
            new String();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return s;
    }



}
