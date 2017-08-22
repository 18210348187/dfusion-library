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

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * 通用密码、手机号、验证码输入框输入字符判断及错误提示 类
 *
 * @author Lemon
 * @use EditTextUtil.xxxMethod(...);
 */
public class EditTextUtil {


    //显示/隐藏输入法<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    /**
     * 隐藏输入法
     *
     * @param context              上下文
     * @param toGetWindowTokenView window
     */
    public static void hideKeyboard(Context context, View toGetWindowTokenView) {
        showKeyboard(context, null, toGetWindowTokenView, false);
    }

    /**
     * 显示/隐藏输入法
     *
     * @param context                                         上下文
     * @param et                                              EditText控件
     * @param toGetWindowTokenView(为null时toGetWindowTokenView = et) 包含et的父View，键盘根据toGetWindowTokenView的位置来弹出/隐藏
     * @param show                                            是否显示
     */
    private static void showKeyboard(Context context, EditText et, View toGetWindowTokenView, boolean show) {
        if (context == null) {
            return;
        }

        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);//imm必须与context唯一对应
        if (toGetWindowTokenView == null) {
            toGetWindowTokenView = et;
        }
        if (toGetWindowTokenView == null) {
            return;
        }

        if (!show) {
            imm.hideSoftInputFromWindow(toGetWindowTokenView.getWindowToken(), 0);
            if (et != null) {
                et.clearFocus();
            }
        } else {
            if (et != null) {
                et.setFocusable(true);
                et.setFocusableInTouchMode(true);
                et.requestFocus();
                imm.toggleSoftInputFromWindow(toGetWindowTokenView.getWindowToken()
                        , InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            }
        }
    }

    //显示/隐藏输入法>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


}
