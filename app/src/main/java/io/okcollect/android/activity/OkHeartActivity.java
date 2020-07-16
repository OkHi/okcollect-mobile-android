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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import io.okcollect.android.BuildConfig;
import io.okcollect.android.OkCollect;
import io.okcollect.android.callback.OkCollectCallback;


public class OkHeartActivity extends AppCompatActivity {

    private static final String TAG = "OkHeartActivity";
    private static WebView myWebView;
    private static String firstname, lastname, phonenumber, clientKey, color, name, logo, appbarcolor;
    private static Boolean appbarvisible, enablestreetview;
    private static OkCollectCallback okCollectCallback;
    private static boolean completedWell, isWebInterface;
    private static io.okcollect.android.database.DataProvider dataProvider;
    private static String environment;


    private static String convertStreamToString(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        Boolean firstLine = true;
        while ((line = reader.readLine()) != null) {
            if (firstLine) {
                sb.append(line);
                firstLine = false;
            } else {
                sb.append("\n").append(line);
            }
        }
        reader.close();
        return sb.toString();
    }

    private static String getStringFromFile(String filePath) throws IOException {
        File fl = new File(filePath);
        FileInputStream fin = new FileInputStream(fl);
        String ret = convertStreamToString(fin);
        fin.close();
        return ret;
    }

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

        color = null;
        name = null;
        logo = null;
        try {
            Bundle bundle = getIntent().getExtras();
            try {
                firstname = bundle.getString("firstname");
            } catch (Exception e) {
            }

            try {
                lastname = bundle.getString("lastname");
            } catch (Exception e) {
            }
            try {
                phonenumber = bundle.getString("phone");
            } catch (Exception e) {
            }

            File filesDirCustom = new File(getFilesDir() + "/custom.txt");
            if (filesDirCustom.exists()) {
                try {
                    String customString = getStringFromFile(filesDirCustom.getAbsolutePath());
                    if (customString != null) {
                        if (customString.length() > 0) {
                            JSONObject jsonObject = new JSONObject(customString);
                            String tempColor = jsonObject.optString("color", "rgb(0, 131, 143)");
                            String tempName = jsonObject.optString("name", "OKHI");
                            String tempLogo = jsonObject.optString("logo", "https://cdn.okhi.co/okhi-logo-white.svg");
                            String tempappbarcolor = jsonObject.optString("appbarcolor", "#f0f0f0");
                            Boolean tempappbarvisible = jsonObject.optBoolean("appbarvisibility", false);
                            Boolean tempstreetview = jsonObject.optBoolean("enablestreetview", false);
                            if (tempColor != null) {
                                if (tempColor.length() > 0) {
                                    color = tempColor;
                                }
                            }
                            if (tempName != null) {
                                if (tempName.length() > 0) {
                                    name = tempName;
                                }
                            }
                            if (tempLogo != null) {
                                if (tempLogo.length() > 0) {
                                    logo = tempLogo;
                                }
                            }

                            if (tempappbarcolor != null) {
                                if (tempappbarcolor.length() > 0) {
                                    appbarcolor = tempappbarcolor;
                                }
                            }
                            if (tempappbarvisible != null) {
                                appbarvisible = tempappbarvisible;
                            }
                            if (tempstreetview != null) {
                                enablestreetview = tempstreetview;
                            }

                        }
                    }
                } catch (Exception e) {
                }
            }

        } catch (Exception e) {

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
        }

    }

    public void startApp() {

        try {
            if (color != null) {
                if (color.length() > 0) {
                } else {
                    color = "rgb(0, 131, 143)";
                }
            } else {
                color = "rgb(0, 131, 143)";
            }
            if (name != null) {
                if (name.length() > 0) {

                } else {
                    name = "OKHI";
                }
            } else {
                name = "OKHI";
            }
            if (logo != null) {
                if (logo.length() > 0) {

                } else {
                    logo = "https://cdn.okhi.co/okhi-logo-white.svg";
                }
            } else {
                logo = "https://cdn.okhi.co/okhi-logo-white.svg";
            }

            if (appbarcolor != null) {
                if (appbarcolor.length() > 0) {
                } else {
                    appbarcolor = "#f0f0f0";
                }
            } else {
                appbarcolor = "#f0f0f0";
            }
            if (appbarvisible != null) {

            } else {
                appbarvisible = false;
            }
            if (enablestreetview != null) {
            } else {
                enablestreetview = true;
            }
            String tologinwith;
            if ((phonenumber.startsWith("07")) && (phonenumber.length() == 10)) {
                tologinwith = "+2547" + phonenumber.substring(2);
            } else {
                tologinwith = phonenumber;
            }

            String payload = "{\n" +
                    "      message: 'select_location',\n" +
                    "      payload: {\n" +
                    "        style: {\n" +
                    "          base: {\n" +
                    "            \"color\": \"" + color + "\",\n" +
                    "            \"name\": \"" + name + "\",\n" +
                    "            \"logo\": \"" + logo + "\"\n" +
                    "          },\n" +
                    "        },\n" +
                    "        user: {\n" +
                    "      \"firstName\": \"" + firstname + "\",\n" +
                    "      \"lastName\": \"" + lastname + "\",\n" +
                    "      \"phone\": \"" + tologinwith + "\"\n" +
                    "        },\n" +
                    "        auth: {\n" +
                    "      \"authToken\": \"" + clientKey + "\"\n" +
                    "      },\n" +
                    "        context: {\n" +
                    "          container: {\n" +
                    "           \"name\": \"Android App\",\n" +
                    "            \"version\": \"" + BuildConfig.VERSION_NAME + "\"\n" +
                    "          },\n" +
                    "          developer: {\n" +
                    "            name: 'okhi',\n" +
                    "          },\n" +
                    "          library: {\n" +
                    "           \"name\": \"okCollectMobileAndroid\",\n" +
                    "          \"version\": \"" + BuildConfig.VERSION_NAME + "\"\n" +
                    "          },\n" +
                    "          platform: {\n" +
                    "            name: 'mobile',\n" +
                    "          },\n" +
                    "        },\n" +
                    "        config: {\n" +
                    "      \"streetView\": \"" + enablestreetview + "\",\n" +
                    "          appBar: {\n" +
                    "           \"color\": \"" + appbarcolor + "\",\n" +
                    "           \"visible\": " + appbarvisible + "\n" +
                    "          },\n" +
                    "        },\n" +
                    "      },\n" +
                    "    }";

            Log.i("OkHeartActivity", payload.toString());
            myWebView.evaluateJavascript("javascript:receiveAndroidMessage(" + payload + ")", null);
        } catch (Exception e) {

        }
    }

    @Override
    protected void onDestroy() {

        try {

            if (completedWell) {

            } else {
                if (isWebInterface) {

                } else {
                    final JSONObject jsonObject1 = new JSONObject();
                    jsonObject1.put("message", "fatal_exit");
                    JSONObject payload1 = new JSONObject();
                    payload1.put("Error", "Address creation did not complete");
                    jsonObject1.put("payload", payload1);
                    okCollectCallback.querycomplete(jsonObject1);
                }

            }

        } catch (Exception e) {
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
            /*
            if (Uri.parse(url).getHost().equals("https://manager-v5.okhi.io")) {
               return false;
            } else return !Uri.parse(url).getHost().equals("https://manager-v5.okhi.io");
            */
        }

        @Override
        public void onPageFinished(WebView view, String urlString) {

        }
    }
}
