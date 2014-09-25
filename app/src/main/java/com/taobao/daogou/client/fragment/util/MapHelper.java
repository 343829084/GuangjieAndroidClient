package com.taobao.daogou.client.fragment.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;

import com.taobao.daogou.client.util.CommonHelper;
import com.taobao.daogou.client.util.Constants;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by rd on 14-8-14.
 */
public class MapHelper {

    private static MapHelper mInstance = null;

    public static synchronized MapHelper getInstance(Context context) {
        if (mInstance == null)
            mInstance = new MapHelper(context);
        return mInstance;
    }

    private final Context mContext;

    public MapHelper(Context context) {
        mContext = context.getApplicationContext();
    }

    public void getLogos(final String[] names,
                         final onLogoLoaded onLogoLoaded,
                         final Handler handler) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Map<String, Bitmap> logoMap = new HashMap<String, Bitmap>();
                final File cacheDir = CommonHelper.getCacheDir(mContext, "logoBitmapCache");
                for (String fileName : names) {
                    if (logoMap.get(fileName) != null) continue;
                    try {
                        Log.v("bitmap", fileName);

                        Bitmap bitmap = getBitmap(fileName, cacheDir);
                        logoMap.put(fileName, bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        onLogoLoaded.onLogoLoaded(logoMap);
                    }
                });
            }
        }).start();
    }

    private Bitmap getBitmap(final String name, final File cacheDir) throws IOException {
        Bitmap bitmap = null;
        File cacheFile = new File(cacheDir, name);
        if (cacheFile.exists()) {
            bitmap = BitmapFactory.decodeStream(new FileInputStream(cacheFile));
        }

        if (bitmap != null) return bitmap;

        cacheFile.delete();
        cacheFile.createNewFile();
        URL url = new URL(getLogoUrl(name));
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        BufferedInputStream is = new BufferedInputStream(connection.getInputStream());
        FileOutputStream stream = new FileOutputStream(cacheFile);
        byte buffer[] = new byte[1024];

        while (is.read(buffer) != -1)
            stream.write(buffer);
        stream.close();

        bitmap = BitmapFactory.decodeStream(new FileInputStream(cacheFile));
        return bitmap;
    }

    private String getLogoUrl(String name) {
        return String.format("%sstatic/logo/%s", Constants.SERVER_IP, name);
    }
}
