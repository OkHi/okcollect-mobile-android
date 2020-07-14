package io.okcollect.android;

import android.Manifest;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import io.okcollect.android.callback.OkCollectCallback;


public final class OkCollect extends ContentProvider {

    private static final String TAG = "OkCollect";
    protected static io.okcollect.android.database.DataProvider dataProvider;
    private static String firstname, lastname, phonenumber, requestSource;
    private static Context mContext;
    private static OkCollectCallback callback;

    public OkCollect() {
    }

    public static void initialize(@NonNull final String clientKey, @NonNull final String branchId, @NonNull final String environment) throws RuntimeException {

        displayLog("initialize");
        //dataProvider.insertStuff("enableverify", ""+verify);

        if (clientKey != null) {
            if (clientKey.length() > 0) {
                startInitialization(clientKey, branchId, environment, false);

            } else {
                throw new RuntimeException("Initialization error", new Throwable("Confirm your application key is correct"));
            }
        } else {
            throw new RuntimeException("Initialization error", new Throwable("Confirm your application key is not null"));
        }

    }


    public static void displayClient(@NonNull OkCollectCallback okCollectCallback, @NonNull JSONObject userObject) throws RuntimeException {

        displayLog("display client " + userObject.toString());

        if (userObject != null) {
            if (userObject.length() > 0) {
                if (okCollectCallback != null) {

                    if (checkPermission()) {
                        startActivity(okCollectCallback, userObject);
                    } else {
                        String cause = checkPermissionCause();
                        if ((cause.equalsIgnoreCase("Manifest.permission.ACCESS_FINE_LOCATION granted")) ||
                                (cause.equalsIgnoreCase("Manifest.permission.ACCESS_BACKGROUND_LOCATION granted"))) {
                            startActivity(okCollectCallback, userObject);
                        } else {
                            String verify = "false";
                            File filesDir = new File(mContext.getFilesDir() + "/verify.txt");
                            if (filesDir.exists()) {
                                displayLog("filesdir exists");
                                try {
                                    verify = getStringFromFile(filesDir.getAbsolutePath());
                                    displayLog("verify " + verify);
                                } catch (Exception e) {
                                    // Hmm, the applicationId file was malformed or something. Assume it
                                    // doesn't match.
                                    displayLog("error " + e.toString());
                                }
                            } else {
                                displayLog("filesdir does not exist");
                            }
                            if (verify.equalsIgnoreCase("true")) {
                                try {
                                    JSONObject responseJson = new JSONObject();
                                    responseJson.put("message", "fatal_exit");
                                    JSONObject payloadJson = new JSONObject();
                                    payloadJson.put("errorCode", -1);
                                    payloadJson.put("error", "Location permission not granted");
                                    payloadJson.put("message", cause);
                                    responseJson.put("payload", payloadJson);
                                    displayLog(responseJson.toString());
                                    okCollectCallback.querycomplete(responseJson);
                                } catch (JSONException jse) {

                                }
                            } else {
                                startActivity(okCollectCallback, userObject);
                            }
                        }
                    }
                } else {
                    throw new RuntimeException("DisplayClient error", new Throwable("Confirm OkCollectCallback is not null"));
                }
            } else {
                throw new RuntimeException("DisplayClient error", new Throwable("Confirm your JSONObject is not null"));
            }
        } else {
            throw new RuntimeException("DisplayClient error", new Throwable("Confirm your JSONObject is not null"));
        }


    }

    private static void startInitialization(final String applicationKey, final String branchid, final String environment, final Boolean verify) {
        displayLog("workmanager startInitialization " + verify);

        try {
            dataProvider.insertStuff("branchid", branchid);
            dataProvider.insertStuff("environment", environment);
        } catch (Exception io) {

        } finally {

        }
        try {
            writeToFile(applicationKey);
        } catch (Exception io) {

        } finally {

        }
        dataProvider.insertStuff("verify", "" + verify);
        dataProvider.insertStuff("applicationKey", applicationKey);

    }

    public static void customize(@NonNull String appThemeColor, @NonNull String organisationName, @NonNull String appLogo,
                                 @NonNull String appBarColor, @NonNull Boolean appBarVisibility,
                                 @NonNull Boolean enableStreetView) {

        try {
            displayLog("okhi customized");
            JSONObject jsonObject = new JSONObject();
            if (appThemeColor != null) {
                if (appThemeColor.length() > 0) {
                    jsonObject.put("color", appThemeColor);
                }
            }
            if (organisationName != null) {
                if (organisationName.length() > 0) {
                    jsonObject.put("name", organisationName);
                }
            }

            if (appLogo != null) {
                if (appLogo.length() > 0) {
                    jsonObject.put("logo", appLogo);
                }
            }
            if (appBarColor != null) {
                if (appBarColor.length() > 0) {
                    jsonObject.put("appbarcolor", appBarColor);
                }
            }
            if (appBarVisibility != null) {
                jsonObject.put("appbarvisibility", appBarVisibility);
            }

            if (enableStreetView != null) {
                jsonObject.put("enablestreetview", enableStreetView);
            }
            String customString = jsonObject.toString();
            //displayLog("logo "+jsonObject.get("logo"));
            //String testString = "{\"color\":\"" + appBarColor + "\", \"name\": \"" + organisationName + "\",\"logo\": \"" + appLogo + "\"}";
            displayLog("custom string " + customString);
            writeToFileCustomize(customString);

        } catch (Exception io) {

        } finally {

        }
    }

    private static void startActivity(@NonNull final OkCollectCallback okCollectCallback, @NonNull final JSONObject jsonObject) {
        displayLog("startActivity");
        callback = okCollectCallback;
        firstname = jsonObject.optString("firstName");
        lastname = jsonObject.optString("lastName");
        phonenumber = jsonObject.optString("phone");
        requestSource = "create";

        dataProvider.insertStuff("phonenumber", phonenumber);
        dataProvider.insertStuff("requestSource", requestSource);


        try {

            io.okcollect.android.callback.AuthtokenCallback authtokenCallback = new io.okcollect.android.callback.AuthtokenCallback() {
                @Override
                public void querycomplete(String response, boolean success) {
                    if (success) {
                        displayLog("success response " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String token = jsonObject.optString("authorization_token");
                            displayLog("token " + token);
                            dataProvider.insertStuff("authtoken", token);

                            Intent intent = new Intent(mContext, io.okcollect.android.activity.OkHeartActivity.class);
                            intent.putExtra("firstname", firstname);
                            intent.putExtra("lastname", lastname);
                            intent.putExtra("phone", phonenumber);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            displayLog("here");
                            mContext.startActivity(intent);

                        } catch (Exception e) {
                            displayLog("error " + e.toString());
                        }
                    } else {
                        displayLog("failed response " + response);
                        try {
                            JSONObject jsonObject2 = new JSONObject();
                            jsonObject2.put("error", "The credentials you have provided are invalid");
                            JSONObject jsonObject1 = new JSONObject();
                            jsonObject1.put("message", "unauthorized");
                            jsonObject1.put("payload", jsonObject2);
                            okCollectCallback.querycomplete(jsonObject1);
                        } catch (Exception e) {

                        }
                    }
                }
            };

            String branchid = dataProvider.getPropertyValue("branchid");
            String applicationKey = dataProvider.getPropertyValue("applicationKey");
            String environment = dataProvider.getPropertyValue("environment");

            displayLog("branchid " + branchid + " clientkey " + applicationKey);

            String tologinwith;
            if ((phonenumber.startsWith("07")) && (phonenumber.length() == 10)) {
                tologinwith = "+2547" + phonenumber.substring(2);
            } else {
                tologinwith = phonenumber;
            }

            io.okcollect.android.asynctask.AnonymoussigninTask anonymoussigninTask =
                    new io.okcollect.android.asynctask.AnonymoussigninTask(mContext, authtokenCallback,
                            branchid, applicationKey, "verify", tologinwith, environment);
            anonymoussigninTask.execute();


        } catch (Exception e) {
            displayLog("error calling receiveActivity activity " + e.toString());
        }

    }

    private static String checkPermissionCause() {

        String environment = dataProvider.getPropertyValue("environment");

        String permission;

        boolean permissionAccessFineLocationApproved =
                ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED;

        if (permissionAccessFineLocationApproved) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                boolean backgroundLocationPermissionApproved =
                        ActivityCompat.checkSelfPermission(mContext,
                                Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                                == PackageManager.PERMISSION_GRANTED;

                if (backgroundLocationPermissionApproved) {
                    // App can access location both in the foreground and in the background.
                    // Start your service that doesn't have a foreground service type
                    // defined.

                    permission = "Manifest.permission.ACCESS_BACKGROUND_LOCATION granted";

                } else {
                    // App can only access location in the foreground. Display a dialog
                    // warning the user that your app must have all-the-time access to
                    // location in order to function properly. Then, request background
                    // location.

                    permission = "Manifest.permission.ACCESS_BACKGROUND_LOCATION not granted";
                }
            } else {
                permission = "Manifest.permission.ACCESS_FINE_LOCATION granted";
            }
        } else {
            // App doesn't have access to the device's location at all. Make full request
            // for permission.

            permission = "Manifest.permission.ACCESS_FINE_LOCATION not granted";
        }
        return permission;
    }

    public static boolean checkPermission() {

        Boolean permission;

        boolean permissionAccessFineLocationApproved =
                ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED;

        if (permissionAccessFineLocationApproved) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                boolean backgroundLocationPermissionApproved =
                        ActivityCompat.checkSelfPermission(mContext,
                                Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                                == PackageManager.PERMISSION_GRANTED;

                if (backgroundLocationPermissionApproved) {
                    // App can access location both in the foreground and in the background.
                    // Start your service that doesn't have a foreground service type
                    // defined.

                    permission = true;

                } else {
                    // App can only access location in the foreground. Display a dialog
                    // warning the user that your app must have all-the-time access to
                    // location in order to function properly. Then, request background
                    // location.

                    permission = false;
                }
            } else {
                permission = true;
            }
        } else {
            // App doesn't have access to the device's location at all. Make full request
            // for permission.

            permission = false;
        }
        return permission;
    }

    private static void displayLog(String log) {
        ////Log.i(TAG, log);
    }

    private static void writeToFile(String customString) {
        try {
            File path = mContext.getFilesDir();
            File file = new File(path, "okcollect.txt");
            if (!file.exists()) {
                FileOutputStream stream = new FileOutputStream(file);
                try {

                    stream.write(customString.getBytes());
                } catch (Exception e) {
                    displayLog("filestream error " + e.toString());
                } finally {
                    stream.close();
                }
            } else {
                file.delete();
                FileOutputStream stream = new FileOutputStream(file);
                try {

                    stream.write(customString.getBytes());
                } catch (Exception e) {
                    displayLog("filestream error " + e.toString());
                } finally {
                    stream.close();
                }
            }

        } catch (Exception e) {
            displayLog("write to file error " + e.toString());

        }

    }

    private static void writeToFileCustomize(String apiKey) {
        try {
            File path = mContext.getFilesDir();
            File file = new File(path, "custom.txt");
            if (!file.exists()) {
                FileOutputStream stream = new FileOutputStream(file);
                try {

                    stream.write(apiKey.getBytes());
                } catch (Exception e) {
                    displayLog("filestream error " + e.toString());
                } finally {
                    stream.close();
                }
            } else {
                file.delete();
                FileOutputStream stream = new FileOutputStream(file);
                try {

                    stream.write(apiKey.getBytes());
                } catch (Exception e) {
                    displayLog("filestream error " + e.toString());
                } finally {
                    stream.close();
                }
            }

        } catch (Exception e) {
            displayLog("write to file error " + e.toString());

        }

    }

    public static OkCollectCallback getCallback() {
        return callback;
    }

    public static void setCallback(OkCollectCallback callback) {
        OkCollect.callback = callback;
    }

    private static String convertStreamToString(InputStream is) throws IOException {
        displayLog("convertStreamToString1");
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
        displayLog("convertStreamToString2");
        return sb.toString();
    }

    private static String getStringFromFile(String filePath) throws IOException {
        displayLog("getStringFromFile1");
        File fl = new File(filePath);
        FileInputStream fin = new FileInputStream(fl);
        String ret = convertStreamToString(fin);
        //Make sure you close all streams.
        fin.close();
        displayLog("getStringFromFile2");
        return ret;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public boolean onCreate() {
        // get the context (Application context)
        mContext = getContext();
        dataProvider = new io.okcollect.android.database.DataProvider(mContext);

        return true;
    }

}
