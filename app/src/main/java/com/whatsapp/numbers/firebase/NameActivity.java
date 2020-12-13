package com.whatsapp.numbers.firebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.whatsapp.numbers.R;
import com.whatsapp.numbers.activities.MainActivity;
import com.whatsapp.numbers.ads.MyFacebook;
import com.whatsapp.numbers.utils.MyConstant;
//import com.startapp.sdk.adsbase.StartAppAd;

public class NameActivity extends AppCompatActivity {
    private LinearLayout mLayout;
    private EditText mName;
    private com.facebook.ads.InterstitialAd interstitialAd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name);
        mName = findViewById(R.id.name);





        initViews();
        loadFromFirebase();

        findViewById(R.id.buttonNext).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = mName.getText().toString();
                if (TextUtils.isEmpty(name)){
                    Toast.makeText(NameActivity.this, "Required Name", Toast.LENGTH_SHORT).show();
                }else{
                    if (interstitialAd != null && interstitialAd.isAdLoaded()){
                        interstitialAd.show();

                        InterstitialAdListener interstitialAdListener = new InterstitialAdListener() {
                            @Override
                            public void onInterstitialDisplayed(Ad ad) {
                                // Interstitial ad displayed callback
                                //   Log.e(TAG, "Interstitial ad displayed.");
                            }

                            @Override
                            public void onInterstitialDismissed(Ad ad) {
                                // Interstitial dismissed callback
                                //     Log.e(TAG, "Interstitial ad dismissed.");
                                interstitialAd.loadAd();
                                startActivity(new Intent(NameActivity.this , MainActivity.class));
                                finish();

                            }

                            @Override
                            public void onError(Ad ad, AdError adError) {
                                // Ad error callback
                                //       Log.e(TAG, "Interstitial ad failed to load: " + adError.getErrorMessage());
                            }

                            @Override
                            public void onAdLoaded(Ad ad) {
                                // Interstitial ad is loaded and ready to be displayed
                                //         Log.d(TAG, "Interstitial ad is loaded and ready to be displayed!");
                                // Show the ad

                            }

                            @Override
                            public void onAdClicked(Ad ad) {
                                // Ad clicked callback
                                //           Log.d(TAG, "Interstitial ad clicked!");
                            }

                            @Override
                            public void onLoggingImpression(Ad ad) {
                                // Ad impression logged callback
                            }
                        };

                        // For auto play video ads, it's recommended to load the ad
                        // at least 30 seconds before it is shown
                        interstitialAd.loadAd(
                                interstitialAd.buildLoadAdConfig()
                                        .withAdListener(interstitialAdListener)
                                        .build());


                    }else {
                    startActivity(new Intent(NameActivity.this , MainActivity.class));
                    finish();

                   // StartAppAd.showAd(NameActivity.this);
                }

                }



            }
        });
    }


    private void initViews(){
        mLayout = findViewById(R.id.fb_native_ad);
    }

    private void loadFromFirebase(){
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child(MyConstant.FACEBOOK);
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String interstitial = (String) snapshot.child(MyConstant.INTERSTITIAL).getValue();
                String mNative = (String) snapshot.child(MyConstant.NATIVE).getValue();

                MyFacebook.loadNativeAd(NameActivity.this , mNative , mLayout , 600);
                Interads(interstitial);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        //StartAppAd.onBackPressed(this);
        super.onBackPressed();
    }

    private void Interads(String id) {

        interstitialAd = new com.facebook.ads.InterstitialAd(this , id);
        interstitialAd.loadAd();
    }

}