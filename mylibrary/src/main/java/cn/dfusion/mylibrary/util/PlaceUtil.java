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

/**
 * 地点相关类
 *
 */
public class PlaceUtil {

    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_DISTRICT = 2;
    public static final int LEVEL_TOWN = 3;
    public static final int LEVEL_ROAD = 4;
    private static final int[] LEVELS = {
            LEVEL_PROVINCE,
            LEVEL_CITY,
            LEVEL_DISTRICT,
            LEVEL_TOWN,
            LEVEL_ROAD,
    };

    private static final String NAME_PROVINCE = "省";
    private static final String NAME_CITY = "市";
    private static final String NAME_DISTRICT = "区";
    private static final String NAME_TOWN = "镇";
    private static final String NAME_ROAD = "街";
    private static final String[] LEVEL_NAMES = {
            NAME_PROVINCE,
            NAME_CITY,
            NAME_DISTRICT,
            NAME_TOWN,
            NAME_ROAD,
    };


    /**
     * @param level 级别
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

    /**
     * 根据级别获取名称
     *
     * @param level 级别
     * @return String
     */
    public static String getNameByLevel(int level) {
        return isContainLevel(level) ? LEVEL_NAMES[level - LEVEL_PROVINCE] : "";
    }

}
