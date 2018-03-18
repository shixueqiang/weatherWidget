package com.shixq.www.weather.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RemoteViews;

import com.shixq.www.weather.MainActivity;
import com.shixq.www.weather.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * Created by shixq on 2018/3/17.
 */

public class WeatherWidgetProvider extends AppWidgetProvider {
    private static String TAG = "WeatherWidgetProvider";
    private WebView mWebView;
    private Context mContext;
    public static final String ACTION_REFRESH = "com.shixq.weather.refresh";
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Weather weather = (Weather) msg.obj;
            refreshWidget(mContext, weather);
        }
    };

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        String action = intent.getAction();
        Log.e(TAG, "receive action " + action);
        if (action.equals(ACTION_REFRESH)) {
            Weather weather = (Weather) intent.getSerializableExtra("weather");
            refreshWidget(context, weather);
        }
    }

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        mContext = context;
        initWebView(context);
        mWebView.loadUrl(MainActivity.WEATHER_URL);
        Log.e(TAG, "onUpdate appWidgetIds size " + appWidgetIds.length);

    }

    private void initWebView(Context context) {
        if (mWebView == null) {
            mWebView = new WebView(context);
            mWebView.setWebViewClient(new MyWebViewClient());

            WebSettings webSettings = mWebView.getSettings();

            // add java script interface
            // note:if api level lower than 17(android 4.2), addJavascriptInterface has security
            // issue, please use x5 or see https://developer.android.com/reference/android/webkit/
            // WebView.html#addJavascriptInterface(java.lang.Object, java.lang.String)
            webSettings.setJavaScriptEnabled(true);
            mWebView.removeJavascriptInterface("searchBoxJavaBridge_");
            mWebView.addJavascriptInterface(new WeatherInterface(), "weather");

            // init webview settings
            webSettings.setAllowContentAccess(true);
            webSettings.setDatabaseEnabled(true);
            webSettings.setDomStorageEnabled(true);
            webSettings.setAppCacheEnabled(true);
            webSettings.setSaveFormData(false);
            webSettings.setUseWideViewPort(true);
            webSettings.setLoadWithOverviewMode(true);
        }
    }

    private void refreshWidget(Context context, Weather weather) {
        // Create an Intent to launch ExampleActivity
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        // Get the layout for the App Widget and attach an on-click listener
        // to the button
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        views.setTextViewText(R.id.text_city, weather.getCityName());
        views.setTextViewText(R.id.text_wind, weather.getWind());
        views.setTextViewText(R.id.text_weather, weather.getWeather() + " " + weather.getCurrTemp() + "℃");
        views.setTextViewText(R.id.text_temp, weather.getMinTemp() + "—" + weather.getMaxTemp());
        views.setTextViewText(R.id.text_pm2, weather.getPm25());
        views.setTextViewText(R.id.text_pm2_description, weather.getPm25Description());
        views.setOnClickPendingIntent(R.id.root_layout, pendingIntent);

        AppWidgetManager mAppWidgetManager = AppWidgetManager.getInstance(context.getApplicationContext());
        ComponentName componentName = new ComponentName(context.getApplicationContext(), WeatherWidgetProvider.class);
        // Tell the AppWidgetManager to perform an update on the current app widget
        mAppWidgetManager.updateAppWidget(componentName, views);
    }

    /**
     * 每删除一次窗口小部件就调用一次
     */
    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        mWebView = null;
    }

    /**
     * 当最后一个该窗口小部件删除时调用该方法
     */
    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        mWebView = null;
    }

    /**
     * 当该窗口小部件第一次添加到桌面时调用该方法
     */
    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        Log.e(TAG, "onEnabled");
        mContext = context;
    }

    class WeatherInterface {
        @JavascriptInterface
        public void onGetBodyHtml(String html) {
            Document doc = Jsoup.parseBodyFragment(html);
            String cityName = doc.select("div#weather-position-address h2").get(0).text();
            String currTemp = doc.select("div.n_wd h1 span").get(0).text();
            String weather = doc.select("div.n_wd h1 em").get(0).text();
            String wind = doc.select("div.n_wd h2 span").get(0).text();
            String pm25 = doc.select("div.n_wd h3 a.aqi span").get(0).text();
            String pm25Description = doc.select("div.n_wd h3 a.aqi b").get(0).text();
            String maxTemp = doc.select("div#maxTemp svg tspan").get(1).text();
            String minTemp = doc.select("div#minTemp svg tspan").get(1).text();
            Weather weatherModel = new Weather();
            weatherModel.setCityName(cityName);
            weatherModel.setCurrTemp(currTemp);
            weatherModel.setWeather(weather);
            weatherModel.setWind(wind);
            weatherModel.setPm25(pm25);
            weatherModel.setPm25Description(pm25Description);
            weatherModel.setMaxTemp(maxTemp);
            weatherModel.setMinTemp(minTemp);
            Log.e(TAG, weatherModel.toString());
            Message msg = mHandler.obtainMessage();
            msg.obj = weatherModel;
            msg.sendToTarget();
        }
    }


    class MyWebViewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            view.loadUrl("javascript:weather.onGetBodyHtml(document.getElementsByTagName(\"body\")[0].innerHTML);");
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return true;
        }
    }
}
