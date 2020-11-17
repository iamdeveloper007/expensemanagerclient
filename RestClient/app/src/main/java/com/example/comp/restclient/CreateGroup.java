package com.example.comp.restclient;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.comp.restclient.MainActivity.JSON;
import static com.example.comp.restclient.MainActivity.USERINFOPREF;
import static com.example.comp.restclient.MainActivity.postUrl;

/**
 * Created by Comp on 6/25/2017.
 */

public class CreateGroup extends Activity implements RestInterface {

    Button bRegister;
    EditText eGroupname, eEMailID, eAdminUsername, eAdminPassword;
    String resPonse = "";

    SharedPreferences spUserInfo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creategroup);

        bRegister = (Button) findViewById(R.id.register);
        eGroupname = (EditText) findViewById(R.id.groupName);
        eEMailID = (EditText) findViewById(R.id.emailID);
        eAdminUsername = (EditText) findViewById(R.id.adminUserName);
        eAdminPassword = (EditText) findViewById(R.id.userPassword);

        spUserInfo = getSharedPreferences(USERINFOPREF, Context.MODE_PRIVATE);

        bRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    JSONObject groupInfo = new JSONObject();
                    groupInfo.put("groupName", eGroupname.getText().toString());
                    groupInfo.put("emailID", eEMailID.getText().toString());
                    groupInfo.put("userName", eAdminUsername.getText().toString());
                    groupInfo.put("password", eAdminPassword.getText().toString());

                    postRequest(postUrl + "/creategroup", groupInfo.toString());

                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(),
                            e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void postRequest(String postUrl, String postBody) {


        final OkHttpClient client = new OkHttpClient();

        RequestBody body = RequestBody.create(JSON, postBody);

        final Request request = new Request.Builder()
                .url(postUrl)
                .post(body)
                .build();

        AsyncTask<Void, Void, String> asyncTask = new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {
                try {
                    Response response = client.newCall(request).execute();
                    if (!response.isSuccessful()) {
                        return resPonse;
                    }
                    resPonse = response.body().string();
                   /* client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            call.cancel();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            resPonse = response.body().string();
                        }
                    });*/
                } catch (Exception e) {
                    e.printStackTrace();
                    return resPonse;
                }
                return resPonse;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (s != null) {
                    if (resPonse.contains("success")) {
                        Toast.makeText(getApplicationContext(), resPonse, Toast.LENGTH_SHORT).show();
                        try {
                            Thread.sleep(1000);

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        try {
                            Thread.sleep(1000 * 2);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        setContentView(R.layout.activity_main);
                    } else {
                        Toast.makeText(getApplicationContext(), "Failed to create group. Please try again later.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };

        asyncTask.execute();
    }
}
