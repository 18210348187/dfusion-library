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

package cn.dfusion.mylibrary.manager;

import android.os.Handler;
import android.os.HandlerThread;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.dfusion.mylibrary.util.StringUtil;

/**
 * 线程管理类
 *
 * @author Lemon
 * @use ThreadManager.getInstance().runThread(...);
 * 在使用ThreadManager的Context被销毁前ThreadManager.getInstance().destroyThread(...);
 * 在应用退出前ThreadManager.getInstance().finish();
 */
public class ThreadManager {


    private Map<String, ThreadBean> threadMap;

    private ThreadManager() {
        threadMap = new HashMap<>();
    }

    private static final ThreadManager instance = new ThreadManager();

    public static ThreadManager getInstance() {
        return instance;
    }


    /**
     * 运行线程
     *
     * @param name     名称
     * @param runnable 线程
     * @return Handler
     */
    public Handler runThread(String name, Runnable runnable) {
        if (!StringUtil.isNotEmpty(name, true) || runnable == null) {
            return null;
        }
        name = StringUtil.getTrimedString(name);

        Handler handler = getHandler(name);
        if (handler != null) {
            destroyThread(name);
        }

        HandlerThread thread = new HandlerThread(name);
        thread.start();//创建一个HandlerThread并启动它
        handler = new Handler(thread.getLooper());//使用HandlerThread的looper对象创建Handler
        handler.post(runnable);//将线程post到Handler中

        threadMap.put(name, new ThreadBean(name, thread, runnable, handler));
        return handler;
    }

    /**
     * 获取线程Handler
     *
     * @param name 名称
     * @return Handler
     */
    private Handler getHandler(String name) {
        ThreadBean tb = getThread(name);
        return tb == null ? null : tb.getHandler();
    }

    /**
     * 获取线程
     *
     * @param name 名称
     * @return ThreadBean
     */
    private ThreadBean getThread(String name) {
        return name == null ? null : threadMap.get(name);
    }


    /**
     * 销毁线程
     *
     * @param nameList 名称列表
     */
    public void destroyThread(List<String> nameList) {
        if (nameList != null) {
            for (String name : nameList) {
                destroyThread(name);
            }
        }
    }

    /**
     * 销毁线程
     *
     * @param name 名称
     */
    private void destroyThread(String name) {
        destroyThread(getThread(name));
    }

    /**
     * 销毁线程
     *
     * @param tb ThreadBean
     */
    private void destroyThread(ThreadBean tb) {
        if (tb == null) {
            return;
        }

        destroyThread(tb.getHandler(), tb.getRunnable());
        if (tb.getName() != null) { // StringUtil.isNotEmpty(tb.getName(), true)) {
            threadMap.remove(tb.getName());
        }
    }

    /**
     * 销毁线程
     *
     * @param handler  回调
     * @param runnable 线程
     */
    private void destroyThread(Handler handler, Runnable runnable) {
        if (handler == null || runnable == null) {
            return;
        }

        try {
            handler.removeCallbacks(runnable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 结束ThreadManager所有进程
     */
    public void finish() {
        if (threadMap == null || threadMap.keySet() == null) {
            threadMap = null;
            return;
        }
        List<String> nameList = new ArrayList<>(threadMap.keySet());//直接用Set在系统杀掉应用时崩溃
        if (nameList != null) {
            for (String name : nameList) {
                destroyThread(name);
            }
        }
        threadMap = null;
    }


    /**
     * 线程类
     */
    private static class ThreadBean {

        private String name;
        @SuppressWarnings("unused")
        private HandlerThread thread;
        private Runnable runnable;
        private Handler handler;

        ThreadBean(String name, HandlerThread thread, Runnable runnable, Handler handler) {
            this.name = name;
            this.thread = thread;
            this.runnable = runnable;
            this.handler = handler;
        }

        public String getName() {
            return name;
        }

        public Runnable getRunnable() {
            return runnable;
        }

        public Handler getHandler() {
            return handler;
        }
    }


}
