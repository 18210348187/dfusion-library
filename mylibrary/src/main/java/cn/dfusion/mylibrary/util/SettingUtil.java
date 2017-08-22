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
 * 应用设置工具类
 *
 * @must application中在DataKeeper.init();后SettingUtil.init(...);
 * @warn 修改服务器地址（URL_SERVER_ADDRESS_NORMAL_HTTP等）
 */
public final class SettingUtil {

    static final boolean isReleased = false;//应用已发布

    private SettingUtil() {/*不能实例化**/}


    public static boolean cache = true;//开启缓存
    public static boolean preload = true;//开启预加载

}
