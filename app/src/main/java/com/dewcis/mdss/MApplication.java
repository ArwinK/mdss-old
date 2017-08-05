package com.dewcis.mdss;

import java.io.File;
import android.app.Application;
import com.dewcis.mdss.volley.LruBitmapCache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.dewcis.mdss.volley.OkHttpStack;

import android.text.TextUtils;

/**
 * Created by henriquedn on 4/14/15.
 * henrydkm@gmail.com
 * henry.maina@dewcis.com
 */
public class MApplication extends Application {
    public static final String TAG = "mobilehealth";//MApplication.class.getName();
    public static final boolean LOGDEBUG = true;
    public static final String PACKAGE = MApplication.class.getPackage().getName();
    public static final String url = "http://demo.dewcis.com/mcdss/android?tag=";//"http://apps.aphrc.org/android?tag=";//"http://192.168.0.55:8080/mpamanech/android?tag=";//

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    LruBitmapCache mLruBitmapCache;

    private static MApplication mInstance;

    public MApplication(){

    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static synchronized MApplication getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueueOkhttp() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());

            Network network = new BasicNetwork(new OkHttpStack() );/*{

				@Override
				public HttpResponse performRequest(Request<?> arg0, Map<String, String> arg1)
						throws IOException, AuthFailureError {
					// TODO Auto-generated method stub
					return null;
				}
			});*/
            mRequestQueue = new RequestQueue(new DiskBasedCache(new File(getCacheDir(), "Cache")), network);
            mRequestQueue.start();
        }

        return mRequestQueue;
    }


    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            getLruBitmapCache();
            mImageLoader = new ImageLoader(this.mRequestQueue, mLruBitmapCache);
        }

        return this.mImageLoader;
    }

    public LruBitmapCache getLruBitmapCache() {
        if (mLruBitmapCache == null)
            mLruBitmapCache = new LruBitmapCache();
        return this.mLruBitmapCache;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueueOkhttp().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req, String tag, boolean isNotOkHttp) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueueOkhttp().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req, boolean isNotOkHttp) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }


}
