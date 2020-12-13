package com.whatsapp.numbers.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.telephony.SubscriptionPlan;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.whatsapp.numbers.Config;
import com.whatsapp.numbers.R;
import com.whatsapp.numbers.ads.MyFacebook;
import com.whatsapp.numbers.firebase.NameActivity;
import com.whatsapp.numbers.firebase.StartActivity;
import com.whatsapp.numbers.utils.MyConstant;

public class ActivitySplash extends AppCompatActivity {

    Boolean isCancelled = false;
    private ProgressBar progressBar;
    String id = "0";
    String url = "";
    private String display = "false";
    private com.facebook.ads.InterstitialAd mInterstitial;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        if(getIntent().hasExtra("nid")) {
            id = getIntent().getStringExtra("nid");
            url = getIntent().getStringExtra("external_link");
        }


        adsPage();
        loadFromFirebase();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;
                if (display.equals("true")){
                    intent = new Intent(getApplicationContext(), StartActivity.class);
                }else{
                    intent = new Intent(getApplicationContext(), NameActivity.class);

                }

                if (mInterstitial != null && mInterstitial.isAdLoaded()){
                    MyFacebook.facebookShowInterstitialWithFinish(ActivitySplash.this , mInterstitial , intent);
                }else{
                    startActivity(intent);
                    finish();

                }
            }
        }, Config.SPLASH_TIME);




    }


    private void loadFbInterstitial(String id){
        mInterstitial = new com.facebook.ads.InterstitialAd(this , id);
        mInterstitial.loadAd();
    }
    private void loadFromFirebase(){
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child(MyConstant.FACEBOOK);
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String banner = (String) snapshot.child(MyConstant.BANNER).getValue();
                String interstitial = (String) snapshot.child(MyConstant.INTERSTITIAL).getValue();
                loadFbInterstitial(interstitial);


                assert banner != null;
                Log.d("firebase" , banner);
                assert interstitial != null;
                Log.d("firebase" , interstitial);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void adsPage(){
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("ads");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                display = (String) snapshot.child("display").getValue();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}
