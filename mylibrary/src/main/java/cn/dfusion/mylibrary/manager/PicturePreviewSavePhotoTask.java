package cn.dfusion.mylibrary.manager;

import android.os.AsyncTask;
import android.os.Build;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import cz.msebera.android.httpclient.Header;

/**
 * 保存文件到本地
 */

public class PicturePreviewSavePhotoTask extends AsyncTask {
    private String mUrl;
    private File mFile;
    private PicturePreviewSavePhotoTaskHandler mSavePhotoTaskHandler;

    public PicturePreviewSavePhotoTask() {
    }

    public void save(String url, File file, final PicturePreviewSavePhotoTaskHandler handler) {
        mUrl = url;
        mFile = file;
        mSavePhotoTaskHandler = handler;
        if (Build.VERSION.SDK_INT >= 11) {
            executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            execute();
        }
    }

    public void cancelTask() {
        if (getStatus() != Status.FINISHED) {
            cancel(true);
        }
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        // 本地图片
        if (mUrl.startsWith("file")) {
            saveFile(mUrl, mFile);
        }
        // 网络图片
        else {
            SyncHttpClient client = new SyncHttpClient();
            client.get(mUrl, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    saveFile(responseBody, mFile);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    mSavePhotoTaskHandler.onFailure();
                }
            });
        }

        return null;
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        mSavePhotoTaskHandler.onFailure();
    }

    @Override
    protected void onCancelled(Object o) {
        super.onCancelled(o);
        mSavePhotoTaskHandler.onFailure();
    }

    private void saveFile(String url, File file) {
        url = url.replace("file://", "");
        FileInputStream fis = null;
        ByteArrayOutputStream baos = null;
        try {
            fis = new FileInputStream(url);
            baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int count;
            while ((count = fis.read(buffer)) >= 0) {
                baos.write(buffer, 0, count);
            }
            byte[] bytes = baos.toByteArray();
            saveFile(bytes, file);
        } catch (Exception e) {
            e.printStackTrace();
            mSavePhotoTaskHandler.onFailure();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    mSavePhotoTaskHandler.onFailure();
                }
            }
            if (baos != null) {
                try {
                    baos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    mSavePhotoTaskHandler.onFailure();
                }
            }
        }
    }

    /**
     * 根据byte数组生成文件
     *
     * @param bytes 生成文件用到的byte数组
     */
    private void saveFile(byte[] bytes, File file) {

        FileOutputStream outputStream = null;
        // 创建BufferedOutputStream对象
        BufferedOutputStream bufferedOutputStream = null;
        try {
            // 获取FileOutputStream对象
            outputStream = new FileOutputStream(file);
            // 获取BufferedOutputStream对象
            bufferedOutputStream = new BufferedOutputStream(outputStream);
            // 往文件所在的缓冲输出流中写byte数据
            bufferedOutputStream.write(bytes);
            // 刷出缓冲输出流，该步很关键，要是不执行flush()方法，那么文件的内容是空的。
            bufferedOutputStream.flush();
        } catch (Exception e) {
            // 打印异常信息
            e.printStackTrace();
            mSavePhotoTaskHandler.onFailure();
        } finally {
            // 关闭创建的流对象
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    mSavePhotoTaskHandler.onFailure();
                }
            }
            if (bufferedOutputStream != null) {
                try {
                    bufferedOutputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    mSavePhotoTaskHandler.onFailure();
                }
            }
        }
        mSavePhotoTaskHandler.onSuccess();
    }

    public interface PicturePreviewSavePhotoTaskHandler {
        void onSuccess();

        void onFailure();
    }
}
