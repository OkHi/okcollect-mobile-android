package io.okcollect.android.activity;

import android.content.ContentValues;
import android.location.Location;
import android.provider.Settings;
import android.webkit.JavascriptInterface;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

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

    private void stopPeriodicPing() {

    }

    /**
     * Show a message from the web page
     */
    @JavascriptInterface
    public void receiveMessage(String results) {
        displayLog("receiveMessage called " + results);
        try {
            HashMap<String, String> loans = new HashMap<>();
            //loans.put("phonenumber",postDataParams.get("phone"));
            //loans.put("ualId", model.getUalId());
            HashMap<String, String> parameters = new HashMap<>();
            parameters.put("eventName", "Android SDK");
            parameters.put("type", "okHeartResponse");
            parameters.put("subtype", "results");
            parameters.put("onObject", "okHeartAndroidSDK");
            parameters.put("view", "webAppInterface");
            parameters.put("appKey", "" + appkey);
            sendEvent(parameters, loans);
        } catch (Exception e1) {
            displayLog("error attaching afl to ual " + e1.toString());
        }

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
                                    try {
                                        HashMap<String, String> loans = new HashMap<>();
                                        //loans.put("phonenumber",postDataParams.get("phone"));
                                        //loans.put("ualId", model.getUalId());
                                        HashMap<String, String> parameters = new HashMap<>();
                                        parameters.put("eventName", "Android SDK");
                                        parameters.put("type", "okHeartResponse");
                                        parameters.put("subtype", "app_state");
                                        parameters.put("onObject", "okHeartAndroidSDK");
                                        parameters.put("view", "webAppInterface");
                                        parameters.put("appKey", "" + appkey);
                                        sendEvent(parameters, loans);
                                    } catch (Exception e1) {
                                        displayLog("error attaching afl to ual " + e1.toString());
                                    }
                                    try {
                                        sendEvent(appkey, "app_state");
                                    } catch (Exception e) {
                                        displayLog("error sending event " + e.toString());
                                    }
                                    mContext.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            mContext.startApp();
                                        }
                                    });
                                }
                            } else {
                                try {
                                    HashMap<String, String> loans = new HashMap<>();
                                    //loans.put("phonenumber",postDataParams.get("phone"));
                                    //loans.put("ualId", model.getUalId());
                                    HashMap<String, String> parameters = new HashMap<>();
                                    parameters.put("eventName", "Android SDK");
                                    parameters.put("type", "okHeartResponse");
                                    parameters.put("subtype", "app_state notReady");
                                    parameters.put("onObject", "okHeartAndroidSDK");
                                    parameters.put("view", "webAppInterface");
                                    parameters.put("appKey", "" + appkey);
                                    sendEvent(parameters, loans);
                                } catch (Exception e1) {
                                    displayLog("error attaching afl to ual " + e1.toString());
                                }
                            }
                        } else {
                            try {
                                HashMap<String, String> loans = new HashMap<>();
                                //loans.put("phonenumber",postDataParams.get("phone"));
                                //loans.put("ualId", model.getUalId());
                                HashMap<String, String> parameters = new HashMap<>();
                                parameters.put("eventName", "Android SDK");
                                parameters.put("type", "okHeartResponse");
                                parameters.put("subtype", "app_state noPayload");
                                parameters.put("onObject", "okHeartAndroidSDK");
                                parameters.put("view", "webAppInterface");
                                parameters.put("appKey", "" + appkey);
                                sendEvent(parameters, loans);
                            } catch (Exception e1) {
                                displayLog("error attaching afl to ual " + e1.toString());
                            }
                        }
                        break;
                    case "location_created":
                        displayLog("location_created");
                        try {
                            Long i = saveAddressToFirestore(payload, "location_created");
                            /*
                            startForegroundService();
                            if (i > 0) {
                                displayLog("saveAddressToFirestore " + i);
                                String tempVerify = dataProvider.getPropertyValue("verify");
                                displayLog("verify " + tempVerify);
                                if (tempVerify != null) {
                                    if (tempVerify.length() > 0) {
                                        if (tempVerify.equalsIgnoreCase("true")) {
                                            decideWhatToStart();
                                        } else {
                                            stopPeriodicPing();
                                        }
                                    } else {
                                        stopPeriodicPing();
                                    }
                                } else {
                                    stopPeriodicPing();
                                }
                            }
                            */
                            /*
                            if (i > 0) {
                                //
                                try {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        mContext.startForegroundService(new Intent(mContext, io.okcollect.android.services.ForegroundService.class));
                                    } else {
                                        mContext.startService(new Intent(mContext, io.okcollect.android.services.ForegroundService.class));
                                    }

                                } catch (Exception jse) {
                                    displayLog("jsonexception jse " + jse.toString());
                                }
                            } else {
                                //put an event to capture this issue perhaps
                            }
                            */
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
                            sendEvent(appkey, "location_created");
                        } catch (Exception e) {
                            displayLog("error sending event " + e.toString());
                        } finally {
                            OkHeartActivity.setCompletedWell(true);
                            OkHeartActivity.setIsWebInterface(true);
                            mContext.finish();
                        }

                        break;
                    case "location_updated":
                        displayLog("location_updated");
                        try {
                            Long i = saveAddressToFirestore(payload, "location_updated");
                            /*
                            startForegroundService();
                            if (i > 0) {
                                displayLog("saveAddressToFirestore " + i);
                                String tempVerify = dataProvider.getPropertyValue("verify");
                                displayLog("verify " + tempVerify);
                                if (tempVerify != null) {
                                    if (tempVerify.length() > 0) {
                                        if (tempVerify.equalsIgnoreCase("true")) {
                                            decideWhatToStart();
                                        } else {
                                            stopPeriodicPing();
                                        }
                                    } else {
                                        stopPeriodicPing();
                                    }
                                } else {
                                    stopPeriodicPing();
                                }
                            }
                            */

                            /*
                            if (i > 0) {
                                //
                                try {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        mContext.startForegroundService(new Intent(mContext, io.okcollect.android.services.ForegroundService.class));
                                    } else {
                                        mContext.startService(new Intent(mContext, io.okcollect.android.services.ForegroundService.class));
                                    }

                                } catch (Exception jse) {
                                    displayLog("jsonexception jse " + jse.toString());
                                }
                            } else {
                                //put an event to capture this issue perhaps
                            }
                            */
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
                            parameters.put("subtype", "location_updated");
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
                            sendEvent(appkey, "location_updated");
                        } catch (Exception e) {
                            displayLog("error sending event " + e.toString());
                        } finally {
                            OkHeartActivity.setCompletedWell(true);
                            OkHeartActivity.setIsWebInterface(true);
                            mContext.finish();
                        }
                        break;
                    case "location_selected":
                        displayLog("location_selected");
                        try {
                            Long i = saveAddressToFirestore(payload, "location_selected");
                            /*
                            startForegroundService();
                            if (i > 0) {
                                displayLog("saveAddressToFirestore " + i);
                                String tempVerify = dataProvider.getPropertyValue("verify");
                                displayLog("verify " + tempVerify);
                                if (tempVerify != null) {
                                    if (tempVerify.length() > 0) {
                                        if (tempVerify.equalsIgnoreCase("true")) {
                                            decideWhatToStart();
                                        } else {
                                            stopPeriodicPing();
                                        }
                                    } else {
                                        stopPeriodicPing();
                                    }
                                } else {
                                    stopPeriodicPing();
                                }
                            }
                             */

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
                            parameters.put("subtype", "location_selected");
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
                            sendEvent(appkey, "location_selected");
                        } catch (Exception e) {
                            displayLog("error sending event " + e.toString());
                        } finally {
                            OkHeartActivity.setCompletedWell(true);
                            OkHeartActivity.setIsWebInterface(true);
                            mContext.finish();
                        }
                        break;
                    case "fatal_exit":
                        displayLog("fatal_exit");
                        try {
                            HashMap<String, String> loans = new HashMap<>();
                            //loans.put("phonenumber",postDataParams.get("phone"));
                            //loans.put("ualId", model.getUalId());
                            HashMap<String, String> parameters = new HashMap<>();
                            parameters.put("eventName", "Android SDK");
                            parameters.put("type", "okHeartResponse");
                            parameters.put("subtype", "fatal_exit");
                            parameters.put("onObject", "okHeartAndroidSDK");
                            parameters.put("view", "webAppInterface");
                            parameters.put("appKey", "" + appkey);
                            sendEvent(parameters, loans);
                        } catch (Exception e1) {
                            displayLog("error attaching afl to ual " + e1.toString());
                        }
                        try {
                            /*
                            if(results != null){
                                if(results.length() > 0){
                                    if(results.contains("network")){
                                        JSONObject jsonObject1 = new JSONObject();
                                        jsonObject1.put("code",network_error);
                                        jsonObject1.put("message", "The application was unable to reach OkHi servers");
                                        OkCollect.getCallback().querycomplete(jsonObject1);
                                    }
                                    else if(results.contains("credentials")){
                                        JSONObject jsonObject1 = new JSONObject();
                                        jsonObject1.put("code",unauthorized);
                                        jsonObject1.put("message", "The credentials you have provided are invalid");
                                        OkCollect.getCallback().querycomplete(jsonObject1);
                                    }
                                    else if(results.contains("auth")){
                                        JSONObject jsonObject1 = new JSONObject();
                                        jsonObject1.put("code",unauthorized);
                                        jsonObject1.put("message", "The credentials you have provided are invalid");
                                        OkCollect.getCallback().querycomplete(jsonObject1);
                                    }
                                    else if(results.contains("permission")){
                                        JSONObject jsonObject1 = new JSONObject();
                                        jsonObject1.put("code",permission_denied);
                                        jsonObject1.put("message", "Location permissions hasn't been granted by the user");
                                        OkCollect.getCallback().querycomplete(jsonObject1);
                                    }
                                    else{
                                        JSONObject jsonObject1 = new JSONObject();
                                        jsonObject1.put("code",unknown_error);
                                        jsonObject1.put("message", "An unknown error occurred");
                                        OkCollect.getCallback().querycomplete(jsonObject1);
                                    }
                                }
                                else{
                                    JSONObject jsonObject1 = new JSONObject();
                                    jsonObject1.put("code",unknown_error);
                                    jsonObject1.put("message", "An unknown error occurred");
                                    OkCollect.getCallback().querycomplete(jsonObject1);
                                }
                            }
                            else{
                                JSONObject jsonObject1 = new JSONObject();
                                jsonObject1.put("code",unknown_error);
                                jsonObject1.put("message", "An unknown error occurred");

                            }
                            */
                            jsonObject.put("payload", payload);
                            jsonObject.put("message", "fatal_exit");
                            OkCollect.getCallback().querycomplete(jsonObject);
                        } catch (Exception e) {
                            displayLog("error calling back " + e.toString());

                        }
                        try {
                            sendEvent(appkey, "fatal_exit");
                        } catch (Exception e) {
                            displayLog("error sending event " + e.toString());
                        } finally {
                            OkHeartActivity.setCompletedWell(true);
                            OkHeartActivity.setIsWebInterface(true);
                            mContext.finish();
                        }
                        break;
                        /*
                    case "android_retrieve_gps_location":
                        displayLog("android_retrieve_gps_location");
                        if(OkHeartActivity.getLat() != null){
                            mContext.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try{
                                        JSONObject jsonObject = new JSONObject();
                                        jsonObject.put("message","android_gps_location_found");
                                        JSONObject payload = new JSONObject();
                                        payload.put("lat", OkHeartActivity.getLat());
                                        payload.put("lng", OkHeartActivity.getLng());
                                        payload.put( "accuracy", OkHeartActivity.getAcc());
                                        payload.put( "timestamp", System.currentTimeMillis());
                                        jsonObject.put("payload",payload);
                                        displayLog(jsonObject.toString());
                                        mContext.sendGPSLocation(jsonObject);
                                    }
                                    catch (JSONException e){
                                        displayLog("jsonexception error "+e.toString());
                                    }
                                }
                            });
                        }
                        else{
                            mContext.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try{
                                        JSONObject jsonObject = new JSONObject();
                                        jsonObject.put("message","android_gps_location_not_found");
                                        JSONObject payload = new JSONObject();
                                        payload.put("Error", "location not found");
                                        jsonObject.put("payload",payload);
                                        displayLog(jsonObject.toString());
                                        mContext.sendGPSLocation(jsonObject);
                                    }
                                    catch (JSONException e){
                                        displayLog("jsonexception error "+e.toString());
                                    }
                                }
                            });
                        }

                        break;
                        */
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

        try {
            HashMap<String, String> loans = new HashMap<>();
            loans.put("phonenumber", phone);
            loans.put("uniqueId", uniqueId);
            HashMap<String, String> parameters = new HashMap<>();
            parameters.put("eventName", "Android SDK");
            parameters.put("type", "okHeartResponse");
            parameters.put("subtype", action);
            parameters.put("onObject", "okHeartAndroidSDK");
            parameters.put("view", "webAppInterface");
            parameters.put("appKey", "" + appkey);
            parameters.put("branch", "app_interswitch");
            parameters.put("userAffiliation", "interswitch");
            parameters.put("ualId", ualId);
            try {
                Location location2 = new Location("geohash");
                location2.setLatitude(lat);
                location2.setLongitude(lng);

                io.okcollect.android.utilities.geohash.GeoHash hash = io.okcollect.android.utilities.geohash.GeoHash.fromLocation(location2, 12);
                parameters.put("location", hash.toString());
            } catch (Exception e) {
                displayLog("geomap error " + e.toString());
            }
            parameters.put("latitude", "" + lat);
            parameters.put("longitude", "" + lng);
            //parameters.put("gpsAccuracy", "" + acc);
            sendEvent(parameters, loans);
        } catch (Exception e1) {
            displayLog("error attaching afl to ual " + e1.toString());
        }

        return i;

        /*

        Map<String, Object> data = new HashMap<>();
        data.put("latitude", lat);
        data.put("longitude", lng);
        data.put("timestamp", new Timestamp(new Date()));
        GeoPoint geoPoint = new GeoPoint(lat, lng);
        data.put("geoPoint", geoPoint);
        data.put("firstName", firstName);
        data.put("lastName", lastName);
        data.put("phone", phone);
        data.put("streetName", streetName);
        data.put("propertyName", propertyName);

        data.put("directions", directions);
        data.put("placeId", placeId);
        data.put("ualId", ualId);
        data.put("url", url);
        data.put("title", title);
        data.put("plusCode", plusCode);
        data.put("appKey", appkey);

        Map<String, Object> users = new HashMap<>();
        users.put("firstName", firstName);
        users.put("lastName", lastName);
        users.put("phone", phone);
        users.put("uniqueId", uniqueId);
        users.put("appKey", appkey);

        mFirestore.collection("users").document(uniqueId).set(users, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        displayLog("Document written successfully");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                displayLog("Document write failure " + e.getMessage());
            }
        });

        mFirestore.collection("addresses").document(uniqueId).collection("addresses")
                .document(ualId).set(data, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        displayLog("Document written successfully");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                displayLog("Document write failure " + e.getMessage());
            }
        });
        */

    }

    private void sendEvent(final String appkey, final String action) {


    }


    private void startForegroundService() {
        /*
        String noForeground = dataProvider.getPropertyValue("noforeground");
        String verify = dataProvider.getPropertyValue("verify");
        String applicationKey = dataProvider.getPropertyValue("applicationkey");
        displayLog("foreground " + noForeground);
        displayLog("verify " + verify);
        if (verify != null) {
            if (verify.length() > 0) {
                if (verify.equalsIgnoreCase("true")) {
                    if (noForeground != null) {
                        displayLog("noforeground is not null");
                        if (noForeground.length() > 0) {
                            displayLog("noforeground length is not zero");
                            String brand = Build.MANUFACTURER;
                            if (noForeground.toLowerCase().contains(brand.toLowerCase())) {
                                displayLog("we have brand " + noForeground + " " + brand);
                                try {
                                    HashMap<String, String> loans = new HashMap<>();
                                    loans.put("uniqueId", uniqueId);
                                    loans.put("applicationKey", applicationKey);
                                    loans.put("phonenumber", phonenumber);
                                    HashMap<String, String> parameters = new HashMap<>();
                                    parameters.put("eventName", "Foreground Notification");
                                    parameters.put("subtype", "notShown");
                                    parameters.put("type", "noshow");
                                    parameters.put("onObject", "notification");
                                    parameters.put("view", "webAppInterface");
                                    sendEvent(parameters, loans);
                                } catch (Exception e1) {
                                    displayLog("error attaching afl to ual " + e1.toString());
                                }
                            } else {
                                displayLog("we do not have brand " + noForeground + " " + brand);
                                try {
                                    HashMap<String, String> loans = new HashMap<>();
                                    loans.put("uniqueId", uniqueId);
                                    loans.put("applicationKey", applicationKey);
                                    loans.put("phonenumber", phonenumber);
                                    HashMap<String, String> parameters = new HashMap<>();
                                    parameters.put("eventName", "Foreground Notification");
                                    parameters.put("subtype", "shown");
                                    parameters.put("type", "show");
                                    parameters.put("onObject", "notification");
                                    parameters.put("view", "webAppInterface");
                                    sendEvent(parameters, loans);
                                } catch (Exception e1) {
                                    displayLog("error attaching afl to ual " + e1.toString());
                                }
                                try {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        mContext.startForegroundService(new Intent(mContext, io.okcollect.android.services.LocationService.class));
                                    } else {
                                        mContext.startService(new Intent(mContext, io.okcollect.android.services.LocationService.class));
                                    }

                                } catch (Exception jse) {
                                    displayLog("jsonexception jse " + jse.toString());
                                }
                            }
                        } else {
                            displayLog("noforeground length is zero");
                            try {
                                HashMap<String, String> loans = new HashMap<>();
                                loans.put("uniqueId", uniqueId);
                                loans.put("applicationKey", applicationKey);
                                loans.put("phonenumber", phonenumber);
                                HashMap<String, String> parameters = new HashMap<>();
                                parameters.put("eventName", "Foreground Notification");
                                parameters.put("subtype", "shown");
                                parameters.put("type", "show");
                                parameters.put("onObject", "notification");
                                parameters.put("view", "webAppInterface");
                                sendEvent(parameters, loans);
                            } catch (Exception e1) {
                                displayLog("error attaching afl to ual " + e1.toString());
                            }
                            try {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    mContext.startForegroundService(new Intent(mContext, io.okcollect.android.services.LocationService.class));
                                } else {
                                    mContext.startService(new Intent(mContext, io.okcollect.android.services.LocationService.class));
                                }

                            } catch (Exception jse) {
                                displayLog("jsonexception jse " + jse.toString());
                            }
                        }
                    } else {
                        displayLog("noforeground is null");
                        try {
                            HashMap<String, String> loans = new HashMap<>();
                            loans.put("uniqueId", uniqueId);
                            loans.put("applicationKey", applicationKey);
                            loans.put("phonenumber", phonenumber);
                            HashMap<String, String> parameters = new HashMap<>();
                            parameters.put("eventName", "Foreground Notification");
                            parameters.put("subtype", "shown");
                            parameters.put("type", "show");
                            parameters.put("onObject", "notification");
                            parameters.put("view", "webAppInterface");
                            sendEvent(parameters, loans);
                        } catch (Exception e1) {
                            displayLog("error attaching afl to ual " + e1.toString());
                        }
                        try {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                mContext.startForegroundService(new Intent(mContext, io.okcollect.android.services.LocationService.class));
                            } else {
                                mContext.startService(new Intent(mContext, io.okcollect.android.services.LocationService.class));
                            }

                        } catch (Exception jse) {
                            displayLog("jsonexception jse " + jse.toString());
                        }
                    }
                }
            }
        }
*/

    }


    private void decideWhatToStart() {
        /*
        List<io.okcollect.android.datamodel.AddressItem> addressItemList = dataProvider.getAllAddressList();
        displayLog("addressItemList " + addressItemList.size());
        if (addressItemList.size() > 0) {
            String tempKill = dataProvider.getPropertyValue("kill_switch");
            if (tempKill != null) {
                if (tempKill.length() > 0) {
                    if (tempKill.equalsIgnoreCase("true")) {
                        String tempResume_ping_frequency = dataProvider.getPropertyValue("resume_ping_frequency");
                        if (tempResume_ping_frequency != null) {
                            if (tempResume_ping_frequency.length() > 0) {
                                Integer pingTime = Integer.parseInt(tempResume_ping_frequency);
                                startReplacePeriodicPing(pingTime, uniqueId);
                            } else {
                                startReplacePeriodicPing(360000000, uniqueId);
                            }
                        } else {
                            startReplacePeriodicPing(360000000, uniqueId);
                        }
                    } else {
                        String tempPing_frequency = dataProvider.getPropertyValue("ping_frequency");
                        if (tempPing_frequency != null) {
                            if (tempPing_frequency.length() > 0) {
                                Integer pingTime = Integer.parseInt(tempPing_frequency);
                                startKeepPeriodicPing(pingTime, uniqueId);
                            } else {
                                startKeepPeriodicPing(3600000, uniqueId);
                            }
                        } else {
                            startKeepPeriodicPing(3600000, uniqueId);
                        }
                    }
                } else {
                    String tempPing_frequency = dataProvider.getPropertyValue("ping_frequency");
                    if (tempPing_frequency != null) {
                        if (tempPing_frequency.length() > 0) {
                            Integer pingTime = Integer.parseInt(tempPing_frequency);
                            startKeepPeriodicPing(pingTime, uniqueId);
                        } else {
                            startKeepPeriodicPing(3600000, uniqueId);
                        }
                    } else {
                        startKeepPeriodicPing(3600000, uniqueId);
                    }
                }
            } else {
                String tempPing_frequency = dataProvider.getPropertyValue("ping_frequency");
                if (tempPing_frequency != null) {
                    if (tempPing_frequency.length() > 0) {
                        Integer pingTime = Integer.parseInt(tempPing_frequency);
                        startKeepPeriodicPing(pingTime, uniqueId);
                    } else {
                        startKeepPeriodicPing(3600000, uniqueId);
                    }
                } else {
                    startKeepPeriodicPing(3600000, uniqueId);
                }
            }
        } else {
            stopPeriodicPing();
        }
        */
    }

    private void startKeepPeriodicPing(Integer pingTime, String uniqueId) {

        displayLog("workmanager startKeepPeriodicPing");
        try {
            HashMap<String, String> loans = new HashMap<>();
            loans.put("uniqueId", uniqueId);
            loans.put("phonenumber", phonenumber);
            HashMap<String, String> parameters = new HashMap<>();
            parameters.put("eventName", "Data Collection Service");
            parameters.put("subtype", "startKeepPeriodicPing");
            parameters.put("type", "doWork");
            parameters.put("onObject", "app");
            parameters.put("view", "webAppInterface");
            sendEvent(parameters, loans);
        } catch (Exception e1) {
            displayLog("error attaching afl to ual " + e1.toString());
        }


    }

    private void startReplacePeriodicPing(Integer pingTime, String uniqueId) {
        displayLog("workmanager startReplacePeriodicPing");
        try {
            HashMap<String, String> loans = new HashMap<>();
            loans.put("uniqueId", uniqueId);
            loans.put("phonenumber", phonenumber);
            HashMap<String, String> parameters = new HashMap<>();
            parameters.put("eventName", "Data Collection Service");
            parameters.put("subtype", "startReplacePeriodicPing");
            parameters.put("type", "doWork");
            parameters.put("onObject", "app");
            parameters.put("view", "webAppInterface");
            sendEvent(parameters, loans);
        } catch (Exception e1) {
            displayLog("error attaching afl to ual " + e1.toString());
        }

    }

    private void sendEvent(HashMap<String, String> parameters, HashMap<String, String> loans) {

    }

    private void displayLog(String log) {
        //Log.i(TAG, log);
    }
}

