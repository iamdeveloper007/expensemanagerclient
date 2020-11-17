package com.example.comp.restclient;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import static com.example.comp.restclient.MainActivity.UserID;
import static com.example.comp.restclient.MainActivity.postUrl;

/**
 * Created by Comp on 6/29/2017.
 */

public class AddExpense extends Fragment implements RestInterface {

    EditText eDescription, eAmount;
    Button bAddExpense;
    String resPonse="";

    SharedPreferences spUserInfo;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.actitivity_addexpense, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Add Expense");

        eDescription    = (EditText) getView().findViewById(R.id.userName);
        eAmount         = (EditText) getView().findViewById(R.id.spentAmount);
        bAddExpense     = (Button)   getView().findViewById(R.id.addExpense);

        spUserInfo= getActivity().getSharedPreferences(USERINFOPREF, Context.MODE_PRIVATE);


        bAddExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    JSONObject groupInfo = new JSONObject();
                    groupInfo.put("description", eDescription.getText().toString());
                    groupInfo.put("amount", eAmount.getText().toString());
                    groupInfo.put("userID", spUserInfo.getString(UserID, "0"));

                    postRequest(postUrl + "/addexpense", groupInfo.toString());

                } catch (Exception e) {
                    Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getActivity(), resPonse, Toast.LENGTH_SHORT).show();
                        Fragment fragment = new ShowExpense();
                        if (fragment != null) {
                                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                            ft.replace(R.id.content_frame, fragment);
                            ft.commit();
                        }
                    } else {
                       Toast.makeText(getActivity(), "Failed to add expense. Please try again later.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };

        asyncTask.execute();
    }


    public void onBackPressed()
    {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        fm.popBackStack();
    }
}