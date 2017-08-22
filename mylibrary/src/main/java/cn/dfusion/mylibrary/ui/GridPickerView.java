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
import android.content.res.Resources;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.dfusion.mylibrary.R;
import cn.dfusion.mylibrary.base.BaseView;
import cn.dfusion.mylibrary.model.Entry;
import cn.dfusion.mylibrary.model.GridPickerConfig;
import cn.dfusion.mylibrary.util.ScreenUtil;
import cn.dfusion.mylibrary.util.StringUtil;

/**
 * 网格选择器View
 *
 * @author Lemon
 *         必需调用init方法
 */
public class GridPickerView extends BaseView<List<Entry<Integer, String>>> {

    /**
     * tabs切换和gridView的内容切换
     */
    interface OnTabClickListener {

        void onTabClick(int tabPosition, TextView tvTab);
    }

    private OnTabClickListener onTabClickListener;

    void setOnTabClickListener(OnTabClickListener onTabClickListener) {
        this.onTabClickListener = onTabClickListener;
    }


    private OnItemSelectedListener onItemSelectedListener;

    void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener) {
        this.onItemSelectedListener = onItemSelectedListener;
    }

    private int contentHeight;

    GridPickerView(Activity context, Resources resources) {
        super(context, resources);
        contentHeight = (int) getDimension(R.dimen.grid_picker_content_height);
    }

    public LinearLayout llGridPickerViewTabContainer;
    public GridView gvGridPickerView;

    /**
     * 获取View
     */
    @SuppressLint("InflateParams")
    @Override
    public View createView(LayoutInflater inflater) {
        convertView = inflater.inflate(R.layout.grid_picker_view, null);

        llGridPickerViewTabContainer = findViewById(R.id.llGridPickerViewTabContainer);
        gvGridPickerView = findViewById(R.id.gvGridPickerView);

        return convertView;
    }

    ArrayList<String> getSelectedItemList() {
        ArrayList<String> list = new ArrayList<>();
        for (GridPickerConfig gpcb : configList) {
            list.add(gpcb.getSelectedItemName());
        }
        return list;
    }

    private int getTabCount() {
        return configList == null ? 0 : configList.size();
    }

    private boolean isOnLastTab() {
        return getTabCount() > 0 && getCurrentTabPosition() >= getTabCount() - 1;
    }

    private int currentTabPosition;

    int getCurrentTabPosition() {
        return currentTabPosition;
    }


    private String currentSelectedItemName;

    String getCurrentSelectedItemName() {
        return currentSelectedItemName;
    }

    private int currentSelectedItemPosition;

    int getCurrentSelectedItemPosition() {
        return currentSelectedItemPosition;
    }


    int getSelectedItemPosition(int tabPosition) {
        return configList.get(tabPosition).getSelectedItemPostion();
    }


    public List<Entry<Integer, String>> getList() {
        return adapter == null ? null : adapter.list;
    }


    @Override
    public void bindView(List<Entry<Integer, String>> l) {/*do nothing,必须init**/}


    /**
     * 初始化，必须使用且只能使用一次
     *
     * @param configList         configList
     * @param lastList           lastList
     * @param currentTabPosition tab开始位置
     */
    final void init(ArrayList<GridPickerConfig> configList, List<Entry<Integer, String>> lastList, int currentTabPosition) {
        if (configList == null || configList.size() <= 0) {
            return;
        }

        this.currentTabPosition = currentTabPosition;

        int tabWidth = configList.size() < 4 ? ScreenUtil.getScreenWidth(context) / configList.size() : 3;
        llGridPickerViewTabContainer.removeAllViews();
        for (int i = 0; i < configList.size(); i++) {//需要重新设置来保持每个tvTab宽度一致

            addTab(i, tabWidth, StringUtil.getTrimedString(configList.get(i)));
        }
        llGridPickerViewTabContainer.getChildAt(currentTabPosition)
                .setBackgroundResource(R.color.alpha_3);

        this.configList = configList;

        bindView(currentTabPosition, lastList, configList.get(currentTabPosition).getSelectedItemPostion());
    }


    /**
     * 添加tab
     *
     * @param tabPosition tab位置
     * @param tabWidth    tab宽度
     * @param name        tab内容
     */
    @SuppressLint("NewApi")
    private void addTab(final int tabPosition, int tabWidth, String name) {
        if (!StringUtil.isNotEmpty(name, true)) {
            return;
        }
        name = StringUtil.getTrimedString(name);

        final TextView tvTab = new TextView(context);
        tvTab.setLayoutParams(new LayoutParams(tabWidth, LayoutParams.MATCH_PARENT));
        tvTab.setGravity(Gravity.CENTER);
        tvTab.setTextColor(context.getResources().getColor(R.color.black));
        tvTab.setBackgroundResource(R.drawable.to_alpha);
        tvTab.setTextSize(18);
        tvTab.setSingleLine(true);
        tvTab.setText(name);
        tvTab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tabPosition == getCurrentTabPosition()) {
                    return;
                }

                if (onTabClickListener != null) {
                    onTabClickListener.onTabClick(tabPosition, tvTab);
                }
                //点击就是要切换list，这些配置都要改bindView(tabSuffix, tabPosition, tabName, list, numColumns, maxShowRows, itemPosition)
            }
        });
        llGridPickerViewTabContainer.addView(tvTab);
    }


    private static final int MAX_NUM_TABS = 12;//最大标签数量

    private ArrayList<GridPickerConfig> configList;
    private GridPickerAdapter adapter;

    /**
     * 设置并显示内容//可能导致每次都不变
     *
     * @param tabPosition tab位置
     * @param list        内容
     */
    void bindView(final int tabPosition, List<Entry<Integer, String>> list) {
        bindView(tabPosition, list, getSelectedItemPosition(tabPosition));
    }

    /**
     * @param tabPosition  tab位置
     * @param list         内容
     * @param itemPosition 选中内容
     */
    void bindView(final int tabPosition, List<Entry<Integer, String>> list, int itemPosition) {//GridView
        if (configList == null || configList.size() <= 0) {
            return;
        }
        GridPickerConfig gpcb = configList.get(tabPosition);
        if (gpcb == null) {
            return;
        }

        if (list == null || list.size() <= 0) {
            return;
        }
        if (tabPosition >= MAX_NUM_TABS) {
            // bindView  tabPosition >= MAX_NUM_TABS,防止恶意添加标签导致数量过多选择困难甚至崩溃;
            return;
        }

        itemPosition = getItemPosition(itemPosition, list);

        int numColumns = gpcb.getNumColumns();
        if (numColumns <= 0) {
            numColumns = 3;
        }

        int maxShowRows = gpcb.getMaxShowRows();
        if (maxShowRows <= 0) {
            maxShowRows = 5;
        }

        //Tabs<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

        doOnItemSelected(tabPosition, itemPosition, list.get(itemPosition).getValue());

        //Tabs>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


        //gridView<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

        adapter = new GridPickerAdapter(context, itemPosition, contentHeight / maxShowRows);
        adapter.refresh(list);
        adapter.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentSelectedItemName = adapter.getCurrentItemName();
                if (!isOnLastTab() && onItemSelectedListener != null) {
                    onItemSelectedListener.onItemSelected(parent, view, position, id);
                    return;
                }
                doOnItemSelected(tabPosition, position, adapter.getCurrentItemName());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        gvGridPickerView.setNumColumns(numColumns);
        gvGridPickerView.setAdapter(adapter);
        gvGridPickerView.smoothScrollToPosition(itemPosition);

        //gridView>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    }

    /**
     * 获取itemPosition，解决部分不可点击的item被选中的问题
     *
     * @param itemPosition item选中位置
     * @param list         列表内容
     * @return int
     */
    private int getItemPosition(int itemPosition, List<Entry<Integer, String>> list) {
        if (itemPosition < 0) {
            itemPosition = 0;
        } else if (itemPosition >= list.size()) {
            itemPosition = list.size() - 1;
        }

        if (!isItemEnabled(list, itemPosition)) {
            int length = Math.max(itemPosition, list.size() - itemPosition);
            for (int i = 1; i <= length; i++) {
                if (isItemEnabled(list, itemPosition - i)) {
                    return itemPosition - i;
                }
                if (isItemEnabled(list, itemPosition + i)) {
                    return itemPosition + i;
                }
            }
        }

        return itemPosition;
    }

    private boolean isItemEnabled(List<Entry<Integer, String>> list, int itemPosition) {
        return list != null && itemPosition >= 0 && itemPosition < list.size()
                && list.get(itemPosition).getKey() == GridPickerAdapter.TYPE_CONTNET_ENABLE;
    }


    /**
     * 在onItemSelected时响应,
     * 之后可能会跳转到下一个tab，导致 tabPositionWhenItemSelect+=1; selectedItemPosition = 0;
     *
     * @param tabPosition  tab位置
     * @param itemPosition item位置
     * @param itemName     item名称
     */
    void doOnItemSelected(int tabPosition, int itemPosition, String itemName) {
        currentTabPosition = tabPosition < getTabCount() ? tabPosition : getTabCount() - 1;
        currentSelectedItemPosition = itemPosition;
        currentSelectedItemName = StringUtil.getTrimedString(itemName);

        configList.set(currentTabPosition, configList.get(currentTabPosition).set(currentSelectedItemName, itemPosition));

        for (int i = 0; i < llGridPickerViewTabContainer.getChildCount(); i++) {
            ((TextView) llGridPickerViewTabContainer.getChildAt(i)).setText(configList.get(i).getTabName());
            llGridPickerViewTabContainer.getChildAt(i).setBackgroundResource(
                    i == tabPosition ? R.color.alpha_3 : R.color.alpha_complete);
        }
    }


}
