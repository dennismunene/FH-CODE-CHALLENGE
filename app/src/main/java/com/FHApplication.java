package com;

import android.app.Application;

import com.dmk.fh.org.Constants;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.SyncConfiguration;
import io.realm.SyncUser;

public class FHApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);


        String path = "/~/child" ;
        String url = Constants.REALM_URL + path;




    }
}
