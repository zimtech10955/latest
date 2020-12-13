package com.whatsapp.numbers.firebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.whatsapp.numbers.R;
import com.whatsapp.numbers.activities.MainActivity;
import com.whatsapp.numbers.activities.facebook;
import com.whatsapp.numbers.ads.MyFacebook;
import com.whatsapp.numbers.utils.MyConstant;

import de.hdodenhof.circleimageview.CircleImageView;

public class GirlDetailActivity extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("switch");

   
    private TextView mName, mAge, mCountry;
    private ImageView mImage;
    private com.facebook.ads.InterstitialAd interstitialAd;
    private LinearLayout mLayout;

    private InterstitialAd mInterstitialAd;
    private ImageView mCall, mChat;


    final String TAG = "value";

    private String value="off";









    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_girl_detail);






        init();





        final String TAG = "value";

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {


            // get data from  database
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
               String value = dataSnapshot.getValue(String.class);

                Log.d(TAG, "Value is: " +value);

            }





            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });








        setupData();
        eventHandling();
        loadFromFirebase();
    }


    private void init(){
        mName = findViewById(R.id.displayName);
        mAge = findViewById(R.id.displayAge);
        mCountry = findViewById(R.id.displayCountry);
        mImage = (ImageView) findViewById(R.id.profileImage);
        mLayout = findViewById(R.id.fb_native_ad);
        mCall = findViewById(R.id.imageViewPhone);
        mChat = findViewById(R.id.imageViewChat);











    }

    private void eventHandling() {

        mCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//CLICK ON CALL

           //     if(value.equals("true")) {

                    Intent intent;
                    intent = new Intent(getApplicationContext(), facebook.class);
                    startActivity(intent);
             //   }else
                   // alertDialog();

            }
        });





        mChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//CLICK ON CHAT

              //  if(value.equals("true"))
               //  alertDialog();
              //  else
                {
                    Intent intent;
                    intent = new Intent(getApplicationContext(), facebook.class);
                    startActivity(intent);
                }
                }

        });
    }
    private void alertDialog(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Alert");
        alert.setMessage("Are you sure you want to get phone number if yes then click on yes. Thanks");
        alert.setCancelable(false);
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (interstitialAd != null && interstitialAd.isAdLoaded()){
                    MyFacebook.facebookShowInterstitialWithoutIntent(interstitialAd);
                }
                goNumberActivity();
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (interstitialAd != null && interstitialAd.isAdLoaded()){
                    MyFacebook.facebookShowInterstitialWithoutIntent(interstitialAd);
                }

                dialog.dismiss();
            }
        });
        alert.show();

    }

    private void goNumberActivity(){

        String age = getIntent().getStringExtra(MyConstant.AGE);
        String country = getIntent().getStringExtra(MyConstant.COUNTRY);
        String image = getIntent().getStringExtra(MyConstant.CATEGORY_IMAGE);
        String name = getIntent().getStringExtra(MyConstant.CATEGORY_NAME);
       // String number = getIntent().getStringExtra(MyConstant.NUMBER);

        Intent intent = new Intent(GirlDetailActivity.this , GetGirlNumberActivity.class);
        intent.putExtra(MyConstant.AGE , age);
        intent.putExtra(MyConstant.COUNTRY , country);
        intent.putExtra(MyConstant.CATEGORY_IMAGE , image);
        intent.putExtra(MyConstant.CATEGORY_NAME , name);
       // intent.putExtra(MyConstant.NUMBER , number);

        startActivity(intent);
    }
    private void setupData(){
        String age = getIntent().getStringExtra(MyConstant.AGE);
        String country = getIntent().getStringExtra(MyConstant.COUNTRY);
        String image = getIntent().getStringExtra(MyConstant.CATEGORY_IMAGE);
        String name = getIntent().getStringExtra(MyConstant.CATEGORY_NAME);
        String number = getIntent().getStringExtra(MyConstant.NUMBER);

        mName.setText(name);
        mAge.setText(age + " Years");
        mCountry.setText(country);
        Glide.with(this).load(image).into(mImage);
    }

    private void loadFromFirebase(){
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child(MyConstant.FACEBOOK);
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String interstitial = (String) snapshot.child(MyConstant.INTERSTITIAL).getValue();
                String mNative = (String) snapshot.child(MyConstant.NATIVE).getValue();

                MyFacebook.loadNativeAd(GirlDetailActivity.this , mNative , mLayout , 500);
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

}