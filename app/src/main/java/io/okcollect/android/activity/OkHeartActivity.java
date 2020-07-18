package io.okcollect.android.activity;

import android.Manifest;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import org.json.JSONObject;

import io.okcollect.android.BuildConfig;
import io.okcollect.android.OkCollect;
import io.okcollect.android.callback.OkCollectCallback;


public class OkHeartActivity extends AppCompatActivity {

    private static WebView myWebView;
    private static String firstname, lastname, phonenumber, clientKey,
            organisationName, appThemeColor, appLogo, appBarColor;
    private static Boolean appBarVisibility, enableStreetView;
    private static OkCollectCallback okCollectCallback;
    private static boolean completedWell, isWebInterface;
    private static io.okcollect.android.database.DataProvider dataProvider;
    private static String environment;

    public static void setCompletedWell(boolean completedWell) {
        OkHeartActivity.completedWell = completedWell;
    }

    public static void setIsWebInterface(boolean isWebInterface) {
        OkHeartActivity.isWebInterface = isWebInterface;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(io.okcollect.android.R.layout.activity_okheart);
        dataProvider = new io.okcollect.android.database.DataProvider(this);
        environment = dataProvider.getPropertyValue("environment");
        clientKey = dataProvider.getPropertyValue("authtoken");

        completedWell = false;
        isWebInterface = false;

        try {
            Bundle bundle = getIntent().getExtras();
            try {
                firstname = bundle.getString("firstname");
            } catch (Exception e) {
                //Log.i("OkHeartActivity", "get bundle parameter error "+e.toString());
            }

            try {
                lastname = bundle.getString("lastname");
            } catch (Exception e) {
                //Log.i("OkHeartActivity", "get bundle parameter error "+e.toString());
            }
            try {
                phonenumber = bundle.getString("phone");
            } catch (Exception e) {
                //Log.i("OkHeartActivity", "get bundle parameter error "+e.toString());
            }
            try {
                appThemeColor = bundle.getString("appThemeColor");
            } catch (Exception e) {
                //Log.i("OkHeartActivity", "get bundle parameter error "+e.toString());
            }
            try {
                appBarVisibility = bundle.getBoolean("appBarVisibility");
            } catch (Exception e) {
                //Log.i("OkHeartActivity", "get bundle parameter error "+e.toString());
            }
            try {
                appBarColor = bundle.getString("appBarColor");
            } catch (Exception e) {
                //Log.i("OkHeartActivity", "get bundle parameter error "+e.toString());
            }
            try {
                appLogo = bundle.getString("appLogo");
            } catch (Exception e) {
                //Log.i("OkHeartActivity", "get bundle parameter error "+e.toString());
            }
            try {
                organisationName = bundle.getString("organisationName");
            } catch (Exception e) {
                //Log.i("OkHeartActivity", "get bundle parameter error "+e.toString());
            }
            try {
                enableStreetView = bundle.getBoolean("enableStreetView");
            } catch (Exception e) {
                //Log.i("OkHeartActivity", "get bundle parameter error "+e.toString());
            }

        } catch (Exception e) {
            //Log.i("OkHeartActivity", "get bundle parameter error "+e.toString());
        }


        myWebView = OkHeartActivity.this.findViewById(io.okcollect.android.R.id.webview);
        myWebView.setWebViewClient(new MyWebViewClient());

        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        }, 0);

        WebSettings webSettings = myWebView.getSettings();
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setGeolocationEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setDomStorageEnabled(true);
        myWebView.addJavascriptInterface(new WebAppInterface(OkHeartActivity.this), "Android");
        if (environment != null) {
            if (environment.length() > 0) {
                if (environment.equalsIgnoreCase("PROD")) {
                    myWebView.loadUrl("https://manager-v5.okhi.io");
                } else if (environment.equalsIgnoreCase("SANDBOX")) {
                    myWebView.loadUrl("https://sandbox-manager-v5.okhi.io");
                } else {
                    myWebView.loadUrl("https://manager-v5.okhi.io");
                }
            } else {
                myWebView.loadUrl("https://manager-v5.okhi.io");
            }
        } else {
            myWebView.loadUrl("https://manager-v5.okhi.io");
        }
        myWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
            }
        });
        try {
            okCollectCallback = OkCollect.getCallback();
        } catch (Exception e) {
            //Log.i("OkHeartActivity", "get bundle parameter error "+e.toString());
        }
    }

    public void startApp() {
        String authorization_token = dataProvider.getPropertyValue("authorization_token");
        try {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("message", "select_location");
                JSONObject payload1 = new JSONObject();
                JSONObject style = new JSONObject();
                JSONObject base = new JSONObject();
                if (appThemeColor != null) {
                    if (appThemeColor.length() > 0) {
                        base.put("color", appThemeColor);
                    }
                }
                if (organisationName != null) {
                    if (organisationName.length() > 0) {
                        base.put("name", organisationName);
                    }
                }
                if (appLogo != null) {
                    if (appLogo.length() > 0) {
                        base.put("logo", appLogo);
                    }
                }
                style.put("base", base);
                payload1.put("style", style);

                JSONObject user = new JSONObject();
                user.put("firstName", firstname);
                user.put("lastName", lastname);
                user.put("phone", phonenumber);
                payload1.put("user", user);

                JSONObject auth = new JSONObject();
                auth.put("authToken", authorization_token);
                payload1.put("auth", auth);

                JSONObject context = new JSONObject();
                JSONObject container = new JSONObject();
                container.put("name", "okCollectMobileAndroid");
                container.put("version", BuildConfig.VERSION_NAME);
                context.put("container", container);

                JSONObject developer = new JSONObject();
                developer.put("name", "okhi");
                context.put("developer", developer);

                JSONObject library = new JSONObject();
                library.put("name", "okCollectMobileAndroid");
                library.put("version", BuildConfig.VERSION_NAME);
                context.put("library", library);

                JSONObject platform = new JSONObject();
                platform.put("name", "mobile");
                context.put("platform", platform);
                payload1.put("context", context);

                JSONObject config = new JSONObject();
                if (enableStreetView != null) {
                    config.put("streetView", enableStreetView);
                }
                JSONObject appBar = new JSONObject();
                if (appBarColor != null) {
                    if (appBarColor.length() > 0) {
                        appBar.put("color", appBarColor);
                    }
                }
                if (appBarVisibility != null) {
                    appBar.put("visible", appBarVisibility);
                }
                if (appBar != null) {
                    config.put("appBar", appBar);
                }
                payload1.put("config", config);
                jsonObject.put("payload", payload1);
                myWebView.evaluateJavascript("javascript:receiveAndroidMessage(" + jsonObject.toString().replace("\\", "") + ")", null);
            } catch (Exception e) {
                //Log.i("OkHeartActivity", "get bundle parameter error "+e.toString());
            }
        } catch (Exception e) {
            //Log.i("OkHeartActivity", "get bundle parameter error "+e.toString());
        }
    }

    @Override
    protected void onDestroy() {

        try {
            if (!(completedWell)) {
                if (!(isWebInterface)) {
                    final JSONObject jsonObject1 = new JSONObject();
                    jsonObject1.put("message", "fatal_exit");
                    JSONObject payload1 = new JSONObject();
                    payload1.put("Error", "Address creation did not complete");
                    jsonObject1.put("payload", payload1);
                    okCollectCallback.querycomplete(jsonObject1);
                }
            }
        } catch (Exception e) {
            //Log.i("OkHeartActivity", "get bundle parameter error "+e.toString());
        }
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && myWebView.canGoBack()) {
            myWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private class MyWebViewClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // TODO Auto-generated method stub
            super.onPageStarted(view, url, favicon);
        }
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return false;
        }

        @Override
        public void onPageFinished(WebView view, String urlString) {
        }
    }
}
