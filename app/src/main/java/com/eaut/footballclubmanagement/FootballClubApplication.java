package com.eaut.footballclubmanagement;

import android.app.Application;

import com.eaut.footballclubmanagement.network.RetrofitClient;

public class FootballClubApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        RetrofitClient.initialize(this);
    }
}
