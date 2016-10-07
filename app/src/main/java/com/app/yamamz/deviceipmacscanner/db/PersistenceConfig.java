package com.app.yamamz.deviceipmacscanner.db;

import android.database.sqlite.SQLiteDatabase;

import org.droitateddb.config.Persistence;
import org.droitateddb.hooks.DbUpdate;
import org.droitateddb.hooks.Update;

/**
 * Created by Yamamz on 10/4/2016.
 */
@Update
@Persistence(dbName = "device.db", dbVersion = 1)
public class PersistenceConfig implements DbUpdate {
    @Override
    public void onUpdate(SQLiteDatabase db, int oldVersion, int newVersion) {
        // here you can put your update code, when changing the database version
    }
}