package io.okcollect.android.activity;

import android.Manifest;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import io.okcollect.android.OkCollect;
import io.okcollect.android.callback.OkCollectCallback;


public class OkHeartActivity extends AppCompatActivity {

    private static final String TAG = "OkHeartActivity";
    private static WebView myWebView;
    private static JSONObject jsonObject;
    private static Double lat, lng;
    private static Float acc;
    private static String firstname, lastname, phonenumber, apiKey, color, name, logo, appbarcolor;
    private static Boolean appbarvisible, enablestreetview;
    private static OkCollectCallback okCollectCallback;
    private static boolean completedWell, isWebInterface;
    private static String uniqueId;
    private static String verify;
    private static io.okcollect.android.database.DataProvider dataProvider;
    private static String environment;


    private static String convertStreamToString(InputStream is) throws IOException {
        //displayLog("convertStreamToString1");
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
        //displayLog("convertStreamToString2");
        return sb.toString();
    }

    private static String getStringFromFile(String filePath) throws IOException {
        //displayLog("getStringFromFile1");
        File fl = new File(filePath);
        FileInputStream fin = new FileInputStream(fl);
        String ret = convertStreamToString(fin);
        //Make sure you close all streams.
        fin.close();
        //displayLog("getStringFromFile2");
        return ret;
    }

    private static String getFirstname() {
        return firstname;
    }

    private static void setFirstname(String firstname) {
        OkHeartActivity.firstname = firstname;
    }

    private static String getLastname() {
        return lastname;
    }

    private static void setLastname(String lastname) {
        OkHeartActivity.lastname = lastname;
    }

    private static String getPhonenumber() {
        return phonenumber;
    }

    private static void setPhonenumber(String phonenumber) {
        OkHeartActivity.phonenumber = phonenumber;
    }

    private static Double getLat() {
        return lat;
    }

    private static void setLat(Double lat) {
        OkHeartActivity.lat = lat;
    }

    private static Double getLng() {
        return lng;
    }

    private static void setLng(Double lng) {
        OkHeartActivity.lng = lng;
    }

    private static Float getAcc() {
        return acc;
    }

    private static void setAcc(Float acc) {
        OkHeartActivity.acc = acc;
    }

    private static void displayLog(String log) {
       // Log.i(TAG, log);
    }

    private static boolean isCompletedWell() {
        return completedWell;
    }

    public static void setCompletedWell(boolean completedWell) {
        OkHeartActivity.completedWell = completedWell;
    }

    private static boolean isIsWebInterface() {
        return isWebInterface;
    }

    public static void setIsWebInterface(boolean isWebInterface) {
        OkHeartActivity.isWebInterface = isWebInterface;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(io.okcollect.android.R.layout.activity_okheart);
        displayLog("start");
        dataProvider = new io.okcollect.android.database.DataProvider(this);
        environment = dataProvider.getPropertyValue("environment");
        apiKey = dataProvider.getPropertyValue("authtoken");
        displayLog("environment " + environment);

        /*
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(new Intent(this, io.okcollect.android.services.ForegroundService.class));
            } else {
                startService(new Intent(this, io.okcollect.android.services.ForegroundService.class));
            }
        } catch (Exception jse) {
            displayLog("jsonexception jse " + jse.toString());
        }
        */

        completedWell = false;
        isWebInterface = false;
        //checkInternet();
        lat = null;
        lng = null;
        acc = null;
        color = null;
        name = null;
        logo = null;
        verify = "false";


        try {
            Bundle bundle = getIntent().getExtras();
            try {
                firstname = bundle.getString("firstname");
            } catch (Exception e) {
                displayLog("firstname error " + e.toString());
            }

            try {
                lastname = bundle.getString("lastname");
            } catch (Exception e) {
                displayLog("lastname error " + e.toString());
            }
            try {
                phonenumber = bundle.getString("phone");
            } catch (Exception e) {
                displayLog("phonenumber error " + e.toString());
            }
            try {
                uniqueId = bundle.getString("uniqueId");
            } catch (Exception e) {
                displayLog("uniqueId error " + e.toString());
            }
            //apiKey = dataProvider.getPropertyValue("applicationKey");
            displayLog("applicationKey"+apiKey);

            /*
            File filesDirVerify = new File(getFilesDir() + "/verify.txt");
            if (filesDirVerify.exists()) {
                displayLog("filesDirVerify exists");
                try {
                    verify = getStringFromFile(filesDirVerify.getAbsolutePath());
                    displayLog("verify " + verify);
                } catch (Exception e) {
                    // Hmm, the applicationId file was malformed or something. Assume it
                    // doesn't match.
                    displayLog("filesDirVerify error " + e.toString());
                }
            } else {
                displayLog("filesDirVerify does not exist");
            }

            File filesDir = new File(getFilesDir() + "/okcollect.txt");
            if (filesDir.exists()) {
                displayLog("filesdir exists");
                try {
                    apiKey = getStringFromFile(filesDir.getAbsolutePath());
                    displayLog("api key " + apiKey);
                } catch (Exception e) {
                    // Hmm, the applicationId file was malformed or something. Assume it
                    // doesn't match.
                    displayLog("error " + e.toString());
                }
            } else {
                displayLog("filesdir does not exist");
            }
            */

            File filesDirCustom = new File(getFilesDir() + "/custom.txt");
            if (filesDirCustom.exists()) {
                displayLog("custom dir exists");
                try {
                    String customString = getStringFromFile(filesDirCustom.getAbsolutePath());
                    displayLog("custom string " + customString);
                    if (customString != null) {
                        if (customString.length() > 0) {
                            JSONObject jsonObject = new JSONObject(customString);
                            String tempColor = jsonObject.optString("color", "rgb(0, 131, 143)");
                            String tempName = jsonObject.optString("name", "interswitch");
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
                    // Hmm, the applicationId file was malformed or something. Assume it
                    // doesn't match.
                    displayLog("error " + e.toString());
                }
            } else {
                displayLog("custom dir does not exist");
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
        myWebView.addJavascriptInterface(new WebAppInterface(OkHeartActivity.this, apiKey), "Android");
        //myWebView.loadUrl("https://manager-v5.okhi.dev");
        //myWebView.loadUrl("https://7b70b228.ngrok.io");


        if (environment != null) {
            if (environment.length() > 0) {
                if (environment.equalsIgnoreCase("PROD")) {
                    myWebView.loadUrl("https://manager-v5.okhi.io");
                } else if (environment.equalsIgnoreCase("DEVMASTER")) {
                    myWebView.loadUrl("https://dev-manager-v5.okhi.io");
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
            if (okCollectCallback != null) {
                displayLog("okheartcallback is not null");
            } else {
                displayLog("okheartcallback is null");
            }
        } catch (Exception e) {
            displayLog("error calling back " + e.toString());
        }

        try {

            JSONObject identifyjson = new JSONObject();
            identifyjson.put("userId", "8VXRqG8YhN");
            try {
                io.okcollect.android.callback.SegmentIdentifyCallBack segmentIdentifyCallBack = new io.okcollect.android.callback.SegmentIdentifyCallBack() {
                    @Override
                    public void querycomplete(String response, boolean status) {
                        if (status) {
                            displayLog("things went ok with send to omtm identify");

                            try {
                                io.okcollect.android.callback.SegmentTrackCallBack segmentTrackCallBack = new io.okcollect.android.callback.SegmentTrackCallBack() {
                                    @Override
                                    public void querycomplete(String response, boolean status) {
                                        if (status) {
                                            displayLog("things went ok with send to omtm track");
                                        } else {
                                            displayLog("something went wrong with send to omtm track");
                                        }
                                    }
                                };
                                JSONObject eventjson = new JSONObject();
                                eventjson.put("userId", "8VXRqG8YhN");
                                eventjson.put("event", "SDK Initialization");

                                JSONObject trackjson = new JSONObject();
                                trackjson.put("environment", environment);
                                trackjson.put("event", "SDK start");

                                trackjson.put("action", "start");
                                trackjson.put("actionSubtype", "start");
                                trackjson.put("clientProduct", "okHeartAndroidSDK");
                                trackjson.put("clientProductVersion", io.okcollect.android.BuildConfig.VERSION_NAME);
                                trackjson.put("clientKey", apiKey);
                                trackjson.put("appLayer", "client");
                                trackjson.put("onObject", "sdk");
                                trackjson.put("product", "okHeartAndroidSDK");
                                trackjson.put("type", "start");
                                trackjson.put("subtype", "start");
                                trackjson.put("uniqueId", uniqueId);

                                eventjson.put("properties", trackjson);
                                io.okcollect.android.asynctask.SegmentTrackTask segmentTrackTask = new io.okcollect.android.asynctask.SegmentTrackTask(segmentTrackCallBack, eventjson, environment);
                                segmentTrackTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            } catch (JSONException e) {
                                displayLog("track error omtm error " + e.toString());
                            }
                        } else {
                            displayLog("something went wrong with send to omtm identify");
                        }

                    }
                };
                io.okcollect.android.asynctask.SegmentIdentifyTask segmentIdentifyTask = new io.okcollect.android.asynctask.SegmentIdentifyTask(segmentIdentifyCallBack, identifyjson, environment);
                segmentIdentifyTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            } catch (Exception e) {
                displayLog("Error initializing analytics_omtm " + e.toString());
            }
        } catch (Exception jse) {
            displayLog("jsonexception jse " + jse.toString());
        }

    }

    public void startApp() {
        //checkInternet();
        displayLog("startApp ");

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
                    name = "interswitch";
                }
            } else {
                name = "interswitch";
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
                enablestreetview = false;
            }


            displayLog("color " + color + " name " + name + " logo " + logo);
            displayLog("appbarcolor " + appbarcolor + " appbarvisible " + appbarvisible + " enablestreetview " + enablestreetview);

            String tologinwith;
            if ((phonenumber.startsWith("07")) && (phonenumber.length() == 10)) {
                tologinwith = "+2547" + phonenumber.substring(2);
            } else {
                tologinwith = phonenumber;
            }
            /*
            String stuff = "{\n" +
                    "  \"message\": \"select_location\",\n" +
                    "  \"payload\": {\n" +
                    "    \"user\": {\n" +
                    "      \"firstName\": \"" + firstname + "\",\n" +
                    "      \"lastName\": \"" + lastname + "\",\n" +
                    "      \"phone\": \"" + tologinwith + "\"\n" +
                    "    },\n" +
                    "    \"style\": {\n" +
                    "      \"base\": {\n" +
                    "        \"color\": \"" + color + "\",\n" +
                    "        \"name\": \"" + name + "\",\n" +
                    "        \"logo\": \"" + logo + "\"\n" +
                    "      }\n" +
                    "    },\n" +

                    "    \"config\": {\n" +
                    "      \"appBar\": {\n" +
                    "        \"color\": \"" + appbarcolor + "\",\n" +
                    "        \"visible\": " + appbarvisible + "\n" +
                    "      },\n" +
                    "    \"streetView\": " + enablestreetview + "\n" +
                    "    },\n" +

                    "    \"auth\": {\n" +
                    "      \"authToken\": \"" + apiKey + "\"\n" +
                    "    },\n" +
                    "    \"parent\": {\n" +
                    "      \"name\": \"okHeartAndroidSDK\",\n" +
                    "      \"version\": \"" + io.okcollect.android.BuildConfig.VERSION_NAME + "\",\n" +
                    "      \"build\": \"" + io.okcollect.android.BuildConfig.VERSION_CODE + "\",\n" +
                    "      \"namespace\": \"com.develop.okheartandroidsdk.okhi\"\n" +
                    "    }\n" +
                    "    \"context\": {\n" +
                    "      \"platform\": \"android\",\n" +
                    "      \"developer\": \"okhi\",\n" +
                    "      \"library\": {\n" +
                    "           \"name\": \"okcollect-android-sdk\",\n" +
                    "            \"version\": \"2.0.0\"\n" +
                    "       },\n" +
                    "      \"container\": {\n" +
                    "           \"name\": \"Quickteller App\",\n" +
                    "            \"version\": \"2.0.0\"\n" +
                    "       },\n" +
                    "    }\n" +
                    "  }\n" +
                    "}";
            */


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
                    "      \"authToken\": \"" + apiKey + "\"\n" +
                    "      },\n" +
                    "        context: {\n" +
                    "          container: {\n" +
                    "           \"name\": \"Android App\",\n" +
                    "            \"version\": \"2.0.4\"\n" +
                    "          },\n" +
                    "          developer: {\n" +
                    "            name: 'okhi',\n" +
                    "          },\n" +
                    "          library: {\n" +
                    "           \"name\": \"okCollectMobileAndroid\",\n" +
                    "            \"version\": \"2.0.4\"\n" +
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
            displayLog(payload);
            myWebView.evaluateJavascript("javascript:receiveAndroidMessage(" + payload + ")", null);
        } catch (Exception e) {
            displayLog("jsonexception error " + e.toString());
        }
    }

    private void sendGPSLocation(JSONObject jsonObject) {

        myWebView.evaluateJavascript("javascript:receiveAndroidData(" + jsonObject.toString() + ")", null);

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
                    displayLog(jsonObject.toString());
                    okCollectCallback.querycomplete(jsonObject1);
                }

            }

        } catch (Exception e) {
            displayLog("error calling back 1 " + e.toString());
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
        private static final String TAG = "MyWebViewClient";

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // TODO Auto-generated method stub
            super.onPageStarted(view, url, favicon);
            displayLog("onPageStarted");
        }


        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            displayLog("shouldOverrideUrlLoading");
            if (Uri.parse(url).getHost().equals("https://manager-v5.okhi.io")) {
                // This is my website, so do not override; let my WebView load the page

                return false;
            } else return !Uri.parse(url).getHost().equals("https://dev-manager-v5.okhi.io");
            // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
            //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            //startActivity(intent);
        }

        @Override
        public void onPageFinished(WebView view, String urlString) {
            displayLog("onPageFinished loadVariables(newURL) " + urlString);
            /*
            if(newURL!="") {
                myWebView.loadUrl("javascript:loadVariables(" + "\"" + newURL + "\")");
            }
            */
        }

        private void displayLog(String log) {
            Log.i(TAG, log);
        }
    }
}
