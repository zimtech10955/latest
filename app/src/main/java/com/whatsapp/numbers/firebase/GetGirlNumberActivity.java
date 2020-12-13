package com.whatsapp.numbers.firebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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

public class GetGirlNumberActivity extends AppCompatActivity {
    private TextView mName , mAge , mCountry , mNumber;
    private ImageView mImage;
    private com.facebook.ads.InterstitialAd interstitialAd;
    private LinearLayout mLayout;
    private Button mGetNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_girl_number);


        init();
        setupData();

        eventHandling();
        loadFromFirebase();
    }
    
    private void eventHandling(){
        mGetNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (interstitialAd != null && interstitialAd.isAdLoaded()){
                    MyFacebook.facebookShowInterstitialWithoutIntent(interstitialAd);
                }
                startActivity(new Intent(GetGirlNumberActivity.this , MainActivity.class));
                finish();
                Toast.makeText(GetGirlNumberActivity.this, "Try again later, Number not found", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void init(){
        mName = findViewById(R.id.displayName);
        mAge = findViewById(R.id.displayAge);
        mCountry = findViewById(R.id.displayCountry);
        mLayout = findViewById(R.id.fb_native_ad);
        mGetNumber = findViewById(R.id.buttonGetNumbers);
       // mNumber = findViewById(R.id.displayNumber);
    }

    private void setupData(){
        String age = getIntent().getStringExtra(MyConstant.AGE);
        String country = getIntent().getStringExtra(MyConstant.COUNTRY);
       // String image = getIntent().getStringExtra(MyConstant.CATEGORY_IMAGE);
        String name = getIntent().getStringExtra(MyConstant.CATEGORY_NAME);
        String number = getIntent().getStringExtra(MyConstant.NUMBER);

        mName.setText(name);
        mAge.setText(age + " Years");
        mCountry.setText(country);
       // mNumber.setText(number);

    }

    private void loadFromFirebase(){
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child(MyConstant.FACEBOOK);
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String interstitial = (String) snapshot.child(MyConstant.INTERSTITIAL).getValue();
                String mNative = (String) snapshot.child(MyConstant.NATIVE).getValue();

                MyFacebook.loadNativeAd(GetGirlNumberActivity.this , mNative , mLayout , 650);
                Interads(interstitial);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void Interads(String id) {

        interstitialAd = new com.facebook.ads.InterstitialAd(this , id);
        interstitialAd.loadAd();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (interstitialAd != null && interstitialAd.isAdLoaded()){
            MyFacebook.facebookShowInterstitialWithoutIntent(interstitialAd);
        }

    }
}