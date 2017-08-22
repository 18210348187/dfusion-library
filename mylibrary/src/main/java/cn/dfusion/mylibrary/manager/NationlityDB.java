package cn.dfusion.mylibrary.manager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 民族管理类
 */

public class NationlityDB {
    private static final String NATIONLITY_DB_NAME = "nationality.db";
    private static final String NATIONLITY_TABLE_NAME = "nationality";

    private static NationlityDB nationlityDB;

    private SQLiteDatabase db;

    private NationlityDB(Context context, String path) {
        db = context.openOrCreateDatabase(path, Context.MODE_PRIVATE, null);
    }

    public static synchronized NationlityDB getInstance(Context context, String packageName) {
        if (nationlityDB == null) {
            nationlityDB = openNationlityDB(context, packageName);
        }
        return nationlityDB;
    }

    private static NationlityDB openNationlityDB(Context context, String packageName) {
        String path = "/data"
                + Environment.getDataDirectory().getAbsolutePath()
                + File.separator + packageName + File.separator
                + NATIONLITY_DB_NAME;
        File db = new File(path);
        if (!db.exists()) {
            try {
                InputStream is = context.getAssets().open(NATIONLITY_DB_NAME);
                FileOutputStream fos = new FileOutputStream(db);
                int len;
                byte[] buffer = new byte[1024];
                while ((len = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                    fos.flush();
                }
                fos.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(0);
            }
        }
        return new NationlityDB(context, path);
    }

    public List<String> getAll() {
        List<String> list = new ArrayList<>();
        @SuppressLint("Recycle")
        Cursor c = db.rawQuery("SELECT * from " + NATIONLITY_TABLE_NAME, null);
        while (c.moveToNext()) {
            list.add(c.getString(c.getColumnIndex("name")));
        }
        return list;
    }
}
