package com.whatsapp.numbers.firebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.whatsapp.numbers.R;
import com.whatsapp.numbers.activities.MainActivity;
import com.whatsapp.numbers.ads.MyFacebook;
import com.whatsapp.numbers.firebase.model.Apps;
import com.whatsapp.numbers.firebase.myadapter.AppsAdapter;
import com.whatsapp.numbers.utils.MyConstant;

import java.util.ArrayList;
import java.util.List;

public class StartActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private List<Apps> list;
    private boolean flag = false;
    private com.facebook.ads.InterstitialAd interstitialAd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        list = new ArrayList<>();

        eventHandling();
        bindRecyclerView();
        loadData();


        getData();

    }
    private void loadNative(String id){
        if (id != null){
            MyFacebook.loadFbNativeAd(this , id);
        }

    }


    private void eventHandling(){
        findViewById(R.id.buttonStart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this , NameActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                if (interstitialAd.isAdLoaded() && interstitialAd!=null){
                    MyFacebook.facebookShowInterstitialWithoutFinish(StartActivity.this , interstitialAd , intent);

                }else{
                    startActivity(intent);

                }


            }
        });
    }

    private void bindRecyclerView(){
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this , 3));
        mRecyclerView.setHasFixedSize(true);
    }


    private void loadInterstitial(String id) {
        interstitialAd = new com.facebook.ads.InterstitialAd(this ,id);
        interstitialAd.loadAd();
    }
    private void loadData(){
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("apps");
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot npsnapshot : dataSnapshot.getChildren()){
                        Apps l=npsnapshot.getValue(Apps.class);
                        list.add(l);
                    }
                    AppsAdapter appsAdapter = new AppsAdapter(StartActivity.this , list);
                    mRecyclerView.setAdapter(appsAdapter);
                    appsAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static void openAppRating(Context context  , String appId) {
        Intent rateIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("market://details?id=" + appId));
        boolean marketFound = false;

        // find all applications able to handle our rateIntent
        final List<ResolveInfo> otherApps = context.getPackageManager()
                .queryIntentActivities(rateIntent, 0);
        for (ResolveInfo otherApp: otherApps) {
            // look for Google Play application
            if (otherApp.activityInfo.applicationInfo.packageName
                    .equals("com.android.vending")) {

                ActivityInfo otherAppActivity = otherApp.activityInfo;
                ComponentName componentName = new ComponentName(
                        otherAppActivity.applicationInfo.packageName,
                        otherAppActivity.name
                );
                // make sure it does NOT open in the stack of your activity
                rateIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                // task reparenting if needed
                rateIntent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                // if the Google Play was already open in a search result
                //  this make sure it still go to the app page you requested
                rateIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                // this make sure only the Google Play app is allowed to
                // intercept the intent
                rateIntent.setComponent(componentName);
                context.startActivity(rateIntent);
                marketFound = true;
                break;

            }
        }

        // if GP not present on device, open web browser
        if (!marketFound) {
            Intent webIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id="+appId));
            context.startActivity(webIntent);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    private void getData(){
        FirebaseDatabase.getInstance().getReference().child("facebook")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {


                        String interstitial = (String) snapshot.child(MyConstant.INTERSTITIAL).getValue();
                        String fbNative = (String) snapshot.child(MyConstant.NATIVE).getValue();

                        loadInterstitial(interstitial);
                        loadNative(fbNative);


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private boolean displayAd(int saved , int db){
        return saved == db;
    }



}