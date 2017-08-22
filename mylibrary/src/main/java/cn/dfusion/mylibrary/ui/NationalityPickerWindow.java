package cn.dfusion.mylibrary.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.dfusion.mylibrary.base.BaseViewBottomWindow;
import cn.dfusion.mylibrary.manager.NationlityDB;
import cn.dfusion.mylibrary.model.Entry;
import cn.dfusion.mylibrary.model.GridPickerConfig;
import cn.dfusion.mylibrary.util.StringUtil;

/**
 * 民族弹出窗口
 * <p>
 * 使用：
 * toActivity或startActivityForResult (NationalityPickerWindow.createIntent(...), requestCode);
 * 然后在onActivityResult方法内
 * data.getStringExtra(NationalityPickerWindow.RESULT_NATIONALITY); 可得到民族
 */

public class NationalityPickerWindow extends BaseViewBottomWindow<List<Entry<Integer, String>>, GridPickerView> {

    private static final String TAG = "NationalityPickerWindow";

    public static final String INTENT_PACKAGE_NAME = "INTENT_PACKAGE_NAME";

    public static final String RESULT_NATIONALITY = "RESULT_NATIONALIT";

    private NationlityDB nationlityDB;

    private List<Entry<Integer, String>> list;

    /**
     * 启动这个Activity的Intent
     *
     * @param context     上下文
     * @param packageName 包名
     * @return Intent
     */
    public static Intent createIntent(Context context, String packageName) {
        return new Intent(context, NationalityPickerWindow.class).
                putExtra(INTENT_PACKAGE_NAME, packageName);
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

    //UI显示区(操作UI，但不存在数据获取或处理代码，也不存在事件监听代码)<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    @Override
    public void initView() {
        super.initView();
    }

    //Data数据区(存在数据获取或处理代码，但不存在事件监听代码)<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<


    @Override
    public void initData() {
        super.initData();

        runThread(TAG + "initData", new Runnable() {

            @Override
            public void run() {
                if (nationlityDB == null) {
                    nationlityDB = NationlityDB.getInstance(context, StringUtil.getTrimedString(
                            getIntent().getStringExtra(INTENT_PACKAGE_NAME)));
                }

                final ArrayList<GridPickerConfig> configList = new ArrayList<>();
                configList.add(new GridPickerConfig("", "汉族", 0));

                final ArrayList<String> selectedItemNameList = new ArrayList<>();
                for (GridPickerConfig gpcb : configList) {
                    selectedItemNameList.add(gpcb.getSelectedItemName());
                }

                list = getList(selectedItemNameList);
                runUiThread(new Runnable() {

                    @Override
                    public void run() {
                        containerView.init(configList, list, 0);
                    }
                });
            }
        });
    }

    private synchronized List<Entry<Integer, String>> getList(ArrayList<String> selectedItemList) {
        if (selectedItemList == null || selectedItemList.size() <= 0) {
            return null;
        }

        list = new ArrayList<>();
        List<String> nameList = nationlityDB.getAll();
        if (nameList != null) {
            for (String name : nameList) {
                list.add(new Entry<>(GridPickerAdapter.TYPE_CONTNET_ENABLE, name));
            }
        }
        return list;
    }

    @Override
    public String getTitleName() {
        return "选择民族";
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

    @Override
    protected void setResult() {
        ArrayList<String> placeList = containerView.getSelectedItemList();
        String nation = "";
        if (placeList != null) {
            for (String s : placeList) {
                nation += StringUtil.getTrimedString(s);
            }
        }
        setResult(RESULT_OK, new Intent().putExtra(RESULT_NATIONALITY, nation));
    }

    //Event事件区(只要存在事件监听代码就是)<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    @Override
    public void initEvent() {//必须调用
        super.initEvent();

        containerView.setOnTabClickListener(onTabClickListener);
        containerView.setOnItemSelectedListener(onItemSelectedListener);
    }


    private GridPickerView.OnTabClickListener onTabClickListener = new GridPickerView.OnTabClickListener() {

        @Override
        public void onTabClick(int tabPosition, TextView tvTab) {
            setPickerView(tabPosition, containerView.getSelectedItemPosition(tabPosition));
        }
    };

    private AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {
            containerView.doOnItemSelected(containerView.getCurrentTabPosition()
                    , position, containerView.getCurrentSelectedItemName());
            setPickerView(containerView.getCurrentTabPosition() + 1, 0);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    private void setPickerView(final int tabPosition, final int itemPositon) {
        runThread(TAG + "setPickerView", new Runnable() {
            @Override
            public void run() {

                list = getList(containerView.getSelectedItemList());
                runUiThread(new Runnable() {
                    @Override
                    public void run() {
                        containerView.bindView(tabPosition, list, itemPositon);
                    }
                });
            }
        });
    }


    //类相关监听<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    @Override
    protected void onDestroy() {
        super.onDestroy();
        nationlityDB = null;
    }
}
