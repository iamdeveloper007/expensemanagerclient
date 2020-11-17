package com.example.comp.restclient;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
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

public class ShowExpense extends Fragment {

    String resPonse = "";
    SharedPreferences spUserInfo;
    TableLayout tLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_showexpense, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Expenses");
        spUserInfo = getActivity().getSharedPreferences(USERINFOPREF, Context.MODE_PRIVATE);
        /**
         * List Expense
         */
        showExpenseList();
    }

    void showExpenseList() {
        try {
            String loginUserID = spUserInfo.getString(UserID, "0");
            JSONObject userInfo = new JSONObject();
            userInfo.put("userID", loginUserID);
            postRequest(postUrl + "/getexpense", userInfo.toString());
        } catch (Exception ex) {
            System.out.println(ex);
        }
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
                try {
                    super.onPostExecute(s);
                    float fUnbilledamount = 0;
                    if (s != null) {
                        if (resPonse.length() > 0) {
                            JSONArray jsonArray = new JSONArray(resPonse);
                            TableLayout tableLayout = (TableLayout) getActivity().findViewById(R.id.tableLayout);

                            if  (jsonArray.length() == 0 )
                            {
                                Toast.makeText(getContext(), "No expenses added after rent calculation.", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            for (int nIndex = 0; nIndex < jsonArray.length(); nIndex++) {
                                JSONObject jsonRowData = jsonArray.getJSONObject(nIndex);
                                TableRow rowValue = new TableRow(getContext());
                                if(nIndex == 0)
                                {
                                    rowValue.setBackgroundColor(Color.parseColor("#e6e6ff"));
                                }
                                else if (nIndex % 2 == 0)
                                    rowValue.setBackgroundColor(Color.parseColor("#b3cccc"));
                                else
                                    rowValue.setBackgroundColor(Color.parseColor("#e6f2ff"));

                                rowValue.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                                        TableLayout.LayoutParams.WRAP_CONTENT));

                                String[] rowData = {
                                        jsonRowData.get("description").toString(),
                                        jsonRowData.get("amount").toString(),
                                        jsonRowData.get("timePeriod").toString()
                                };

                                if ( nIndex != 0)
                                    fUnbilledamount = fUnbilledamount + Float.parseFloat(jsonRowData.get("amount").toString());
                                for (String value : rowData) {
                                    TextView tv = new TextView(getContext());
                                    tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                                            TableRow.LayoutParams.WRAP_CONTENT));
                                    tv.setGravity(Gravity.CENTER);
                                    tv.setTextSize(18);
                                    tv.setPadding(5, 5, 5, 5);
                                    tv.setText(value);
                                    rowValue.addView(tv);
                                } //End of for loop
                                tableLayout.addView(rowValue);
                                System.out.println("unbilledAmount" + fUnbilledamount);
                                TextView unbilledAmount = (TextView) getActivity().findViewById(R.id.unbilledAmount);
                                unbilledAmount.setText("Unbilled Expenses - " + fUnbilledamount);
                            }
                        } else {
                            Toast.makeText(getContext(), "No expsense added yet !!!", Toast.LENGTH_SHORT).show();
                        }
                    }

                } catch (Exception e) {
                    System.out.println(resPonse + e);
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
