package io.okcollect.android.activity;


import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import io.okcollect.android.OkCollect;
import io.okcollect.android.R;
import io.okcollect.android.callback.OkCollectCallback;

public class TestActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_ACCESS_FINE_LOCATION = 83;
    private EditText firstnameedt, lastnameedt, phoneedt;
    private Button submitbtn, pingbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        firstnameedt = findViewById(R.id.firstname);
        lastnameedt = findViewById(R.id.lastname);
        phoneedt = findViewById(R.id.phone);
        submitbtn = findViewById(R.id.submit);
        pingbtn = findViewById(R.id.ping);

        OkCollect.initialize("cb613bb2-d132-4e86-a873-1988275ca2d7", "X6VQy8pMxw", "sandbox");
        OkCollect.customize("rgb(0, 1, 13)", "sendy", "https://lh3.ggpht.com/GE2EnJs1M1Al9_Ol2Q1AV0VdSsvjR2dsVWO_2ARuaGVS-CJUhJGbEt_OMHlvR2b8zg=s180", "rgb(255, 0, 0)", true, true);


        final OkCollectCallback okCollectCallback = new OkCollectCallback() {
            @Override
            public void querycomplete(JSONObject result) {
                displayLog(result.toString());

            }
        };

        submitbtn.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View view) {

                                             displayLog("submit clicked");


                                             try {
                                                 JSONObject jsonObject = new JSONObject();
                                                 jsonObject.put("firstName", "Ramogi");
                                                 jsonObject.put("lastName", "Ochola");
                                                 jsonObject.put("phone", "+254713567907");
                                                 OkCollect.displayClient(okCollectCallback, jsonObject);
                                             } catch (JSONException e) {
                                                 displayLog("json exception error " + e.toString());
                                             }

                                         }
                                     }
        );

        pingbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                displayLog("onrequest permission");
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    displayLog("accepted location permissions");
                    Toast.makeText(TestActivity.this, "Press submit", Toast.LENGTH_LONG).show();


                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    displayLog("denied location permission");

                }
                return;
            }
        }
    }


    private void displayLog(String log) {
        Log.i("TestActivity", log);
    }

}
