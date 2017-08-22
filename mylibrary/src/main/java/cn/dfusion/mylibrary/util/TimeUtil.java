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

import java.util.Calendar;
import java.util.Date;

/**
 * 通用时间类
 *
 * @use TimeUtil.xxxMethod(...);
 */
public class TimeUtil {

    private TimeUtil() {/* 不能实例化**/}


    public static final int LEVEL_YEAR = 0;
    public static final int LEVEL_MONTH = 1;
    public static final int LEVEL_DAY = 2;
    public static final int LEVEL_HOUR = 3;
    public static final int LEVEL_MINUTE = 4;
    public static final int LEVEL_SECOND = 5;
    public static final int[] LEVELS = {
            LEVEL_YEAR,
            LEVEL_MONTH,
            LEVEL_DAY,
            LEVEL_HOUR,
            LEVEL_MINUTE,
            LEVEL_SECOND,
    };

    public static final String NAME_YEAR = "年";
    public static final String NAME_MONTH = "月";
    public static final String NAME_DAY = "日";


    /**
     * @param level 等级
     * @return boolean
     */
    public static boolean isContainLevel(int level) {
        for (int existLevel : LEVELS) {
            if (level == existLevel) {
                return true;
            }
        }
        return false;
    }


    public static class Day {


        static final int TYPE_SUNDAY = 0;
        static final int TYPE_MONDAY = 1;
        static final int TYPE_TUESDAY = 2;
        static final int TYPE_WEDNESDAY = 3;
        static final int TYPE_THURSDAY = 4;
        static final int TYPE_FRIDAY = 5;
        static final int TYPE_SATURDAY = 6;
        static final int[] DAY_OF_WEEK_TYPES = {
                TYPE_SUNDAY,
                TYPE_MONDAY,
                TYPE_TUESDAY,
                TYPE_WEDNESDAY,
                TYPE_THURSDAY,
                TYPE_FRIDAY,
                TYPE_SATURDAY,
        };

        static final String NAME_SUNDAY = "日";
        static final String NAME_MONDAY = "一";
        static final String NAME_TUESDAY = "二";
        static final String NAME_WEDNESDAY = "三";
        static final String NAME_THURSDAY = "四";
        static final String NAME_FRIDAY = "五";
        static final String NAME_SATURDAY = "六";
        static final String[] DAY_OF_WEEK_NAMES = {
                NAME_SUNDAY,
                NAME_MONDAY,
                NAME_TUESDAY,
                NAME_WEDNESDAY,
                NAME_THURSDAY,
                NAME_FRIDAY,
                NAME_SATURDAY,
        };


        /**
         * @param type 类型
         * @return boolean
         */
        static boolean isContainType(int type) {
            for (int existType : DAY_OF_WEEK_TYPES) {
                if (type == existType) {
                    return true;
                }
            }
            return false;
        }

        public static String getDayNameOfWeek(int type) {
            return isContainType(type) ? DAY_OF_WEEK_NAMES[type - TYPE_SUNDAY] : "";
        }

    }


    public static boolean isLeapYear(int year) {
        return (year % 4 == 0 && year % 100 != 0) || year % 400 == 0;
    }

    /**
     * 获取日期 年，月， 日 对应值
     *
     * @param date 日期
     * @return int[]
     */
    public static int[] getDateDetail(Date date) {
        return date == null ? null : getDateDetail(date.getTime());
    }

    /**
     * 获取日期 年，月， 日 对应值
     *
     * @param time 时间
     * @return int[]
     */
    public static int[] getDateDetail(long time) {
        final Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(time);
        return new int[]{
                mCalendar.get(Calendar.YEAR),//0
                mCalendar.get(Calendar.MONTH) + 1,//1
                mCalendar.get(Calendar.DAY_OF_MONTH),//2
        };
    }

    public static boolean fomerIsBigger(int[] fomer, int[] current) {
        if (fomer == null || current == null) {
            return false;
        }
        int compareLength = fomer.length < current.length ? fomer.length : current.length;

        for (int i = 0; i < compareLength; i++) {
            if (fomer[i] < current[i]) {
                return false;
            }
            if (fomer[i] > current[i]) {
                return true;
            }
        }

        return false;
    }

}
