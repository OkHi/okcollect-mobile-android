package io.okcollect.android;

import android.Manifest;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import org.json.JSONObject;

import io.okcollect.android.callback.OkCollectCallback;


public final class OkCollect extends ContentProvider {
    private static final String TAG = "OkCollect";
    protected static io.okcollect.android.database.DataProvider dataProvider;
    private static String firstname, lastname, phonenumber;
    private static Context mContext;
    private static OkCollectCallback callback;

    public OkCollect() {
    }

    public static void initialize(@NonNull final String clientKey, @NonNull final String branchId,
                                  @NonNull final String environment) throws RuntimeException {

        if (clientKey != null) {
            if (clientKey.length() > 0) {
                startInitialization(clientKey, branchId, environment);
            } else {
                throw new RuntimeException("Initialization error", new Throwable("Confirm your client key is correct"));
            }
        } else {
            throw new RuntimeException("Initialization error", new Throwable("Confirm your client key is not null"));
        }
    }


    public static void displayClient(@NonNull OkCollectCallback okCollectCallback,
                                     @NonNull JSONObject userObject) throws RuntimeException {
        if (checkPermission()) {
            startActivity(okCollectCallback, userObject);
        } else {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("error", "Location permissions hasn't been granted by the user");
                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("message", "permission_denied");
                jsonObject1.put("payload", jsonObject);
                okCollectCallback.querycomplete(jsonObject1);
            } catch (Exception e) {
            }
        }
    }

    private static void startInitialization(final String clientKey, final String branchid, final String environment) {

        try {
            dataProvider.insertProperty("branchid", branchid);
            dataProvider.insertProperty("environment", environment);
            dataProvider.insertProperty("clientKey", clientKey);
        } catch (Exception io) {
        } finally {
        }
    }

    public static void customize(@NonNull String appThemeColor, @NonNull String organisationName, @NonNull String appLogo,
                                 @NonNull String appBarColor, @NonNull Boolean appBarVisibility,
                                 @NonNull Boolean enableStreetView) {

        try {
            dataProvider.insertProperty("appThemeColor", appThemeColor);
            dataProvider.insertProperty("organisationName", organisationName);
            dataProvider.insertProperty("appLogo", appLogo);
            dataProvider.insertProperty("appBarColor", appBarColor);
            dataProvider.insertProperty("appBarVisibility", "" + appBarVisibility);
            dataProvider.insertProperty("enableStreetView", "" + enableStreetView);
        } catch (Exception io) {
        } finally {
        }
    }

    private static void startActivity(@NonNull final OkCollectCallback okCollectCallback, @NonNull final JSONObject jsonObject) {

        callback = okCollectCallback;
        firstname = jsonObject.optString("firstName");
        lastname = jsonObject.optString("lastName");
        phonenumber = jsonObject.optString("phone");
        dataProvider.insertProperty("phonenumber", phonenumber);

        try {

            final String appThemeColor = dataProvider.getPropertyValue("appThemeColor");
            final String organisationName = dataProvider.getPropertyValue("organisationName");
            final String appLogo = dataProvider.getPropertyValue("appLogo");

            final String appBarColor = dataProvider.getPropertyValue("appBarColor");
            final String appBarVisibility = dataProvider.getPropertyValue("appBarVisibility");
            final String enableStreetView = dataProvider.getPropertyValue("enableStreetView");

            io.okcollect.android.callback.AuthtokenCallback authtokenCallback = new io.okcollect.android.callback.AuthtokenCallback() {
                @Override
                public void querycomplete(String response, boolean success) {
                    if (success) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String token = jsonObject.optString("authorization_token");
                            dataProvider.insertProperty("authorization_token", token);

                            Intent intent = new Intent(mContext, io.okcollect.android.activity.OkHeartActivity.class);
                            intent.putExtra("firstname", firstname);
                            intent.putExtra("lastname", lastname);
                            intent.putExtra("phone", phonenumber);

                            if (appThemeColor != null) {
                                intent.putExtra("appThemeColor", appThemeColor);
                            }
                            if (organisationName != null) {
                                intent.putExtra("organisationName", organisationName);
                            }
                            if (appLogo != null) {
                                intent.putExtra("appLogo", appLogo);
                            }
                            if (appBarColor != null) {
                                intent.putExtra("appBarColor", appBarColor);
                            }
                            if (appBarVisibility != null) {
                                try {
                                    Boolean temp = Boolean.parseBoolean(appBarVisibility);
                                    intent.putExtra("appBarVisibility", temp);
                                } catch (Exception e) {
                                }
                            }
                            if (enableStreetView != null) {
                                try {
                                    Boolean temp = Boolean.parseBoolean(enableStreetView);
                                    intent.putExtra("enableStreetView", temp);
                                } catch (Exception e) {
                                }
                            }
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            mContext.startActivity(intent);

                        } catch (Exception e) {
                        }
                    } else {
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
            String clientKey = dataProvider.getPropertyValue("clientKey");
            String environment = dataProvider.getPropertyValue("environment");

            io.okcollect.android.asynctask.AnonymoussigninTask anonymoussigninTask =
                    new io.okcollect.android.asynctask.AnonymoussigninTask(authtokenCallback,
                            branchid, clientKey, phonenumber, environment);
            anonymoussigninTask.execute();
        } catch (Exception e) {
        }

    }

    private static boolean checkPermission() {

        Boolean permission;

        boolean permissionAccessFineLocationApproved =
                ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED;

        if (permissionAccessFineLocationApproved) {
            permission = true;
        } else {
            permission = false;
        }
        return permission;
    }

    public static OkCollectCallback getCallback() {
        return callback;
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
        mContext = getContext();
        dataProvider = new io.okcollect.android.database.DataProvider(mContext);
        return true;
    }

}
