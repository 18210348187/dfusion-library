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

package cn.dfusion.mylibrary.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.dfusion.mylibrary.base.BaseViewBottomWindow;
import cn.dfusion.mylibrary.model.Entry;
import cn.dfusion.mylibrary.model.GridPickerConfig;
import cn.dfusion.mylibrary.util.StringUtil;
import cn.dfusion.mylibrary.util.TimeUtil;

/**
 * 日期选择窗口
 * <p>
 * toActivity或startActivityForResult (DatePickerWindow.createIntent(...), requestCode);
 * 然后在onActivityResult方法内
 * 和android系统SDK内一样，month从0开始
 * 返回选中日期：
 * data.getLongExtra(DatePickerWindow.RESULT_TIME_IN_MILLIS);          // long
 * data.getStringExtra(DatePickerWindow.RESULT_DATE_DETAIL);           // yyyy-MM-dd
 * date.getStringArrayExtra(DatePickerWindow.RESULT_DATE_DETAIL_LIST); // 日期的list
 */
public class DatePickerWindow extends BaseViewBottomWindow<List<Entry<Integer, String>>, GridPickerView> {
    private static final String TAG = "DatePickerWindow";

    //启动方法<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    public static final String INTENT_MIN_DATE = "INTENT_MIN_DATE";
    public static final String INTENT_MAX_DATE = "INTENT_MAX_DATE";
    public static final String INTENT_DEFAULT_DATE = "INTENT_DEFAULT_DATE";
    public static final String INTENT_CURRENT_POSITION = "INTENT_CURRENT_POSITION";

    public static final String RESULT_TIME_IN_MILLIS = "RESULT_TIME_IN_MILLIS";
    public static final String RESULT_DATE_DETAIL_LIST = "RESULT_DATE_DETAIL_LIST";
    public static final String RESULT_DATE_DETAIL = "RESULT_DATE_DETAIL";

    /**
     * 启动这个Activity的Intent
     *
     * @param context           上下文
     * @param limitYearMonthDay 限定最小日期
     * @return Intent
     */
    public static Intent createIntent(Context context, int[] limitYearMonthDay) {
        return createIntent(context, limitYearMonthDay, null,0);
    }

    /**
     * 启动这个Activity的Intent
     *
     * @param context             上下文
     * @param limitYearMonthDay   限定最小日期
     * @param defaultYearMonthDay 缺省值
     * @return Intent
     */
    public static Intent createIntent(Context context, int[] limitYearMonthDay, int[] defaultYearMonthDay) {
        return createIntent(context, limitYearMonthDay, defaultYearMonthDay,0);
    }

    /**
     * 启动这个Activity的Intent
     *
     * @param context             上下文
     * @param limitYearMonthDay   限定最小日期
     * @param defaultYearMonthDay 缺省值
     * @return Intent
     */
    public static Intent createIntent(Context context, int[] limitYearMonthDay, int[] defaultYearMonthDay,int currentPosition) {
        int[] selectedDate = TimeUtil.getDateDetail(System.currentTimeMillis());
        int[] minYearMonthDay;
        int[] maxYearMonthDay;
        if (TimeUtil.fomerIsBigger(limitYearMonthDay, selectedDate)) {
            minYearMonthDay = selectedDate;
            maxYearMonthDay = limitYearMonthDay;
        } else {
            minYearMonthDay = limitYearMonthDay;
            maxYearMonthDay = selectedDate;
        }
        return createIntent(context, minYearMonthDay, maxYearMonthDay, defaultYearMonthDay,currentPosition);
    }

    /**
     * 启动这个Activity的Intent
     *
     * @param context         上下文
     * @param minYearMonthDay 限定最小日期
     * @param maxYearMonthDay 限定最大日期
     * @return Intent
     */
    public static Intent createIntent(Context context, int[] minYearMonthDay, int[] maxYearMonthDay, int[] defaultYearMonthDay,
                                      int currentPosition) {
        return new Intent(context, DatePickerWindow.class).
                putExtra(INTENT_MIN_DATE, minYearMonthDay).
                putExtra(INTENT_MAX_DATE, maxYearMonthDay).
                putExtra(INTENT_DEFAULT_DATE, defaultYearMonthDay)
                .putExtra(INTENT_CURRENT_POSITION,currentPosition);
    }

    //启动方法>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //功能归类分区方法，必须调用<<<<<<<<<<
        initView();
        initData();
        initEvent();
        //功能归类分区方法，必须调用>>>>>>>>>>

    }


    //UI显示区(操作UI，但不存在数据获取或处理代码，也不存在事件监听代码)<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<


    @Override
    public void initView() {//必须调用
        super.initView();

    }

    private List<Entry<Integer, String>> list;

    private void setPickerView(final int tabPosition) {
        runThread(TAG + "setPickerView", new Runnable() {
            @Override
            public void run() {

                final ArrayList<Integer> selectedItemList = new ArrayList<>();
                for (GridPickerConfig gpcb : configList) {
                    selectedItemList.add(Integer.valueOf(StringUtil.getNumber(gpcb.getSelectedItemName())));
                }

                list = getList(tabPosition, selectedItemList);

                runUiThread(new Runnable() {
                    @Override
                    public void run() {
                        containerView.bindView(tabPosition, list);

                        //防止选中非闰年2月29日
                        if (tabPosition < 2) {
                            ArrayList<String> selectedList = containerView.getSelectedItemList();
                            if (selectedList != null && selectedList.size() >= 3) {

                                if (!TimeUtil.isLeapYear(Integer.valueOf(
                                        StringUtil.getNumber(selectedList.get(0))))) {

                                    if ("2".equals(StringUtil.getNumber(selectedList.get(1)))
                                            && "29".equals(StringUtil.getNumber(selectedList.get(2)))) {

                                        onItemSelectedListener.onItemSelected(
                                                null, null, containerView.getCurrentSelectedItemPosition(), 0);
                                    }
                                }
                            }
                        }
                    }
                });
            }
        });
    }


    //UI显示区(操作UI，但不存在数据获取或处理代码，也不存在事件监听代码)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


    //Data数据区(存在数据获取或处理代码，但不存在事件监听代码)<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<


    //	private long minDate;
    //	private long maxDate;

    private int[] minDateDetails;
    private int[] maxDateDetails;
    private int[] defaultDateDetails;

    private ArrayList<GridPickerConfig> configList;

    @Override
    public void initData() {//必须调用
        super.initData();

        intent = getIntent();
        minDateDetails = intent.getIntArrayExtra(INTENT_MIN_DATE);
        maxDateDetails = intent.getIntArrayExtra(INTENT_MAX_DATE);
        defaultDateDetails = intent.getIntArrayExtra(INTENT_DEFAULT_DATE);

        if (minDateDetails == null || minDateDetails.length <= 0) {
            minDateDetails = new int[]{1970, 1, 1};
        }
        if (maxDateDetails == null || maxDateDetails.length <= 0) {
            maxDateDetails = new int[]{2020, 11, 31};
        }
        if (minDateDetails == null || minDateDetails.length <= 0
                || maxDateDetails == null || minDateDetails.length != maxDateDetails.length) {
            finish();
            return;
        }
        if (defaultDateDetails == null || defaultDateDetails.length < 3) {
            defaultDateDetails = TimeUtil.getDateDetail(System.currentTimeMillis());
        }


        runThread(TAG + "initData", new Runnable() {

            @Override
            public void run() {
                final ArrayList<Integer> selectedItemList = new ArrayList<>();
                selectedItemList.add(defaultDateDetails[0]);
                selectedItemList.add(defaultDateDetails[1]);
                selectedItemList.add(defaultDateDetails[2]);

                final int currentPosition = intent.getIntExtra(INTENT_CURRENT_POSITION,0);
                list = getList(currentPosition, selectedItemList);

                runUiThread(new Runnable() {

                    @Override
                    public void run() {
                        containerView.init(configList, list, currentPosition);
                    }
                });
            }
        });

    }


    @SuppressLint("ResourceAsColor")
    private synchronized List<Entry<Integer, String>> getList(int tabPosition, ArrayList<Integer> selectedItemList) {
        int level = TimeUtil.LEVEL_YEAR + tabPosition;
        if (selectedItemList == null || selectedItemList.size() != 3 || !TimeUtil.isContainLevel(level)) {
            return null;
        }

        list = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.set(selectedItemList.get(0), selectedItemList.get(1) - 1, 1);
        switch (level) {
            case TimeUtil.LEVEL_YEAR:
                for (int i = 0; i < maxDateDetails[0] - minDateDetails[0]; i++) {
                    list.add(new Entry<>(GridPickerAdapter.TYPE_CONTNET_ENABLE, String.valueOf(i + 1 + minDateDetails[0])));
                }
                break;
            case TimeUtil.LEVEL_MONTH:
                for (int i = 0; i < 12; i++) {
                    list.add(new Entry<>(GridPickerAdapter.TYPE_CONTNET_ENABLE, String.valueOf(i + 1)));
                }
                break;
            case TimeUtil.LEVEL_DAY:
                for (int i = calendar.get(Calendar.DAY_OF_WEEK) - 1; i < 7; i++) {
                    list.add(new Entry<>(GridPickerAdapter.TYPE_TITLE, TimeUtil.Day.getDayNameOfWeek(i)));
                }
                for (int i = 0; i < calendar.get(Calendar.DAY_OF_WEEK) - 1; i++) {
                    list.add(new Entry<>(GridPickerAdapter.TYPE_TITLE, TimeUtil.Day.getDayNameOfWeek(i)));
                }
                for (int i = 0; i < calendar.getActualMaximum(Calendar.DATE); i++) {
                    list.add(new Entry<>(GridPickerAdapter.TYPE_CONTNET_ENABLE, String.valueOf(i + 1)));
                }
                break;
            default:
                break;
        }

        if (configList == null || configList.size() < 3) {
            configList = new ArrayList<>();

            configList.add(new GridPickerConfig(TimeUtil.NAME_YEAR, "" + selectedItemList.get(0)
                    , selectedItemList.get(0) - 1 - minDateDetails[0], 5, 4));
            configList.add(new GridPickerConfig(TimeUtil.NAME_MONTH, "" + selectedItemList.get(1)
                    , selectedItemList.get(1) - 1, 4, 3));
            configList.add(new GridPickerConfig(TimeUtil.NAME_DAY, "" + selectedItemList.get(2)
                    , selectedItemList.get(2) - 1 + 7, 7, 6));
        }

        return list;
    }


    @Override
    public String getTitleName() {
        return "选择日期";
    }

    @Override
    public String getReturnName() {
        return null;
    }

    @Override
    public String getForwardName() {
        return null;
    }

    @Override
    protected GridPickerView createView() {
        return new GridPickerView(context, getResources());
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void setResult() {
        intent = new Intent();

        List<String> list = containerView.getSelectedItemList();
        if (list != null && list.size() >= 3) {
            ArrayList<Integer> detailList = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                detailList.add(Integer.valueOf(StringUtil.getNumber(list.get(i))));
            }
            detailList.set(1, detailList.get(1) - 1);

            Calendar calendar = Calendar.getInstance();
            calendar.set(detailList.get(0), detailList.get(1), detailList.get(2));
            intent.putExtra(RESULT_TIME_IN_MILLIS, calendar.getTimeInMillis());
            intent.putIntegerArrayListExtra(RESULT_DATE_DETAIL_LIST, detailList);
            intent.putExtra(RESULT_DATE_DETAIL, new SimpleDateFormat("yyyy-MM-dd").format(new Date(calendar.getTimeInMillis())));
        }

        setResult(RESULT_OK, intent);
    }


    //Data数据区(存在数据获取或处理代码，但不存在事件监听代码)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


    //Event事件区(只要存在事件监听代码就是)<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    @Override
    public void initEvent() {//必须调用
        super.initEvent();

        containerView.setOnTabClickListener(onTabClickListener);
        containerView.setOnItemSelectedListener(onItemSelectedListener);
    }


    private GridPickerView.OnTabClickListener onTabClickListener = new GridPickerView.OnTabClickListener() {

        @Override
        public void onTabClick(int tabPosition, TextView tvTab) {
            setPickerView(tabPosition);
        }
    };

    private OnItemSelectedListener onItemSelectedListener = new OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {
            containerView.doOnItemSelected(containerView.getCurrentTabPosition()
                    , position, containerView.getCurrentSelectedItemName());
            int tabPosition = containerView.getCurrentTabPosition() + 1;
            setPickerView(tabPosition);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    //系统自带监听方法<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<


    //类相关监听<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<


    //类相关监听>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    //系统自带监听方法>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


    //Event事件区(只要存在事件监听代码就是)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


    //内部类,尽量少用<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<


    //内部类,尽量少用>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

}