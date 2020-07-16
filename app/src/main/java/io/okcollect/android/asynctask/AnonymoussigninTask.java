package io.okcollect.android.asynctask;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeUnit;

import io.okcollect.android.callback.AuthtokenCallback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class AnonymoussigninTask extends AsyncTask<Void, Void, String> {
    private String branchId, clientKey, scope, userid;
    private int responseCode;
    private AuthtokenCallback authtokenCallback;
    private String environment;

    public AnonymoussigninTask(AuthtokenCallback authtokenCallback, String branchId, String clientKey,
                               String scope, String userid, String environment) {
        this.authtokenCallback = authtokenCallback;
        this.branchId = branchId;
        this.clientKey = clientKey;
        this.scope = scope;
        this.userid = userid;
        this.environment = environment;
    }

    @Override
    protected String doInBackground(Void... params) {
        String result = null;

        try {
            String urlString;
            if (environment.equalsIgnoreCase("PROD")) {
                urlString = "https://api.okhi.io/v5/auth/anonymous-signin";
            } else if (environment.equalsIgnoreCase("SANDBOX")) {
                urlString = "https://sandbox-api.okhi.io/v5/auth/anonymous-signin";
            } else {
                urlString = "https://api.okhi.io/v5/auth/anonymous-signin";
            }

            OkHttpClient.Builder b = new OkHttpClient.Builder();
            b.connectTimeout(5, TimeUnit.SECONDS);
            b.readTimeout(5, TimeUnit.SECONDS);
            b.writeTimeout(5, TimeUnit.SECONDS);

            OkHttpClient client = b.build();
            String branchclient = branchId + ":" + clientKey;

            final String basicAuth = "Basic " + Base64.encodeToString(branchclient.getBytes(), Base64.NO_WRAP);

            RequestBody formBody = new FormBody.Builder()
                    .add("scopes[0]", scope)
                    .add("phone", userid)
                    .build();

            Request request = new Request.Builder()
                    .url(urlString)
                    .post(formBody)
                    .addHeader("Authorization", basicAuth)
                    .build();

            Response response = client.newCall(request).execute();
            ResponseBody responseBody = response.body();
            result = responseBody.string();
            responseCode = response.code();
        } catch (UnsupportedEncodingException e) {
            //Log.i("Anonymous","unsupported encoding exception " + e.toString());
        } catch (IOException io) {
            //Log.i("Anonymous","io exception " + io.toString());
        } catch (IllegalArgumentException iae) {
            //Log.i("Anonymous","illegal argument exception " + iae.toString());
        }

        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        if ((200 <= responseCode) && (responseCode < 300)) {
            authtokenCallback.querycomplete(result, true);
        } else {
            authtokenCallback.querycomplete(result, false);
        }


    }
}
