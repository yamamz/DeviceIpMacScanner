package com.app.yamamz.deviceipmacscanner.controller;

import android.app.Activity;
import android.database.Cursor;

import com.app.yamamz.deviceipmacscanner.db.Database;

/**
 * Created by yamamz on 10/9/2016.
 */

public class Host {



    /**
     * Fetches the MAC vendor from the database
     *
     * @param mac      MAC address
     * @param activity The calling activity
     */
    public static String getMacVendor(String mac, Activity activity) {
        Database db = new Database(activity);
        Cursor cursor = db.queryDatabase("SELECT vendor FROM ouis WHERE mac LIKE ?", new String[]{mac});
        String vendor;

        try {
            if (cursor != null && cursor.moveToFirst()) {
                vendor = cursor.getString(cursor.getColumnIndex("vendor"));
            } else {
                vendor = "Vendor not in database";
            }
        } finally {
            if (cursor != null) {
                cursor.close();
                db.close();
            }
        }

        return vendor;
    }

}
