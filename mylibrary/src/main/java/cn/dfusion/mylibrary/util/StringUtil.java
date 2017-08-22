/*Copyright ©2015 TommyLemon(https://github.com/TommyLemon)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.*/

package cn.dfusion.mylibrary.util;

import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 通用字符串(String)相关类,为null时返回""
 *
 * @use StringUtil.xxxMethod(...);
 */
public class StringUtil {

    public StringUtil() {
    }


    private static String currentString = "";

    /**
     * 获取刚传入处理后的string
     *
     * @return String
     * @must 上个影响currentString的方法 和 这个方法都应该在同一线程中，否则返回值可能不对
     */
    public static String getCurrentString() {
        return currentString == null ? "" : currentString;
    }

    //获取string,为null时返回"" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    /**
     * 获取string,为null则返回""
     *
     * @param tv TextView
     * @return String
     */
    public static String getString(TextView tv) {
        if (tv == null || tv.getText() == null) {
            return "";
        }
        return getString(tv.getText().toString());
    }

    /**
     * 获取string,为null则返回""
     *
     * @param object 对象
     * @return String
     */
    public static String getString(Object object) {
        return object == null ? "" : getString(String.valueOf(object));
    }

    /**
     * 获取string,为null则返回""
     *
     * @param cs 字符
     * @return String
     */
    public static String getString(CharSequence cs) {
        return cs == null ? "" : getString(cs.toString());
    }

    /**
     * 获取string,为null则返回""
     *
     * @param s 字符串
     * @return String
     */
    public static String getString(String s) {
        return s == null ? "" : s;
    }

    //获取string,为null时返回"" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    //获取去掉前后空格后的string<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    /**
     * 获取去掉前后空格后的string,为null则返回""
     *
     * @param tv TextView
     * @return String
     */
    public static String getTrimedString(TextView tv) {
        return getTrimedString(getString(tv));
    }

    /**
     * 获取去掉前后空格后的string,为null则返回""
     *
     * @param object 对象
     * @return String
     */
    public static String getTrimedString(Object object) {
        return getTrimedString(getString(object));
    }

    /**
     * 获取去掉前后空格后的string,为null则返回""
     *
     * @param cs 字符
     * @return String
     */
    public static String getTrimedString(CharSequence cs) {
        return getTrimedString(getString(cs));
    }

    /**
     * 获取去掉前后空格后的string,为null则返回""
     *
     * @param s 字符串
     * @return String
     */
    public static String getTrimedString(String s) {
        return getString(s).trim();
    }

    //获取去掉前后空格后的string>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


    //获取去掉所有空格后的string <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    //获取去掉所有空格后的string >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


    //获取string的长度<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    /**
     * 获取string的长度,为null则返回0
     *
     * @param tv   TextView
     * @param trim 去除空格
     * @return int
     */
    public static int getLength(TextView tv, boolean trim) {
        return getLength(getString(tv), trim);
    }

    /**
     * 获取string的长度,为null则返回0
     *
     * @param s    字符串
     * @param trim 去除空格
     * @return int
     */
    private static int getLength(String s, boolean trim) {
        s = trim ? getTrimedString(s) : s;
        return getString(s).length();
    }

    //获取string的长度>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


    //判断字符是否非空 <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    /**
     * 判断字符是否非空
     *
     * @param s    字符串
     * @param trim 去除空格
     * @return boolean
     */
    public static boolean isNotEmpty(String s, boolean trim) {
        if (s == null) {
            return false;
        }
        if (trim) {
            s = s.trim();
        }
        if (s.length() <= 0) {
            return false;
        }

        currentString = s;

        return true;
    }

    //判断字符是否非空 >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


    //判断字符类型 <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    //判断是否全是数字
    private static boolean isNumer(String number) {
        if (!isNotEmpty(number, true)) {
            return false;
        }

        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(number);
        if (!isNum.matches()) {
            return false;
        }

        currentString = number;

        return true;
    }

    //判断字符类型 >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


    //提取特殊字符<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    /**
     * 去掉string内所有非数字类型字符
     *
     * @param s 字符串
     * @return String
     */
    public static String getNumber(String s) {
        if (!isNotEmpty(s, true)) {
            return "";
        }

        String numberString = "";
        String single;
        for (int i = 0; i < s.length(); i++) {
            single = s.substring(i, i + 1);
            if (isNumer(single)) {
                numberString += single;
            }
        }

        return numberString;
    }

    //提取特殊字符>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    //校正（自动补全等）字符串<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<


    //校正（自动补全等）字符串>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

}
