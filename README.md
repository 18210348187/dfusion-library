# dfusion-library
cn.dfusion.mylibrary

# 使用：

1、AlertDialog

new AlertDialog(context, "标题", "内容", "确认按钮文字", "取消按钮文字", requestCode, listener).show();

//不显示取消按钮
new AlertDialog(context, "标题", "内容", false , requestCode, listener).show();

//不显示取消按钮
new AlertDialog(context, "标题", "内容", false , "确认按钮文字", requestCode, listener).show();

