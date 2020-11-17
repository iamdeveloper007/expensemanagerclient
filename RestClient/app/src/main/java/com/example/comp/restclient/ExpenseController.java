package com.example.comp.restclient;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

import static com.example.comp.restclient.MainActivity.USERINFOPREF;
import static com.example.comp.restclient.MainActivity.UserType;

public class ExpenseController extends AppCompatActivity implements OnItemClickListener {

    ActionBarDrawerToggle mDrawerToggle;
    DrawerLayout mDrawer;
    TextView messageTextView;
    ListView mDrawerListView;
    ArrayAdapter<String> adapter = null;

    SharedPreferences spUserInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_expensecontrol);
            // Get references to the TextView and DrawerLayout
            messageTextView = (TextView) findViewById(R.id.messageTextView);
            mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);

            // These lines are needed to display the top-left hamburger button


            // Make the hamburger button work
            mDrawerToggle = new ActionBarDrawerToggle(this, mDrawer, R.string.drawer_open, R.string.drawer_close) {
                @Override
                public void onDrawerClosed(View drawerView) {

                }

                @Override
                public void onDrawerOpened(View drawerView) {
                }
            };


            // Change the TextView message when ListView item is clicked

            spUserInfo = getSharedPreferences(USERINFOPREF, Context.MODE_PRIVATE);
            String[] accessControl;
            String strUserType = spUserInfo.getString(UserType, "1");
            System.out.println("********************************" + strUserType);
            if (strUserType.equals("2")) {
                accessControl = getResources().getStringArray(R.array.AdminAccessList);
            } else {
                accessControl = getResources().getStringArray(R.array.UserAccessList);
            }

            List<String> list = (List) Arrays.asList(accessControl);
            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
            mDrawerListView = (ListView) findViewById(R.id.left_drawer_admin);
            mDrawerListView.setItemsCanFocus(false);
            mDrawerListView.setAdapter(adapter);
            mDrawerListView.setOnItemClickListener(this);

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            mDrawer.setDrawerListener(mDrawerToggle);
            mDrawerToggle.syncState();

            Fragment fragment = new ShowExpense();

            if (fragment != null) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, fragment);
                ft.commit();
            }

        } catch (Exception ex) {
            System.out.println(ex);
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // mDrawerListView.setItemChecked(position, true);
        //messageTextView.setText("Menu Item at position " + position + " clicked.");
        Fragment fragment = null;
        switch (position) {
            case 0: {
                fragment = new ShowExpense();
            }
            break;
            case 1: {
                fragment = new AddExpense();
            }
            break;
            case 2: {
                finish();
                setContentView(R.layout.activity_main);
            }
            break;
            case 3: {
                fragment = new CreateUser();
            }
            break;
            case 4: {
                fragment = new CalculateRent();
            }
            break;
            case 5: {
                fragment = new CommonExpense();
            }
            break;
            case 6: {
                fragment = new ShowCommonPayable();
            }
            break;
            default:
                break;
        }

        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }
        mDrawer.closeDrawer(GravityCompat.START);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
}
