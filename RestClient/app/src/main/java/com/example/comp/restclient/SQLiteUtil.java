package com.example.comp.restclient;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONObject;

/**
 * Created by Comp on 6/17/2017.
 */
public class SQLiteUtil {

    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase database;
    private static SQLiteUtil instance;

    /**
     * Private constructor to aboid object creation from outside classes.
     *
     * @param context
     */
    private SQLiteUtil(Context context) {
        this.openHelper = new DatabaseOpenHelper(context);
    }

    /**
     * Return a singleton instance of DatabaseAccess.
     *
     * @param context the Context
     * @return the instance of DabaseAccess
     */
    public static SQLiteUtil getInstance(Context context) {
        if (instance == null) {
            instance = new SQLiteUtil(context);
        }
        return instance;
    }

    /**
     * Open the database connection.
     */
    public void open() {
        this.database = openHelper.getWritableDatabase();
    }

    /**
     * Close the database connection.
     */
    public void close() {
        if (database != null) {
            this.database.close();
        }
    }

    public void addUserInfo(String groupName, String userName) {
        try {
            SQLiteDatabase db = openHelper.getWritableDatabase();
            db.execSQL("Insert into expense_userInfo (GroupName, UserName) Values ('" + groupName + "','" + userName + "')");
            db.close(); // Closing database connection
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    // Check user information has beed recorded already.
    public boolean isUserInfoRecorded() {
        SQLiteDatabase db = null;
        boolean bRet = false;
        try {
            db = openHelper.getWritableDatabase();
            Cursor cursor = db.rawQuery("Select Count(userName) from expense_userInfo", null);
            if (cursor != null) {
                cursor.moveToFirst();
                if (cursor.getInt(0) > 0) {
                    bRet = true;
                }
                cursor.close();
            }
        } catch (Exception ex) {
            System.out.println(ex);
        } finally {
            if (db != null) {
                db.close(); // Closing database connection
            }
        }
        return bRet;
    }

    public JSONObject getUserInfo() {
        JSONObject userInfo = null;
        SQLiteDatabase db = null;
        try {
            db = openHelper.getWritableDatabase();
            Cursor cursor = db.rawQuery("Select GroupName, UserName from expense_userInfo", null);
            if (cursor != null) {
                cursor.moveToFirst();
                userInfo = new JSONObject();
                userInfo.put("groupName", cursor.getString(0));
                userInfo.put("userName", cursor.getString(1));
                cursor.close();
            }
        } catch (Exception ex) {
            System.out.println(ex);
        } finally {
            if (db != null) {
                db.close(); // Closing database connection
            }
        }
        return userInfo;
    }

    // Adding new contact
   /* public void addMobileInfo(MobileInfo MobileInfo) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_INFO, MobileInfo.getnID()); // MobileInfo Name
        values.put(COLUMN_COLOR, MobileInfo.getData()); // MobileInfo color

        // Inserting Row
        db.insert(TABLE_MATERIE, null, values);
        db.close(); // Closing database connection
    }*/

    // Getting single contact
    /*public MobileInfo getMobileInfo(int id) {

        //ELIMINATO COLUMN_DAY e COLUMN_HOUR
        Cursor cursor = database.rawQuery("Select Information from MobileInformation Where RowID = "+id, null);
        if (cursor != null)
            cursor.moveToFirst();

        MobileInfo MobileInfo = new MobileInfo(Integer.parseInt(cursor.getString(0)), cursor.getString(1));
        // return contact
        return MobileInfo;
    }*/

    // Getting All Contacts
   /* public List<MobileInfo> getAllMaterie() {
        List<MobileInfo> MobileInfoList = new ArrayList<MobileInfo>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_MATERIE;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                MobileInfo MobileInfo = new MobileInfo();
                MobileInfo.setID(Integer.parseInt(cursor.getString(0)));
                MobileInfo.setMobileInfo(cursor.getString(1));
                MobileInfo.setColor(Integer.parseInt(cursor.getString(2)));
                // Adding contact to list
                MobileInfoList.add(MobileInfo);
            } while (cursor.moveToNext());
        }

        // return contact list
        return MobileInfoList;
    }

    // Getting contacts Count
    /*public int getMobileInfoCount() {
        String countQuery = "SELECT  * FROM " + TABLE_MATERIE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }*/

    // Updating single contact
    /*public int updateMobileInfo(MobileInfo MobileInfo) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, MobileInfo.getMobileInfo());
        values.put(COLUMN_COLOR, MobileInfo.getColor());

        // updating row
        return db.update(TABLE_MATERIE, values, COLUMN_ID + " = ?",
                new String[] { String.valueOf(MobileInfo.getID()) });
    }

    // Deleting single contact
    public void deleteMobileInfo(MobileInfo MobileInfo) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MATERIE, COLUMN_ID + " = ?",
                new String[] { String.valueOf(MobileInfo.getID()) });
        db.close();
    }*/

}//Close class database
