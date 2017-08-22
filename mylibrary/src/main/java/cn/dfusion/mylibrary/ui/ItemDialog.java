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

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import cn.dfusion.mylibrary.R;
import cn.dfusion.mylibrary.util.StringUtil;

/**
 * 通用纵向Item选项dialog
 * <p>
 * 使用： new ItemDialog(...).show();
 */
public class ItemDialog extends Dialog implements OnItemClickListener {

    public interface OnDialogItemClickListener {

        /**
         * 点击item事件的回调方法
         *
         * @param requestCode 传入的用于区分某种情况下的showDialog
         * @param position    位置
         * @param item        内容
         */
        void onDialogItemClick(int requestCode, int position, String item);
    }

    private Context context;
    private String[] items;
    private String title;

    private int requestCode;
    private OnDialogItemClickListener listener;

    /**
     * 带监听器参数的构造函数
     */
    public ItemDialog(Context context, String[] items,
                      int requestCode, OnDialogItemClickListener listener) {
        this(context, items, null, requestCode, listener);
    }

    private ItemDialog(Context context, String[] items, String title,
                       int requestCode, OnDialogItemClickListener listener) {
        super(context, R.style.MyDialog);

        this.context = context;
        this.items = items;
        this.title = title;
        this.requestCode = requestCode;
        this.listener = listener;
    }

    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_dialog);
        setCanceledOnTouchOutside(true);

        TextView tvItemDialogTitle = findViewById(R.id.tvItemDialogTitle);
        ListView lvItemDialog = findViewById(R.id.lvItemDialog);

        tvItemDialogTitle.setVisibility(StringUtil.isNotEmpty(title, true) ? View.VISIBLE : View.GONE);
        tvItemDialogTitle.setText(StringUtil.getCurrentString());

        adapter = new ArrayAdapter<>(context, R.layout.item_dialog_item, items);
        lvItemDialog.setAdapter(adapter);
        lvItemDialog.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (listener != null) {
            listener.onDialogItemClick(requestCode, position, adapter == null ? null : adapter.getItem(position));
        }
        dismiss();
    }

}

