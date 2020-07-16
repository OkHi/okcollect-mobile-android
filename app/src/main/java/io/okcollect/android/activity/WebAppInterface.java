package io.okcollect.android.activity;

import android.webkit.JavascriptInterface;

import org.json.JSONException;
import org.json.JSONObject;

import io.okcollect.android.OkCollect;


class WebAppInterface {
    private static final String TAG = "WebAppInterface";
    OkHeartActivity mContext;

    WebAppInterface(OkHeartActivity c) {
        mContext = c;
    }
    
    @JavascriptInterface
    public void receiveMessage(String results) {

        try {
            final JSONObject jsonObject = new JSONObject(results);

            String message = jsonObject.optString("message");
            JSONObject payload = jsonObject.optJSONObject("payload");
            if (payload != null) {

            } else {

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
                        try {
                            OkCollect.getCallback().querycomplete(jsonObject);
                        } catch (Exception e) {

                        }
                        try {
                            OkHeartActivity.setCompletedWell(true);
                            OkHeartActivity.setIsWebInterface(true);
                            mContext.finish();
                        } catch (Exception e) {

                        } finally {
                        }

                        break;
                    case "location_updated":
                        try {
                            OkCollect.getCallback().querycomplete(jsonObject);
                        } catch (Exception e) {
                        }
                        try {
                            OkHeartActivity.setCompletedWell(true);
                            OkHeartActivity.setIsWebInterface(true);
                            mContext.finish();
                        } catch (Exception e) {
                        } finally {

                        }
                        break;
                    case "location_selected":
                        try {
                            OkCollect.getCallback().querycomplete(jsonObject);
                        } catch (Exception e) {
                        }
                        try {
                            OkHeartActivity.setCompletedWell(true);
                            OkHeartActivity.setIsWebInterface(true);
                            mContext.finish();
                        } catch (Exception e) {
                        } finally {
                        }
                        break;
                    case "fatal_exit":
                        try {
                            jsonObject.put("payload", payload);
                            jsonObject.put("message", "fatal_exit");
                            OkCollect.getCallback().querycomplete(jsonObject);
                        } catch (Exception e) {
                        }
                        try {
                            OkHeartActivity.setCompletedWell(true);
                            OkHeartActivity.setIsWebInterface(true);
                            mContext.finish();
                        } catch (Exception e) {
                        } finally {

                        }
                        break;

                    default:
                        break;

                }
            } catch (Exception e) {

            }
        } catch (JSONException e) {

        }
    }
}

