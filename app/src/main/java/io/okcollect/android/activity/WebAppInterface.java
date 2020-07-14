package io.okcollect.android.activity;

import android.content.ContentValues;
import android.provider.Settings;
import android.webkit.JavascriptInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import io.okcollect.android.OkCollect;


class WebAppInterface {
    private static final String TAG = "WebAppInterface";
    private static String appkey;
    OkHeartActivity mContext;
    //private FirebaseFirestore mFirestore;
    private String uniqueId;
    private io.okcollect.android.database.DataProvider dataProvider;
    private String environment, phonenumber;

    /**
     * Instantiate the interface and set the context
     */
    WebAppInterface(OkHeartActivity c, String applicationKey) {
        mContext = c;
        appkey = applicationKey;
        //mFirestore = FirebaseFirestore.getInstance();
        uniqueId = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
        dataProvider = new io.okcollect.android.database.DataProvider(mContext);
        environment = dataProvider.getPropertyValue("environment");
        phonenumber = dataProvider.getPropertyValue("phonenumber");
    }

    /**
     * Show a message from the web page
     */
    @JavascriptInterface
    public void receiveMessage(String results) {
        displayLog("receiveMessage called " + results);


        try {
            final JSONObject jsonObject = new JSONObject(results);

            String message = jsonObject.optString("message");
            JSONObject payload = jsonObject.optJSONObject("payload");
            if (payload != null) {
                displayLog("payload is not null " + payload);
            } else {
                displayLog("payload is null " + payload);
                String backuppayload = jsonObject.optString("payload");
                if (backuppayload != null) {
                    payload = new JSONObject();
                    payload.put("error", backuppayload);
                } else {

                }
            }

            try {

                switch (message) {
                    case "app_state":
                        displayLog("app_state");


                        if (payload != null) {
                            Boolean ready = payload.optBoolean("ready");
                            if (ready != null) {
                                if (ready) {

                                    mContext.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            mContext.startApp();
                                        }
                                    });
                                }
                            } else {

                            }
                        } else {

                        }
                        break;
                    case "location_created":
                        displayLog("location_created");
                        try {
                            Long i = saveAddressToFirestore(payload, "location_created");

                        } catch (Exception e) {
                            displayLog("error saveAddressToFirestore " + e.toString());
                        }
                        try {
                            HashMap<String, String> loans = new HashMap<>();
                            //loans.put("phonenumber",postDataParams.get("phone"));
                            //loans.put("ualId", model.getUalId());
                            HashMap<String, String> parameters = new HashMap<>();
                            parameters.put("eventName", "Android SDK");
                            parameters.put("type", "okHeartResponse");
                            parameters.put("subtype", "location_created");
                            parameters.put("onObject", "okHeartAndroidSDK");
                            parameters.put("view", "webAppInterface");
                            parameters.put("appKey", "" + appkey);
                            //sendEvent(parameters, loans);
                        } catch (Exception e1) {
                            displayLog("error attaching afl to ual " + e1.toString());
                        }
                        try {
                            OkCollect.getCallback().querycomplete(jsonObject);
                        } catch (Exception e) {
                            displayLog("error calling back " + e.toString());
                        }
                        try {
                            OkHeartActivity.setCompletedWell(true);
                            OkHeartActivity.setIsWebInterface(true);
                            mContext.finish();
                        } catch (Exception e) {
                            displayLog("error sending event " + e.toString());
                        } finally {

                        }

                        break;
                    case "location_updated":
                        displayLog("location_updated");
                        try {
                            Long i = saveAddressToFirestore(payload, "location_updated");

                        } catch (Exception e) {
                            displayLog("error saveAddressToFirestore " + e.toString());
                        }

                        try {
                            OkCollect.getCallback().querycomplete(jsonObject);
                        } catch (Exception e) {
                            displayLog("error calling back " + e.toString());
                        }
                        try {
                            OkHeartActivity.setCompletedWell(true);
                            OkHeartActivity.setIsWebInterface(true);
                            mContext.finish();
                        } catch (Exception e) {
                            displayLog("error sending event " + e.toString());
                        } finally {

                        }
                        break;
                    case "location_selected":
                        displayLog("location_selected");
                        try {
                            Long i = saveAddressToFirestore(payload, "location_selected");

                        } catch (Exception e) {
                            displayLog("error saveAddressToFirestore " + e.toString());
                        }
                        try {
                            OkCollect.getCallback().querycomplete(jsonObject);
                        } catch (Exception e) {
                            displayLog("error calling back " + e.toString());
                        }
                        try {
                            OkHeartActivity.setCompletedWell(true);
                            OkHeartActivity.setIsWebInterface(true);
                            mContext.finish();
                        } catch (Exception e) {
                            displayLog("error sending event " + e.toString());
                        } finally {

                        }
                        break;
                    case "fatal_exit":
                        displayLog("fatal_exit");

                        try {
                            jsonObject.put("payload", payload);
                            jsonObject.put("message", "fatal_exit");
                            OkCollect.getCallback().querycomplete(jsonObject);
                        } catch (Exception e) {
                            displayLog("error calling back " + e.toString());

                        }
                        try {
                            OkHeartActivity.setCompletedWell(true);
                            OkHeartActivity.setIsWebInterface(true);
                            mContext.finish();
                        } catch (Exception e) {
                            displayLog("error sending event " + e.toString());
                        } finally {

                        }
                        break;

                    default:
                        displayLog("default");
                        break;

                }
            } catch (Exception e) {
                displayLog("switch error " + e.toString());
            }
        } catch (JSONException e) {
            displayLog("");
        }
    }

    private Long saveAddressToFirestore(JSONObject payload, String action) {

        displayLog("saveAddressToFirestore");

        JSONObject location = payload.optJSONObject("location");
        JSONObject user = payload.optJSONObject("user");
        String firstName = user.optString("firstName");
        String lastName = user.optString("lastName");
        String phone = user.optString("phone");
        String streetName = location.optString("streetName");
        String propertyName = location.optString("propertyName");
        String directions = location.optString("directions");
        String placeId = location.optString("placeId");
        String ualId = location.optString("id");
        String url = location.optString("url");
        String title = location.optString("title");
        String plusCode = location.optString("plusCode");
        String branch = "app_interswitch";
        Double lat = location.optDouble("lat");
        Double lng = location.optDouble("lng");

        ContentValues contentValues = new ContentValues();
        contentValues.put(io.okcollect.android.utilities.Constants.COLUMN_CUSTOMERNAME, firstName);
        contentValues.put(io.okcollect.android.utilities.Constants.COLUMN_PHONECUSTOMER, phone);
        contentValues.put(io.okcollect.android.utilities.Constants.COLUMN_STREETNAME, streetName);
        contentValues.put(io.okcollect.android.utilities.Constants.COLUMN_PROPERTYNAME, propertyName);
        contentValues.put(io.okcollect.android.utilities.Constants.COLUMN_DIRECTION, directions);
        contentValues.put(io.okcollect.android.utilities.Constants.COLUMN_LOCATIONNICKNAME, placeId);
        contentValues.put(io.okcollect.android.utilities.Constants.COLUMN_CLAIMUALID, ualId);
        contentValues.put(io.okcollect.android.utilities.Constants.COLUMN_IMAGEURL, url);
        contentValues.put(io.okcollect.android.utilities.Constants.COLUMN_LOCATIONNAME, title);
        contentValues.put(io.okcollect.android.utilities.Constants.COLUMN_BRANCH, "hq_okhi");
        contentValues.put(io.okcollect.android.utilities.Constants.COLUMN_LAT, lat);
        contentValues.put(io.okcollect.android.utilities.Constants.COLUMN_LNG, lng);
        contentValues.put(io.okcollect.android.utilities.Constants.COLUMN_UNIQUEID, uniqueId);

        Long i = dataProvider.insertAddressList(contentValues);

        return i;


    }


    private void displayLog(String log) {
        ////Log.i(TAG, log);
    }
}

