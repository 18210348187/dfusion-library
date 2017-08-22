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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import cn.dfusion.mylibrary.R;

/**
 * 通用操作类
 * <p>
 * 使用 CommonUtil.xxxMethod(...);
 */
public class CommonUtil {

    public CommonUtil() {/* 不能实例化**/}


    //启动新Activity方法<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    /**
     * 打开新的Activity，向左滑入效果
     *
     * @param intent 交互
     */
    public static void toActivity(final Activity context, final Intent intent) {
        toActivity(context, intent, true);
    }

    /**
     * 打开新的Activity
     *
     * @param intent        交互
     * @param showAnimation 显示动画
     */
    public static void toActivity(final Activity context, final Intent intent, final boolean showAnimation) {
        toActivity(context, intent, -1, showAnimation);
    }

    /**
     * 打开新的Activity，向左滑入效果
     *
     * @param intent      交互
     * @param requestCode 请求码
     */
    public static void toActivity(final Activity context, final Intent intent, final int requestCode) {
        toActivity(context, intent, requestCode, true);
    }

    /**
     * 打开新的Activity
     *
     * @param intent        交互
     * @param requestCode   请求码
     * @param showAnimation 显示动画
     */
    public static void toActivity(final Activity context, final Intent intent, final int requestCode, final boolean showAnimation) {
        if (context == null || intent == null) {
            return;
        }
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (requestCode < 0) {
                    context.startActivity(intent);
                } else {
                    context.startActivityForResult(intent, requestCode);
                }
                if (showAnimation) {
                    context.overridePendingTransition(R.anim.right_push_in, R.anim.hold);
                } else {
                    context.overridePendingTransition(R.anim.null_anim, R.anim.null_anim);
                }
            }
        });
    }
    //启动新Activity方法>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    //show short toast 方法<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    /**
     * 快捷显示short toast方法，需要long toast就用 Toast.makeText(string, Toast.LENGTH_LONG).show(); ---不常用所以这个类里不写
     *
     * @param context     上下文
     * @param stringResId 资源字符串id
     */
    public static void showShortToast(final Context context, int stringResId) {
        try {
            showShortToast(context, context.getResources().getString(stringResId));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 快捷显示short toast方法，需要long toast就用 Toast.makeText(string, Toast.LENGTH_LONG).show(); ---不常用所以这个类里不写
     *
     * @param string 显示内容
     */
    public static void showShortToast(final Context context, final String string) {
        if (context == null) {
            return;
        }
        Toast.makeText(context, string, Toast.LENGTH_SHORT).show();
    }

    //show short toast 方法>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

}