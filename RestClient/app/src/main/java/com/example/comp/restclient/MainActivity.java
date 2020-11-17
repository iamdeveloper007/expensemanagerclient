package com.example.comp.restclient;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.comp.restclient.R.id.login;

public class MainActivity extends AppCompatActivity implements RestInterface {
    Button bLogin;
    Button bCancel;
    EditText eGroupName, eUsername, ePassword;
    TextView tvCreateGroup;
    String userName = "";
    String password = "";

    public static final String postUrl = "http://172.16.11.9:8080/ExpManager/processpost";
    String resPonse = "";

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static final String USERINFOPREF = "USERINFOPREF" ;
    public static final String Name = "nameKey";
    public static final String UserID = "userIDKey";
    public static final String UserType = "userTypeKey";
    public static final String UserGroupID = "userGroupIDKey";

    SharedPreferences spUserInfo;

    public static SQLiteUtil SqliteObj = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SqliteObj = SQLiteUtil.getInstance(this);

        bLogin = (Button) findViewById(login);
        eGroupName = (EditText) findViewById(R.id.groupName);
        eUsername = (EditText) findViewById(R.id.userName);
        ePassword = (EditText) findViewById(R.id.password);
        bCancel = (Button) findViewById(R.id.cancel);
        tvCreateGroup = (TextView) findViewById(R.id.link_signup);


        if  (SqliteObj.isUserInfoRecorded())
        {
            try {
                JSONObject jsonObject;
                if ((jsonObject = SqliteObj.getUserInfo()) != null) {
                    eGroupName.setText(jsonObject.get("groupName").toString());
                    eUsername.setText(jsonObject.get("userName").toString());
                }
            }catch (Exception ex)
            {
                System.out.println(ex);
            }
        }

        final OkHttpClient client = new OkHttpClient();

        spUserInfo= getSharedPreferences(USERINFOPREF, Context.MODE_PRIVATE);

        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    userName = eUsername.getText().toString();
                    password = ePassword.getText().toString();

                    JSONObject credInfo = new JSONObject();
                    credInfo.put("groupName", eGroupName.getText().toString());
                    credInfo.put("userName", userName);
                    credInfo.put("password", password);

                    postRequest(postUrl + "/login", credInfo.toString());

                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(),
                            e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tvCreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CreateGroup.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
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
                   /*client.newCall(request).enqueue(new Callback() {
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
                try {
                    super.onPostExecute(s);
                    JSONObject jsonObject = null;
                    if (s != null) {
                        if (resPonse == null)
                        {
                            Toast.makeText(getApplicationContext(), "Failed to connect. Please try again later.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        else if(!resPonse.contains("userType"))
                        {
                            Toast.makeText(getApplicationContext(), "Authentication Failed.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        try
                        {
                            jsonObject = new JSONObject(resPonse);
                        }
                        catch (Exception ex)
                        {
                            Toast.makeText(getApplicationContext(), "Failed to connect. Please try again later.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        //UserID
                        if (jsonObject.get("userType").toString().equalsIgnoreCase("1") || jsonObject.get("userType").toString().equalsIgnoreCase("2")) {
                            //Toast.makeText(getApplicationContext(), resPonse, Toast.LENGTH_SHORT).show();
                            SharedPreferences.Editor editor = spUserInfo.edit();
                            editor.putString(UserID, jsonObject.get("userID").toString());
                            editor.putString(UserType, jsonObject.get("userType").toString());
                            editor.putString(UserGroupID, jsonObject.get("groupID").toString());
                            editor.commit();
                            SqliteObj.open();
                            if (!SqliteObj.isUserInfoRecorded())
                                SqliteObj.addUserInfo(eGroupName.getText().toString(), userName);
                            SqliteObj.close();
                            startActivity(new Intent(MainActivity.this, ExpenseController.class));
                            //setContentView(R.layout.activity_expensecontrol);
                        }/* else if (jsonObject.get("UserType").toString().equalsIgnoreCase("2")) {
                            Toast.makeText(getApplicationContext(),
                                    resPonse, Toast.LENGTH_SHORT).show();
                            // setContentView(R.layout.activity_expensecontrol);
                            startActivity(new Intent(MainActivity.this, ListExpense.class));
                        }*/ else {
                            Toast.makeText(getApplicationContext(), "Wrong Credentials", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception ex) {
                    Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        };

        asyncTask.execute();
    }
}